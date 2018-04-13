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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.order.CookMethodItemLayout;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.scala.check.CashierDiscount;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.DiscountPlan;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.Promotion;
import com.sanyipos.sdk.model.scala.check.CashierPromotion;
import com.sanyipos.sdk.utils.OrderUtil;

public class ChooseDiscountAdapter extends BaseAdapter {
	private List<DiscountPlan> data = new ArrayList<DiscountPlan>();
	private Context mContext;
	private LayoutInflater inflater;
	private int currentPos = -1;
	

	public ChooseDiscountAdapter(Context context) {
		this.mContext = context;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public DiscountPlan getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return data.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DiscountPlan discountPlan = data.get(position);
		long id = discountPlan.id;
		String name = discountPlan.name;
		CookMethodItemLayout l;

		if (convertView == null) {
			l = (CookMethodItemLayout) inflater.inflate(R.layout.choose_promotion_dialog_detail, parent, false);
		} else {
			l = (CookMethodItemLayout) convertView;
		}
		
		TextView textViewPromotionName = (TextView) l.findViewById(R.id.textViewPromotionName);
		l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_gridview_item_bg_normal));
		textViewPromotionName.setText(name);
		if (currentPos == position) {
			l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_gird_item_bg_single));
		}
		return l;
	}
	
	public void setDiscount(List<DiscountPlan> discountPlans, List<CashierDiscount> cashierDiscounts){
		initData(discountPlans, cashierDiscounts);
		notifyDataSetChanged();
	}


	private void initData(List<DiscountPlan> discountPlans, List<CashierDiscount> cashierDiscounts) {
		// TODO Auto-generated method stub
		currentPos = -1;
		this.data = discountPlans;
		for (int i = 0; i < data.size(); i++) {
			DiscountPlan discountPlan = data.get(i);
			for (int j = 0; j < cashierDiscounts.size(); j++) {
				CashierDiscount cashierDiscount = cashierDiscounts.get(j);
				if (discountPlan.id == cashierDiscount.discountPlanId) {
					currentPos = i;
				}
			}
		}
	}
}
