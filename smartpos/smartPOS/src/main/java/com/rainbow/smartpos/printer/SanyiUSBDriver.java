package com.rainbow.smartpos.printer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.rainbow.smartpos.service.PrintService;
import com.rainbow.smartpos.Restaurant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SanyiUSBDriver {

    protected static final int STD_USB_REQUEST_GET_DESCRIPTOR = 0x06;
    protected static final int LIBUSB_DT_STRING = 0x03;

    private UsbManager mUsbManager;
    private Context mContext;
    public PendingIntent mPermissionIntent;
    private UsbPrinter usingPrinter;

    private List<UsbPrinter> allPrinters = new ArrayList<UsbPrinter>();
    private UsbPermissionListener listener;

    public static interface UsbPermissionListener {
        void onPermissionAssign();

        void onPermissionReject();
    }

    public SanyiUSBDriver(UsbManager usbManager, Context context, PendingIntent usbIntent, UsbPermissionListener listener) {
        mUsbManager = usbManager;
        mContext = context;
        mPermissionIntent = usbIntent;
        this.listener = listener;
        initAllUsbDevice();
    }

    /**
     * 过滤USB设备，如果返回值为空，则说明该设备被过滤掉，否则，该设备可以显示
     *
     * @param usbDevice
     * @return
     */
    private UsbDevice filterUsbDevice(UsbDevice usbDevice) {

		/* add filters */
        return usbDevice;
    }

    public UsbDeviceConnection openUsingDevice() {
        return mUsbManager.openDevice(usingPrinter.getDevice());
    }

    public UsbDeviceConnection openUsingDevice(UsbDevice device) {
        return mUsbManager.openDevice(device);
    }

    public void initPrinter(UsbDevice device) {
        this.allPrinters.add(generatePrinterInstance(device));
    }

    public void initAllUsbDevice() {
        if (mUsbManager != null && mContext != null) {
            HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
            for (Map.Entry<String, UsbDevice> e : deviceList.entrySet()) {
                final UsbDevice device = filterUsbDevice(e.getValue());
                UsbInterface intf = device.getInterface(0);
                if (device.getDeviceClass() == UsbConstants.USB_CLASS_PRINTER || intf.getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
                    if (!mUsbManager.hasPermission(device)) {
                        mUsbManager.requestPermission(device, mPermissionIntent);
                    } else {
                        UsbPrinter printerInstance = generatePrinterInstance(device);
                        if (printerInstance != null) {
                            allPrinters.add(printerInstance);
                        }
                    }
                }
            }
        }
    }

    public UsbPrinter findPrinter(String id) {
        for (UsbPrinter p : allPrinters) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public UsbPrinter generatePrinterInstance(UsbDevice device) {
        UsbDeviceConnection connection = mUsbManager.openDevice(device);
        UsbInterface intf = device.getInterface(0);
        connection.claimInterface(intf, true);
        int ep = -1;

        for (int i = 0; i < intf.getEndpointCount(); i++) {
            if (intf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_OUT) {
                ep = i;
                break;
            }
        }
        if (ep != -1) {
            return new UsbPrinter((getPrinterDescription(device, intf)), device, ep);
        }
        return null;
    }

    private String getPrinterDescription(final UsbDevice device, UsbInterface intf) {
        UsbDeviceConnection connection = mUsbManager.openDevice(device);
        connection.claimInterface(intf, true);
        UsbEndpoint ep = null;
        for (int i = 0; i < intf.getEndpointCount(); i++) {
            if (intf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_OUT) {
                ep = intf.getEndpoint(i);
            }
        }
        byte[] rawDescs = connection.getRawDescriptors();

        byte[] buffer = new byte[255];
        int idxMan = rawDescs[14];
        int idxPrd = rawDescs[15];

        int rdo = connection.controlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_STANDARD,
                STD_USB_REQUEST_GET_DESCRIPTOR, (LIBUSB_DT_STRING << 8) | idxMan, 0x0409, buffer, 0xFF, 0);
        try {
            String manufacturer = new String(buffer, 2, rdo - 2, "UTF-16LE");
            rdo = connection.controlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_STANDARD,
                    STD_USB_REQUEST_GET_DESCRIPTOR, (LIBUSB_DT_STRING << 8) | idxPrd, 0x0409, buffer, 0xFF, 0);
            String product = new String(buffer, 2, rdo - 2, "UTF-16LE");
            connection.releaseInterface(intf);
            return manufacturer + " " + product;
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return "";
    }


    public byte[] helloWorld() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(0x1B);
            baos.write("@".getBytes());
            baos.write("Hello World".getBytes());
            baos.write(0x1B);
            baos.write("d".getBytes());
            baos.write((byte) 6);

//            System.out.println("Hello World");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public List<UsbPrinter> getAllPrinters() {
        return allPrinters;
    }

    public UsbPrinter getUsingPrinter() {
        return usingPrinter;
    }

    public void setUsingPrinter(UsbPrinter usingPrinter) {
        this.usingPrinter = usingPrinter;
    }

    public boolean usbReceiverIsRegistered = false;

    public BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Restaurant.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Restaurant.usbDriver.initPrinter(device);
                            if (listener != null) {
                                listener.onPermissionAssign();
                            }
                        }
                    } else {
                        Log.d("sanyipos", "permission denied for device " + device);
                        if (listener != null) {
                            listener.onPermissionReject();
                        }
                    }
                }
            }
        }
    };

    public static boolean isPrinterServcieStarted(Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PrintService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
