package com.rainbow.smartpos.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;

public class SettingFragment extends Fragment implements OnCheckedChangeListener, OnClickListener {
	private View view;
	private RadioGroup settingGroup;
	private RadioButton general_setting;
	private AboutSettingFragment mAboutSettingFragment = new AboutSettingFragment();
	private MainScreenActivity activity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub.
		view = inflater.inflate(R.layout.fragment_setting, container, false);
		activity = (MainScreenActivity) getActivity();
		initView();
		return view;
	}

	private void initView() {
		// TODO Auto-generated method stub
		settingGroup = (RadioGroup) view.findViewById(R.id.setting_group);
		general_setting = (RadioButton) view.findViewById(R.id.general_setting);
		settingGroup.setOnCheckedChangeListener(this);
		view.findViewById(R.id.back).setOnClickListener(this);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.general_setting:
			break;
		case R.id.printer_setting:
			break;
		case R.id.about_setting:
			replaceFragment(mAboutSettingFragment);
			break;
		default:
			break;
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		general_setting.setChecked(true);
	}

	public void replaceFragment(Fragment fragment) {
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame_layout, fragment).commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		activity.displayView(MainScreenActivity.MANAGER_FRAGMENT, new Bundle());
	}
}
