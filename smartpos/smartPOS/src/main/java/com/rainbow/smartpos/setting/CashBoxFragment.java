package com.rainbow.smartpos.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.utils.ConstantsUtil;

public class CashBoxFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.printer_preference);
		findPreference("open_cashbox").setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_CASHIER)) {
					SanyiScalaRequests.openDrawerRequest(new ICallBack() {

						
						@Override
						public void onFail(String error) {
							// TODO Auto-generated method stub
						}
						
						@Override
						public void onSuccess(String status) {
							// TODO Auto-generated method stub
						}
					});

				} else {
					Toast.makeText(getActivity(), "只有收银权限才能执行此操作", Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});

	}
}
