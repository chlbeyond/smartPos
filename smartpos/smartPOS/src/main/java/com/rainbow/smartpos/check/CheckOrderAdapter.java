package com.rainbow.smartpos.check;

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

import java.util.ArrayList;
import java.util.List;

public class CheckOrderAdapter extends BaseAdapter {
	private Activity activity;
	private  LayoutInflater inflater = null;

	List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
	private int memberTypeId = -1;

	public CheckOrderAdapter(Activity a) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// public void addSection(Order order, ArrayList<OrderDetail> orderDetails)
	// {
	// this.sections.put(order, orderDetails);
	// }

	public int getOrderCount() {
		return OrderUtil.getOrderCount(orderDetails);
	}

	public long getMemberTypeId() {
		return memberTypeId;
	}

	public void setMemberTypeId(int memberTypeId) {
		this.memberTypeId = memberTypeId;
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

	// public void addOrder(OrderDetail orderDetail) {
	// int lastIndex = orderDetails.size();
	// if (orderDetail.getSetItemId() > 0) {
	// numOfSetOrderItems++;
	// } else {
	// int size = sections.get(orderDetail.getParentOrder()).size();
	// orderDetail.setSortOrder(size + 1 - numOfSetOrderItems);
	// if (size != 0) {
	// OrderDetail lastOrderDetail =
	// sections.get(orderDetail.getParentOrder()).get(size - 1);
	// lastIndex = orderDetails.indexOf(lastOrderDetail) + 1;
	// }
	// }
	//
	// // insert into section
	// sections.get(orderDetail.getParentOrder()).add(orderDetail);
	// // update order amount
	// double orderDetailTotal = OrderUtil.getOrderDetailTotal(orderDetail, 0);
	// orderDetail.getParentOrder().setOrderTotal(orderDetail.getParentOrder().getOrderTotal()
	// + orderDetailTotal);
	// // recaulate total order
	//
	// currentOrderTotal += orderDetailTotal;
	// currentOrderCount = currentOrderCount + orderDetail.getQuantity();
	// orderDetails.add(lastIndex, orderDetail);
	//
	// // order.setSort_order(orderDetails.size() + 1);
	// // orderDetails.add(order);
	// }

	public OrderDetail getOrderById(long orderId) {
		for (OrderDetail order : orderDetails) {
			if (order.getId() == orderId) {
				return order;
			}
		}
		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.check_order_row, null);
		OrderDetail currentOrderDetail = getItem(position);
		// add header if it is first order detail in order and there are more
		// than two order(table)
		// in the same bill
		LinearLayout orderHeaderContainer = (LinearLayout) vi.findViewById(R.id.linearLayoutOrderListHeader);
		orderHeaderContainer.removeAllViews();

//		if (currentOrderDetail.getSortOrder() == 1 && orders.size() > 1) {
//			LinearLayout orderHeaderLayout = (LinearLayout) inflater.inflate(R.layout.order_list_row_header, null);
//			// set table name
//			TextView tableName = (TextView) orderHeaderLayout.findViewById(R.id.textViewOrderTableName);
//			tableName.setText(currentOrderDetail.getParentOrder().getTableName());
//
//			TextView personCount = (TextView) orderHeaderLayout.findViewById(R.id.textViewOrderTablePersonCount);
//			personCount.setText(String.valueOf(currentOrderDetail.getParentOrder().getPersonCount()) + " 人");
//
//			TextView orderTotal = (TextView) orderHeaderLayout.findViewById(R.id.textViewOrderTableTotal);
//			orderTotal.setText(Restaurant.currentcyFormatter.format(currentOrderDetail.getParentOrder().getOrderTotal()));
//
//			orderHeaderContainer.addView(orderHeaderLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		}
		LinearLayout ingredientLayoutContainer = (LinearLayout) vi.findViewById(R.id.linearLayoutCheckOrderIngredient);
		ingredientLayoutContainer.removeAllViews();
		int ingredientId = 100000;
		int setIngredientId = 400000;
		int setCookId = 500000;
		if (currentOrderDetail.getIngredient().size() > 0) {

			for (int i = 0; i < currentOrderDetail.getIngredient().size(); i++) {
				// add ingredient if any
				OrderDetail ingredient = currentOrderDetail.getIngredient().get(i);
				LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
				ingredientLayout.setId(ingredientId + i);

				// set ingredient name
				TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderDishName);
				ingredientName.setText("(加料) " + ingredient.getName() + " x " + ingredient.getUnitCount());

				// set ingredient price
				TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderPrice);
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(ingredient.getOriginPrice()));
				
				if (ingredient.isFree()) {
					ingredientPrice.setText(OrderUtil.decimalFormatter.format(ingredient.getOriginPrice()));
				}
				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int quantity = currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity();
				ingredientQuantity.setText(String.valueOf(quantity*ingredient.getUnitCount()));

				// set ingredient price
				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
				double currentPrice = ingredient.getCurrentPrice();
				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(currentPrice * quantity * ingredient.getUnitCount()));

				ingredientLayoutContainer.addView(ingredientLayout);
			}
		}

		if (currentOrderDetail.getRemark() != null && currentOrderDetail.getRemark().length() != 0) {
			LinearLayout remarkLayout = (LinearLayout) inflater.inflate(R.layout.order_list_row_remark, null);
			// set ingredient name
			TextView remarkName = (TextView) remarkLayout.findViewById(R.id.textViewOrderListDishName);
			remarkName.setText("备注:" + currentOrderDetail.getRemark());
			ingredientLayoutContainer.addView(remarkLayout);
		}
		if (currentOrderDetail.isWeight()) {
			LinearLayout remarkLayout = (LinearLayout) inflater.inflate(R.layout.order_list_row_remark, null);
			// set ingredient name
			TextView remarkName = (TextView) remarkLayout.findViewById(R.id.textViewOrderListDishName);
			remarkName.setText("称重:" + String.valueOf(currentOrderDetail.getWeight()) + " " + currentOrderDetail.getUnitName());
			ingredientLayoutContainer.addView(remarkLayout);
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
				
				
				if (giftOrder.isFree()) {
					ingredientPrice.setText(OrderUtil.decimalFormatter.format(giftOrder.getOriginPrice()));
				}
				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int quantity = giftOrder.getQuantity() - giftOrder.getVoid_quantity();
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
				
				if (setOrder.isMultiUnitProduct && null != setOrder.getUnitName()) {
					ingredientName.setText("(道" + String.valueOf(i + 1) + ") " + setOrder.getName()+"("+currentOrderDetail.getUnitName()+")");
				}else{
					ingredientName.setText("(道" + String.valueOf(i + 1) + ") " + setOrder.getName());
				}

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
				ingredientNetPrice.setText(String.valueOf(setOrder.getCurrentPrice() * quantity * setOrder.getUnitCount()));

				ingredientLayoutContainer.addView(ingredientLayout);

				if (setOrder.getIngredient().size() > 0) {
					for (int j = 0; j < setOrder.getIngredient().size(); j++) {
						OrderDetail setIngredient = setOrder.getIngredient().get(j);
						LinearLayout setIngredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
						setIngredientLayout.setId(setIngredientId + i * 100 + j);

						// set ingredient name
						TextView setIngredientName = (TextView) setIngredientLayout.findViewById(R.id.textViewCheckOrderDishName);
						setIngredientName.setText("(加料) " + setIngredient.getName() + " x " + setIngredient.getUnitCount());
						// set ingredient price
						TextView setIngredientPrice = (TextView) setIngredientLayout.findViewById(R.id.textViewCheckOrderPrice);
						setIngredientPrice.setText(OrderUtil.decimalFormatter.format(setIngredient.getOriginPrice()));

						// set ingredient quantity
						TextView setIngredientQuantity = (TextView) setIngredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
						int setIngreDientQuantity = setOrder.getQuantity() - setOrder.getVoid_quantity();
						setIngredientQuantity.setText(String.valueOf(setIngreDientQuantity*setIngredient.getUnitCount()));

						TextView textViewOrderListVoid = (TextView) setIngredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
						textViewOrderListVoid.setText(String.valueOf(setIngredient.getCurrentPrice() * setIngreDientQuantity * setIngredient.getUnitCount()));

						ingredientLayoutContainer.addView(setIngredientLayout);
					}
				}
				if (setOrder.getRemark() != null && setOrder.getRemark().length() != 0) {
					LinearLayout remarkLayout = (LinearLayout) inflater.inflate(R.layout.order_list_row_remark, null);
					TextView remarkName = (TextView) remarkLayout.findViewById(R.id.textViewOrderListDishName);
					remarkName.setText("备注:" + setOrder.getRemark());
					ingredientLayoutContainer.addView(remarkLayout);
				}
				if (setOrder.getPublicCookMethod().size() > 0) {
					for (int j = 0; j < setOrder.getPublicCookMethod().size(); j++) {
						// add ingredient if any
						OrderDetail order = setOrder.getPublicCookMethod().get(j);
						if (order.getQuantity() == 0) {
							continue;
						}
						LinearLayout cookLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
						cookLayout.setId(setCookId + i);

						// set ingredient name
						TextView cookName = (TextView) cookLayout.findViewById(R.id.textViewCheckOrderDishName);
						cookName.setText("(做法) " + order.getName());
						// set ingredient price
						TextView cookPrice = (TextView) cookLayout.findViewById(R.id.textViewCheckOrderPrice);
						double cookprice = order.getCurrentPrice();
						cookPrice.setText(OrderUtil.decimalFormatter.format(cookprice));
						
						if (order.isFree()) {
							cookPrice.setText(OrderUtil.decimalFormatter.format(order.getOriginPrice()));
						}
						// set ingredient quantity
						TextView cookQuantity = (TextView) cookLayout.findViewById(R.id.textViewCheckOrderQuantity);
						int cooksquantity = order.getQuantity() - order.getVoid_quantity();
						cookQuantity.setText(String.valueOf(cooksquantity));

						TextView cookNetPrice = (TextView) cookLayout.findViewById(R.id.textViewCheckOrderNetPrice);
						double currentPrice = order.getCurrentPrice();
						cookNetPrice.setText(OrderUtil.decimalFormatter.format(currentPrice * cooksquantity));

						ingredientLayoutContainer.addView(cookLayout);
					}
				}

			}
		}
		int cookMethodId = 400000;
		if (currentOrderDetail.getPublicCookMethod().size() > 0) {
			for (int i = 0; i < currentOrderDetail.getPublicCookMethod().size(); i++) {
				// add ingredient if any
				OrderDetail order = currentOrderDetail.getPublicCookMethod().get(i);
				if (order.getQuantity() == 0) {
					continue;
				}
				LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
				ingredientLayout.setId(cookMethodId + i);

				// set ingredient name
				TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderDishName);
				ingredientName.setText("(做法) " + order.getName());
				// set ingredient price
				TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderPrice);
				double price = order.getCurrentPrice();
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(price));
				
				if (order.isFree()) {
					ingredientPrice.setText(OrderUtil.decimalFormatter.format(order.getOriginPrice()));
				}

				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int cookQuantity = order.getQuantity() - order.getVoid_quantity();
				ingredientQuantity.setText(String.valueOf(cookQuantity));

				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
				double currentPrice = order.getCurrentPrice();
				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(currentPrice * cookQuantity));

				ingredientLayoutContainer.addView(ingredientLayout);
			}
		}
		TextView orderRowNo = (TextView) vi.findViewById(R.id.textViewCheckOrdeRowNo);
		orderRowNo.setText(Integer.toString(position + 1));

		TextView orderDishName = (TextView) vi.findViewById(R.id.textViewCheckOrderDishName);
		StringBuffer remark = new StringBuffer();
		if (currentOrderDetail.isFree()) {
			remark.append("(赠)");
		}
		if (currentOrderDetail.getCouponId() > 0) {
			remark.append("(券)");
		}
		if (currentOrderDetail.getIsSpecial()) {
			remark.append("(特)");
		}
		if (currentOrderDetail.isPriceChanged() && !currentOrderDetail.isMarket()) {
			remark.append("(改)");
		}
		if (currentOrderDetail.isVip()) {
			remark.append("(会)");
		}
		if (currentOrderDetail.isVoucher()) {
			remark.append("(代)");
		}
		if (currentOrderDetail.isDiscount()) {
			remark.append("(折)");
		}
		if (currentOrderDetail.isMarket()) {
			remark.append("(时)");
		}
		if (currentOrderDetail.getVoid_quantity() > 0) {
			remark.append("(退)");
		}
		if (currentOrderDetail.isHold()) {
			remark.append("(挂)");
		}
		if (currentOrderDetail.isMultiUnitProduct && null != currentOrderDetail.getUnitName()) {
			orderDishName.setText(remark + currentOrderDetail.getName()+"("+currentOrderDetail.getUnitName()+")");
		}else{
			orderDishName.setText(remark + currentOrderDetail.getName());
		}
		

		TextView orderDishPrice = (TextView) vi.findViewById(R.id.textViewCheckOrderPrice);
		double price = currentOrderDetail.getCurrentPrice();
		orderDishPrice.setText(OrderUtil.decimalFormatter.format(price));
		if (currentOrderDetail.isWeight()) {
			if (currentOrderDetail.getIsSpecial()) {
				orderDishPrice.setText(OrderUtil.decimalFormatter.format(currentOrderDetail.getOriginPrice()));
			}
		}
		
		if (currentOrderDetail.isFree()) {
			orderDishPrice.setText(OrderUtil.decimalFormatter.format(currentOrderDetail.getOriginPrice()));
		}
		TextView orderQuantity = (TextView) vi.findViewById(R.id.textViewCheckOrderQuantity);

		orderQuantity.setText(String.valueOf(currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity()));

		TextView orderNetPrice = (TextView) vi.findViewById(R.id.textViewCheckOrderNetPrice);
		orderNetPrice.setText(OrderUtil.decimalFormatter.format(currentOrderDetail.getRealCurrentPrice()));

		return vi;

	}

	public void setOrderDetails(List<OrderDetail> details) {
		this.orderDetails = details;
	}
	
	public void reset() {
		this.orderDetails.clear();
	}

}
