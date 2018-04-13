package com.rainbow.smartpos.order;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.utils.OrderUtil;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends BaseAdapter {
    public Context context;

    private static LayoutInflater inflater = null;

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public ListView listView;
    public List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
    List<OrderDetail> orderDetailListForDisplay = new ArrayList<OrderDetail>();
    public String[] colors = {"#33B45A", "#2951A8", "#B5A6CF", "#BD1C84", "#5970C0"};
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private int selectedIndex = -1;
    private int chooseOpIndex = -1;
    private OrderFragment mOrderFragment;


    private OnListViewItemClickListener mOnItemClickListener = null;

    // define interface
    public static interface OnListViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public OrderListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(1, 1, 1, 1);
    }

    public void setOrderDetails(List<OrderDetail> details) {
        this.orderDetailList = details;
        for (OrderDetail detail : details) {
            this.addOrderDetailForDisplay(detail);
        }
    }

    public void cleanDisplay() {
        orderDetailListForDisplay.clear();
    }


    /**
     * 判断是否有菜品没有落单
     *
     * @return
     */
    public boolean isDishNoPlace() {
        boolean flag = false;
        for (int i = 0; i < orderDetailList.size(); i++) {
            OrderDetail orderDetail = orderDetailList.get(i);
            if (!orderDetail.isPlaced()) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 判断是否有菜品已经落单
     *
     * @return
     */
    public boolean isDishPlace() {
        boolean flag = false;
        for (int i = 0; i < orderDetailList.size(); i++) {
            OrderDetail orderDetail = orderDetailList.get(i);
            if (orderDetail.isPlaced()) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * @param orderFramgent the orderFramgent to set
     */
    public void setOrderFramgent(OrderFragment orderFramgent) {
        mOrderFragment = orderFramgent;
    }

    public interface OrderUpdateListener {
        public void onOrderUpdate();
    }

    @Override
    public int getCount() {
        return this.orderDetailListForDisplay.size();
    }

    @Override
    public OrderDetail getItem(int position) {
        return orderDetailList.get(position);
    }

    public void addOrderDetailCount() {
        if (chooseOpIndex != -1) {
            OrderDetail orderdetail = orderDetailList.get(chooseOpIndex);
            if (orderdetail.isWeight()) {
                Toast.makeText(context, "称重菜品无法修改数量", Toast.LENGTH_SHORT).show();
                return;
            }
            if (orderdetail.isSet()) {
                Toast.makeText(context, "套餐无法修改数量", Toast.LENGTH_SHORT).show();
                return;
            }
            orderdetail.setQuantity(orderdetail.getQuantity() + 1);
            notifyDataSetChanged();
        }
    }

    public void reduceOrderDetailCount() {
        if (chooseOpIndex != -1) {
            OrderDetail orderdetail = orderDetailList.get(chooseOpIndex);
            if (orderdetail.getQuantity() > 1) {
                orderdetail.setQuantity(orderdetail.getQuantity() - 1);
                notifyDataSetChanged();
            } else {
                deleteOrderDetail();
            }
        }
    }

    public void deleteOrderDetail() {
        if (chooseOpIndex != -1) {
            OrderDetail deleteOrder = orderDetailList.get(chooseOpIndex);
            removeOrderDetailForDisplay(deleteOrder);
            orderDetailList.remove(deleteOrder);
            notifyDataSetChanged();
            chooseOpIndex = -1;
        } else {
            Toast.makeText(context, "未选中菜品", Toast.LENGTH_SHORT).show();
        }
    }

    public OrderDetail getCurrentSelectOrder() {
        if (selectedIndex != -1) {
            return orderDetailListForDisplay.get(selectedIndex);
        }
        return null;
    }

    /**
     * 设置全部菜品挂起
     */
    public void setDishHold(boolean bool) {
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setHold(bool);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置全部菜品不挂起
     */
    public void clearDishHold() {
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setHold(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(OrderDetail detail) {
        if (detail.getParent() != null) {
            return;
        }
        this.orderDetailList.remove(detail);
        removeOrderDetailForDisplay(detail);

        notifyDataSetChanged();
    }

    public int getProductTotalNoGift(long product_id) {
        int total = 0;
        for (int i = 0; i < orderDetailList.size(); i++) {
            OrderDetail order = orderDetailList.get(i);
            if (order.getProductId() == product_id && order.getGiftOrders().size() == 0) {
                total++;
            }

        }
        return total;
    }

    private void addOrderDetailForDisplay(OrderDetail detail) {
        this.orderDetailListForDisplay.add(detail);
        if (detail.isSet()) {
            for (OrderDetail setItem : detail.setOrders) {
                this.orderDetailListForDisplay.add(setItem);
            }
        }
    }

    //
    private void removeOrderDetailForDisplay(OrderDetail detail) {
        this.orderDetailListForDisplay.remove(detail);
        if (detail.isSet()) {
            for (OrderDetail setItem : detail.setOrders) {
                this.orderDetailListForDisplay.remove(setItem);
            }
        }
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetailList.add(orderDetail);
        orderDetail.setSortOrder(this.orderDetailList.size());
        addOrderDetailForDisplay(orderDetail);

    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetailList;
    }

    public double getOrderTotal() {
        return OrderUtil.getOrderTotal(orderDetailList, 0xffffffff);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        KLog.d("OrderListAdapter", "getView" + position);
        View vi = convertView;
        OrderDetail currentOrderDetail = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.order_list_row_new, null);
        }
        vi.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout ingredientLayoutContainer = (LinearLayout) vi.findViewById(R.id.linearLayoutCheckOrderIngredient);
        LinearLayout linearLayoutOrderDetail = (LinearLayout) vi.findViewById(R.id.linearLayoutOrderDetail);
        ingredientLayoutContainer.removeAllViews();
        int ingredientId = 100000;
        int setIngredientId = 400000;
        int setCookId = 500000;

        // int setOrderId = 300000;
        // if (currentOrderDetail.getSetOrderDetailList().size() > 0) {
        //
        // for (int i = 0; i <
        // currentOrderDetail.getSetOrderDetailList().size(); i++) {
        // // add ingredient if any
        // TextView textView = new TextView(context);
        // textView.setBackgroundColor(Color.parseColor("#D5D5D5"));
        // android.widget.LinearLayout.LayoutParams textParams = new
        // android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
        // 1);
        // ingredientLayoutContainer.addView(textView, textParams);
        //
        // OrderDetail setOrder =
        // currentOrderDetail.getSetOrderDetailList().get(i);
        // LinearLayout setOrderLayout = (LinearLayout)
        // inflater.inflate(R.layout.order_list_row_new, null);
        // setOrderLayout.setId(setOrderId + i);
        // setOrderLayout.findViewById(R.id.add_quantity).setVisibility(View.INVISIBLE);
        // setOrderLayout.findViewById(R.id.reduce_quantity).setVisibility(View.INVISIBLE);
        // setOrderLayout.findViewById(R.id.delete_dish).setVisibility(View.INVISIBLE);
        // TextView orderRowNo = (TextView)
        // setOrderLayout.findViewById(R.id.textViewOrderListRowNo);
        // orderRowNo.setVisibility(View.INVISIBLE);
        // StringBuffer remark = new StringBuffer();
        // if (currentOrderDetail.isHold()) {
        // remark.append("(挂)");
        // }
        // if (currentOrderDetail.isFree()) {
        // remark.append("(赠)");
        // }
        // if (currentOrderDetail.getCouponId() > 0) {
        // remark.append("(券)");
        // }
        // if (currentOrderDetail.isPriceChanged() &&
        // !currentOrderDetail.isMarket()) {
        // remark.append("(改)");
        // }
        // if (currentOrderDetail.isMarket()) {
        // remark.append("(时)");
        // }
        // if (currentOrderDetail.getVoid_quantity() > 0) {
        // remark.append("(退)");
        // }
        // TextView orderDishName = (TextView)
        // setOrderLayout.findViewById(R.id.textViewOrderListDishName);
        // if (setOrder.getUnitName() != null &&
        // !setOrder.getUnitName().isEmpty() && setOrder.isMultiUnitProduct) {
        // orderDishName.setText(remark +
        // setOrder.getName()+"("+setOrder.getUnitName()+")");
        // }else{
        // orderDishName.setText(remark + setOrder.getName());
        // }
        // TextView orderDishPrice = (TextView)
        // setOrderLayout.findViewById(R.id.textViewOrderListPrice);
        // orderDishPrice.setText(OrderUtil.dishPriceFormatter.format(setOrder.getCurrentPrice()));
        //
        // if (currentOrderDetail.isFree()) {
        // orderDishPrice.setText(OrderUtil.dishPriceFormatter.format(setOrder.getOriginPrice()));
        // }
        //
        // TextView orderQuantity = (TextView)
        // setOrderLayout.findViewById(R.id.textViewOrderListQuantity);
        // orderQuantity.setText("×"+String.valueOf(setOrder.getQuantity()));
        //
        // ingredientLayoutContainer.addView(setOrderLayout);
        //
        // if (setOrder.getIngredient().size() > 0) {
        // for (int j = 0; j < setOrder.getIngredient().size(); j++) {
        // OrderDetail setIngredient = setOrder.getIngredient().get(j);
        // LinearLayout setIngredientLayout = (LinearLayout)
        // inflater.inflate(R.layout.order_list_row_ingredient_new, null);
        // setIngredientLayout.setId(setIngredientId + i * 100 + j);
        //
        // // set ingredient name
        // TextView setIngredientName = (TextView)
        // setIngredientLayout.findViewById(R.id.textViewOrderListDishName);
        // setIngredientName.setText("-加料: " + setIngredient.getName() + "<" +
        // setIngredient.getUnitCount()+">");
        // // set ingredient price
        // TextView setIngredientPrice = (TextView)
        // setIngredientLayout.findViewById(R.id.textViewOrderListPrice);
        // setIngredientPrice.setText(OrderUtil.dishPriceFormatter.format(setIngredient.getOriginPrice()));
        //
        // // set ingredient quantity
        // TextView setIngredientQuantity = (TextView)
        // setIngredientLayout.findViewById(R.id.textViewOrderListQuantity);
        // int setIngreDientQuantity = setOrder.getQuantity() -
        // setOrder.getVoid_quantity();
        // setIngredientQuantity.setText("x"+String.valueOf(setIngreDientQuantity*setIngredient.getUnitCount()));
        //
        // ingredientLayoutContainer.addView(setIngredientLayout);
        // }
        // }
        // if (setOrder.getPublicCookMethod().size() > 0) {
        // for (int j = 0; j < setOrder.getPublicCookMethod().size(); j++) {
        // // add ingredient if any
        // OrderDetail order = setOrder.getPublicCookMethod().get(j);
        // if (order.getQuantity() == 0) {
        // continue;
        // }
        // LinearLayout cookLayout = (LinearLayout)
        // inflater.inflate(R.layout.order_list_row_ingredient_new, null);
        // cookLayout.setId(setCookId + i);
        //
        // // set ingredient name
        // TextView cookName = (TextView)
        // cookLayout.findViewById(R.id.textViewOrderListDishName);
        // cookName.setText("-做法: " + order.getName());
        // // set ingredient price
        // TextView cookPrice = (TextView)
        // cookLayout.findViewById(R.id.textViewOrderListPrice);
        // double cookprice = order.getCurrentPrice();
        // cookPrice.setText(OrderUtil.dishPriceFormatter.format(cookprice));
        //
        // if (order.isFree()) {
        // cookPrice.setText(OrderUtil.dishPriceFormatter.format(order.getOriginPrice()));
        // }
        // // set ingredient quantity
        // TextView cookQuantity = (TextView)
        // cookLayout.findViewById(R.id.textViewOrderListQuantity);
        // int cooksquantity = order.getQuantity() - order.getVoid_quantity();
        // cookQuantity.setText("x"+String.valueOf(cooksquantity));
        //
        // ingredientLayoutContainer.addView(cookLayout);
        // }
        // }
        // if (setOrder.getRemark() != null && setOrder.getRemark().length() !=
        // 0) {
        // LinearLayout remarkLayout = (LinearLayout)
        // inflater.inflate(R.layout.order_list_row_ingredient_new, null);
        // TextView remarkName = (TextView)
        // remarkLayout.findViewById(R.id.textViewOrderListDishName);
        // remarkName.setText("-备注: " + setOrder.getRemark());
        // ingredientLayoutContainer.addView(remarkLayout);
        // }
        //
        // }
        // }
        int cookMethodId = 400000;
        if (currentOrderDetail.getPublicCookMethod().size() > 0 || currentOrderDetail.getPrivateCookMethod().size() > 0) {
            List<OrderDetail> cooks = new ArrayList<>();
            cooks.addAll(currentOrderDetail.getPrivateCookMethod());
            cooks.addAll(currentOrderDetail.getPublicCookMethod());
            for (int i = 0; i < cooks.size(); i++) {
                // add ingredient if any
                OrderDetail order = cooks.get(i);
                if (order.getQuantity() == 0) {
                    continue;
                }
                LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.order_list_row_ingredient_new, null);
                ingredientLayout.setId(cookMethodId + i);

                // set ingredient name
                TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListDishName);
                ingredientName.setText("-做法: " + order.getName());
                // set ingredient price
                TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListPrice);
                double price = order.getCurrentPrice();
                ingredientPrice.setText(OrderUtil.dishPriceFormatter.format(price));

                if (order.isFree()) {
                    ingredientPrice.setText(OrderUtil.dishPriceFormatter.format(order.getOriginPrice()));
                }

                // set ingredient quantity
                TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListQuantity);
                int cookQuantity = order.getQuantity() - order.getVoid_quantity();
                ingredientQuantity.setText("x" + String.valueOf(cookQuantity));

                ingredientLayoutContainer.addView(ingredientLayout);
            }
        }
        if (currentOrderDetail.getIngredient().size() > 0) {

            for (int i = 0; i < currentOrderDetail.getIngredient().size(); i++) {
                // add ingredient if any
                OrderDetail ingredient = currentOrderDetail.getIngredient().get(i);
                LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.order_list_row_ingredient_new, null);
                ingredientLayout.setId(ingredientId + i);

                // set ingredient name
                TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListDishName);
                ingredientName.setText("-加料: " + ingredient.getName() + "(" + ingredient.getUnitCount() + ")");

                // set ingredient price
                TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListPrice);
                ingredientPrice.setText(OrderUtil.dishPriceFormatter.format(ingredient.getOriginPrice()));

                if (ingredient.isFree()) {
                    ingredientPrice.setText(OrderUtil.dishPriceFormatter.format(ingredient.getOriginPrice()));
                }
                // set ingredient quantity
                TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListQuantity);
                int quantity = currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity();
                ingredientQuantity.setText("x" + String.valueOf(quantity * ingredient.getUnitCount()));

                ingredientLayoutContainer.addView(ingredientLayout);
            }
        }

        if (currentOrderDetail.getRemark() != null && currentOrderDetail.getRemark().length() != 0) {
            LinearLayout remarkLayout = (LinearLayout) inflater.inflate(R.layout.order_list_row_ingredient_new, null);
            // set ingredient name
            TextView remarkName = (TextView) remarkLayout.findViewById(R.id.textViewOrderListDishName);
            remarkName.setText("-备注: " + currentOrderDetail.getRemark());
            TextView ingredientPrice = (TextView) remarkLayout.findViewById(R.id.textViewOrderListPrice);
            TextView ingredientQuantity = (TextView) remarkLayout.findViewById(R.id.textViewOrderListQuantity);
            ingredientPrice.setVisibility(View.GONE);
            ingredientQuantity.setVisibility(View.GONE);
            ingredientLayoutContainer.addView(remarkLayout);
        }

        TextView orderRowNo = (TextView) vi.findViewById(R.id.textViewOrderListRowNo);

        boolean isNotSetItem = false;

        int sortOrder = -1;
        for (int j = 0; j < orderDetailList.size(); j++) {
            if (orderDetailList.get(j) == currentOrderDetail) {
                sortOrder = j;
                isNotSetItem = true;
                break;
            }
        }
        final int order = sortOrder;
        if (isNotSetItem) {
            orderRowNo.setVisibility(View.VISIBLE);
            orderRowNo.setBackgroundColor(Color.parseColor(getRowNoColor(order)));
            orderRowNo.setText(Integer.toString(order + 1));
        } else {
            orderRowNo.setVisibility(View.INVISIBLE);
        }
        TextView orderDishName = (TextView) vi.findViewById(R.id.textViewOrderListDishName);
        LinearLayout remarkLayout = (LinearLayout) vi.findViewById(R.id.remarkLayout);
        remarkLayout.removeAllViews();
        if (currentOrderDetail.isHold()) {
            addRemarkText(remarkLayout, "挂");
        }
        if (currentOrderDetail.isFree()) {
            addRemarkText(remarkLayout, "赠");
        }
        if (currentOrderDetail.getCouponId() > 0) {
            addRemarkText(remarkLayout, "劵");
        }
        if (currentOrderDetail.isPriceChanged() && !currentOrderDetail.isMarket()) {
            addRemarkText(remarkLayout, "改");
        }
        if (currentOrderDetail.isMarket()) {
            addRemarkText(remarkLayout, "时");
        }

        if (currentOrderDetail.getUnitName() != null && !currentOrderDetail.getUnitName().isEmpty() && currentOrderDetail.isMultiUnitProduct) {
            orderDishName.setText(currentOrderDetail.getName() + "(" + currentOrderDetail.getUnitName() + ")");
        } else {
            orderDishName.setText(currentOrderDetail.getName());
        }
        TextView orderDishPrice = (TextView) vi.findViewById(R.id.textViewOrderListPrice);
        orderDishPrice.setText(OrderUtil.dishPriceFormatter.format(currentOrderDetail.getCurrentPrice()));

        if (currentOrderDetail.isFree()) {
            orderDishPrice.setText(OrderUtil.dishPriceFormatter.format(currentOrderDetail.getOriginPrice()));
        }

        TextView orderQuantity = (TextView) vi.findViewById(R.id.textViewOrderListQuantity);
        orderQuantity.setText("×" + String.valueOf(currentOrderDetail.getQuantity()));
        vi.findViewById(R.id.add_quantity).setVisibility(View.VISIBLE);
        vi.findViewById(R.id.reduce_quantity).setVisibility(View.VISIBLE);
        //vi.findViewById(R.id.delete_dish).setVisibility(View.VISIBLE);
        if (null != currentOrderDetail.getParent()) {
            vi.findViewById(R.id.add_quantity).setVisibility(View.INVISIBLE);
            vi.findViewById(R.id.reduce_quantity).setVisibility(View.INVISIBLE);
            //vi.findViewById(R.id.delete_dish).setVisibility(View.INVISIBLE);
        }

        vi.findViewById(R.id.add_quantity).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (order != chooseOpIndex) {
                    chooseOpIndex = order;
                }
                addOrderDetailCount();
            }
        });
        vi.findViewById(R.id.reduce_quantity).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (order != chooseOpIndex) {
                    chooseOpIndex = order;
                }
                reduceOrderDetailCount();
            }
        });
    /*	vi.findViewById(R.id.delete_dish).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (order != chooseOpIndex) {
					chooseOpIndex = order;
				}
				deleteOrderDetail();
			}
		});*/
        linearLayoutOrderDetail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mOnItemClickListener != null) {
                    // 注意这里使用getTag方法获取数据
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });

        KLog.d("OrderListAdapter", "getView finish" + position);
        return vi;

    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public void addRemarkText(LinearLayout layout, String remarkText) {
        TextView textView = new TextView(context);
        textView.setText(remarkText);
        textView.setTextSize(context.getResources().getDimension(R.dimen.app_info_text_size));
        textView.setPadding(2, 0, 2, 0);
        textView.setTextColor(Color.parseColor("#FF0000"));
        textView.setBackgroundResource(R.drawable.red_remark_bg);
        layout.addView(textView, params);
    }

    public void setOnItemClickListener(OnListViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private String getRowNoColor(int position) {
        return colors[position % colors.length];
    }

    public void updateItem(int index) {
        if (listView == null) return;

        if (listView != null) {
            int start = listView.getFirstVisiblePosition();
            View view = listView.getChildAt(index - start);
            getView(index-1, view, listView);

        }


    }
}
