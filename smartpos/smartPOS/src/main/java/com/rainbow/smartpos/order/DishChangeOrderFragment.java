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
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class DishChangeOrderFragment extends Fragment implements OnClickListener {
	private View view;
	public Button buttonOrderItemOptionAllChangeConfirm;
	public Button buttonOrderItemOptionChangeConfirm;
	public Button buttonContinueOrder;
	public ChangeDishAdapter changeDishAdapter;
	public GridView gridView_order_fragment_change;
	public List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
	public List<OrderDetail> orderDetailPlaceList = new ArrayList<OrderDetail>();
	public MainScreenActivity activity;
	public void setOrderList(List<OrderDetail> orderList) {
		this.orderDetailList = orderList;

	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		orderDetailPlaceList.clear();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.batch_change_dish, container, false);
		activity = (MainScreenActivity) getActivity();
		initView();
		return view;
	}

	public void initView() {
		gridView_order_fragment_change = (GridView) view.findViewById(R.id.gridView_order_fragment_change);
		buttonOrderItemOptionAllChangeConfirm = (Button) view.findViewById(R.id.buttonOrderItemOptionAllChangeConfirm);
		buttonOrderItemOptionAllChangeConfirm.setOnClickListener(this);
		buttonOrderItemOptionChangeConfirm = (Button) view.findViewById(R.id.buttonOrderItemOptionChangeConfirm);
		buttonOrderItemOptionChangeConfirm.setOnClickListener(this);
		buttonContinueOrder = (Button) view.findViewById(R.id.buttonContinueOrder);
		buttonContinueOrder.setOnClickListener(this);
		changeDishAdapter = new ChangeDishAdapter();
		initData();
		initButton();
		gridView_order_fragment_change.setAdapter(changeDishAdapter);
		gridView_order_fragment_change.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				changeDishAdapter.addSeclected(position);
				changeDishAdapter.notifyDataSetChanged();
				initButton();
			}
		});
	}

	public void clearChangeDishSelect() {
		changeDishAdapter.clearSelected();
		changeDishAdapter.notifyDataSetChanged();
	}

	private void initButton() {
		// TODO Auto-generated method stub
		if (orderDetailPlaceList.size() > 0) {
			buttonOrderItemOptionAllChangeConfirm.setEnabled(true);
		} else {
			buttonOrderItemOptionAllChangeConfirm.setEnabled(false);
		}
		if (changeDishAdapter.getSeclected().size() > 0) {
			buttonOrderItemOptionChangeConfirm.setEnabled(true);
		} else {
			buttonOrderItemOptionChangeConfirm.setEnabled(false);
		}
	}

	public void initData() {
		// TODO Auto-generated method stub
		for (OrderDetail order : orderDetailList) {
			if (order.isPlaced() && order.getQuantity() > order.getVoid_quantity()) {
				orderDetailPlaceList.add(order);
			}
		}

	}

	public class ChangeDishAdapter extends BaseAdapter {

		public LayoutInflater inflater;
		public List<String> sList = new ArrayList<String>();

		public ChangeDishAdapter() {
			this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return orderDetailPlaceList.size();
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

		public void addSeclected(int positon) {
			if (!sList.contains(Integer.toString(positon))) {
				sList.add(Integer.toString(positon));
			} else {
				sList.remove(Integer.toString(positon));
			}
		}
		
		public void selectAll(){
			sList.clear();
			for (int i = 0; i < orderDetailPlaceList.size(); i++) {
				sList.add(Integer.toString(i));
			}
		}

		public void clearSelected() {
			sList.removeAll(sList);
		}

		public List<String> getSeclected() {
			return sList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.fragment_order_dish_detail, parent, false);
			view.setBackground(getResources().getDrawable(R.drawable.gridview_item_selector1));
			TextView textViewDishDetailName = (TextView) view.findViewById(R.id.textViewDishDetailName);
			textViewDishDetailName.setText(orderDetailPlaceList.get(position).getName());
			TextView textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
			textViewDishPrice.setText(Double.toString(orderDetailPlaceList.get(position).getCurrentPrice()));
//			TextView textViewDishOrdered = (TextView) view.findViewById(R.id.textViewDishOrdered);
//			for (String s : sList) {
//				if (Integer.valueOf(s) == position) {
//					textViewDishOrdered.setText("√");
//					textViewDishOrdered.setVisibility(View.VISIBLE);
//				}
//			}
			return view;

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonOrderItemOptionAllChangeConfirm:
		case R.id.buttonOrderItemOptionChangeConfirm:
			if (v.getId() == R.id.buttonOrderItemOptionAllChangeConfirm) {
				changeDishAdapter.selectAll();
				changeDishAdapter.notifyDataSetChanged();
				if (orderDetailPlaceList.size() > 0) {
						//MainScreenActivity.getOrderFragment().changeDish(orderDetailPlaceList);
				} else {
					Toast.makeText(getActivity(), "尚未选择菜品", Toast.LENGTH_SHORT).show();
					return;
				}
			} else {
				if (changeDishAdapter.getSeclected().size() > 0) {
					List<OrderDetail> order = new ArrayList<OrderDetail>();
					for (int i = 0; i < orderDetailPlaceList.size(); i++) {
						for (int j = 0; j < changeDishAdapter.getSeclected().size(); j++) {
							if (i == Integer.valueOf(changeDishAdapter.getSeclected().get(j))) {
								order.add(orderDetailPlaceList.get(i));
							}
						}
					}
					//MainScreenActivity.getOrderFragment().changeDish(order);
				} else {
					Toast.makeText(getActivity(), "尚未选择菜品", Toast.LENGTH_SHORT).show();
					return;
				}

			}
			break;
		case R.id.buttonContinueOrder:
			//MainScreenActivity.getOrderFragment().refreshOrderList();
//			activity.orderFragment.removeOrderitemOptionFragment();
			break;

		default:
			break;
		}
	}

}
