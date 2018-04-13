package com.rainbow.smartpos.tablemanage;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.VirtualTable;

public class UnMergeTableGroupAdapter extends BaseAdapter {
	public Context context;
	public LayoutInflater inflater;
	public int selectIndex = 0;
	List<VirtualTable> virtualTables;

	public UnMergeTableGroupAdapter(Context context , List<VirtualTable> virtualTables) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.virtualTables = virtualTables;
	}

	public void setSelectIndex(int position) {
		this.selectIndex = position;
	}
	
	public Integer getSelectIndex(){
		return selectIndex;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return virtualTables.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.table_status_indicator, parent, false);
		TextView textView_table_status_indicator = (TextView) v.findViewById(R.id.textView_table_status_indicator);
		TextView textView_table_status_indicator_count = (TextView) v.findViewById(R.id.textView_table_status_indicator_count);
		textView_table_status_indicator_count.setVisibility(View.GONE);
		textView_table_status_indicator.setText(virtualTables.get(position).tag);
		textView_table_status_indicator.setTextColor(Color.parseColor("#676767"));
		if (selectIndex == position) {
			textView_table_status_indicator.setTextColor(Color.parseColor("#3982B7"));
		}
		return v;
	}
}
