package com.rainbow.smartpos.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.android.sdk.androidUtil.CacheUtils;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 下载apk对话框
 * 
 * @author ming.cheng
 * @date 2014/11/14
 * 
 */
public class DownloadApkDialog extends Dialog {

	public static interface IDownloadListener {
		public void onCancel();

		public void onCompleted(String apkPath);

		public void onFail(String error);
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

	private Context mContext;
	private String apkFilePath;
	private String fileName;
	private IDownloadListener listener;
	private String apkUrl;

	private int progress;
	private boolean cancelUpdate;

	public DownloadApkDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public DownloadApkDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public DownloadApkDialog(Context context, String apkUrl, IDownloadListener listener) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		this.listener = listener;
		this.apkUrl = apkUrl;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_running_apk);

		mTipsTextView = (TextView) findViewById(R.id.tipText);
		mCurrentVersion = (TextView) findViewById(R.id.currentVersion);
		mNewVersion = (TextView) findViewById(R.id.newVersion);
		mprogress = (ProgressBar) findViewById(R.id.progress);
		mProgressText = (TextView) findViewById(R.id.progressText);
		progressView = findViewById(R.id.progressLayout);
		bottomView = findViewById(R.id.bottomView);

		progressView.setVisibility(View.VISIBLE);
		bottomView.setVisibility(View.GONE);
		mTipsTextView.setText("版本更新");
		new downloadApkThread().start();

		//更新提示
//		 findViewById(R.id.cancelBtn).setOnClickListener(new
//		 android.view.View.OnClickListener() {
//		
//		 @Override
//		 public void onClick(View v) {
//		 listener.onCancel();
//		 DownloadApkDialog.this.dismiss();
//		 }
//		 });
//		
//		 findViewById(R.id.sureBtn).setOnClickListener(new
//		 android.view.View.OnClickListener() {
//		
//		 @Override
//		 public void onClick(View v) {
//		 progressView.setVisibility(View.VISIBLE);
//		 bottomView.setVisibility(View.GONE);
//		 mTipsTextView.setText("版本更新");
//		 new downloadApkThread().start();
//		 }
//		 });

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
				DownloadApkDialog.this.dismiss();
				listener.onCompleted(apkFilePath + "/" + fileName);
				break;
			case DOWNFAIL:
				DownloadApkDialog.this.dismiss();
				break;
			}
		};
	};

	/**
	 * 下载apk的方法
	 * 
	 * @author rongsheng
	 * 
	 */
	public class downloadApkThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				fileName = String.format("%s-%s.apk", "app", "SmartPos");
				String state = Environment.getExternalStorageState();
				File cacheDir = CacheUtils.getCacheDirectory(mContext, state.equals(Environment.MEDIA_MOUNTED), "apks");
				apkFilePath = cacheDir.getAbsolutePath();
				Log.d("ming.cheng", apkFilePath + "/" + fileName);
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
				KLog.d("DownloadApkDialog", apkUrl);
				if (!apkUrl.contains("http://")) {
					apkUrl = "http://"+apkUrl;

					KLog.d("DownloadApkDialog", apkUrl);
				}
				URL url = new URL(apkUrl);
				// 创建连接
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(5 * 1000);// 设置超时时间
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Charser", "GBK,utf-8;q=0.7,*;q=0.3");
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
				listener.onFail("下载地址异常,下载失败");
				mHandler.sendEmptyMessage(DOWNFAIL);
			} catch (IOException e) {
				e.printStackTrace();
				listener.onFail("读取文件异常,下载失败");
				mHandler.sendEmptyMessage(DOWNFAIL);
			}

		}
	}

}
