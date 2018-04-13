package com.rainbow.smartpos.check;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.DiscountPlan;

public class DiscountAdapter extends BaseAdapter {
	public long selectDiscountId;
	public List<DiscountPlan> discounts;
	public Context context;
	public LayoutInflater inflater = null;

	public DiscountAdapter(Context context, long select, List<DiscountPlan> disounts) {
		this.context = context;
		this.selectDiscountId = select;
		this.discounts = disounts;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return discounts.size() + 1;
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

	@SuppressWarnings("unused")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		if (convertView == null) {
			view = inflater.inflate(R.layout.printer_setting_dialog_view_item, null);
		} else {
			view = convertView;
		}
		if (position == 0) {
			TextView textView = (TextView) view.findViewById(R.id.printerName);
			textView.setText("不使用折扣");
			RadioButton radioButton = (RadioButton) view.findViewById(R.id.radioButton);
			radioButton.setChecked(false);
			if (selectDiscountId == -1) {
				radioButton.setChecked(true);
			}
		} else {
			TextView textView = (TextView) view.findViewById(R.id.printerName);
			textView.setText(discounts.get(position - 1).name);
			RadioButton radioButton = (RadioButton) view.findViewById(R.id.radioButton);
			radioButton.setChecked(false);
			if (selectDiscountId == discounts.get(position - 1).id) {
				radioButton.setChecked(true);
			}
		}
		return view;
	}
}
