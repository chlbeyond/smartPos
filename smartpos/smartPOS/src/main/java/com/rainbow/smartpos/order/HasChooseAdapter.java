package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;

import java.util.ArrayList;
import java.util.List;

public class HasChooseAdapter extends BaseAdapter {
	public Context mContext;
	public LayoutInflater inflater;
	private List<ProductRest> dishs = new ArrayList<ProductRest>();;
	private int flag;
	private OrderDetail orderDetail;
	public HasChooseAdapter(Context context, OrderDetail orderDetail, int flag) {
		this.mContext = context;
		this.orderDetail = orderDetail;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.flag = flag;
		initData();
	}
	private void initData(){
		dishs.clear();
		switch (flag) {
		case OrderOptionDialog.COOK_METHOD:
			for (ProductRest productRest: SanyiSDK.rest.publicCooks) {


			}
			break;
		case OrderOptionDialog.INGREDIENT:
			for (ProductRest productRest: SanyiSDK.rest.ingredients) {


			}
			break;

		default:
			break;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dishs.size();
	}

	@Override
	public ProductRest getItem(int position) {
		// TODO Auto-generated method stub
		return dishs.get(position);
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
			l = (CookMethodItemLayout) inflater.inflate(R.layout.choose_cookmethod_dialog_detail, parent, false);

		} else {
			l = (CookMethodItemLayout) convertView;
		}
		ProductRest dish = dishs.get(position);
		l.setBackground(mContext.getResources().getDrawable(R.drawable.dish_detail_bg_selector));
		TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
		TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
		textViewDishUnitName.setTextColor(mContext.getResources().getColor(R.color.White));
		textViewDishUnitPrice.setTextColor(mContext.getResources().getColor(R.color.White));
		ImageView delete_hint = (ImageView) l.findViewById(R.id.delete_hint);
		delete_hint.setVisibility(View.VISIBLE);
		textViewDishUnitName.setText(dish.name);
		l.setBackground(mContext.getResources().getDrawable(R.drawable.has_choose_promotion_selector));
//		textViewDishUnitPrice.setText(OrderUtil.dishPriceFormatter.format(dish.getOriginPrice())+"  x"+dish.count);
		return l;
	}
	
	public void setSelect(int position){
		ProductRest dish = dishs.get(position);
		refresh();
	}
	public void refresh(){
		initData();
		notifyDataSetChanged();
	}
	public void setCooks(){
		orderDetail.publicCookMethod.clear();
		for (ProductRest cook : dishs) {
			OrderDetail o = new OrderDetail();
            o.setProductId(cook.id);
            o.setGoodsId(cook.goods);
            o.setName(cook.name);
            o.setOriginPrice(cook.price);
            o.setQuantity(orderDetail.getQuantity());
            o.setVoid_quantity(0);
            o.setCurrentPrice(cook.price);
            orderDetail.addPublicCookMethod(o); 
		}
	}
	public void setIngredients(){
		orderDetail.ingredients.clear();
		List<OrderDetail> ingredients = new ArrayList<OrderDetail>();
		for (ProductRest product : dishs) {
			OrderDetail ingridient = new OrderDetail();
			ingridient.setProductId(product.id);
			ingridient.setGoodsId(product.goods);
			ingridient.setName(product.name);
			ingridient.setOriginPrice(product.originPrice);
			ingridient.setVoid_quantity(0);
			ingridient.setCurrentPrice(product.price);
			orderDetail.addIngredient(ingridient);
		}
	}
}
