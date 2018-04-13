package com.rainbow.smartpos.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.GoodsSet.SetItems.SetItemDetails;
import com.sanyipos.sdk.utils.OrderUtil;

public class SetItemDishAdapter extends BaseAdapter {
	private Context mContext;
	private List<SetItemDetails> setItems = new ArrayList<SetItemDetails>();
	int CELL_WIDTH = 80;
	GridView.LayoutParams params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, CELL_WIDTH);
	public LayoutInflater inflater;
	private Map<Integer,Integer> map=new HashMap<>();
	private int groupposition;

	public int selection = -1;

	public SetItemDishAdapter(Context c, List<SetItemDetails> setItems,Map<Integer,Integer> map,int groupposition) {
		mContext = c;
		this.setItems = setItems;
		this.map=map;
		this.groupposition=groupposition;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	public int getCount() {
		return setItems.size();
	}

	public SetItemDetails getItem(int position) {
		return setItems.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public void refresh() {
		notifyDataSetChanged();
	}

	public void setSelection(int position) {
		this.selection = position;
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		SetItemDetails detail = setItems.get(position);
//		Boolean selection = detail.isIsDefault();
		CookMethodItemLayout l;
		if (convertView == null) {
			l = (CookMethodItemLayout) inflater.inflate(R.layout.choose_set_item_detail, parent, false);

		} else {
			l = (CookMethodItemLayout) convertView;
		}
		if (position==map.get(groupposition)) {
			l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_gird_item_bg_single));
		}else{
			l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_gridview_item_bg_normal));
		}
		TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
		TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
		textViewDishUnitName.setText(detail.getName());
		if (detail.isMulti() && null != detail.getUnitTypeName()) {
			textViewDishUnitName.setText(textViewDishUnitName.getText()+"("+detail.getUnitTypeName()+")");
		}
		textViewDishUnitPrice.setText(OrderUtil.dishPriceFormatter.format(detail.getPlusPrice())+" x"+detail.getQuantity());
		return l;
	}
}
