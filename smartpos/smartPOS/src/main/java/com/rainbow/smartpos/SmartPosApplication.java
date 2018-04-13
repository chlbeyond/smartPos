package com.rainbow.smartpos;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings.Secure;
import android.support.multidex.MultiDex;

import com.rainbow.smartpos.printer.AidlUtil;
import com.sanyipos.android.sdk.androidUtil.DecodeBaseUtil;
import com.sanyipos.android.sdk.androidUtil.RegisteDataUtils;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.ClientConfig;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.utils.DateHelper;
import com.sanyipos.sdk.utils.LoggerWrapper;
import com.sanyipos.sdk.utils.SendLogUtil;
import com.socks.library.KLog;
import com.zhy.autolayout.config.AutoLayoutConifg;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SmartPosApplication extends Application {

    public static SanyiSDK sanyiSDK;
    public final static SimpleDateFormat defaultFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US);
    boolean flag = true;


//    private Handler handler = new Handler(this.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            PosAgentRequest request = ((PosAgentRequest) msg.obj);
//            if (request != null) {
//                if (request.getResponseCode() == ConstantsUtil.RESPONSE_SUCCESS) {
//                    request.onRequestSuccess();
//                }else {
//                    request.onRequestException();
//                }
//            }
////            if (progressHUD != null)
////                progressHUD.dismiss();
//        }
//    };

    @Override
    public void onCreate() {
        super.onCreate();
        Restaurant.uuid = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        Restaurant.operationLogTxtName = Restaurant.operationLogTxtPath + DateHelper.hDateFormatter.format(System.currentTimeMillis()) + ".txt";
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        initHttpClient();
        initLocalConfig();
        AutoLayoutConifg.getInstance().useDeviceSize();
        AidlUtil.getInstance().connectPrinterService(this);
        // Intent intent = new Intent(this, SendLogService.class);
        // bindService(intent, new ServiceConnection() {
        // @Override
        // public void onServiceConnected(ComponentName name, IBinder service) {
        // SendLogServiceBinder binder = (SendLogServiceBinder) service;
        // binder.getService().startSend();
        // }
        //
        // @Override
        // public void onServiceDisconnected(ComponentName name) {
        // // TODO Auto-generated method stub
        //
        // }
        // }, Context.BIND_AUTO_CREATE);
        // Restaurant.initOperationLogText();
        clearWebViewCache();
//        initOperationLogFile();
        inspectLogFileSize();
        KLog.init(true);
        sendLogTiming();
//        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        MobclickAgent.setDebugMode( true );
//        MobclickAgent.setCatchUncaughtExceptions(true);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 定时发送log文件到服务器
     */
    private void sendLogTiming() {
        SendLogUtil.sendFileToAgent(Restaurant.operationLogTxtPath);

    }

    /**
     * 检查log文件大小,大于50M则删除
     */
    private void inspectLogFileSize() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (flag) {
                    double sdkLogSize = getDirSize(new File(Restaurant.operationLogTxtPath));
                    if (sdkLogSize >= 1) { // 单位为'M'
                        flag = false;
                        delFolder(Restaurant.operationLogTxtPath);
                    }
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    /**
     * 获取文件夹大小,以“兆”为单位
     *
     * @param file
     * @return
     */
    public static double getDirSize(File file) {
        // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                if(children != null && children.length > 0)
                    for (File f : children)
                        size += getDirSize(f);
                return size;
            } else {// 如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
//            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

    /**
     * 删除目录下所有文件
     *
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除指定文件夹下所有文件
    // param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    private void initOperationLogFile() {
        // TODO Auto-generated method stub
        File file = new File(Restaurant.operationLogTxtPath);
        if (file.exists()) {
            for (File files : file.listFiles())
                if (isDelete(files)) {
                    files.delete();
                }
        }
    }

    public static void writeLogs(String tag) {
//		String content = tag + defaultFormatter.format(System.currentTimeMillis());
//		String fileName = "client"+DateHelper.hDateFormatter.format(System.currentTimeMillis()) + ".txt";
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			String path = Restaurant.clientLogTxtPath;
//			File dir = new File(path);
//			if (!dir.exists()) {
//				dir.mkdirs();
//			}
//			try {
//				// 创建一个FileWriter对象，其中boolean型参数则表示是否以追加形式写文件
//				FileWriter fw = new FileWriter(path + fileName, true);
//				// 追加内容
//				fw.write(content + "\r\n");
//
//				// 关闭文件输出流
//				fw.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			// 发送给开发人员
//			//sendCrashLog2PM(path+fileName);
//		}
    }

    private boolean isDelete(File file) {
        try {
            String fileTime = file.getName().substring(0, file.getName().length() - 4);
            Long fileTimeStamp = Long.valueOf(fileTime);
            if (System.currentTimeMillis() - fileTimeStamp > DateHelper.dayTimeStamp * 3) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    private void initLocalConfig() {
        // TODO Auto-generated method stub
        Restaurant.isPublicDevice = SharePreferenceUtil.getBooleanPreference(getApplicationContext(), SmartPosPrivateKey.ST_PUBLICE_DEVICE, false);
        Restaurant.isInputSoftMode = SharePreferenceUtil.getBooleanPreference(getApplicationContext(), SmartPosPrivateKey.ST_POPUP_KEYBOARD, true);
        Restaurant.preIsExit = SharePreferenceUtil.getBooleanPreference(getApplicationContext(), SmartPosPrivateKey.ST_PRE_IS_EXIT, false);
        Restaurant.isFastMode = SharePreferenceUtil.getBooleanPreference(getApplicationContext(), SmartPosPrivateKey.ST_IS_SNACK_PATTERN, false);
        Restaurant.fastModeInputNumber = SharePreferenceUtil.getBooleanPreference(getApplicationContext(), SmartPosPrivateKey.ST_IS_SNACK_INPUT_NUMBER, true);
    }

    private void initHttpClient() {
        sanyiSDK = SanyiSDK.getSDK();
        sanyiSDK.init(new DefaultHttpClient(sanyiSDK.getHttpParams()), SharePreferenceUtil.getPreference(getApplicationContext(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, ""),
                Restaurant.getVersionCode(getApplicationContext()), Restaurant.getVersionName(getApplicationContext()), ClientConfig.CLIENT_SMARTPOS_FOR_ANDROID, Restaurant.uuid,
                RegisteDataUtils.getPosRegisteData(getApplicationContext()));
        sanyiSDK.initClientInfo("Android", Build.VERSION.RELEASE);
        sanyiSDK.initLogger(new LoggerWrapper() {

            @Override
            public void debug(String p1, String p2) {
                // TODO Auto-generated method stub
                com.rainbow.smartpos.util.Logger.i("info", p1 + p2);

            }
        });

        sanyiSDK.initDecodeWrapper(new DecodeBaseUtil());

//        SanyiSDK.getSDK().initRequestCompleteListener(getClass().getName(), new PosAgent.PosAgentRequestListener() {
//
//            @Override
//            public void requestComplete(PosAgentRequest request) {
//                // TODO Auto-generated method stub
//                Message message = new Message();
//                message.obj = request;
//                handler.sendMessage(message);
//            }
//        });

    }

    public String startPingAgent() {
//        Timer timer;
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
        // TODO Auto-generated method stub
        StringBuffer log = new StringBuffer();
        try {
            Process mIpAddrProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 " + SharePreferenceUtil.getPreference(this, SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, null));
            int mExitValue = mIpAddrProcess.waitFor();
            InputStreamReader reader = new InputStreamReader(mIpAddrProcess.getInputStream());
            BufferedReader buffer = new BufferedReader(reader);
            String line = "";
            while ((line = buffer.readLine()) != null) {
//                if (mExitValue == 0) {
                log = log.append(line);
//                    if (line.contains("time=")) {
//                        Message message = new Message();
//                        message.obj = HANDLER_UPDAPTE_PING;
//                        Bundle bundle = new Bundle();
//                        bundle.putString("ping", line.substring(line.indexOf("time=") + 5, line.length()));
//                        message.setData(bundle);
//                        handler.sendMessage(message);
//                    }
//                } else {
//                    Message message = new Message();
//                    message.obj = HANDLER_UPDAPTE_PING;
//                    Bundle bundle = new Bundle();
//                    bundle.putString("ping", "网络断开");
//                    message.setData(bundle);
//                    handler.sendMessage(message);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return log.toString();
    }
//        }, 1000 * 5, 1000);
//}

    /*
     * 清除WebView缓存
     */
    public void clearWebViewCache() {

        // 清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // WebView 缓存文件
        File appCacheDir = new File("/data/data/" + getPackageName() + "/app_webview");
        // 删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {

                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {

        }
    }
}
