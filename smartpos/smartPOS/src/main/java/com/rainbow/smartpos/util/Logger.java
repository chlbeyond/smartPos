package com.rainbow.smartpos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rainbow.smartpos.SmartPosApplication;
import com.sanyipos.sdk.utils.DateHelper;
import com.rainbow.smartpos.Restaurant;

import android.os.Environment;
import android.util.Log;

/**
 * A custom Android log class
 * 
 * @author syxc
 */
public final class Logger {

	private static final String TAG = "Logger";

	public static boolean debug = true; // Log switch open, development,
										// released when closed(LogCat)
	public static int level = Log.ERROR; // Write file level

	public static void v(String tag, String msg) {
		trace(Log.VERBOSE, tag, msg);
	}

	public static void v(String tag, String msg, Throwable tr) {
		trace(Log.VERBOSE, tag, msg, tr);
	}

	public static void d(String tag, String msg) {
		trace(Log.DEBUG, tag, msg);
	}

	public static void d(String tag, String msg, Throwable tr) {
		trace(Log.DEBUG, tag, msg, tr);
	}

	public static void i(String tag, String msg) {
		trace(Log.INFO, tag, msg);
	}

	public static void i(String tag, String msg, Throwable tr) {
		trace(Log.INFO, tag, msg, tr);
	}

	public static void w(String tag, String msg) {
		trace(Log.WARN, tag, msg);
	}

	public static void w(String tag, String msg, Throwable tr) {
		trace(Log.WARN, tag, msg, tr);
	}

	public static void e(String tag, String msg) {
		trace(Log.ERROR, tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		trace(Log.ERROR, tag, msg, tr);
	}

	/**
	 * Custom Log output style
	 * 
	 * @param type
	 *            Log type
	 * @param tag
	 *            TAG
	 * @param msg
	 *            Log message
	 */
	private static void trace(final int type, String tag, final String msg) {
		// LogCat
		if (debug) {
			switch (type) {
			case Log.VERBOSE:
				Log.v(tag, msg);
				break;
			case Log.DEBUG:
				Log.d(tag, msg);
				break;
			case Log.INFO:
				Log.i(tag, msg);
				break;
			case Log.WARN:
				Log.w(tag, msg);
				break;
			case Log.ERROR:
				Log.e(tag, msg);
				break;
			}
			writeLog(type, msg);
		}
	}

	/**
	 * Custom Log output style
	 * 
	 * @param type
	 * @param tag
	 * @param msg
	 * @param tr
	 */
	private static void trace(final int type, final String tag, final String msg, final Throwable tr) {
		// LogCat
		if (debug) {
			switch (type) {
			case Log.VERBOSE:
				Log.v(tag, msg);
				break;
			case Log.DEBUG:
				Log.d(tag, msg);
				break;
			case Log.INFO:
				Log.i(tag, msg);
				break;
			case Log.WARN:
				Log.w(tag, msg);
				break;
			case Log.ERROR:
				Log.e(tag, msg);
				break;
			}
		}
		// Write to file
		if (type >= level) {
			writeLog(type, msg + '\n' + Log.getStackTraceString(tr));
		}
	}

	/**
	 * Write log file to the SDCard
	 * 
	 * @param type
	 * @param msg
	 */
	private static void writeLog(int type, String msg) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}

		try {
			final StackTraceElement[] tags = new Throwable().fillInStackTrace().getStackTrace(); // TODO:
			final StackTraceElement tag = new Throwable().fillInStackTrace().getStackTrace()[3]; // TODO:

			msg = new StringBuilder().append("\r\n").append(getDateFormat(SmartPosApplication.defaultFormatter.format(new Date())))

			.append(getLogType(type)).append(" - ").append(tag.getMethodName()).append("(): ").append(msg).toString();

			//final String fileName = new StringBuffer().append(Restaurant.operationLogTxtPath).append(DateHelper.dateFormater.format(System.currentTimeMillis())).append("_log.txt").toString();

			recordLog(msg, true);
		} catch (Exception e) {
			Logger.e("Logger: ", e.getMessage());
		}
	}

	private static String getLogType(int type) {
		switch (type) {
		case Log.VERBOSE:
			return " VERBOSE ";
		case Log.DEBUG:
			return " DEBUG ";
		case Log.INFO:
			return " INFO ";
		case Log.WARN:
			return " WARN ";
		case Log.ERROR:
			return " ERROR ";
		default:
			return " NULL ";
		}
	}

	/**
	 * Write log
	 * 
	 * @param logDir
	 *            Log path to save
	 * @param fileName
	 * @param msg
	 *            Log content
	 * @param append
	 *            Save as type, false override save, true before file add save
	 */
	private static void recordLog(String msg, boolean append) {
		try {

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = new File(Restaurant.operationLogTxtPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				if (!(new File(Restaurant.operationLogTxtName).exists())) {
					Restaurant.operationLogTxtName = Restaurant.operationLogTxtPath + "sdk" + DateHelper.hDateFormatter.format(System.currentTimeMillis()) + ".txt";
				}
				FileOutputStream fos = new FileOutputStream(Restaurant.operationLogTxtName, append);
				fos.write(msg.getBytes());
				fos.write("\n".getBytes());
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			//recordLog(e.getMessage(), append);
		}
	}

	/**
	 * Write msg to file
	 * 
	 * @param file
	 * @param msg
	 * @param append
	 */
	private static void write(final File file, final String msg, final boolean append) {

		final FileOutputStream fos;
		try {
			fos = new FileOutputStream(file, append);
			try {
				fos.write(msg.getBytes());
			} catch (IOException e) {
				Logger.e(TAG, "write fail!!!", e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						Logger.d(TAG, "Exception closing stream: ", e);
					}
				}
			}
		} catch (FileNotFoundException e) {
			Logger.e(TAG, "write fail!!!", e);
		}

	}

	private static String getDateFormat(String pattern) {
		final DateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());
	}

	private static File createDir(String dir) {
		final File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

}
