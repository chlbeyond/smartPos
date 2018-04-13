package com.rainbow.smartpos.bill;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.StringResourceUtil;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.Payment;
import com.sanyipos.sdk.model.PaymentWrapper;
import com.sanyipos.sdk.utils.OrderUtil;

public class BillPaymentAdapter extends BaseAdapter {
	private Activity activity;
	private static LayoutInflater inflater = null;
	List<PaymentWrapper> allPayments = new ArrayList<PaymentWrapper>();

	public BillPaymentAdapter(Activity a) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void reset() {
		this.allPayments.clear();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return allPayments.size();
	}

	@Override
	public PaymentWrapper getItem(int position) {
		return allPayments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(OrderDetail object) {
		allPayments.remove(object);
		// object.delete();
		notifyDataSetChanged();
	}

	public List<PaymentWrapper> getAllPayments() {
		return allPayments;
	}

	public double getPaymentTotal() {
		double total = 0;
		for (int i = 0; i < allPayments.size(); i++) {
			PaymentWrapper payment = allPayments.get(i);
			total = total + Double.valueOf(payment.getValue()) - Double.valueOf(payment.getPaymentChange());
		}
		return total;
	}

	public void addPayment(PaymentWrapper payment) {
		payment.setRowNo(allPayments.size() + 1);
		allPayments.add(payment);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.check_payment_row, null);
		PaymentWrapper payment = allPayments.get(position);

		TextView rowNo = (TextView) vi.findViewById(R.id.textViewCheckPaymentNo);
		rowNo.setText(Integer.toString(payment.getRowNo()));

		TextView paymentName = (TextView) vi.findViewById(R.id.textViewCheckPaymentName);
		paymentName.setText(StringResourceUtil.getPaymentNameByPaymentType(activity, payment.getPaymentType()));

		TextView paymentAmount = (TextView) vi.findViewById(R.id.textViewCheckPaymentAmount);

		paymentAmount.setText(OrderUtil.decimalFormatter.format(Double.valueOf(payment.getPaymentAmount())));

		TextView paymentChange = (TextView) vi.findViewById(R.id.textViewCheckPaymentChange);

		paymentChange.setText(OrderUtil.decimalFormatter.format(Double.valueOf(payment.getPaymentChange())));

		TextView paymentReceipt = (TextView) vi.findViewById(R.id.textViewCheckPaymentReceipt);
		if (payment.getPaymentType() == Payment.PAYMENT_VOUCHER) {
			paymentReceipt.setText(payment.getSn());
		} else {
			paymentReceipt.setText(payment.getPaymentReceipt());
		}

		return vi;

	}
}
