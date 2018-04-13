package com.sanyipos.android.sdk.checkServices;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sanyi.androidsdk.R;
import com.sanyipos.android.sdk.androidUtil.CacheUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * 下载apk对话框
 *
 * @author ming.cheng
 * @date 2014/11/14
 */
public class DownloadShaDialog extends Dialog {

    public static interface IDownloadListener {
        public void onCancel();

        public void onCompleted(String apkPath);

        public void onFail();
    }

    private final int DOWNING = 1;
    private final int FINISHED = 2;
    private final int DOWNFAIL = 3;

    private TextView mCurrentVersion;
    private TextView mNewVersion;
    private ProgressBar mprogress;
    private TextView mProgressText;
    private View progressView;
    private View bottomView;
    private TextView mTipsTextView;

    private Map<String, String> data;
    private String deviceId;
    private Context mContext;
    private String apkFilePath;
    private String fileName;
    private IDownloadListener listener;

    private int progress;
    private boolean cancelUpdate;

    public DownloadShaDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public DownloadShaDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    public DownloadShaDialog(Context context, Map<String, String> data, String deviceId, IDownloadListener listener) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        this.data = data;
        this.listener = listener;
        this.deviceId = deviceId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_apk);

        mTipsTextView = (TextView) findViewById(R.id.tipText);
        mCurrentVersion = (TextView) findViewById(R.id.currentVersion);
        mNewVersion = (TextView) findViewById(R.id.newVersion);
        mprogress = (ProgressBar) findViewById(R.id.progress);
        mProgressText = (TextView) findViewById(R.id.progressText);
        mNewVersion.setText(String.format("发现新版本：%s.%s", data.get("name"), data.get("version")));
        mCurrentVersion.setText(String.format("当前版本：%s.%s", data.get("currentVersionName"), data.get("currentVersionCode")));
        progressView = findViewById(R.id.progressLayout);
        bottomView = findViewById(R.id.bottomView);
        progressView.setVisibility(View.GONE);
        bottomView.setVisibility(View.VISIBLE);
        mTipsTextView.setText("有新版本，请更新");

        progressView.setVisibility(View.VISIBLE);
        bottomView.setVisibility(View.GONE);
        mTipsTextView.setText("版本更新");
        new downloadApkThread(this.deviceId).start();

        // findViewById(R.id.cancelBtn).setOnClickListener(new
        // android.view.View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // listener.onCancel();
        // DownloadApkDialog.this.dismiss();
        // }
        // });
        //
        // findViewById(R.id.sureBtn).setOnClickListener(new
        // android.view.View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // progressView.setVisibility(View.VISIBLE);
        // bottomView.setVisibility(View.GONE);
        // mTipsTextView.setText("版本更新");
        // new downloadApkThread().start();
        // }
        // });

    }

    @Override
    public void show() {
        super.show();

        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Toast.makeText(mContext, "请等待操作结束", Toast.LENGTH_LONG).show();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNING:
                    int pg = (Integer) msg.obj;
                    mprogress.setProgress(pg);
                    mProgressText.setText(String.format("%d%%", pg));
                    break;
                case FINISHED:
                    dismiss();
                    listener.onCompleted(apkFilePath + "/" + fileName);
                    break;
                case DOWNFAIL:
                    dismiss();
                    break;
            }
        }

        ;
    };

    /**
     * 下载apk的方法
     *
     * @author rongsheng
     */
    public class downloadApkThread extends Thread {

        private String deviceId;

        public downloadApkThread(String deviceId) {
            this.deviceId = deviceId;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                fileName = data.get("version");
                String state = Environment.getExternalStorageState();
                File cacheDir = CacheUtils.getCacheDirectory(mContext, state.equals(Environment.MEDIA_MOUNTED), "apk/sha");
                apkFilePath = cacheDir.getAbsolutePath();
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                } else {
                    // 清除掉该目录下的所有文件
                    File[] files = cacheDir.listFiles();
                    for (File file : files) {
                        if (file.canWrite()) {
                            file.delete();
                        }
                    }
                }
                // 获得存储卡的路径
                String downloadUrl = data.get("url");
                if (!downloadUrl.contains("http://")) {
                    downloadUrl = "http://" + downloadUrl;
                }
                URL url = new URL(downloadUrl);
                // 创建连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5 * 1000);// 设置超时时间
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Charser", "GBK,utf-8;q=0.7,*;q=0.3");
                conn.addRequestProperty("sanyi-device-id", deviceId);
                // 获取文件大小
                int length = conn.getContentLength();
                // 创建输入流
                InputStream is = conn.getInputStream();

                FileOutputStream fos = new FileOutputStream(new File(apkFilePath + "/" + fileName));
                int count = 0;
                // 缓存
                byte buf[] = new byte[1024];
                // 写入到文件中
                do {
                    int numread = is.read(buf);
                    count += numread;
                    // 计算进度条位置
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    Message message = new Message();
                    message.obj = progress;
                    message.what = DOWNING;
                    mHandler.sendMessage(message);
                    if (numread <= 0) {
                        // 下载完成
                        // 取消下载对话框显示
                        Message message2 = new Message();
                        message2.obj = 100;
                        message2.what = FINISHED;
                        mHandler.sendMessage(message2);
                        break;
                    }
                    // 写入文件
                    fos.write(buf, 0, numread);
                } while (!cancelUpdate);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                listener.onFail();
                mHandler.sendEmptyMessage(DOWNFAIL);
            } catch (IOException e) {
                e.printStackTrace();
                listener.onFail();
                mHandler.sendEmptyMessage(DOWNFAIL);
            }

        }
    }

}
