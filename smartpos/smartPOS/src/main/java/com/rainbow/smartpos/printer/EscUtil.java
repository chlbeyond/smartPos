package com.rainbow.smartpos.printer;

import android.graphics.Bitmap;

public class EscUtil {
	/**
	 * 
	 * reusable init esc code
	 * 
	 */
	public void escInit(EscPrinter printer) {
		printer.write(0x1B);
		printer.write("@");
	}

	/**
	 * resets all printer settings to default
	 * 
	 */
	public void resetToDefault(EscPrinter printer) {
		setInverse(false, printer);
		setBold(false, printer);
		setUnderline(0, printer);
		setJustification(0, printer);
	}
	/**
	 * 
	 * @param txt
	 *            String to print
	 */
	public void printString(String str, EscPrinter printer) {
		// escInit();
		printer.write(str);
		printer.write(0xA);
	}

	public void storeString(String str, EscPrinter printer) {
		printer.write(str);
	}

	public void storeChar(int hex, EscPrinter printer) {
		printer.write(hex);
	}

	public void printStorage(EscPrinter printer) {
		printer.write(0xA);
	}
	/**
	 * Prints n lines of blank paper.
	 * */
	public void feed(int feed, EscPrinter printer) {
		// escInit();
		printer.write(0x1B);
		printer.write("d");
		printer.write(feed);
	}
	public void fontsize(int size, EscPrinter printer) {
		// escInit();
		printer.write(0x1D);
		printer.write(0x21);
		switch (size) {
			case 1 :
				printer.write(1);
				break;
			case 2 :
				printer.write(17);
				break;
			case 3 :
				printer.write(35);
				break;
			case 4 :
				printer.write(52);
				break;
			case 6 :
				printer.write(70);
				break;
			case 7 :
				printer.write(85);
				break;
			default :
				break;
		}

	}
	public void fontsizeB(int size, EscPrinter printer) {
		// escInit();
		printer.write(0x1B);
		printer.write(0x21);
		switch (size) {
			case 1 :
				printer.write(1);
				break;
			case 2 :
				printer.write(16);
				break;
			case 3 :
				printer.write(35);
				break;
			case 4 :
				printer.write(52);
				break;
			case 6 :
				printer.write(70);
				break;
			case 7 :
				printer.write(85);
				break;
			default :
				break;
		}

	}

	/**
	 * Prints a string and outputs n lines of blank paper.
	 * */

	public void printAndFeed(String str, int feed, EscPrinter printer) {
		// escInit();
		printer.write(str);
		// output extra paper
		printer.write(0x1B);
		printer.write("d");
		printer.write(feed);
	}

	/**
	 * Sets bold
	 * */
	public void setBold(Boolean bool, EscPrinter printer) {
		printer.write(0x1B);
		printer.write("E");
		printer.write((int) (bool ? 1 : 0));
	}

	/**
	 * Sets white on black printing
	 * */
	public void setInverse(Boolean bool, EscPrinter printer) {
		printer.write(0x1D);
		printer.write("B");
		printer.write((int) (bool ? 1 : 0));
	}

	/**
	 * Sets underline and weight
	 * 
	 * @param val
	 *            0 = no underline. 1 = single weight underline. 2 = double
	 *            weight underline.
	 * */

	public void setUnderline(int val, EscPrinter printer) {
		printer.write(0x1B);
		printer.write("-");
		printer.write(val);
	}

	/**
	 * Sets left, center, right justification
	 * 
	 * @param val
	 *            0 = left justify. 1 = center justify. 2 = right justify.
	 * */

	public void setJustification(int val, EscPrinter printer) {
		printer.write(0x1B);
		printer.write("a");
		printer.write(val);
	}

	/**
	 * Encode and print QR code
	 * 
	 * @param str
	 *            String to be encoded in QR.
	 * @param errCorrection
	 *            The degree of error correction. (48 <= n <= 51) 48 = level L /
	 *            7% recovery capacity. 49 = level M / 15% recovery capacity. 50
	 *            = level Q / 25% recovery capacity. 51 = level H / 30% recovery
	 *            capacity.
	 * 
	 * @param moduleSize
	 *            The size of the QR module (pixel) in dots. The QR code will
	 *            not print if it is too big. Try setting this low and
	 *            experiment in making it larger.
	 */
	public void printQR(String str, int errCorrect, int moduleSize, EscPrinter printer) {
		// save data function 80
		printer.write(0x1D);// init
		printer.write("(k");// adjust height of barcode
		printer.write(str.length() + 3); // pl
		printer.write(0); // ph
		printer.write(49); // cn
		printer.write(80); // fn
		printer.write(48); //
		printer.write(str);

		// error correction function 69
		printer.write(0x1D);
		printer.write("(k");
		printer.write(3); // pl
		printer.write(0); // ph
		printer.write(49); // cn
		printer.write(69); // fn
		printer.write(errCorrect); // 48<= n <= 51

		// size function 67
		printer.write(0x1D);
		printer.write("(k");
		printer.write(3);
		printer.write(0);
		printer.write(49);
		printer.write(67);
		printer.write(moduleSize);// 1<= n <= 16

		// print function 81
		printer.write(0x1D);
		printer.write("(k");
		printer.write(3); // pl
		printer.write(0); // ph
		printer.write(49); // cn
		printer.write(81); // fn
		printer.write(48); // m
	}

	/**
	 * Encode and print barcode
	 * 
	 * @param code
	 *            String to be encoded in the barcode. Different barcodes have
	 *            different requirements on the length of data that can be
	 *            encoded.
	 * @param type
	 *            Specify the type of barcode 65 = UPC-A. 66 = UPC-E. 67 =
	 *            JAN13(EAN). 68 = JAN8(EAN). 69 = CODE39. 70 = ITF. 71 =
	 *            CODABAR. 72 = CODE93. 73 = CODE128.
	 * 
	 * @param h
	 *            height of the barcode in points (1 <= n <= 255)
	 * @param w
	 *            width of module (2 <= n <=6). Barcode will not print if this
	 *            value is too large.
	 * @param font
	 *            Set font of HRI characters 0 = font A 1 = font B
	 * @param pos
	 *            set position of HRI characters 0 = not printed. 1 = Above
	 *            barcode. 2 = Below barcode. 3 = Both above and below barcode.
	 */
	public void printBarcode(String code, int type, int h, int w, int font, int pos, EscPrinter printer) {

		// need to test for errors in length of code
		// also control for input type=0-6

		// GS H = HRI position
		printer.write(0x1D);
		printer.write("H");
		printer.write(pos); // 0=no print, 1=above, 2=below, 3=above & below

		// GS f = set barcode characters
		printer.write(0x1D);
		printer.write("f");
		printer.write(font);

		// GS h = sets barcode height
		printer.write(0x1D);
		printer.write("h");
		printer.write(h);

		// GS w = sets barcode width
		printer.write(0x1D);
		printer.write("w");
		printer.write(w);// module = 1-6

		// GS k
		printer.write(0x1D); // GS
		printer.write("k"); // k
		printer.write(type);// m = barcode type 0-6
		printer.write(code.length()); // length of encoded string
		printer.write(code);// d1-dk
		printer.write(0);// print barcode
	}

	/**
	 * Encode and print PDF 417 barcode
	 * 
	 * @param code
	 *            String to be encoded in the barcode. Different barcodes have
	 *            different requirements on the length of data that can be
	 *            encoded.
	 * @param type
	 *            Specify the type of barcode 0 - Standard PDF417 1 - Standard
	 *            PDF417
	 * 
	 * @param h
	 *            Height of the vertical module in dots 2 <= n <= 8.
	 * @param w
	 *            Height of the horizontal module in dots 1 <= n <= 4.
	 * @param cols
	 *            Number of columns 0 <= n <= 30.
	 * @param rows
	 *            Number of rows 0 (automatic), 3 <= n <= 90.
	 * @param error
	 *            set error correction level 48 <= n <= 56 (0 - 8).
	 * 
	 */
	public void printPSDCode(String code, int type, int h, int w, int cols, int rows, int error, EscPrinter printer) {

		// print function 82
		printer.write(0x1D);
		printer.write("(k");
		printer.write(code.length()); // pl Code length
		printer.write(0); // ph
		printer.write(48); // cn
		printer.write(80); // fn
		printer.write(48); // m
		printer.write(code); // data to be encoded

		// function 65 specifies the number of columns
		printer.write(0x1D);// init
		printer.write("(k");// adjust height of barcode
		printer.write(3); // pl
		printer.write(0); // pH
		printer.write(48); // cn
		printer.write(65); // fn
		printer.write(cols);

		// function 66 number of rows
		printer.write(0x1D);// init
		printer.write("(k");// adjust height of barcode
		printer.write(3); // pl
		printer.write(0); // pH
		printer.write(48); // cn
		printer.write(66); // fn
		printer.write(rows); // num rows

		// module width function 67
		printer.write(0x1D);
		printer.write("(k");
		printer.write(3);// pL
		printer.write(0);// pH
		printer.write(48);// cn
		printer.write(67);// fn
		printer.write(w);// size of module 1<= n <= 4

		// module height fx 68
		printer.write(0x1D);
		printer.write("(k");
		printer.write(3);// pL
		printer.write(0);// pH
		printer.write(48);// cn
		printer.write(68);// fn
		printer.write(h);// size of module 2 <= n <= 8

		// error correction function 69
		printer.write(0x1D);
		printer.write("(k");
		printer.write(4);// pL
		printer.write(0);// pH
		printer.write(48);// cn
		printer.write(69);// fn
		printer.write(48);// m
		printer.write(error);// error correction

		// choose pdf417 type function 70
		printer.write(0x1D);
		printer.write("(k");
		printer.write(3);// pL
		printer.write(0);// pH
		printer.write(48);// cn
		printer.write(70);// fn
		printer.write(type);// set mode of pdf 0 or 1

		// print function 81
		printer.write(0x1D);
		printer.write("(k");
		printer.write(3); // pl
		printer.write(0); // ph
		printer.write(48); // cn
		printer.write(81); // fn
		printer.write(48); // m

	}

	/**
	 * Store custom character input array of column bytes
	 * 
	 * @param columnArray
	 *            Array of bytes (0-255). Ideally not longer than 24 bytes.
	 * 
	 * @param mode
	 *            0 - 8-dot single-density. 1 - 8-dot double-density. 32 -
	 *            24-dot single density. 33 - 24-dot double density.
	 */
	public void storeCustomChar(int[] columnArray, int mode, EscPrinter printer) {

		// function GS*
		printer.write(0x1B);
		printer.write("*");
		printer.write(mode);
		printer.write((mode == 0 || mode == 1) ? columnArray.length : columnArray.length / 3);// number
																								// of
																								// cols
		printer.write(0);
		for (int i = 0; i < columnArray.length; i++) {
			printer.write(columnArray[i]);
		}

	}

	/**
	 * Store custom character input array of column bytes. NOT WORKING
	 * 
	 * @param spacing
	 *            Integer representing Vertical motion of unit in inches. 0-255
	 * 
	 */
	public void setLineSpacing(int spacing, EscPrinter printer) {

		// function ESC 3
		printer.write(0x1B);
		printer.write("3");
		printer.write(spacing);

	}

	public void cut(EscPrinter printer) {
		printer.write(0x1D);
		printer.write("V");
		printer.write(48);
		printer.write(0);
	}

	public void feedAndCut(int feed, EscPrinter printer) {

		feed(feed, printer);
		cut(printer);
	}

	public void beep(EscPrinter printer) {
		printer.write(0x1B);
		printer.write("(A");
		printer.write(4);
		printer.write(0);
		printer.write(48);
		printer.write(55);
		printer.write(3);
		printer.write(15);
	}

	/**
	 * 
	 * Print a sample sheet
	 * 
	 */
	public void printSampler(EscPrinter printer) {
		// print samples of all functions here
		resetToDefault(printer);
		escInit(printer);
		// storeChar(178, printer);
		// storeChar(177, printer);
		// storeChar(176, printer);
		storeString("Hello World", printer);
		printStorage(printer);
		printString("printString();", printer);
		setBold(true, printer);
		printString("setBold(true)", printer);
		setBold(false, printer);
		setUnderline(1, printer);
		printString("setUnderline(1)", printer);
		setUnderline(2, printer);
		printString("setUnderline(2)", printer);
		setUnderline(0, printer);
		setInverse(true, printer);
		printString("setInverse(true)", printer);
		setInverse(false, printer);
		setJustification(0, printer);
		printString("setJustification(0)\n//left - default", printer);
		setJustification(1, printer);
		printString("setJustification(1)\n//center", printer);
		setJustification(2, printer);
		printString("setJustification(2)\n//right", printer);
		setJustification(1, printer);
		printQR("http://www.josephbergen.com", 51, 8, printer);
		printAndFeed("\n##name## ##version##\nby Joseph Bergen\nwww.josephbergen.com", 4, printer);
		resetToDefault(printer);
	}

//	public void printImage(String path, EscPrinter printer, int widthRange) {
//		try {
//			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(path));
//			int width = bitmap.getWidth();
//			int height = bitmap.getHeight();
//			int nl = width % 256;
//			int nh = width / 256;
//			printer.write(0X1B);
//			printer.write('3');
//			printer.write(24);
//			int offset = 0;
//			if (width > widthRange) {
//				try {
//					throw new Exception("==图像宽度超打印范围==");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			while (offset < height) {
//				// $this -> Append(kbyte(27) . "*" . kbyte(33)); # 24 dot double
//				// density
//				// $this -> Append(kbyte($nlow) . kbyte($nhigh));
//				printer.write(0X1B);
//				printer.write('*');
//				printer.write(33);
//				printer.write(nl);
//				printer.write(nh);
//
//				// for($x = 0; $x < $bit_array -> width; ++$x){
//				for (int x = 0; x < width; ++x) {
//					// for($k = 0; $k < 3; ++$k){
//					for (int k = 0; k < 3; ++k) {
//						// $byte = 0;
//						byte byt = 0;
//						// for($b = 0; $b < 8; ++$b){
//						for (int b = 0; b < 8; ++b) {
//							// $y = ((($offset / 8) + $k) * 8) + $b;
//							int y = (((offset / 8) + k) * 8) + b;
//							// int y = (((((offset / 8) + k) * 8) + b) < height)
//							// ? ((((offset / 8) + k) * 8) + b) : 0;
//							// $i = ($y * $bit_array -> width) + $x;
//
//							int p = bitmap.getPixel(x, y);
//							if (p > 0)
//								p = 1;
//
//							byt = (byte) (byt | p << (7 - b));
//
//						}
//						printer.write(byt);
//					}
//				}
//				// $offset += 24;
//				offset += 24;
//				printer.write(10);
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	// The performance of this method
	// is rather poor, place for improvement
	
	
	public int[][] getPixelsSlow(Bitmap image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] result = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				result[row][col] = image.getPixel(col, row);
			}
		}

		return result;
	}
	private final static char ESC_CHAR = 0x1B;
	private final static char GS = 0x1D;
	private final static byte[] LINE_FEED = new byte[]{0x0A};
	private final static byte[] CUT_PAPER = new byte[]{GS, 0x56, 0x00};
	private final static byte[] INIT_PRINTER = new byte[]{ESC_CHAR, 0x40};
	private static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33};
	private final static byte[] SET_LINE_SPACE_24 = new byte[]{ESC_CHAR, 0x33, 24};
	private final static byte[] SET_LINE_SPACE_30 = new byte[]{ESC_CHAR, 0x33, 30};

	
	
	
	public void imagePrint(int[][] pixels, EscPrinter printer) {
		// Set the line spacing at 24 (we'll print 24 dots high)
		printer.write(SET_LINE_SPACE_24);
		for (int y = 0; y < pixels.length; y += 24) {
			// Like I said before, when done sending data,
			// the printer will resume to normal text printing
			printer.write(SELECT_BIT_IMAGE_MODE);
			// Set nL and nH based on the width of the image
			printer.write(new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});
			for (int x = 0; x < pixels[y].length; x++) {
				// for each stripe, recollect 3 bytes (3 bytes = 24 bits)
				printer.write(recollectSlice(y, x, pixels));
			}

			// Do a line feed, if not the printing will resume on the same line
			printer.write(5);
		}
		printer.write(SET_LINE_SPACE_30);
	}

	public byte[] recollectSlice(int y, int x, int[][] img) {
		byte[] slices = new byte[]{0, 0, 0};
		for (int yy = y, i = 0; yy < y + 24 && i < 3; yy += 8, i++) {
			byte slice = 0;
			for (int b = 0; b < 8; b++) {
				int yyy = yy + b;
				if (yyy >= img.length) {
					continue;
				}
				int col = img[yyy][x];
				boolean v = shouldPrintColor(col);
				slice |= (byte) ((v ? 1 : 0) << (7 - b));
			}
			slices[i] = slice;
		}

		return slices;
	}

	public boolean shouldPrintColor(int col) {
		final int threshold = 127;
		int a, r, g, b, luminance;
		a = (col >> 24) & 0xff;
		if (a != 0xff) {// Ignore transparencies
			return false;
		}
		r = (col >> 16) & 0xff;
		g = (col >> 8) & 0xff;
		b = col & 0xff;

		luminance = (int) (0.299 * r + 0.587 * g + 0.114 * b);

		return luminance < threshold;
	}

	public void printPaiduitTicket() {

	}

}
