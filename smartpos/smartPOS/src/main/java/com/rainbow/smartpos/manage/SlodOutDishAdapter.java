package com.rainbow.smartpos.manage;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.order.OrderDishDetailLayout;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.Units;
import com.rainbow.smartpos.Restaurant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SlodOutDishAdapter extends BaseAdapter {
	private Context mContext;
	private List<ProductRest> allDishes = new ArrayList<ProductRest>();
	private List<ProductRest> selectDishs = new ArrayList<ProductRest>();
	private final int CELL_WIDTH = 80;
	GridView.LayoutParams params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, CELL_WIDTH);
	final LayoutInflater inflater;
	private boolean flag;
	private int number = -1;

	public SlodOutDishAdapter(Context c, boolean flag , int number) {
		mContext = c;
		this.flag = flag;
		this.number = number;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		updateDish();
	}

	public void updateDish() {
		allDishes = new ArrayList<ProductRest>();
		if (flag) {
			if (Restaurant.selectedSloidSubcategory != -2) {
				allDishes.addAll(getProductFromUnit(SanyiSDK.rest.getSubGroupsRest(Restaurant.selectedSloidSubcategory).units));
			} else if (Restaurant.selectedSloidCategory != -2) {
				allDishes.addAll(getProductFromUnit(SanyiSDK.rest.getDishByGoodsGroupId(Restaurant.selectedSloidCategory)));
			} else {
				allDishes.addAll(getProductFromUnit(SanyiSDK.rest.products));
			}
		} else {
			switch (number) {
			case MainScreenActivity.HAS_SLODOUT_FRAGMENT:
				allDishes.addAll(SanyiSDK.rest.getAllSoldDish());
				break;
			case MainScreenActivity.HAS_STOPSALE_FRAGMENT:
				allDishes.addAll(SanyiSDK.rest.getAllStopSellDish());
				break;

			default:
				break;
			}
			
		}
	}
	public void addSelect(int position){
		ProductRest product_ = allDishes.get(position);
		boolean isSelect = false;
		for (int i = 0; i < selectDishs.size(); i++) {
			ProductRest pro = selectDishs.get(i);
			if (pro.id == product_.id) {
				isSelect = true;
				selectDishs.remove(i);
				break;
			}
		}
		if (!isSelect) {
			selectDishs.add(product_);
		}
		notifyDataSetChanged();
	}

	public int getCount() {
		return allDishes.size();
	}

	public ProductRest getItem(int position) {
		return allDishes.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	static class ViewHolder {
		public TextView style;
	}

	public void refresh() {
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ProductRest dish = allDishes.get(position);
		OrderDishDetailLayout l;

		// ImageView i;
		if (convertView == null) {
			l = (OrderDishDetailLayout) inflater.inflate(R.layout.fragment_sliod_dish_detail, parent, false);

		} else {
			l = (OrderDishDetailLayout) convertView;
		}
		TextView textViewDishSliodOut = (TextView) l.findViewById(R.id.textViewDishSliodOut);
		TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishPrice);
		textViewDishUnitName.setVisibility(View.GONE);
		textViewDishSliodOut.setVisibility(View.INVISIBLE);
		// change the background color if dish is already ordered
		// if (Restaurant.isDishOrdered(dish.id)) {
		// l.setDishOrdered();
		// } else {
		// l.setDishUnOrdered();
		// }

		// TODO: need to show spicy level as well
		l.setDishName(dish.name);
		if (dish.isMultiUnitProduct) {
			textViewDishUnitName.setVisibility(View.VISIBLE);
			textViewDishUnitName.setTextColor(Color.parseColor("#ff0000"));
			textViewDishUnitName.setText("("+dish.unitName+")");
		}
		
		// if the table is in service, display the totoal order

		if (dish.isSoldout()) {
			textViewDishSliodOut.setVisibility(View.VISIBLE);
			l.setBackground(mContext.getResources().getDrawable(R.drawable.slod_out_dish_background_s));
			l.setNameTextColor("#9fa9b2");
			if (dish.isLongterm()) {
				textViewDishSliodOut.setText("停售");
			}else if (dish.getSoldoutCount() == 0) {
				textViewDishSliodOut.setText("沽清");
			}else if (dish.getSoldoutCount() > 0) {
				if (dish.productType.isIsWeight()) {
					textViewDishSliodOut.setText("剩余:"+new BigDecimal(dish.getSoldoutCount()).setScale(2,BigDecimal.ROUND_HALF_UP)+dish.unitName);
				}else{
					textViewDishSliodOut.setText("剩余:"+Integer.parseInt(new java.text.DecimalFormat("0").format(dish.getSoldoutCount()))+dish.unitName);
				}
			}
		} else {
			textViewDishSliodOut.setVisibility(View.INVISIBLE);
			l.setBackground(mContext.getResources().getDrawable(R.drawable.dish_detail_bg_selector));
			l.setNameTextColor("#273655");
		}
//		textViewDishSelect.setVisibility(View.INVISIBLE);
//		for (int i = 0; i < selectDishs.size(); i++) {
//			Product_ product_ = selectDishs.get(i);
//			if (product_.id == dish.id) {
//				textViewDishSelect.setVisibility(View.VISIBLE);
//				break;
//			}
//		}
		
		return l;
	}
	
	public List<ProductRest> getProductFromUnit(List<Units> units){
		List<ProductRest> productRests = new ArrayList<ProductRest>();
		for (Units unit : units) {
			if (unit.products.size()>1) {
				for (ProductRest productRest : unit.products) {
					productRest.isMultiUnitProduct = true;
					productRests.add(productRest);
				}
			}else{
				productRests.addAll(unit.products);
			}
		}
		return productRests;
	}

}
