package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends BaseAdapter {

	public List<ProductRest> displayDishs = new ArrayList<ProductRest>();
	public Context mContext;
	public LayoutInflater inflater;
	private List<String> selectPos = new ArrayList<String>();
	private List<OrderDetail> ingredients = new ArrayList<OrderDetail>();

	public IngredientAdapter(Context context) {
		this.mContext = context;
		initData();
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private void initData() {
		displayDishs.clear();
		List<ProductRest> ingredients = new ArrayList<ProductRest>();
		ingredients.addAll(SanyiSDK.rest.ingredients);
		if (ingredients.size() > 5) {
			for (int i = 0; i < 4; i++) {
				displayDishs.add(ingredients.get(i));
			}
			ProductRest product = new ProductRest();
			product.id = -1;
			product.name = mContext.getResources().getString(R.string.more);
			displayDishs.add(product);
		} else {
			displayDishs.addAll(ingredients);
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

		CookMethodItemLayout l;

		if (convertView == null) {
			l = (CookMethodItemLayout) inflater.inflate(R.layout.choose_cookmethod_dialog_detail, parent, false);

		} else {
			l = (CookMethodItemLayout) convertView;
			// i = (ImageView) l.getChildAt(0);
		}
		l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_gridview_item_bg_normal));
		TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
		TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
		//CheckBox textViewDishSelect = (CheckBox) l.findViewById(R.id.textViewDishSelect);
		if (dish.id == -1) {
			textViewDishUnitName.setVisibility(View.GONE);
			textViewDishUnitPrice.setVisibility(View.GONE);
			//textViewDishSelect.setVisibility(View.GONE);
			l.setBackground(mContext.getResources().getDrawable(R.drawable.orderdish_background_ser));
		} else {
			textViewDishUnitName.setText(dish.name);
			textViewDishUnitPrice.setText(OrderUtil.dishPriceFormatter.format(dish.price));
			//textViewDishSelect.setChecked(false);
			for (int i = 0; i < selectPos.size(); i++) {
				if (selectPos.get(i).equals(Integer.toString(position))) {
				//	textViewDishSelect.setChecked(true);
					l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_grid_item_bg_multiple));
				}
			}
		}
		return l;
	}

	public void setSelect(String position) {
		if (selectPos.contains(position)) {
			for (int i = 0; i < selectPos.size(); i++) {
				if (selectPos.get(i).equals(position)) {
					selectPos.remove(i);
				}
			}
		} else {
			selectPos.add(position);
		}
		notifyDataSetChanged();
	}

	public List<ProductRest> getSelects() {
		List<ProductRest> selectCookMethods = new ArrayList<ProductRest>();
		for (int i = 0; i < selectPos.size(); i++) {
			int pos = Integer.parseInt(selectPos.get(i));
			selectCookMethods.add(displayDishs.get(pos));
		}
		return selectCookMethods;
	}

}
