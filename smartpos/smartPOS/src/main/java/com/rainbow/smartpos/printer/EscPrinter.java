package com.rainbow.smartpos.printer;

public interface EscPrinter {

	void write(byte b);
	
	void write(int b);
	
	void write(String s);
	
	void write(byte[] ba);
}
