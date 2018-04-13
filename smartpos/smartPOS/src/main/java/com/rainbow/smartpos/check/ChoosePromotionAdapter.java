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
import com.sanyipos.sdk.model.scala.check.CashierParamResult.Promotion;
import com.sanyipos.sdk.model.scala.check.CashierPromotion;
import com.sanyipos.sdk.utils.OrderUtil;

public class ChoosePromotionAdapter extends BaseAdapter {
	private List<Promotion> data = new ArrayList<Promotion>();
	private Context mContext;
	private LayoutInflater inflater;
	private List<Integer> mSelects = new ArrayList<Integer>();
	

	public ChoosePromotionAdapter(Context context) {
		this.mContext = context;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Promotion getItem(int position) {
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
		Promotion promotion = data.get(position);
		long id = promotion.id;
		String name = promotion.name;
		CookMethodItemLayout l;

		if (convertView == null) {
			l = (CookMethodItemLayout) inflater.inflate(R.layout.choose_promotion_dialog_detail, parent, false);
		} else {
			l = (CookMethodItemLayout) convertView;
		}
		
		TextView textViewPromotionName = (TextView) l.findViewById(R.id.textViewPromotionName);
		l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_gridview_item_bg_normal));
		textViewPromotionName.setText(name);
		for (int i = 0; i < mSelects.size(); i++) {
			if (mSelects.get(i) == position) {
				l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_grid_item_bg_multiple));
			}
		}
		return l;
	}
	
	public void setPromotion(List<Promotion> promotions, List<CashierPromotion> cashierPromotions){
		initData(promotions, cashierPromotions);
		notifyDataSetChanged();
	}


	private void initData(List<Promotion> promotions, List<CashierPromotion> cashierPromotions) {
		// TODO Auto-generated method stub
		mSelects.clear();
		this.data = promotions;
		for (int i = 0; i < data.size(); i++) {
			Promotion promotion = data.get(i);
			for (int j = 0; j < cashierPromotions.size(); j++) {
				CashierPromotion cashierPromotion = cashierPromotions.get(j);
				if (promotion.id == cashierPromotion.promotion) {
					if (!mSelects.contains(i)) {
						mSelects.add(i);
					}
				}
			}
		}
	}
	public boolean isSelect(int position){
		if (mSelects.contains(position)) {
			return true;
		}
		return false;
	}
}
