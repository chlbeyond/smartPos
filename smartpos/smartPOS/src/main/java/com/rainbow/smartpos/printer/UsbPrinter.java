package com.rainbow.smartpos.printer;

import java.io.IOException;

import com.rainbow.smartpos.Restaurant;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

public class UsbPrinter extends LocalPrinter{

	private UsbDevice device;
	private int ep;
	private String description;
	
	private UsbEndpoint endPoint;
	private UsbDeviceConnection connection;
	private UsbInterface intf;

	public UsbPrinter(String description,UsbDevice device,int printEp){
		this.description = description;
		this.device = device;	
		this.ep = printEp;
	}

	public UsbDevice getDevice() {
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
//	
	public String getId(){
		if(device == null) return "";
		return device.getVendorId()+"|"+device.getProductId();
	}
	
	@Override
	public void print(byte[] content,int length) {
		if(connection==null){
			connection = Restaurant.usbDriver.openUsingDevice(device);
		}
		if(endPoint==null){
			intf = device.getInterface(0);
			connection.claimInterface(intf, true);
			endPoint = intf.getEndpoint(ep);
		}
		super.printByBuffer(content, length);
	}

	@Override
	protected void write(byte[] content,int length) throws IOException {
		if(connection!=null && endPoint!=null){
			connection.bulkTransfer(endPoint, content, length, 3000);
		}
	}

	@Override
	public void release() {
		if(connection!=null && intf!=null){
			connection.releaseInterface(intf);
		}
	}
}
