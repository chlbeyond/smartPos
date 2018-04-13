package com.sanyipos.android.sdk.androidUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharePreferenceUtil {

	public static void saveStringPreference(Context context, String key, String value) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = sp.edit();
		ed.putString(key, value);
		ed.commit();
	}

	public static void saveBooleanPreference(Context context, String key, boolean value) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = sp.edit();
		ed.putBoolean(key, value);
		ed.commit();
	}

	public static String getPreference(Context context, String key, String def) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(key, def);
	}

	public static boolean getBooleanPreference(Context context, String key, boolean value) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(key, value);
	}

	public static int getPreferenceInteger(Context context, String key, int def) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getInt(key, def);
	}

	public static long gerPreferenceLong(Context context, String key, long def) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getLong(key, def);
	}
}
