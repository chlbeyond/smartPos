package com.rainbow.smartpos.bill;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.utils.OrderUtil;

public class BillDetailAdapter extends BaseAdapter {
	private Activity activity;
	private static LayoutInflater inflater = null;
	List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
	private double currentOrderTotal = 0;
	private int currentOrderCount = 0;
	private int numOfSetOrderItems = 0;

	public BillDetailAdapter(Activity a) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public double getOrderTotal() {
		currentOrderTotal = 0;
		for (int i = 0; i < orderDetails.size(); i++) {
			OrderDetail order = orderDetails.get(i);
			currentOrderTotal = currentOrderTotal + order.getQuantity() * order.getCurrentPrice();
			for (int j = 0; j < order.getIngredient().size(); j++) {
				OrderDetail ingridient = order.getIngredient().get(j);
				currentOrderTotal = currentOrderTotal + ingridient.getCurrentPrice();
			}
		}
		return currentOrderTotal;
	}

	public int getOrderCount() {
		currentOrderCount = 0;
		for (int i = 0; i < orderDetails.size(); i++) {
			OrderDetail order = orderDetails.get(i);
			currentOrderCount = currentOrderCount + order.getQuantity();
		}
		return currentOrderCount;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderDetails.size();
	}

	@Override
	public OrderDetail getItem(int position) {
		return orderDetails.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(OrderDetail object) {
		orderDetails.remove(object);
		// object.delete();
		notifyDataSetChanged();
	}

	public OrderDetail getOrderById(long orderId) {
		OrderDetail order = null;
		for (int i = 0; i < orderDetails.size(); i++) {
			order = orderDetails.get(i);
			if (order.getId() == orderId) {
				break;
			}
		}
		return order;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.check_order_row, null);
		OrderDetail currentOrderDetail = orderDetails.get(position);

		// add header if it is first order detail in order and there are more
		// than two order(table)
		// in the same bill
		LinearLayout orderHeaderContainer = (LinearLayout) vi.findViewById(R.id.linearLayoutOrderListHeader);
		orderHeaderContainer.removeAllViews();
		LinearLayout ingredientLayoutContainer = (LinearLayout) vi.findViewById(R.id.linearLayoutCheckOrderIngredient);
		ingredientLayoutContainer.removeAllViews();
		int ingredientId = 100000;
		if (currentOrderDetail.getIngredient().size() > 0) {

			for (int i = 0; i < currentOrderDetail.getIngredient().size(); i++) {
				// add ingredient if any
				OrderDetail ingredient = currentOrderDetail.getIngredient().get(i);
				LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
				ingredientLayout.setId(ingredientId + i);

				// set ingredient name
				TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderDishName);
				ingredientName.setText(ingredient.getName());

				// set ingredient price
				TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderPrice);
				double price = ingredient.getOriginPrice();
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(price));

				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int quantity = ingredient.getQuantity();
				ingredientQuantity.setText(String.valueOf(quantity));

				// set ingredient price
				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
				double currentPrice = ingredient.getCurrentPrice();
				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(currentPrice * quantity));

				ingredientLayoutContainer.addView(ingredientLayout);
			}
		}

		int giftOrderId = 200000;
		if (currentOrderDetail.getGiftOrders().size() > 0) {

			for (int i = 0; i < currentOrderDetail.getGiftOrders().size(); i++) {
				// add ingredient if any
				OrderDetail giftOrder = currentOrderDetail.getGiftOrders().get(i);
				LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
				ingredientLayout.setId(giftOrderId + i);

				TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderDishName);
				ingredientName.setText("(活动) " + giftOrder.getName());

				// set ingredient price
				TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderPrice);
				double price = giftOrder.getOriginPrice();
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(price));

				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int quantity = giftOrder.getQuantity();
				ingredientQuantity.setText(String.valueOf(quantity));

				// set ingredient price
				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
				double currentPrice = giftOrder.getCurrentPrice();
				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(currentPrice * quantity));

				ingredientLayoutContainer.addView(ingredientLayout);
			}
		}

		int setOrderId = 300000;
		if (currentOrderDetail.getSetOrderDetailList().size() > 0) {

			for (int i = 0; i < currentOrderDetail.getSetOrderDetailList().size(); i++) {
				// add ingredient if any
				OrderDetail setOrder = currentOrderDetail.getSetOrderDetailList().get(i);
				LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
				ingredientLayout.setId(setOrderId + i);

				// set ingredient name
				TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderDishName);
				ingredientName.setText("(道" + String.valueOf(i + 1) + ") " + setOrder.getName());

				// set ingredient price
				TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderPrice);
				double price = setOrder.getOriginPrice();
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(price));

				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int quantity = setOrder.getQuantity();
				ingredientQuantity.setText(String.valueOf(quantity));

				// set ingredient price
				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
				double currentPrice = setOrder.getCurrentPrice();
				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(currentPrice * quantity));

				ingredientLayoutContainer.addView(ingredientLayout);
			}
		}
		TextView orderRowNo = (TextView) vi.findViewById(R.id.textViewCheckOrdeRowNo);
		orderRowNo.setText(Integer.toString(currentOrderDetail.getSortOrder()));

		TextView orderDishName = (TextView) vi.findViewById(R.id.textViewCheckOrderDishName);
		
		if (currentOrderDetail.isMultiUnitProduct && null != currentOrderDetail.getUnitName()) {
			orderDishName.setText(currentOrderDetail.getName()+"("+currentOrderDetail.getUnitName()+")");
		}else{
			orderDishName.setText(currentOrderDetail.getName());
		}
		
		TextView orderDishPrice = (TextView) vi.findViewById(R.id.textViewCheckOrderPrice);
		double price = currentOrderDetail.getCurrentPrice();
		orderDishPrice.setText(OrderUtil.decimalFormatter.format(price));

		TextView orderQuantity = (TextView) vi.findViewById(R.id.textViewCheckOrderQuantity);

		orderQuantity.setText(String.valueOf(currentOrderDetail.getQuantity()));

		TextView orderNetPrice = (TextView) vi.findViewById(R.id.textViewCheckOrderNetPrice);
		Double net = price * currentOrderDetail.getQuantity();
		orderNetPrice.setText(OrderUtil.decimalFormatter.format(net));

		return vi;

	}

}
