package com.rainbow.smartpos.member;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.rainbow.smartpos.R;

public class MemberRecordCardFragment extends Fragment implements OnClickListener {
	private View view = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_member_record_card, container, false);

		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
