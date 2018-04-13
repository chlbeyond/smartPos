package com.rainbow.smartpos.printer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class BufferedEscPrinter implements EscPrinter {

	ByteArrayOutputStream baos;
	
	Charset charset ;
	
	public BufferedEscPrinter(){
		this(Charset.forName("UTF-8"));
	}
	
	public BufferedEscPrinter(Charset charset) {
		this.charset = charset;
		baos = new ByteArrayOutputStream();
	}

	@Override
	public void write(byte b) {
		baos.write(b);
	}

	@Override
	public void write(int b) {
		baos.write(b);
	}

	@Override
	public void write(String s) {
		write(s.getBytes(charset));
	}

	@Override
	public void write(byte[] ba) {
		try {
			baos.write(ba);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] getBytes() {
		return baos.toByteArray();
	}

}
