package com.rainbow.smartpos;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.rainbow.smartpos.printer.SanyiUSBDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;

public class Restaurant {

    public static String operationLogTxtName;
    public static String operationLogTxtPath = "/sdcard/smartpos/logs/operation/";
    public static String CRASHLOGPATH = "/sdcard/smartpos/logs/crashs/";
    public static String clientLogTxtPath = "/mnt/sdcard/op/";
    public static final String UNLOCK_TABLE_ACTION = "android.intent.action.NEW_UNLOCK_CastReceiver";
    public static boolean orderIsRefresh = false;
    public static boolean isAutoGotoCooking = true;
    public static boolean isInputSoftMode = true;
    public static boolean isSpellOrder = false;
    public static boolean isTakeoutVoiceOff = false;
    public static final String uploadLogUrl = "upload/pointperf";
    public static boolean isFastMode = false;
    public static boolean fastModeInputNumber = true;
    public static String uuid;

    public static final String ACTION_USB_PERMISSION = "com.go2smartphone.smartpos.USB_PERMISSION";

    public final static String REQUEST_TIME_OUT = "请求超时，请重试";
    public final static String REQUEST_FAILED = "请求失败，请重试";
    public static SanyiUSBDriver usbDriver;

    public static boolean isPrintServiceRunning = false;

    public static long currentCloudConnectStatus = 0;
    public static long CURRENT_SHOP_STATE;


    public static boolean isPublicDevice = false;
    public static boolean preIsExit = true;


    public static final int SCAN_CODE_CHECK_MEMBER = 101;
    public static final int SCAN_CODE_CHECK_WECHAT = 102;
    public static final int SCAN_CODE_CHECK_VOUCHER = 103;
    public static final int SCAN_CODE_CHECK_PREORDER = 104;


    public static final int EVENT_SOLD = 11;
    public static final int EVENT_LOGIN = 12;
    public static final int EVENT_REQUEST = 13;
    public static final int EVENT_REQUEST_COMPLETE = 14;

    public static long selectedSloidCategory = -2;
    public static long selectedSloidSubcategory = -2;

    public static long shopId = -1;
    public static NumberFormat currentcyFormatter = NumberFormat.getCurrencyInstance();


    public static enum ScreenScale {
        S4B3, S16B9, S21B9
    }

    public static String getVersionName(Context context) {
        String versionName = "1.0";
        try {
            versionName = context.getPackageManager().getPackageInfo("com.rainbow.smartpos", 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;

    }

    public static int getVersionCode(Context context) {
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager().getPackageInfo("com.rainbow.smartpos", 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    public static void writeOperationLogText(String log) {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(Restaurant.operationLogTxtPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (!(new File(operationLogTxtName).exists())) {
                    operationLogTxtName = Restaurant.operationLogTxtPath + System.currentTimeMillis() + ".log";
                }
                FileOutputStream fos = new FileOutputStream(operationLogTxtName, true);
                fos.write(log.getBytes());
                fos.write("\n".getBytes());
                fos.close();
            }
        } catch (Exception e) {
        }
    }


}
