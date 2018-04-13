package com.sanyipos.android.sdk.androidUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.sanyipos.sdk.api.RegisteData;


public class RegisteDataUtils {

	/**
	 * 保存 点菜宝注册信息
	 * 
	 * @param context
	 */
	public static void saveOrderRegisteData(Context context, RegisteData rd) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = sp.edit();
		ed.putString(RegisteDataKeyForSmartOrder.SP_RD_SALT, rd.getSalt());
		ed.putString(RegisteDataKeyForSmartOrder.SP_RD_POSNAME, rd.getPosName());
		ed.putString(RegisteDataKeyForSmartOrder.SP_RD_ACCESSCODE, rd.getAccessCode());
		ed.putInt(RegisteDataKeyForSmartOrder.SP_RD_DEVICEID, rd.getDeviceId());
		ed.putInt(RegisteDataKeyForSmartOrder.SP_RD_SHOPID, rd.getShopId());
		ed.putBoolean(RegisteDataKeyForSmartOrder.SP_RD_ISMASTER, rd.isMaster());
		ed.putBoolean(RegisteDataKeyForSmartOrder.SP_RD_DEVICEREGISTERED, rd.isDeviceRegistered());
		ed.commit();
	}

	/**
	 * 获取点菜宝注册信息
	 * 
	 * @param context
	 * @return
	 */
	public static RegisteData getOrderRegisteData(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		RegisteData registeData = new RegisteData();
		registeData.setPosName(sp.getString(RegisteDataKeyForSmartOrder.SP_RD_POSNAME, ""));
		registeData.setSalt(sp.getString(RegisteDataKeyForSmartOrder.SP_RD_SALT, ""));
		registeData.setMaster(sp.getBoolean(RegisteDataKeyForSmartOrder.SP_RD_ISMASTER, false));
		registeData.setDeviceRegistered(sp.getBoolean(RegisteDataKeyForSmartOrder.SP_RD_DEVICEREGISTERED, false));
		registeData.setDeviceId(sp.getInt(RegisteDataKeyForSmartOrder.SP_RD_DEVICEID, -1));
		registeData.setShopId(sp.getInt(RegisteDataKeyForSmartOrder.SP_RD_SHOPID, -1));
		registeData.setAccessCode(sp.getString(RegisteDataKeyForSmartOrder.SP_RD_ACCESSCODE, ""));
		return registeData;

	}

	/**
	 * 获取POS机注册信息
	 * 
	 * @param context
	 * @return
	 */

	public static RegisteData getPosRegisteData(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		RegisteData registeData = new RegisteData();
		registeData.setPosName(sp.getString(SmartPosPrivateKey.SP_RD_POSNAME, ""));
		registeData.setSalt(sp.getString(SmartPosPrivateKey.SP_RD_SALT, ""));
		registeData.setMaster(sp.getBoolean(SmartPosPrivateKey.SP_RD_ISMASTER, false));
		registeData.setDeviceRegistered(sp.getBoolean(SmartPosPrivateKey.SP_RD_DEVICEREGISTERED, false));
		registeData.setDeviceId(sp.getInt(SmartPosPrivateKey.SP_RD_DEVICEID, -1));
		registeData.setShopId(sp.getInt(SmartPosPrivateKey.SP_RD_SHOPID, -1));
		registeData.setAccessCode(sp.getString(SmartPosPrivateKey.SP_RD_ACCESSCODE, ""));
		return registeData;

	}

	/**
	 * 保存安卓POS注册信息
	 * 
	 * @param context
	 */
	public static void savePosRegisteData(Context context, RegisteData rd) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = sp.edit();
		ed.putString(SmartPosPrivateKey.SP_RD_SALT, rd.getSalt());
		ed.putString(SmartPosPrivateKey.SP_RD_POSNAME, rd.getPosName());
		ed.putString(SmartPosPrivateKey.SP_RD_ACCESSCODE, rd.getAccessCode());
		ed.putInt(SmartPosPrivateKey.SP_RD_DEVICEID, rd.getDeviceId());
		ed.putInt(SmartPosPrivateKey.SP_RD_SHOPID, rd.getShopId());
		ed.putBoolean(SmartPosPrivateKey.SP_RD_ISMASTER, rd.isMaster());
		ed.putBoolean(SmartPosPrivateKey.SP_RD_DEVICEREGISTERED, rd.isDeviceRegistered());
		ed.commit();
	}
}
