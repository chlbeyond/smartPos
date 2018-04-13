package com.rainbow.smartpos.bill;

import android.graphics.Color;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.ClosedBill;
import com.sanyipos.sdk.model.scala.ClosedBill.BillOrder;
import com.sanyipos.sdk.utils.DateHelper;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BillListAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;

	private int selectedPosition = 0;

	List<ClosedBill> closedBillList = new ArrayList<ClosedBill>();

	public void setClosedBillList(List<ClosedBill> closedBillList) {
		this.closedBillList = closedBillList;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	public BillListAdapter(LayoutInflater li) {
		inflater = li;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.fragment_bill_list_row, null);
		}

		if (position == selectedPosition) {
			vi.setBackgroundResource(R.drawable.order_list_item_bg_pressed);
		} else {
			vi.setBackgroundColor(Color.TRANSPARENT);
		}
		TextView billSN = (TextView) vi.findViewById(R.id.textViewBillSN);
		TextView tableName = (TextView) vi.findViewById(R.id.textViewTableName);
		TextView amount = (TextView) vi.findViewById(R.id.buttonTableAmount);
		TextView personCount = (TextView) vi.findViewById(R.id.textViewPersonCount);
		TextView closedTime = (TextView) vi.findViewById(R.id.textViewClosedTime);

		ClosedBill bill = closedBillList.get(position);
		billSN.setText(bill.sn);
		tableName.setText(bill.orders.get(0).tableName);
		TextPaint tp = tableName.getPaint();
		tp.setFakeBoldText(false);
		tableName.setTextColor(Color.parseColor("#000000"));
		personCount.setText(bill.orders.get(0).personCount.toString());
		if (bill.orders.size() > 1) {
			if (bill.orders.get(0).tag != null) {
				tp.setFakeBoldText(true);
				tableName.setTextColor(Color.parseColor("#ff0000"));
				tableName.setText(bill.orders.get(0).tag);
			}
			personCount.setText(getPersonCountSum(bill).toString());
		}
		amount.setText(OrderUtil.decimalFormatter.format(bill.amount));
		closedTime.setText(DateHelper.hmsFormater.format(bill.closedTime));

		return vi;
	}

	public Integer getPersonCountSum(ClosedBill bill) {
		int personCountSum = 0;
		for (BillOrder order : bill.orders) {
			personCountSum += order.personCount;
		}
		return personCountSum;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return closedBillList.size();
	}

	@Override
	public ClosedBill getItem(int position) {
		return closedBillList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return closedBillList.get(position).id;
	}

	public void sort(final int sortid, final boolean desc) {
		Collections.sort(closedBillList, new Comparator<ClosedBill>() {

			@Override
			public int compare(ClosedBill e1, ClosedBill e2) {
				int comparedValue = 0;
				switch (sortid) {
				case R.id.buttonTableAmount:
					comparedValue = Double.compare(e1.amount, e2.amount);
					break;
				case R.id.buttonTableNo:
					comparedValue = e1.orders.get(0).tableName.compareTo(e2.orders.get(0).tableName);
					break;
				case R.id.buttonTablePersonCount:
					comparedValue = Double.compare(e1.orders.size() > 1 ? getPersonCountSum(e1) : e1.orders.get(0).personCount, e2.orders.size() > 1 ? getPersonCountSum(e2)
							: e2.orders.get(0).personCount);
					break;
				case R.id.buttonTableClosedTime:
					comparedValue = e1.closedTime.compareTo(e2.closedTime);
					break;
				}
				if (comparedValue == 0) {
					comparedValue = e1.sn.compareTo(e2.sn);
				}
				if (desc) {
					comparedValue = comparedValue * -1;
				}
				return comparedValue;
			}
		});
		this.notifyDataSetChanged();
	}
}