package com.sanyipos.android.sdk;

import android.app.Application;
import android.content.res.Configuration;


import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.inters.IPollingListener;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

public class SmartposApplication extends Application {
	SanyiSDK sanyiSDK;


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	/**
	 * 初始化httpClient
	 * 
	 * @param host
	 *            主机地址
	 * @param versionCode
	 *            版本号
	 * @param productId
	 *            产品ID
	 */

	public void initHttpClient(String host, int versionCode, String versionName, int productId, String uuid, boolean isDebug) {
		sanyiSDK = SanyiSDK.getSDK();
		HttpParams hp = sanyiSDK.getHttpParams();
		// SchemeRegistry schReg = new SchemeRegistry();
		// schReg.register(new Scheme("http", PlainSocketFactory
		// .getSocketFactory(), 80));
		// schReg.register(new Scheme("https", SSLSocketFactory
		// .getSocketFactory(), 443));
		//
		// // 使用线程安全的连接管理来创建HttpClient
		// ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
		// hp, schReg);
		HttpClient client = new DefaultHttpClient(hp);
		sanyiSDK.init(client, host, versionCode, versionName, productId, uuid, null);

	}

	/**
	 * 开始轮训
	 * 
	 * @param
	 * @param
	 * @param
	 */
	public boolean startPolling() {
		return sanyiSDK.startPolling();
	}

	/**
	 * 设置轮训监听器
	 * 
	 * @param tag
	 * @param listener
	 */
	public void installPollingListener(String tag, IPollingListener listener) {
		sanyiSDK.installPollingListener(tag, listener);
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
