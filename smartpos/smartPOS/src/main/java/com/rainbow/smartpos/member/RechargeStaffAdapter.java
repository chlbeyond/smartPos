package com.rainbow.smartpos.member;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.WaiveRemark;
import com.sanyipos.sdk.model.rest.StaffRest;

public class RechargeStaffAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<StaffRest> staffs;
	public int currentPosition = -1;
	
	public RechargeStaffAdapter(Context context, List<StaffRest> staffs) {
		super();
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.staffs = staffs;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return staffs.size();
	}

	@Override
	public StaffRest getItem(int position) {
		// TODO Auto-generated method stub
		return staffs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		StaffRest staff = staffs.get(position);
		convertView = mInflater.inflate(R.layout.recharge_staff_item, null);
		TextView staff_name = (TextView) convertView.findViewById(R.id.staff_name);
		staff_name.setText(staff.name);
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
	public StaffRest getSelectStaff(){
		return getItem(currentPosition);
	}
}
