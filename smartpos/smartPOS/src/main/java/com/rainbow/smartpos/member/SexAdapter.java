package com.rainbow.smartpos.member;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.StaffRest;

import java.util.List;

public class SexAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<String> sexs;
	public int currentPosition = -1;

	public SexAdapter(Context context, List<String> sexs) {
		super();
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.sexs = sexs;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sexs.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return sexs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = mInflater.inflate(R.layout.recharge_staff_item, null);
		TextView staff_name = (TextView) convertView.findViewById(R.id.staff_name);
		staff_name.setText(sexs.get(position));
		staff_name.setTextColor(context.getResources().getColor(R.color.Black));
		convertView.setBackgroundResource(R.drawable.choose_staff_text_color_selector);
		if (position == currentPosition) {
			staff_name.setTextColor(context.getResources().getColor(R.color.title_table_background));
			convertView.setBackgroundColor(Color.parseColor("#C6C6C6"));
		}
		return convertView;
	}
	public void setSelect(int pos){
		this.currentPosition = pos;
		notifyDataSetChanged();
	}
	public String getSelectSex(){
		return getItem(currentPosition);
	}
}
