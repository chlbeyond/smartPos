package com.rainbow.smartpos.printer;

import android.bluetooth.BluetoothDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import java.io.IOException;
import java.util.List;

/**
 * Created by administrator on 2017/11/7.
 */

public class BlueToothPrinter extends LocalPrinter{


    private BluetoothDevice device;
    private int ep;
    private String description;

    private UsbEndpoint endPoint;
    private UsbDeviceConnection connection;
    private UsbInterface intf;

    private List<String> printInfo;
    public BlueToothPrinter(String description, BluetoothDevice device, int printEp){
        this.description = description;
        this.device = device;
        this.ep = printEp;


         printInfo = AidlUtil.getInstance().getPrinterInfo();
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getDescription() {
        return description;
    }
    //
//	public int getEp() {
//		return ep;
//	}
//	public String getName(){
//		return device.getDeviceName();
//	}

    public String getId(){
        if(device == null) return "";
        return printInfo.get(0);
    }

    @Override
    public void print(byte[] content, int length) {
//        if(connection==null){
//            connection = com.sanyi.order.Restaurant.usbDriver.openUsingDevice(device);
//        }
//        if(endPoint==null){
//            intf = device.getInterface(0);
//            connection.claimInterface(intf, true);
//            endPoint = intf.getEndpoint(ep);
//        }
        BluetoothUtil.sendData(content, length);
//        super.printByBuffer(content, length);
    }

    @Override
    protected void write(byte[] content,int length) throws IOException {
        if(connection!=null && endPoint!=null){
            connection.bulkTransfer(endPoint, content, length, 100);
        }
    }

    @Override
    public void release() {
        if(connection!=null && intf!=null){
            connection.releaseInterface(intf);
        }
    }
}
