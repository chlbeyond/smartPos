package com.minipos.device;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

public class SerialPort {

    private static final String TAG = "SerialPort";

    /**
     * 校验方式<br>
     */
    public static final int  PARITY_NONE            =0;
    public static final int  PARITY_ODD             =1;
    public static final int  PARITY_EVEN            =2;
//	public static final int  PARITY_MARK            =3;
//	public static final int  PARITY_SPACE           =4;

    /**
     * 数据位
     */
    public static final int  DATABITS_5             =5;
    public static final int  DATABITS_6             =6;
    public static final int  DATABITS_7             =7;
    public static final int  DATABITS_8             =8;

    /**
     * 停止位
     */
    public static final int  STOPBITS_1             =1;
    public static final int  STOPBITS_2             =2;
    public static final int  STOPBITS_1_5           =3;

    /**
     * 流控制
     */
    public static final int  FLOWCONTROL_NONE       =0;
    public static final int  FLOWCONTROL_RTSCTS_IN  =1;
    public static final int  FLOWCONTROL_RTSCTS_OUT =2;
    public static final int  FLOWCONTROL_RTSCTS = FLOWCONTROL_RTSCTS_IN | FLOWCONTROL_RTSCTS_OUT;
    public static final int  FLOWCONTROL_XONXOFF_IN =4;
    public static final int  FLOWCONTROL_XONXOFF_OUT=8;
    public static final int  FLOWCONTROL_XONXOFF = FLOWCONTROL_XONXOFF_IN | FLOWCONTROL_XONXOFF_OUT;

	/*
	 * 控制信号
	 */
	/*
	public static final int SIGNAL_LE = 0x001;
	public static final int SIGNAL_DTR = 0x002;
	public static final int SIGNAL_RTS = 0x004;
	public static final int SIGNAL_ST = 0x008;
	public static final int SIGNAL_SR = 0x010;
	public static final int SIGNAL_CTS = 0x020;
	public static final int SIGNAL_CD = 0x040;
	public static final int SIGNAL_RI = 0x080;
	public static final int SIGNAL_DSR = 0x100;
	*/

    static {
        try {
            System.loadLibrary("SerialPortTest");
        }catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }

    private static native FileDescriptor createFileDescriptor(int fd);
    private static native void closeSerialPort(int fd);
    private static native int openSerialPort(String path, int baudrate,
                                             int databits, int parity, int stopbits, int flowcontrol);

    private int mFd;
    private final FileDescriptor mFileDescriptor;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;

    private final String mPort;

    private SerialPort(String port, int fd) {
        mPort = port;
        mFd = fd;
        mFileDescriptor = createFileDescriptor(fd);
    }

    public static SerialPort open(String port, int baudrate) throws IOException {
        return open(port, baudrate, 8,
                SerialPort.PARITY_NONE,
                SerialPort.STOPBITS_1,
                SerialPort.FLOWCONTROL_NONE);
    }

    public boolean isClosed() {
        return mFd < 0;
    }

    public static SerialPort open(String port, int baudrate,
                                  int parity, int databits, int stopbits, int flowcontorl) throws IOException {

        Log.d(TAG, "open : " + port + ", " + baudrate);

        File f = new File(port);
        if(!f.exists())
            throw new FileNotFoundException(f.getAbsolutePath());

        if(!f.canWrite() || !f.canRead())
            changePermission(f);

        int fd = openSerialPort(port, baudrate, databits,
                parity,
                stopbits,
                flowcontorl);

        if(fd < 0)
            throw new IOException("failed to set serial port params");

        return new SerialPort(port, fd);
    }

    private static void changePermission(File f) {
        try {
            String[] cmd = {
                    "/system/xbin/su", "-c", "busybox", "chmod", "777", f.getAbsolutePath()
            };
            final Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream() throws IOException {
        if(mInputStream == null) {
            synchronized(this) {
                if(mInputStream == null) {
                    mInputStream = new FileInputStream(mFileDescriptor);
                }
            }
        }
        return mInputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        if(mOutputStream == null) {
            synchronized(this) {
                if(mOutputStream == null)
                    mOutputStream = new FileOutputStream(mFileDescriptor);
            }
        }
        return mOutputStream;
    }

    public void close() {
        synchronized(this) {
            if(mOutputStream != null) {
                try {
                    mOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mOutputStream = null;
            }
            if(mInputStream != null) {
                try {
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mInputStream = null;
            }
            if(mFd >= 0) {
                closeSerialPort(mFd);
                mFd = -1;
            }
        }
    }

    public static String[] listDevices() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while(itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while(itdev.hasNext()) {
                    String device = itdev.next().getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    public static class Driver {
        public Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }
        private String mDriverName;
        private String mDeviceRoot;
        Vector<File> mDevices = null;
        public Vector<File> getDevices() {
            if (mDevices == null) {
                mDevices = new Vector<File>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                int i;
                for (i=0; i<files.length; i++) {
                    if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
                        //Log.d(TAG, "Found new device: " + files[i]);
                        mDevices.add(files[i]);
                    }
                }
            }
            return mDevices;
        }
        public String getName() {
            return mDriverName;
        }
    }

    private static Vector<Driver> getDrivers() throws IOException {
        Vector<Driver> drivers = new Vector<Driver>();
        LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
        String l;
        while((l = r.readLine()) != null) {
            // Issue 3:
            // Since driver name may contain spaces, we do not extract driver name with split()
            String drivername = l.substring(0, 0x15).trim();
            String[] w = l.split(" +");
            if ((w.length >= 5) && (w[w.length-1].equals("serial"))) {
                //Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length-4]);
                drivers.add(new Driver(drivername, w[w.length-4]));
            }
        }
        r.close();
        return drivers;
    }


}
