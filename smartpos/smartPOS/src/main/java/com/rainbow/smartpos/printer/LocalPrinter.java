package com.rainbow.smartpos.printer;

import android.util.Log;

import java.io.IOException;

public abstract class LocalPrinter {
	
	protected static int PRINT_BUFFERSIZE = 4096;

	protected byte[] print_buffer = new byte[PRINT_BUFFERSIZE];

	public abstract void print(byte[] content , int length);
	
	protected abstract void write(byte[] content,int length)throws IOException;
	
	protected void printByBuffer(byte[] content , int length){
		try {
			if (length >= 0) {
				int offset = 0, printSize;
				while (offset < length) {
					if (length - offset >= PRINT_BUFFERSIZE) {
						printSize = PRINT_BUFFERSIZE;
					} else {
						printSize = length - offset;
					}
					System.arraycopy(content, offset, print_buffer, 0, printSize);
					write(print_buffer,printSize);
					offset += PRINT_BUFFERSIZE;
					Log.d("sanyipos", "print "+printSize+" byte to printer");
				}
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract void release();
}
