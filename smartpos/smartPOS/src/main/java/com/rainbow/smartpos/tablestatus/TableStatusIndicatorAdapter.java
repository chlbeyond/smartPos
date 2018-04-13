package com.rainbow.smartpos.tablestatus;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.TableGroup;

public class TableStatusIndicatorAdapter extends BaseAdapter {
	public Context context;
	public LayoutInflater inflater;
	public static long TABLEGROUP_ALL = -1;
	public int selectIndex = 0;
	public List<TableGroup> tableGroups;

	public TableStatusIndicatorAdapter(Context context) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initData();
	}
	public void initData(){
		tableGroups = new ArrayList<TableGroup>();
		TableGroup allTable = new TableGroup();
		allTable.id = TABLEGROUP_ALL;
		allTable.name = "全部";
		tableGroups.add(allTable);
		tableGroups.addAll(SanyiSDK.rest.tableGroups);
	}

	public void setSelectIndex(int position) {
		this.selectIndex = position;
	}
	
	public int getSelectIndex(){
		return selectIndex;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tableGroups.size();
	}
	public void refresh(){
		initData();
		notifyDataSetChanged();
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
		LinearLayout linearLayout_table_status_indicator_color = (LinearLayout) v.findViewById(R.id.linearLayout_table_status_indicator_color);
		textView_table_status_indicator_count.setText("("+Integer.toString(getFreeTableCount(tableGroups.get(position)))+")");
		//textView_table_status_indicator_count.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.tablestatustabnorbac));
		textView_table_status_indicator.setText(tableGroups.get(position).name);
		textView_table_status_indicator.setTextColor(context.getResources().getColor(R.color.white));
		textView_table_status_indicator_count.setTextColor(context.getResources().getColor(R.color.white));
		if (selectIndex == position) {
			//linearLayout_table_status_indicator_color.setBackgroundColor(Color.parseColor("#ff0000"));
			textView_table_status_indicator.setTextColor(context.getResources().getColor(R.color.user_table_bg));
			textView_table_status_indicator_count.setTextColor(context.getResources().getColor(R.color.user_table_bg));
			//textView_table_status_indicator_count.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.tablestatustabsecbac));
			//v.setBackgroundColor(Color.parseColor("#434f65"));
		}
		return v;
	}

	public int getFreeTableCount(TableGroup tableGroup) {
		int availabels = 0;
		for (int i = 0; i < SanyiSDK.rest.operationData.shopTables.size(); ++i) {
			SeatEntity obj = SanyiSDK.rest.operationData.shopTables.get(i);
			if (obj.state == TableAdapter.AVAILABLE && (tableGroup.id.equals(TABLEGROUP_ALL) || tableGroup.id.equals(obj.tableGroup))) {
				availabels++;
			}
		}
		return availabels;

	}
}
