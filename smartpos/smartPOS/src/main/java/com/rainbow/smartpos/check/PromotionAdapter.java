package com.rainbow.smartpos.check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.presenter.CheckPresenterImpl;
import com.rainbow.smartpos.order.CookMethodItemLayout;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.scala.check.CashierAction;
import com.sanyipos.sdk.model.scala.check.CashierDiscount;
import com.sanyipos.sdk.model.scala.check.CashierPromotion;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class PromotionAdapter extends BaseAdapter {
	public List<Object> promotions = new ArrayList<Object>();
	private Context mContext;
	private LayoutInflater inflater;
	private MainScreenActivity activity;
	private CheckPresenterImpl checkPresenterImpl;
	public PromotionAdapter(Context context, CheckPresenterImpl checkPresenter, List<CashierPromotion> promotions, List<CashierDiscount> discounts) {
		this.mContext = context;
		this.activity = (MainScreenActivity) mContext;
		this.checkPresenterImpl = checkPresenter;
		initData(promotions, discounts);
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	private void initData(List<CashierPromotion> promotions, List<CashierDiscount> discounts){
		this.promotions.clear();
		CashierPromotion promotion = new CashierPromotion();
		promotion.id = -1;
		this.promotions.add(promotion);
		this.promotions.addAll(promotions);
		this.promotions.addAll(discounts);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return promotions.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return promotions.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Object obj = promotions.get(position);
		if (obj instanceof CashierPromotion) {
			return ((CashierPromotion)obj).id;
		}else if(obj instanceof CashierDiscount){
			return ((CashierDiscount)obj).id;
		}
		return -2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Object obj = promotions.get(position);
		long id = -1;
		String name = "";
		double value = -1;
		if (obj instanceof CashierPromotion) {
			id = ((CashierPromotion)obj).id;
			name = ((CashierPromotion)obj).promotionName;
			value = ((CashierPromotion)obj).value;
		}else if(obj instanceof CashierDiscount){
			id = ((CashierDiscount)obj).id;
			name = ((CashierDiscount)obj).discountName;
			value = ((CashierDiscount)obj).value;
		}
		CookMethodItemLayout l;

		if (convertView == null) {
			l = (CookMethodItemLayout) inflater.inflate(R.layout.has_choose_promotion_dialog_detail, parent, false);
		} else {
			l = (CookMethodItemLayout) convertView;
		}
		
		TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
		TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
		LinearLayout flag_name_layout = (LinearLayout) l.findViewById(R.id.flag_name_layout);
		ImageView delete_hint = (ImageView) l.findViewById(R.id.delete_hint);
		if (id == -1) {
			textViewDishUnitName.setVisibility(View.GONE);
			textViewDishUnitPrice.setVisibility(View.GONE);
			flag_name_layout.setVisibility(View.VISIBLE);
			delete_hint.setVisibility(View.GONE);
			l.setBackground(mContext.getResources().getDrawable(R.drawable.dash_coners_bg));
		}else{
			flag_name_layout.setVisibility(View.GONE);
			textViewDishUnitName.setVisibility(View.VISIBLE);
			textViewDishUnitPrice.setVisibility(View.VISIBLE);
			delete_hint.setVisibility(View.VISIBLE);
			textViewDishUnitName.setText(name);
			textViewDishUnitPrice.setText(OrderUtil.dishPriceFormatter.format(value));
			l.setBackground(mContext.getResources().getDrawable(R.drawable.has_choose_promotion_selector));
		}
		delete_hint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (obj instanceof CashierPromotion) {
					checkPresenterImpl.unDoPromotion((CashierPromotion) obj);
				} else if (obj instanceof CashierDiscount) {
					if (checkPresenterImpl.discountId != -1) {
						checkPresenterImpl.useDiscountRequest(CashierAction.C_UNDODISCOUNT,((CashierDiscount) obj).discountName, SanyiSDK.currentUser.id, checkPresenterImpl.discountId);
					}
				}
			}
		});
		return l;
	}
	
	public void refresh(List<CashierPromotion> promotions, List<CashierDiscount> discounts){
		initData(promotions, discounts);
		notifyDataSetChanged();
	}
}
