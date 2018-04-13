package com.sanyipos.android.sdk.checkServices;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;

import java.util.Map;

/**
 * app 校验
 * 包括：验证主机、验证接入码、更新程序
 * @author ming.cheng
 * @date 2014/11/17
 *
 */
public class AppChecker {
	
	private static class Holder{
		public static AppChecker intance = new AppChecker();
	}
	
	private AppChecker(){
		
	}
	
	public static AppChecker getInstance(){
		return Holder.intance;
	}
	
	/**
	 * 检查版本更新
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void checkApkUpdate(final FragmentActivity context, String apkupdateUrl, final String deviceId, String salt, String shopid, final CheckListener listener){
		final LoadingDialog loadingDialog = new LoadingDialog(context);
		loadingDialog.show();
		loadingDialog.setToolTipText("检查新版本...");
		new CheckAPKUpdate(context, apkupdateUrl,deviceId, salt, shopid, new CheckAPKUpdate.ICheckApkUpdateListener() {
			
			@Override
			public void onUpdate(final Map info) {
				loadingDialog.dismiss();
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						new DownloadApkDialog(context, info, deviceId, new DownloadApkDialog.IDownloadListener() {
							
							@Override
							public void onFail() {
								listener.downloadFail();
							}
							
							@Override
							public void onCompleted(String apkPath) {
								listener.downloadCompleted(apkPath);
							}
							
							@Override
							public void onCancel() {
								listener.downloadCancel();
							}
						}).show();
					}
				});
			}
			
			@Override
			public void noUpdate() {
				loadingDialog.dismiss();
				listener.noUpdate();
			}
			
			@Override
			public void checkFail(String msg) {
				loadingDialog.dismiss();
				listener.checkFail(msg);
			}
		}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}
}
