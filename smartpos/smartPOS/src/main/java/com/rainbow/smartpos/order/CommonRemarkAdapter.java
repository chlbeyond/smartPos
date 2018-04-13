package com.rainbow.smartpos.order;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommonRemarkAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	
	public CommonRemarkAdapter(Context context) {
		super();
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return SanyiSDK.rest.ingredients.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return SanyiSDK.rest.ingredients.get(position).name;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.common_remark_item, null);
		}
		convertView.setBackgroundResource(R.drawable.common_remark_selector);
		TextView remark = (TextView) convertView.findViewById(R.id.remark);
		remark.setText(SanyiSDK.rest.ingredients.get(position).name);
		return convertView;
	}

}
