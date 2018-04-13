package com.rainbow.smartpos.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.rainbow.smartpos.Restaurant;

public class AboutSettingFragment extends Fragment {
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_about_setting, container, false);
		initView();
		return view;
	}

	private void initView() {
		// TODO Auto-generated method stub
		TextView access_code = (TextView) view.findViewById(R.id.access_code);
		TextView device_id = (TextView) view.findViewById(R.id.device_id);
		TextView version = (TextView) view.findViewById(R.id.version);
		access_code.setText(SanyiSDK.registerData.getAccessCode());

		int DeviceId = SanyiSDK.registerData.getDeviceId();
		if (DeviceId == -1) {
			device_id.setText("未知");
		} else {
			device_id.setText(String.valueOf(DeviceId));
		}
		version.setText(Restaurant.getVersionName(getActivity()) + " - Build No." + String.valueOf(Restaurant.getVersionCode(getActivity()) + " " + SanyiSDK.rest.config.mode));
	}

}
