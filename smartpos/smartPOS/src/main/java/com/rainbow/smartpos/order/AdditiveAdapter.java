package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class AdditiveAdapter extends BaseAdapter {
	private List<OrderDetail> displayDishs = new ArrayList<OrderDetail>();
	private Context mContext;
	private LayoutInflater inflater;
	private int flag;
	private OrderDetail orderDetail;
	

	public AdditiveAdapter(Context context, OrderDetail orderDetail, int flag) {
		this.mContext = context;
		this.flag = flag;
		this.orderDetail = orderDetail;
		initData();
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	private void initData(){
		displayDishs.clear();
		switch (flag) {
		case OrderOptionDialog.COOK_METHOD:
			initCookMethod(orderDetail);
			break;
		case OrderOptionDialog.INGREDIENT:
			initIngredient(orderDetail);
			break;
		case OrderOptionDialog.PRIVATE_COOK_METHOD:
			initPrivateCookMethod(orderDetail);
		default:
			break;
		}
	}

	private void initPrivateCookMethod(OrderDetail orderDetail) {

	}

	public void initCookMethod(OrderDetail orderDetail){
		displayDishs.addAll(orderDetail.getPublicCookMethod());
		OrderDetail detail = new OrderDetail();
		detail.setId(-1);
		detail.setName(mContext.getString(R.string.cook_method));
		displayDishs.add(detail);
		notifyDataSetChanged();
	}
	
	public void initIngredient(OrderDetail orderDetail){
		displayDishs.addAll(orderDetail.getIngredient());
		OrderDetail detail = new OrderDetail();
		detail.setId(-1);
		detail.setName(mContext.getString(R.string.op_menu_ingredient));
		displayDishs.add(detail);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return displayDishs.size();
	}

	@Override
	public OrderDetail getItem(int position) {
		// TODO Auto-generated method stub
		return displayDishs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		OrderDetail dish = displayDishs.get(position);

		CookMethodItemLayout l;

		if (convertView == null) {
			l = (CookMethodItemLayout) inflater.inflate(R.layout.choose_cookmethod_dialog_detail, parent, false);

		} else {
			l = (CookMethodItemLayout) convertView;
			// i = (ImageView) l.getChildAt(0);
		}
		
		TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
		TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
		textViewDishUnitName.setTextColor(mContext.getResources().getColor(R.color.White));
		textViewDishUnitPrice.setTextColor(mContext.getResources().getColor(R.color.White));
		LinearLayout flag_name_layout = (LinearLayout) l.findViewById(R.id.flag_name_layout);
		ImageView delete_hint = (ImageView) l.findViewById(R.id.delete_hint);
		//CheckBox textViewDishSelect = (CheckBox) l.findViewById(R.id.textViewDishSelect);
		if (dish.getId() == -1) {
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
			textViewDishUnitName.setText(dish.getName());
			textViewDishUnitPrice.setText(OrderUtil.dishPriceFormatter.format(dish.getOriginPrice())+"  x"+dish.getQuantity()/(orderDetail.getQuantity()-orderDetail.getVoid_quantity()));
//			textViewDishUnitPrice.setText(OrderUtil.dishPriceFormatter.format(dish.getOriginPrice())+"  x"+dish.getUnitCount());
			l.setBackground(mContext.getResources().getDrawable(R.drawable.has_choose_promotion_selector));
		}
		return l;
	}
	
	public void refresh(OrderDetail orderDetail){
		this.orderDetail = orderDetail;
		initData();
	}
	public void remove(int position){
		switch (flag) {
		case OrderOptionDialog.COOK_METHOD:
			removeCookMethod(position);
			break;
		case OrderOptionDialog.INGREDIENT:
			removeIngredient(position);
			break;

		default:
			break;
		}
		notifyDataSetChanged();
	}

	private void removeIngredient(int position) {
		// TODO Auto-generated method stub
		OrderDetail ingredient = orderDetail.ingredients.get(position);
		int unit_count = ingredient.getUnitCount();
		if (unit_count > 1) {
			unit_count--;
			ingredient.setUnitCount(unit_count);
		}else{
			orderDetail.ingredients.remove(position);
		}
		initData();
	}

	private void removeCookMethod(int position) {
		// TODO Auto-generated method stub
		displayDishs.remove(position);
		orderDetail.publicCookMethod.remove(position);
	}
}
