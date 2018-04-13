package com.rainbow.smartpos.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngridientFragment extends Fragment {
	public TextView textView_order_fragment_ingridient;
	public Button buttonContinueOrder;
	public Button buttonOrderItemOptionConfirm;
	public GridView gridView_order_fragment_ingridient;
	public OrderDetail order;
	private IngridientAdapter ingridientAdapter;
	public MainScreenActivity activity;
	public IngridientFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public void setCurrentOrder(OrderDetail order) {
		this.order = order;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_order_dish_ingrient, container, false);
		activity = (MainScreenActivity) getActivity();
		textView_order_fragment_ingridient = (TextView) v.findViewById(R.id.textView_order_fragment_ingridient);
		textView_order_fragment_ingridient.setText(order.getName());
		gridView_order_fragment_ingridient = (GridView) v.findViewById(R.id.gridView_order_fragment_ingridient);
		ingridientAdapter = new IngridientAdapter(getActivity());
		gridView_order_fragment_ingridient.setAdapter(ingridientAdapter);
		gridView_order_fragment_ingridient.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
				int count = 1;
				List<Map<String, Integer>> list = ingridientAdapter.getSeclected();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).get("position") == position) {
						count = list.get(i).get("count");
					}
				}
				new IngridientCountDialog(getActivity(), count, new IngridientCountDialog.ChangeIngridientCountListener() {
					@Override
					public void onSuccess(int count) {
						// TODO Auto-generated method stub
						ingridientAdapter.addSeclected(position, count);
						ingridientAdapter.notifyDataSetChanged();
					}
				}).show();
			}
		});
		buttonContinueOrder = (Button) v.findViewById(R.id.buttonContinueOrder);
		buttonContinueOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				activity.orderFragment.removeOrderitemOptionFragment();
//				activity.orderFragment.orderListAdpater.notifyDataSetChanged();
			}
		});
		buttonOrderItemOptionConfirm = (Button) v.findViewById(R.id.buttonOrderItemOptionConfirm);
		buttonOrderItemOptionConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				order.ingredients.clear();
				List<Map<String, Integer>> sList = ingridientAdapter.getSeclected();
				for (int i = 0; i < sList.size(); i++) {
					OrderDetail ingridient = new OrderDetail();
					ProductRest obj = SanyiSDK.rest.ingredients.get(sList.get(i).get("position"));
					ingridient.setProductId(obj.id);
					ingridient.setGoodsId(obj.goods);
					ingridient.setName(obj.name);
					ingridient.setOriginPrice(obj.price);
					ingridient.setUnitCount(sList.get(i).get("count"));
					ingridient.setQuantity(order.getQuantity() * ingridient.getUnitCount());
					ingridient.setVoid_quantity(order.getVoid_quantity() * ingridient.getUnitCount());
					ingridient.setCurrentPrice(obj.price);
					order.addIngredient(ingridient);

				}
				//MainScreenActivity.getOrderFragment().refreshOrderList();
//				activity.orderFragment.removeOrderitemOptionFragment();
			}
		});
		return v;
	}

	public class IngridientAdapter extends BaseAdapter {
		public Context context;
		public LayoutInflater inflater;
		public List<Map<String, Integer>> sList = new ArrayList<Map<String, Integer>>();

		public IngridientAdapter(Context context) {
			this.context = context;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			initList();
		}

		private void initList() {
			// TODO Auto-generated method stub
			if (order.getIngredient() != null) {
				List<OrderDetail> a = order.getIngredient();
				for (int j = 0; j < SanyiSDK.rest.ingredients.size(); j++) {
					for (int i = 0; i < a.size(); i++) {
						Map<String, Integer> map = new HashMap<String, Integer>();
						if (SanyiSDK.rest.ingredients.get(j).goods == a.get(i).getGoodsId()) {
							map.put("position", j);
							map.put("count", a.get(i).getUnitCount());
							sList.add(map);
						}
					}
				}
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return SanyiSDK.rest.ingredients.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.fragment_order_dish_detail, parent, false);
			view.setBackground(getResources().getDrawable(R.drawable.orderdish_background_ser));
			TextView textViewDishDetailName = (TextView) view.findViewById(R.id.textViewDishDetailName);
			textViewDishDetailName.setText(SanyiSDK.rest.ingredients.get(position).name);
			TextView textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
			textViewDishPrice.setText(Double.toString(SanyiSDK.rest.ingredients.get(position).price));
//			TextView textViewDishOrdered = (TextView) view.findViewById(R.id.textViewDishOrdered);
//			for (Map<String, Integer> map : sList) {
//				if (map.get("position") == position) {
//					textViewDishOrdered.setText(map.get("count") + "份");
//					textViewDishOrdered.setVisibility(View.VISIBLE);
//				}
//			}
			return view;
		}

		public void addSeclected(int position, int count) {
			// if (!sList.contains(Integer.toString(position))) {
			// sList.add(Integer.toString(position));
			// } else {
			// sList.remove(Integer.toString(position));
			// }
			boolean flag = false;
			for (int i = 0; i < sList.size(); i++) {
				Map<String, Integer> map = sList.get(i);
				if (map.get("position") == position) {
					flag = true;
					if (count == 0) {
						sList.remove(i);
					} else {
						map.put("position", position);
						map.put("count", count);
					}
				}
			}
			if (!flag) {
				Map<String, Integer> new_map = new HashMap<String, Integer>();
				new_map.put("position", position);
				new_map.put("count", count);
				sList.add(new_map);
			}

		}

		public List<Map<String, Integer>> getSeclected() {
			for (int i = 0; i < sList.size(); i++) {
				Map<String, Integer> map = sList.get(i);
				if (map.get("position") == -1) {
					sList.remove(i);
				}
			}
			return sList;
		}
	}

}
