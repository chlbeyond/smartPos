package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.ProductRest;

import java.math.BigDecimal;
import java.util.List;

public class OrderSizeListAdapter extends BaseAdapter {

	public List<ProductRest> dishs;
	public Context mContext;
	public LayoutInflater inflater;
	private int currentPosition = -1;

	public OrderSizeListAdapter(Context context, List<ProductRest> dishs) {
		this.dishs = dishs;
		this.mContext = context;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		ProductRest dish = dishs.get(position);

		OrderDishDetailLayout l;

		if (convertView == null) {
			l = (OrderDishDetailLayout) inflater.inflate(R.layout.choose_unit_dialog_detail, parent, false);
			// l.setLayoutParams(new ViewGroup.LayoutParams(50, 50));

		} else {
			l = (OrderDishDetailLayout) convertView;
			// i = (ImageView) l.getChildAt(0);
		}
		TextView textViewDishSliodOut = (TextView) l.findViewById(R.id.textViewDishSliodOut);
		l.setDishName(dish.unitName);
		l.setBackground(mContext.getResources().getDrawable(R.drawable.orderdish_background_ser));
		if (dish.soldout) {
			textViewDishSliodOut.setVisibility(View.VISIBLE);
			l.setBackground(mContext.getResources().getDrawable(R.drawable.slod_out_dish_background_s));
			l.setNameTextColor("#9fa9b2");
			if (dish.isLongterm()) {
				textViewDishSliodOut.setText("停售");
			} else if (dish.getSoldoutCount() == 0) {
				textViewDishSliodOut.setText("沽清");
			} else if (dish.getSoldoutCount() > 0) {
				l.setBackground(mContext.getResources().getDrawable(R.drawable.orderdish_background_ser));
//				l.setTextColor("#ffffff");
				if (dish.productType.isIsWeight()) {
					textViewDishSliodOut.setText(new BigDecimal(dish.getSoldoutCount()).setScale(2, BigDecimal.ROUND_HALF_UP) + dish.unitName);
				} else {
					textViewDishSliodOut.setText(String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(dish.getSoldoutCount()))) + dish.unitName);
				}

			}
		} else {
			textViewDishSliodOut.setVisibility(View.GONE);
			l.setBackground(mContext.getResources().getDrawable(R.drawable.orderdish_background_ser));
//			l.setTextColor("#ffffff");
		}
		
		if(currentPosition == position){
			l.setBackground(mContext.getResources().getDrawable(R.drawable.orderdish_background_ser_select));
		}else{
			l.setBackgroundResource(R.drawable.orderdish_background_ser);
		}
		return l;
	}
	public void setSelect(int position){
		currentPosition = position;
		notifyDataSetChanged();
	}
	public ProductRest getSelect(int position){
		if (position >= 0) {
			return dishs.get(position);
		}
		return null;
		
	}

}
