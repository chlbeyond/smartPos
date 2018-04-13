package com.rainbow.smartpos.member;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sanyipos.sdk.model.rest.MemberTypes;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;

import java.util.List;

public class MemberTypeAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<MemberTypes> memberTypes;
	public int currentPosition = -1;

	public MemberTypeAdapter(Context context, List<MemberTypes> memberTypes) {
		super();
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.memberTypes = memberTypes;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return memberTypes.size();
	}

	@Override
	public MemberTypes getItem(int position) {
		// TODO Auto-generated method stub
		return memberTypes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MemberTypes memberType = memberTypes.get(position);
		convertView = mInflater.inflate(R.layout.recharge_staff_item, null);
		TextView staff_name = (TextView) convertView.findViewById(R.id.staff_name);
		staff_name.setText(memberType.name);
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
	public MemberTypes getSelectMemberTypes(){
		return getItem(currentPosition);
	}
}
