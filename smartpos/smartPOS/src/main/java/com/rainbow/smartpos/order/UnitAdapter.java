package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class UnitAdapter extends BaseAdapter {

	public List<ProductRest> displayDishs = new ArrayList<ProductRest>();
	public Context mContext;
	public LayoutInflater inflater;
	private int currentPosition = 0;

	public UnitAdapter(Context context, List<ProductRest> dishs) {
		this.mContext = context;
		initData(dishs);
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	private void initData(List<ProductRest> dishs){
		displayDishs.clear();
		if (dishs.size()>5) {
			for (int i = 0; i < 4; i++) {
				displayDishs.add(dishs.get(i));
			}
			ProductRest productRest = new ProductRest();
			productRest.id = -1;
			productRest.name = mContext.getResources().getString(R.string.more);
			displayDishs.add(productRest);
		}else{
			displayDishs.addAll(dishs);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return displayDishs.size();
	}

	@Override
	public ProductRest getItem(int position) {
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
		ProductRest dish = displayDishs.get(position);

		OrderDishDetailLayout l;

		if (convertView == null) {
			l = (OrderDishDetailLayout) inflater.inflate(R.layout.choose_unit_dialog_detail, parent, false);
			

		} else {
			l = (OrderDishDetailLayout) convertView;
			// i = (ImageView) l.getChildAt(0);
		}
		TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
		TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
		//RadioButton textViewDishSelect = (RadioButton) l.findViewById(R.id.textViewDishSelect);
		l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_gridview_item_bg_normal));
		if (dish.id == -1) {
			textViewDishUnitName.setVisibility(View.GONE);
			textViewDishUnitPrice.setVisibility(View.GONE);
			//textViewDishSelect.setVisibility(View.GONE);
		}else{
			textViewDishUnitName.setText(dish.unitName);
			textViewDishUnitPrice.setText(OrderUtil.dishPriceFormatter.format(dish.price));
			//textViewDishSelect.setChecked(false);
			if (position == currentPosition) {
				//textViewDishSelect.setChecked(true);
				l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_gird_item_bg_single));
			}
		}
		return l;
	}

	public void setSelect(int position) {
		currentPosition = position;
		notifyDataSetChanged();
	}

	public ProductRest getSelect(int position) {
		if (position >= 0) {
			return displayDishs.get(position);
		}
		return null;
	}

}
