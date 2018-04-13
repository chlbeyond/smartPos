package com.rainbow.smartpos.check;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.utils.OrderUtil;

public class CheckPaymentAdapter extends BaseAdapter {
	private Activity activity;
	private static LayoutInflater inflater = null;
	public List<CashierPayment> allPayments = new ArrayList<CashierPayment>();
	private int currentRowNo = 1;

	public CheckPaymentAdapter(Activity a) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return allPayments.size();
	}

	@Override
	public CashierPayment getItem(int position) {
		return allPayments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void removePayment(int positon) {
		allPayments.remove(positon);
	}

	public void remove(OrderDetail object) {
		allPayments.remove(object);
		// object.delete();
		notifyDataSetChanged();
	}

	public List<CashierPayment> getAllPayments() {
		return allPayments;
	}

	public void addPayment(CashierPayment payment) {
		allPayments.add(payment);
	}

	public void addAllPayments(List<CashierPayment> payments) {
		for (CashierPayment p : payments) {
			addPayment(p);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		vi = inflater.inflate(R.layout.check_payment_row, null);
		CashierPayment payment = allPayments.get(position);

		TextView rowNo = (TextView) vi.findViewById(R.id.textViewCheckPaymentNo);
		rowNo.setText(Integer.toString(position + 1));

		TextView paymentName = (TextView) vi.findViewById(R.id.textViewCheckPaymentName);
		paymentName.setText(payment.paymentName);
		TextView paymentAmount = (TextView) vi.findViewById(R.id.textViewCheckPaymentAmount);

		paymentAmount.setText(OrderUtil.decimalFormatter.format(Double.valueOf(payment.value)));

		TextView paymentChange = (TextView) vi.findViewById(R.id.textViewCheckPaymentChange);
		paymentChange.setText(OrderUtil.decimalFormatter.format(Double.valueOf(payment.change)));
		if (payment.paymentType == 6) {
			paymentChange.setText(Html.fromHtml(String.format("抵" + OrderUtil.decimalFormatter.format(Double.valueOf(payment.value) - Double.valueOf(payment.change)))));
		}

		TextView paymentReceipt = (TextView) vi.findViewById(R.id.textViewCheckPaymentReceipt);

		paymentReceipt.setText(payment.transaction);

		return vi;

	}
}
