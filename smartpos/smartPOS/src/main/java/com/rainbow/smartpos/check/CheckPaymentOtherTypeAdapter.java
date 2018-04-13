package com.rainbow.smartpos.check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;

public class CheckPaymentOtherTypeAdapter extends BaseAdapter {
	public Context context;
	private static LayoutInflater inflater = null;
	public CashierParamResult params;

	public CheckPaymentOtherTypeAdapter(Context context, CashierParamResult params) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.params = params;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return params.paymentModes.size();
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
		View vi = convertView;
		vi = inflater.inflate(R.layout.check_payment_other, null);
		vi.setBackgroundResource(R.drawable.order_fragment_tablestatus_button);
		TextView textView_check_payment_other = (TextView) vi.findViewById(R.id.textView_check_payment_other);
		textView_check_payment_other.setText(params.paymentModes.get(position).name);
		return vi;
	}

}
