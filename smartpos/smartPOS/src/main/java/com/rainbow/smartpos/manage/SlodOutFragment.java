package com.rainbow.smartpos.manage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;

public class SlodOutFragment extends Fragment implements OnCheckedChangeListener, OnClickListener {
	public final static int ADD_SLODOUT_FRAGMENT = 0;
	public final static int HAS_SLODOUT_FRAGMENT = 1;
	public final static int HAS_STOPSALE_FRAGMENT = 2;

	private View view;
	private RadioGroup slod_out_manager_group;

	public static AddSlodOutFragment addSlodOutFragment;
	public static HasSlodOutFragment hasSlodOutFragment;
	public static HasStopSaleFragment hasStopSaleFragment;
	public MainScreenActivity activity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_slod_out, container, false);
		activity = (MainScreenActivity) getActivity();
		initView();
		return view;
	}

	private void initView() {
		// TODO Auto-generated method stub
		slod_out_manager_group = (RadioGroup) view.findViewById(R.id.slod_out_manager_group);
		slod_out_manager_group.setOnCheckedChangeListener(this);
		view.findViewById(R.id.back).setOnClickListener(this);
		displayView(ADD_SLODOUT_FRAGMENT);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.can_slod_out:
			displayView(ADD_SLODOUT_FRAGMENT);
			break;
		case R.id.has_slod_out:
			displayView(HAS_SLODOUT_FRAGMENT);
			break;
		case R.id.has_stop_sale:
			displayView(HAS_STOPSALE_FRAGMENT);
			break;
		default:
			break;
		}
	}

	public void hideView(FragmentTransaction ft) {
		if (addSlodOutFragment != null) {
			ft.detach(addSlodOutFragment);
		}
		if (hasSlodOutFragment != null) {
			ft.detach(hasSlodOutFragment);
		}
		if (hasStopSaleFragment != null) {
			ft.detach(hasStopSaleFragment);
		}
	}

	public void displayView(int position) {
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		hideView(ft);
		switch (position) {
		case ADD_SLODOUT_FRAGMENT:
			//if (addSlodOutFragment != null) {
			//	ft.attach(addSlodOutFragment);
			//} else {
			addSlodOutFragment = new AddSlodOutFragment();
			ft.add(R.id.slod_out_frame_layout, addSlodOutFragment);
			//}
			break;
		case HAS_SLODOUT_FRAGMENT:
			if (hasSlodOutFragment != null) {
				ft.attach(hasSlodOutFragment);
			} else {
				hasSlodOutFragment = new HasSlodOutFragment();
				ft.add(R.id.slod_out_frame_layout, hasSlodOutFragment);
			}
			break;
		case HAS_STOPSALE_FRAGMENT:
			if (hasStopSaleFragment != null) {
				ft.attach(hasStopSaleFragment);
			} else {
				hasStopSaleFragment = new HasStopSaleFragment();
				ft.add(R.id.slod_out_frame_layout, hasStopSaleFragment);
			}
			break;
		default:
			break;
		}
		ft.commit();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		activity.displayView(MainScreenActivity.MANAGER_FRAGMENT, new Bundle());
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
}
