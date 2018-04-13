package com.rainbow.smartpos.order;

import com.rainbow.smartpos.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OpMainFragment extends Fragment {
	private View mainView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mainView = inflater.inflate(R.layout.order_op_dialog_layout, null, false);
		return mainView;
	}
}
