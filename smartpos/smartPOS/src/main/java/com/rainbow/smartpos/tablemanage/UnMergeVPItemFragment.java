package com.rainbow.smartpos.tablemanage;

import java.util.List;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class UnMergeVPItemFragment extends Fragment implements OnClickListener{
	private View view;
	//private TextView selectAll;
	private GridView mGridView;
	private TableUnMergeAdapter mAdapter;
	private List<SeatEntity> mCmTables;
	private SeatEntity currentTable;
	
	public void setData(List<SeatEntity> mCmTables){
		this.mCmTables = mCmTables;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.unmerge_vp_item_fragment, container, false);
		initView();
		return view;
	}
	private void initView() {
		// TODO Auto-generated method stub
		//selectAll = (TextView) view.findViewById(R.id.chooseAllBtn);
		mGridView = (GridView) view.findViewById(R.id.gridViewTables);
		mAdapter = new TableUnMergeAdapter(getActivity(), TableUnMergeAdapter.UN_MERGE, mCmTables, currentTable);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mAdapter.addSelect(position);
				mAdapter.notifyDataSetChanged();
			}
		});
		//selectAll.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		TextView tv = (TextView) v;
		String text = tv.getText().toString();
		if (text.equals(getString(R.string.choose_all))) {
			tv.setText(getString(R.string.unselect_all));
			//mAdapter.selectAll(true);
		}else{
			tv.setText(getString(R.string.choose_all));
			//mAdapter.selectAll(false);
		}
	}
	public List<SeatEntity> getUnSelectSeat(){
		if (mAdapter != null) {
			return mAdapter.getMultipleSelectTables();
		}
		return null;
	}
	
	public void clearSelect(){
		if (mAdapter != null) {
			mAdapter.selectPos.clear();
			mAdapter.setSelect(-1);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void refresh(){
		if (null != mAdapter) {
			mAdapter.refreshData();
		}
	}
}
