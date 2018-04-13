package com.sanyipos.android.sdk.checkServices;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

import com.sanyipos.sdk.utils.GenericHelper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CheckAPKUpdate extends AsyncTask<Void, Void, Void> {
	
	public static interface ICheckApkUpdateListener{
		public void onUpdate(Map info);
		public void noUpdate();
		public void checkFail(String msg);
	}

	private String responseText;
	private Context mContext;
	private ICheckApkUpdateListener listener;
	private String uuid;
	private String checkUpdateUrl;
	private String salt;
	private String deviceId;
	private String shopid;
	
	public CheckAPKUpdate(Context context, String checkUpateUrl,String deviceId, String salt, String shopid, ICheckApkUpdateListener listener){
		mContext = context;
		this.listener = listener;
		this.uuid = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
		this.checkUpdateUrl = checkUpateUrl;
		this.salt = salt;
		this.deviceId = deviceId;
		this.shopid = shopid;
	}

	@Override
	protected Void doInBackground(Void... params) {
		int responseCode = 0;
		HttpParams parms = new BasicHttpParams();
		parms.setParameter("charset", HTTP.UTF_8);
		HttpConnectionParams.setConnectionTimeout(parms, 60 * 1000);
		HttpConnectionParams.setSoTimeout(parms, 60 * 1000);

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient(parms);
		HttpGet httpget = new HttpGet(checkUpdateUrl);

		try {
			String millis = Long.toString(System.currentTimeMillis());

			String token = GenericHelper.SHA1(this.salt + "-" + millis) + "|" + millis;
			httpget.addHeader("charset", HTTP.UTF_8);
			httpget.addHeader("X-SanYi-Version", "1.0");
			// TODO: could id include alpha letter? then we need to change
			// deviceId to string
			httpget.addHeader("X-SanYi-UUID", uuid);
			httpget.addHeader("X-SanYi-Token", token);
			httpget.addHeader("Content-Type", "application/json");
			httpget.addHeader("sanyi-device-id", deviceId);
			httpget.addHeader("sanyi-shop-id", shopid);
			// Execute HTTP get Request
			HttpResponse response = httpclient.execute(httpget);
			Map<String, String> hashMap = null;
			if (response != null) {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					responseText = sb.toString();
					int statusCode = response.getStatusLine().getStatusCode();
					JSONObject responseObj = new JSONObject(responseText);
					if (statusCode == HttpStatus.SC_OK) {
						if(responseObj.getInt("code") == 0) {

							hashMap = new HashMap<String, String>();
							hashMap.put("url", responseObj.getString("url"));
							hashMap.put("version", responseObj.getString("version"));
							hashMap.put("name", responseObj.getString("name"));
							String versionName = getVersionName();
							int versionCode = getVersionCode();
							int serverVersionCode = responseObj.getInt("version");
							hashMap.put("currentVersionCode", String.valueOf(versionCode));
							hashMap.put("currentVersionName", versionName);
							if (serverVersionCode > versionCode) {
								hashMap.put("new_versionCode", String.valueOf(serverVersionCode));
								listener.onUpdate(hashMap);
							} else {
								responseCode = 408;
								responseText = "No update";
								listener.noUpdate();
							}
						} else {
							responseCode = 408;
							responseText = "No update";
							listener.noUpdate();
						}

					} else if(statusCode == 400){
						
						int errorCode = responseObj.getInt("error_code");
						if(errorCode == 1004){
							listener.noUpdate();
						}
						
					} else if(statusCode  == HttpStatus.SC_FORBIDDEN){
						/**
						 * {"status":{"message":"非本店设备，禁止接入","code":1000}}
						 * */
						JSONObject msgObj = responseObj.getJSONObject("status");
						if(msgObj != null){
							int code = msgObj.getInt("code");
							String msg = msgObj.getString("message");
							switch(code){
							case 1000:
								listener.checkFail(msg);
								break;
							default:
								listener.checkFail(msg);
								break;
							}
						}else{
							listener.checkFail("status is null");
						}
						
					} else {
						responseCode = 408;
						responseText = "Response is null";
						listener.checkFail(responseText);
					}

				} catch (IOException e) {
					responseCode = 408;
					Log.e("SE3", "IO Exception in reading from stream.");
					responseText = "IO Exception";
					listener.checkFail(responseText);
				}

			}
		} catch (Exception e) {
			responseCode = 408;
			responseText = "Response is null";
			e.printStackTrace();
			listener.checkFail("Exception");
		}
		return null;
	}
	
	/**
	 * ��ȡ��汾��
	 */
	public String getVersionName(){
		String versionName = "";
		try {
			// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionName
			versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	/**
	 * ��ȡbuild�汾��
	 * @return
	 */
	public int getVersionCode(){
		int versionCode = 0;
		try {
			// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
			versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	
}