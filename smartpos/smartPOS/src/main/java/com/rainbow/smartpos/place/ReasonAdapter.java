package com.rainbow.smartpos.place;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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

public class ReasonAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<WaiveRemark> reasons = new ArrayList<WaiveRemark>();
	public int currentPosition = 0;
	private String editReason = "";
	
	public ReasonAdapter(Context context) {
		super();
		this.context = context;
		mInflater = LayoutInflater.from(context);
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		reasons.addAll(SanyiSDK.rest.waiveRemarks);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return reasons.size();
	}

	@Override
	public WaiveRemark getItem(int position) {
		// TODO Auto-generated method stub
		return reasons.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		WaiveRemark waiveRemark = reasons.get(position);
		convertView = mInflater.inflate(R.layout.return_dish_reason_item, null);
		TextView reason_name = (TextView) convertView.findViewById(R.id.reason_name);
		ImageView choose_remark = (ImageView) convertView.findViewById(R.id.choose_remark);
		reason_name.setText(waiveRemark.name);
		EditText edit_reason = (EditText) convertView.findViewById(R.id.edit_reason);
		edit_reason.setVisibility(View.INVISIBLE);
		choose_remark.setImageResource(R.drawable.choose_reason_normal);
		if (position == currentPosition) {
			choose_remark.setImageResource(R.drawable.choose_reason_select);
			if (position == (reasons.size()-1)) {
				edit_reason.setVisibility(View.VISIBLE);
				edit_reason.setFocusable(true);
			}
		}
		edit_reason.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				editReason = s.toString();
			}
		});
		return convertView;
	}
	public void setSelect(int pos){
		this.currentPosition = pos;
		notifyDataSetChanged();
	}
	public long getSelectId(){
		return getItem(currentPosition).id;
	}
	
	public String getEditReason(){
		return editReason;
	}
}
