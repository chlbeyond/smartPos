package com.rainbow.smartpos.printer;

import com.cc.serialport.SerialPort;

import java.io.IOException;
import java.io.OutputStream;


public class ComPrinter extends LocalPrinter{

	private SerialPort port;
	private OutputStream outputStream;
	
	public ComPrinter (SerialPort port){
		this.port = port;
		outputStream = port.getOutputStream();
	}

	@Override
	public void print(byte[] content, int length) {
		super.printByBuffer(content, length);		
	}

	@Override
	protected void write(byte[] content,int length) throws IOException {
		outputStream.write(content,0,length);
	}

	@Override
	public void release() {
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		port.close();		
	}
}
