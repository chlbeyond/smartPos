package android.cashdrawer;

public class CashDrawer {
    private static final String TAG = "CashDrawer";

    public static final int STATUS_OPENED = 0;

    public static final int STATUS_CLOSED = 1;

    public CashDrawer() throws SecurityException {
    }

    // Getters and setters
    public void openCashDrawer() {
        open();
    }

    public int getCashDrawerStatus() {
            return status();
//		i = status();
//		if(i == 0)
//			return "Opened";
//		else
//			return "Closed";
    }

    // JNI
    private native static void open();

    public native int status();

    static {
        try {
            System.loadLibrary("cash_drawer");
        }catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }
}
