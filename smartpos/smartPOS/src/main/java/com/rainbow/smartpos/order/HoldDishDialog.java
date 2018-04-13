package com.rainbow.smartpos.order;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.utils.OrderUtil;

public class HoldDishDialog {
	public static interface IHoldDishListener {
		public void sure();
	}

	private TextView selectAll;
	private TextView sure_btn;
	private MainScreenActivity mAct;
	private List<OrderDetail> orderList;
	private IHoldDishListener listener;
	private HoldDishAdapter adapter;
	private Dialog dialog;
	private boolean isSelectAll = false;
	public List<OrderDetail> orderDetailPlaceList = new ArrayList<OrderDetail>();

	public HoldDishDialog(MainScreenActivity context, List<OrderDetail> orderList, IHoldDishListener listener) {
		// TODO Auto-generated constructor stub
		mAct = context;
		this.orderList = orderList;
		initData();
		this.listener = listener;
	}

	public void show() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mAct);
		View view = LayoutInflater.from(mAct).inflate(R.layout.hold_dish_dialog, null);
		GridView mFoodUnitGridView = (GridView) view.findViewById(R.id.operationGridView);
		selectAll = (TextView) view.findViewById(R.id.selectAll);
		selectAll.setOnClickListener(onClickListener);
		view.findViewById(R.id.sure_btn).setOnClickListener(onClickListener);
		view.findViewById(R.id.iv_close_dialog).setOnClickListener(onClickListener);
		adapter = new HoldDishAdapter();
		mFoodUnitGridView.setAdapter(adapter);
		mFoodUnitGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				adapter.addSeclected(pos);
			}
		});
		builder.setView(view);
		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void initData() {
		// TODO Auto-generated method stub
		for (OrderDetail order : orderList) {
			if (!order.isPlaced() && !order.isHold()) {
				orderDetailPlaceList.add(order);
			}
		}

	}

	public class HoldDishAdapter extends BaseAdapter {
		public LayoutInflater inflater;
		public List<String> sList = new ArrayList<String>();

		public HoldDishAdapter() {
			this.inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
			notifyDataSetChanged();
		}

		public List<String> getSeclected() {
			return sList;
		}

		public void selectAll() {
			sList = new ArrayList<String>();
			for (int i = 0; i < orderDetailPlaceList.size(); i++) {
				sList.add(Integer.toString(i));
			}
			notifyDataSetChanged();
		}

		public void removeAll() {
			sList = new ArrayList<String>();
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.hold_dish_detail_item, parent, false);
			view.setBackground(mAct.getResources().getDrawable(R.drawable.orderdish_background_ser));
			TextView textViewDishDetailName = (TextView) view.findViewById(R.id.textViewDishDetailName);
			OrderDetail orderDetail = orderDetailPlaceList.get(position);
			textViewDishDetailName.setText(orderDetail.getName());
			if (orderDetail.isMultiUnitProduct && null != orderDetail.getUnitName()) {
				textViewDishDetailName.setText(orderDetail.getName() + "(" + orderDetail.getUnitName() + ")");
			}
			TextView textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
			textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(orderDetailPlaceList.get(position).getCurrentPrice()));
			CheckBox checkBoxDishChoose = (CheckBox) view.findViewById(R.id.checkBoxDishChoose);
			checkBoxDishChoose.setChecked(false);
			for (String s : sList) {
				if (Integer.valueOf(s) == position) {
					checkBoxDishChoose.setChecked(true);
				}
			}
			return view;

		}
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.selectAll:
				if (isSelectAll) {
					isSelectAll = false;
					adapter.removeAll();
					selectAll.setText(mAct.getResources().getString(R.string.choose_all));
				} else {
					isSelectAll = true;
					selectAll.setText(mAct.getResources().getString(R.string.cancel_choose_all));
					adapter.selectAll();
				}
				break;
			case R.id.sure_btn:
				if (adapter.getSeclected().size() > 0) {
					for (int i = 0; i < orderDetailPlaceList.size(); i++) {
						for (int j = 0; j < adapter.getSeclected().size(); j++) {
							if (i == Integer.valueOf(adapter.getSeclected().get(j))) {
								orderDetailPlaceList.get(i).setHold(true);
							}
						}
					}
					listener.sure();
					dialog.dismiss();
				} else {
					Toast.makeText(mAct, "尚未选择菜品", Toast.LENGTH_SHORT).show();
					return;
				}

				break;
			case R.id.iv_close_dialog:
				dialog.dismiss();
				break;

			default:
				break;
			}
		}
	};
}
