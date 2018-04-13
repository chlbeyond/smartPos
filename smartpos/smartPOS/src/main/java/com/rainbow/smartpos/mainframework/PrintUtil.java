package com.rainbow.smartpos.mainframework;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.login.InputPasswordFragment;
import com.rainbow.smartpos.login.SanyiPintServiceConnection;
import com.rainbow.smartpos.printer.ComPrinter;
import com.rainbow.smartpos.printer.SanyiUSBDriver;
import com.rainbow.smartpos.printer.UsbPrinter;
import com.rainbow.smartpos.service.PrintService;
import com.rainbow.smartpos.util.NetworkUtil;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.SharePrinterRequest_;
import com.sanyipos.sdk.model.ShopPrinter;

import java.util.List;

/**
 * Created by ss on 2016/4/23.
 */
public class PrintUtil {

    private static List<UsbPrinter> UsbPrinters;
//    private static boolean registered = false;

    public static List<UsbPrinter> getLocalPrint(final Context mContext) {

        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) return null;
        Restaurant.usbDriver = new SanyiUSBDriver(usbManager, mContext, PendingIntent.getBroadcast(mContext, 0, new Intent(Restaurant.ACTION_USB_PERMISSION), 0),
                new SanyiUSBDriver.UsbPermissionListener() {
                    @Override
                    public void onPermissionAssign() {
                        UsbPrinters = Restaurant.usbDriver.getAllPrinters();
                        if(!Restaurant.usbDriver.usbReceiverIsRegistered) {
                            IntentFilter filter = new IntentFilter(Restaurant.ACTION_USB_PERMISSION);
                            mContext.registerReceiver(Restaurant.usbDriver.mUsbReceiver, filter);
                            Restaurant.usbDriver.usbReceiverIsRegistered = true;
                        }
                    }

                    @Override
                    public void onPermissionReject() {

                    }
                });
        if(!Restaurant.usbDriver.usbReceiverIsRegistered) {
            IntentFilter filter = new IntentFilter(Restaurant.ACTION_USB_PERMISSION);
            mContext.registerReceiver(Restaurant.usbDriver.mUsbReceiver, filter);
            Restaurant.usbDriver.usbReceiverIsRegistered = true;
        }

        UsbPrinters = Restaurant.usbDriver.getAllPrinters();
        return UsbPrinters;
    }

    public static void createShareComPrinter(final Context context, final ComPrinter printer) {
        SanyiScalaRequests.sharePrinterRequest(SanyiSDK.registerData.getPosName() + "打印机", NetworkUtil.getIPAddress(true), new SharePrinterRequest_.ISharePrintListener() {

            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSuccess(ShopPrinter result) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "打印机共享成功", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void shareComPrint(final Context context, final ComPrinter printer) {
        InputPasswordFragment.printServiceConnection = new SanyiPintServiceConnection(printer, new SanyiPintServiceConnection.PrinterServiceStartListener() {
            @Override
            public void onPrintServiceStartSuccess() {
                createShareComPrinter(context, printer);
                // initRemotePrinterPreference();
            }

            @Override
            public void onPrintServiceStartFailed() {
            }
        });
        Intent intent = new Intent(context, PrintService.class);
        context.bindService(intent, InputPasswordFragment.printServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void shartPrint(final Context context, final UsbPrinter printer) {
        if (printer == null) return;
        InputPasswordFragment.printServiceConnection = new SanyiPintServiceConnection(printer, new SanyiPintServiceConnection.PrinterServiceStartListener() {
            @Override
            public void onPrintServiceStartSuccess() {
//                Toast.makeText(context, "绑定打印服务成功", Toast.LENGTH_LONG).show();
                createSharedPriner(context, printer);
                // initRemotePrinterPreference();
            }

            @Override
            public void onPrintServiceStartFailed() {
//                Toast.makeText(context, "绑定打印服务失败", Toast.LENGTH_LONG).show();
            }
        });
        Intent intent = new Intent(context, PrintService.class);
        context.bindService(intent, InputPasswordFragment.printServiceConnection, Context.BIND_AUTO_CREATE);
//        Restaurant.usbDriver.usbReceiverIsRegistered = true;

        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, printer.getId());
        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER_NAME, printer.getDescription());
        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, ""); //clear COM printer
        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, ""); //clear COM printer

//        createSharedPriner(context, printer);
    }

    public static void createSharedPriner(final Context context, final UsbPrinter printer) {
        if (printer != null)
            SanyiScalaRequests.sharePrinterRequest(SanyiSDK.registerData.getPosName(), NetworkUtil.getIPAddress(true), new SharePrinterRequest_.ISharePrintListener() {

                @Override
                public void onFail(String error) {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "打印机共享失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(ShopPrinter result) {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "打印机共享成功", Toast.LENGTH_LONG).show();
                }
            });
    }
}
