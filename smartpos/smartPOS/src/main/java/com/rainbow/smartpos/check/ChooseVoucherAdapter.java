package com.rainbow.smartpos.check;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.order.CookMethodItemLayout;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.VoucherType;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.model.scala.check.CashierVoucher;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

public class ChooseVoucherAdapter extends BaseAdapter {
	public List<CashierVoucher> data = new ArrayList<CashierVoucher>();
	public Context mContext;
	public LayoutInflater inflater;
	private CashierParamResult mCashierParam;

	public ChooseVoucherAdapter(Context context, CashierParamResult cashierParam, CashierResult cashierResult) {
		this.mContext = context;
		this.mCashierParam = cashierParam;
		initData(cashierResult);
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private void initData(CashierResult cashierResult) {
		// TODO Auto-generated method stub
		for (int i = 0; i < mCashierParam.voucherTypes.size(); i++) {
			CashierVoucher cashierVoucher = new CashierVoucher();
			VoucherType voucherType = mCashierParam.voucherTypes.get(i);
			cashierVoucher.id = voucherType.id;
			cashierVoucher.name = voucherType.name;
			cashierVoucher.remark = voucherType.remark;
			cashierVoucher.value = voucherType.value;
			data.add(cashierVoucher);
		}
		refreshData(cashierResult);
	}
	
	private void refreshData(CashierResult cashierResult){
		for (int i = 0; i < data.size(); i++) {
			CashierVoucher cashierVoucher = data.get(i);
			cashierVoucher.count = 0;
			for (int j = 0; j < cashierResult.payments.size(); j++) {
				CashierPayment cashierPayment = cashierResult.payments.get(j);
				if ((cashierPayment.paymentType == ConstantsUtil.PAYMENT_VOUCHER) && cashierVoucher.name.equals(cashierPayment.paymentName)) {
					cashierVoucher.count++;
				}
			}
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public CashierVoucher getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		CookMethodItemLayout l;
		if (convertView == null) {
			l = (CookMethodItemLayout) inflater.inflate(R.layout.choose_voucher_dialog_detail, parent, false);
		} else {
			l = (CookMethodItemLayout) convertView;
		}
		CashierVoucher voucher = data.get(position);
		l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_gridview_item_bg_normal));
		TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
		TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
		textViewDishUnitName.setText(voucher.name);
		if (voucher.count > 0) {
			textViewDishUnitPrice.setVisibility(View.VISIBLE);
			textViewDishUnitPrice.setText("x"+voucher.count);
			l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_grid_item_bg_multiple));
		}else{
			textViewDishUnitPrice.setVisibility(View.GONE);
		}
		
		return l;
	}
	public void setSelect(int position, int count){
		data.get(position).count = count;
		notifyDataSetChanged();
	}
	public void refresh(CashierResult cashierResult){
		refreshData(cashierResult);
		notifyDataSetChanged();
	}
}
