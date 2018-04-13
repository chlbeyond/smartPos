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

public class BillOrderAdapter extends BaseAdapter {
	private Activity activity;
	private static LayoutInflater inflater = null;
	private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

	public BillOrderAdapter(Activity a) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// public void addSection(Order order, List<OrderDetail> orderDetails) {
	// this.sections.put(order, orderDetails);
	// }

	public void setOrderDetails(List<OrderDetail> details) {
		this.orderDetails = details;
	}

	public void reset() {
		this.orderDetails.clear();
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
		int setIngredientId = 400000;
		int setCookId = 500000;
		if (currentOrderDetail.ingredients.size() > 0) {

			for (int i = 0; i < currentOrderDetail.ingredients.size(); i++) {
				// add ingredient if any
				OrderDetail ingredient = currentOrderDetail.getIngredient().get(i);
				LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
				ingredientLayout.setId(ingredientId + i);

				// set ingredient name
				TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderDishName);
				ingredientName.setText("(加料) " + ingredient.getName() + " x " + ingredient.getUnitCount()*ingredient.getQuantity());

				// set ingredient price
				TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderPrice);
				double price = ingredient.getCurrentPrice();
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(price));
				
				if (ingredient.isFree()) {
					ingredientPrice.setText(OrderUtil.decimalFormatter.format(ingredient.getOriginPrice()));
				}

				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int quantity = (currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity())*ingredient.getUnitCount();
				ingredientQuantity.setText(String.valueOf(quantity));

				// set ingredient price
				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
//				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(OrderUtil.getOrderDetailTotal(ingredient, OrderUtil.ORDER_CHILD_ITEM_FLAG_GIFT
//						| OrderUtil.ORDER_CHILD_ITEM_FLAG_INGREDIENT | OrderUtil.ORDER_CHILD_ITEM_FLAG_SET)));

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
		int giftOrderId = 200000;
		if (currentOrderDetail.giftOrders.size() > 0) {
			for (int i = 0; i < currentOrderDetail.giftOrders.size(); i++) {
				// add ingredient if any
				OrderDetail giftOrder = currentOrderDetail.getGiftOrders().get(i);
				LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
				ingredientLayout.setId(giftOrderId + i);

				TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderDishName);
				ingredientName.setText("(活动) " + giftOrder.getName());

				// set ingredient price
				TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderPrice);
				double price = giftOrder.getCurrentPrice();
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(price));
				
				if (giftOrder.isFree()) {
					ingredientPrice.setText(OrderUtil.decimalFormatter.format(giftOrder.getOriginPrice()));
				}

				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int quantity = giftOrder.getQuantity();
				ingredientQuantity.setText(String.valueOf(quantity));

				// set ingredient price
				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
//				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(OrderUtil.getOrderDetailTotal(giftOrder, OrderUtil.ORDER_CHILD_ITEM_FLAG_GIFT | OrderUtil.ORDER_CHILD_ITEM_FLAG_INGREDIENT
//						| OrderUtil.ORDER_CHILD_ITEM_FLAG_SET)));
				ingredientLayoutContainer.addView(ingredientLayout);
			}
		}

		int setOrderId = 300000;
		if (currentOrderDetail.setOrders.size() > 0) {
			for (int i = 0; i < currentOrderDetail.setOrders.size(); i++) {
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
				double price = setOrder.getCurrentPrice();
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(price));
				
				if (setOrder.isFree()) {
					ingredientPrice.setText(OrderUtil.decimalFormatter.format(setOrder.getOriginPrice()));
				}

				// set ingredient quantity
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int quantity = setOrder.getQuantity();
				ingredientQuantity.setText(String.valueOf(quantity));

				// set ingredient price
				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
//				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(OrderUtil.getOrderDetailTotal(setOrder, OrderUtil.ORDER_CHILD_ITEM_FLAG_GIFT | OrderUtil.ORDER_CHILD_ITEM_FLAG_INGREDIENT
//						| OrderUtil.ORDER_CHILD_ITEM_FLAG_SET)));

				ingredientLayoutContainer.addView(ingredientLayout);
				if (setOrder.getIngredient().size() > 0) {
					for (int j = 0; j < setOrder.getIngredient().size(); j++) {
						OrderDetail setIngredient = setOrder.getIngredient().get(j);
						LinearLayout setIngredientLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
						setIngredientLayout.setId(setIngredientId + i * 100 + j);

						// set ingredient name
						TextView setIngredientName = (TextView) setIngredientLayout.findViewById(R.id.textViewCheckOrderDishName);
						setIngredientName.setText("(加料) " + setIngredient.getName());
						// set ingredient price
						TextView setIngredientPrice = (TextView) setIngredientLayout.findViewById(R.id.textViewCheckOrderPrice);
						setIngredientPrice.setText(OrderUtil.decimalFormatter.format(setIngredient.getCurrentPrice()));
						
						if (setIngredient.isFree()) {
							setIngredientPrice.setText(OrderUtil.decimalFormatter.format(setIngredient.getOriginPrice()));
						}

						// set ingredient quantity
						TextView setIngredientQuantity = (TextView) setIngredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
						int setQuantity = setOrder.getQuantity() - setOrder.getVoid_quantity();
						setIngredientQuantity.setText(String.valueOf(setQuantity*setIngredient.getUnitCount()));

						TextView setIngredientNetPrice = (TextView) setIngredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
						setIngredientNetPrice.setText(OrderUtil.decimalFormatter.format(setIngredient.getCurrentPrice() * setQuantity));
						ingredientLayoutContainer.addView(setIngredientLayout);
					}
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

						TextView cookName = (TextView) cookLayout.findViewById(R.id.textViewCheckOrderDishName);
						cookName.setText("(做法) " + order.getName());
						TextView cookPrice = (TextView) cookLayout.findViewById(R.id.textViewCheckOrderPrice);
						double cookprice = order.getCurrentPrice();
						cookPrice.setText(OrderUtil.decimalFormatter.format(cookprice));
						
						if (order.isFree()) {
							cookPrice.setText(OrderUtil.decimalFormatter.format(order.getOriginPrice()));
						}

						TextView cookQuantity = (TextView) cookLayout.findViewById(R.id.textViewCheckOrderQuantity);
						cookQuantity.setText(String.valueOf(order.getQuantity() - order.getVoid_quantity()));

						TextView cookNetPrice = (TextView) cookLayout.findViewById(R.id.textViewCheckOrderNetPrice);
						cookNetPrice.setText(OrderUtil.decimalFormatter.format(order.getCurrentPrice() * (order.getQuantity() - order.getVoid_quantity())));
						ingredientLayoutContainer.addView(cookLayout);
					}
				}
				if (setOrder.giftOrders.size() > 0) {
					for (int j = 0; j < setOrder.giftOrders.size(); j++) {
						// add ingredient if any
						OrderDetail giftOrder = setOrder.getGiftOrders().get(j);
						LinearLayout giftLayout = (LinearLayout) inflater.inflate(R.layout.check_list_row_ingredient, null);
						ingredientLayout.setId(giftOrderId + j);

						TextView giftName = (TextView) giftLayout.findViewById(R.id.textViewCheckOrderDishName);
						giftName.setText("(活动) " + giftOrder.getName());

						// set ingredient price
						TextView giftPrice = (TextView) giftLayout.findViewById(R.id.textViewCheckOrderPrice);
						double giftprice = giftOrder.getCurrentPrice();
						giftPrice.setText(OrderUtil.decimalFormatter.format(giftprice));
						
						if (giftOrder.isFree()) {
							giftPrice.setText(OrderUtil.decimalFormatter.format(giftOrder.getOriginPrice()));
						}

						// set ingredient quantity
						TextView giftQuantity = (TextView) giftLayout.findViewById(R.id.textViewCheckOrderQuantity);
						int giftquantity = giftOrder.getQuantity() - giftOrder.getVoid_quantity();
						giftQuantity.setText(String.valueOf(giftquantity));

						// set ingredient price
						TextView giftNetPrice = (TextView) giftLayout.findViewById(R.id.textViewCheckOrderNetPrice);
						giftNetPrice.setText(OrderUtil.decimalFormatter.format(giftquantity * giftOrder.getCurrentPrice()));
						// ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(OrderUtil.getOrderDetailTotal(giftOrder,
						// OrderUtil.ORDER_CHILD_ITEM_FLAG_GIFT
						// | OrderUtil.ORDER_CHILD_ITEM_FLAG_INGREDIENT |
						// OrderUtil.ORDER_CHILD_ITEM_FLAG_SET)));
						ingredientLayoutContainer.addView(giftLayout);
					}
				}

				if (setOrder.getRemark() != null && setOrder.getRemark().length() != 0) {
					LinearLayout remarkLayout = (LinearLayout) inflater.inflate(R.layout.order_list_row_remark, null);
					TextView remarkName = (TextView) remarkLayout.findViewById(R.id.textViewOrderListDishName);
					remarkName.setText("备注:" + setOrder.getRemark());
					ingredientLayoutContainer.addView(remarkLayout);
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
				TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderQuantity);
				int intQuantity = order.getQuantity() - order.getVoid_quantity();
				ingredientQuantity.setText(String.valueOf(intQuantity));

				TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderPrice);
				ingredientPrice.setText(OrderUtil.decimalFormatter.format(order.getCurrentPrice()));
				
				if (order.isFree()) {
					ingredientPrice.setText(OrderUtil.decimalFormatter.format(order.getOriginPrice()));
				}
				
				TextView ingredientNetPrice = (TextView) ingredientLayout.findViewById(R.id.textViewCheckOrderNetPrice);
				double price = order.getCurrentPrice();
				ingredientNetPrice.setText(OrderUtil.decimalFormatter.format(price * intQuantity));

				// set ingredient quantity

				ingredientLayoutContainer.addView(ingredientLayout);
			}
		}
		TextView orderRowNo = (TextView) vi.findViewById(R.id.textViewCheckOrdeRowNo);
		orderRowNo.setText(Integer.toString(position+1));

		TextView orderDishName = (TextView) vi.findViewById(R.id.textViewCheckOrderDishName);
//		if (currentOrderDetail.isFree()) {
//			orderDishName.setText("(赠)" + currentOrderDetail.getName());
//		} else if ((currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity()) == 0) {
//			orderDishName.setText("(退)" + currentOrderDetail.getName());
//		} else if (currentOrderDetail.isPriceChanged() && !currentOrderDetail.isMarket()) {
//			//if (currentOrderDetail.getCurrentPrice() != currentOrderDetail.getOriginPrice() && !currentOrderDetail.getIsSpecial() && currentOrderDetail.getCount() < 0) {
//			orderDishName.setText("(改)" + currentOrderDetail.getName());
//		} else if (currentOrderDetail.getIsSpecial()) {
//			orderDishName.setText("(特)" + currentOrderDetail.getName());
//		} else if (currentOrderDetail.getCouponId() > 0) {
//			orderDishName.setText("(劵)" + currentOrderDetail.getName());
//		} else if(currentOrderDetail.isVip){
//			orderDishName.setText("(会)" + currentOrderDetail.getName());
//		} else {
//			orderDishName.setText(currentOrderDetail.getName());
//		}
		
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

		
		if (currentOrderDetail.isWeight()) {
			orderQuantity.setText(String.valueOf(currentOrderDetail.getWeight()));
		}else{
			orderQuantity.setText(String.valueOf(currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity()));
		}
		TextView orderNetPrice = (TextView) vi.findViewById(R.id.textViewCheckOrderNetPrice);
		
		//orderNetPrice.setText(OrderUtil.decimalFormatter.format(OrderUtil.getOrderDetailTotal(currentOrderDetail, 0)));
		orderNetPrice.setText(OrderUtil.decimalFormatter.format(currentOrderDetail.getRealCurrentPrice()));
		return vi;
	}
}
