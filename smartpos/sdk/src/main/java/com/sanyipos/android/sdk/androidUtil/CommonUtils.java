package com.sanyipos.android.sdk.androidUtil;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class CommonUtils {

	public static String getVersionName(Context context, String packageName) {
		String versionName = "1.0";
		try {
			versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;

	}

	public static int getVersionCode(Context context, String packageName) {
		int versionCode = 1;
		try {
			versionCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

}
