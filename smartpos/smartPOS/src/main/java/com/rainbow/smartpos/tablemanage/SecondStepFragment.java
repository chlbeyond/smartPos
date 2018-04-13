package com.rainbow.smartpos.tablemanage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;

public class SecondStepFragment extends Fragment{
	private View view;
	
	public GridView mGridView;
	private TableManageAdapter mAdapter;
	
	public ChangeTableFragment mChangeTableFragment;
	
	public Bundle bundle;
	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
	
	public void setChangeTableFragment(ChangeTableFragment changeTableFragment) {
		this.mChangeTableFragment = changeTableFragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.first_step_layout , container , false);
		initView();
		return view;
	}

	private void initView() {
		// TODO Auto-generated method stub
		mGridView = (GridView) view.findViewById(R.id.gridViewTables);
		mAdapter = new TableManageAdapter(getActivity(), TableManageAdapter.AVAILABLE);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mAdapter.setSelectPosition(position);
				mAdapter.notifyDataSetChanged();
				mChangeTableFragment.displayView(mChangeTableFragment.THREE_STEP);
			}
		});
	}
	public SeatEntity getSelectTable(){
		if (null != mAdapter) {
			return mAdapter.getSelectTable();
		}
		return null;
	}
	public void refresh(){
		if (null != mAdapter) {
			mAdapter.refreshData();
		}
	}
}
