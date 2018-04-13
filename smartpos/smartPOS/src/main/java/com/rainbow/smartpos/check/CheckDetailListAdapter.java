package com.rainbow.smartpos.check;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.utils.OrderUtil;
import com.socks.library.KLog;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CheckDetailListAdapter extends BaseAdapter {
    public static String TAG = "CheckDetailListAdapter";
    public Context context;
    private LayoutInflater inflater = null;
    public int selection = -1;
    public List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
    public List<OrderDetail> orderDetailsListForDisPlay = new ArrayList<OrderDetail>();
    public String[] colors = {"#33B45A", "#2951A8", "#B5A6CF", "#BD1C84", "#5970C0"};

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public ListView listView;

    private List<Boolean> checked = new ArrayList();

    public enum Type {
        ORDER, CHECK, PREORDER
    }

    public void updateItem(int index) {
        if (listView == null) return;

        if (listView != null) {
            int start = listView.getFirstVisiblePosition();
            View view = listView.getChildAt(index - start);
            getView(index - 1, view, listView);

        }


    }

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getTag() != null) {
                KLog.d(TAG, view.getTag());
                orderDetailList.remove((int) view.getTag());
                notifyDataSetChanged();
            }
        }
    };


    public Type type;
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public CheckDetailListAdapter(Context context, Type type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(1, 1, 1, 1);
        this.type = type;
    }

    public void setOrderDetails(List<OrderDetail> details) {
        this.orderDetailList.clear();
        this.orderDetailsListForDisPlay.clear();
        this.orderDetailList = details;
        this.checked.clear();
        for (OrderDetail order : details) {
            this.orderDetailsListForDisPlay.add(order);
            this.checked.add(Boolean.TRUE);
            if (order.isSet()) {
                for (OrderDetail setOrder : order.setOrders) {
                    this.orderDetailsListForDisPlay.add(setOrder);
                }
            }
//            else if(order.childrenOrderDetail!=null&&order.childrenOrderDetail.size()>0)
//            {
//
//                for (OrderDetail setOrder : order.childrenOrderDetail)
//                {
//                    this.orderDetailsListForDisPlay.add(setOrder);
//                }
//            }
        }

    }


    public List<OrderDetail> getUnPlaceDetail() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetail order : orderDetailList) {
            if (!order.isPlaced()) orderDetails.add(order);
        }
        return orderDetails;
    }

    public boolean isHasPlaceDetail() {
        for (OrderDetail order : orderDetailList) {
            if (order.isPlaced()) return true;
        }
        return false;
    }

    @Override
    public int getCount() {
        switch (type) {
            case ORDER:
                return this.orderDetailsListForDisPlay.size();
            case CHECK:
            case PREORDER:
                return this.orderDetailList.size();

        }
        return this.orderDetailList.size();
    }

    @Override
    public OrderDetail getItem(int position) {
        switch (type) {
            case ORDER:
                return this.orderDetailsListForDisPlay.get(position);
            case CHECK:
            case PREORDER:
                return this.orderDetailList.get(position);

        }
        return orderDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetailList;
    }

    public OrderDetail getCurrentSelectOrder() {
        switch (type) {
            case ORDER:
                return this.orderDetailsListForDisPlay.get(selection);
            case CHECK:
            case PREORDER:
                return this.orderDetailList.get(selection);
        }
        return orderDetailList.get(selection);
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        /**
         * 先检查之前列表中是否已经有该菜了, 如果有,则用户进行的是修改操作,先将该菜品删除,再添加到列表中,以免重复添加
         * */
        for (OrderDetail order : orderDetailList
                ) {
            if (order == orderDetail)
                orderDetailList.remove(order);
        }
        for (OrderDetail orderdetail : orderDetailsListForDisPlay) {
            if (orderdetail == orderDetail)
                orderDetailsListForDisPlay.remove(orderdetail);
        }
        orderDetailList.add(orderDetail);
        orderDetailsListForDisPlay.add(orderDetail);
        checked.add(Boolean.TRUE);
        if (orderDetail.isSet()) {
            for (OrderDetail setOrder : orderDetail.setOrders) {
                orderDetailsListForDisPlay.add(setOrder);
            }
        }
//        else if(orderDetail.childrenOrderDetail!=null&&orderDetail.childrenOrderDetail.size()>0)
//        {
//
//            for (OrderDetail setOrder : orderDetail.childrenOrderDetail)
//            {
//                this.orderDetailsListForDisPlay.add(setOrder);
//            }
//        }
    }

    public void setSelectedIndex(int position) {
        this.selection = position;
    }

    public int getSelectedIndex() {
        return selection;
    }

    public void setDishHold(boolean b) {
        for (OrderDetail order : orderDetailList) {
            order.setHold(b);
        }
    }

    public void setDishFistPlace() {
        for (OrderDetail order : orderDetailList) {
            order.setPlaced(true);
        }
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    public Double getOrderTotal() {
        return OrderUtil.getOrderTotal(orderDetailList, 0xffffffff);
    }

    public List<OrderDetail> getSelectedPreOrderList() {
        List<OrderDetail> result = new ArrayList<>();
        if (type == Type.PREORDER) {
            for (int i = 0; i < checked.size(); ++i) {
                if (checked.get(i).booleanValue())
                    result.add(orderDetailList.get(i));
            }
        }
        return result;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi;
        OrderDetail currentOrderDetail = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.check_detail_list_row_new, null);
        } else {

            vi = convertView;
        }
        vi.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout ingredientLayoutContainer = (LinearLayout) vi.findViewById(R.id.linearLayoutCheckOrderIngredient);
        ingredientLayoutContainer.removeAllViews();
        if (currentOrderDetail.getIngredient().size() > 0) {

            for (int i = 0; i < currentOrderDetail.getIngredient().size(); i++) {
                // add ingredient if any
                OrderDetail ingredient = currentOrderDetail.getIngredient().get(i);
                LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);

//                if(type == Type.PREORDER) {
//                    ingredientLayout.findViewById(R.id.check).setVisibility(View.INVISIBLE);
//                } else {
//                    ingredientLayout.findViewById(R.id.check).setVisibility(View.GONE);
//                }
                // set ingredient name
                TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListDishName);
                ingredientName.setText("-加料: " + ingredient.getName());// + "(" + ingredient.getUnitCount() + ")");

                // set ingredient price
                TextView orderDishPrice = (TextView) ingredientLayout.findViewById(R.id.textViewPrice);
                TextView orderDishRealPrice = (TextView) ingredientLayout.findViewById(R.id.textViewRealPrice);
                double price = ingredient.getCurrentPrice();
                if (ingredient.isFree()) {
                    price = ingredient.getOriginPrice();
                }
                double realPrice = ingredient.getRealCurrentPrice();
                if (price == realPrice) {
                    orderDishPrice.setTextColor(context.getResources().getColor(R.color.check_price_color));
                    orderDishRealPrice.setVisibility(View.GONE);
                } else {
                    orderDishPrice.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
                    orderDishPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    orderDishRealPrice.setVisibility(View.VISIBLE);
                }
                orderDishPrice.setText(OrderUtil.dishPriceFormatter.format(price));
                orderDishRealPrice.setText(OrderUtil.dishPriceFormatter.format(realPrice));

                // set ingredient quantity
                TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListQuantity);
                int quantity = currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity();
//                ingredientQuantity.setText("x" + String.valueOf(quantity * ingredient.getUnitCount()));
                ingredientQuantity.setText("x" + String.valueOf(ingredient.getQuantity()));

                ingredientLayoutContainer.addView(ingredientLayout);
            }
        }

        if (currentOrderDetail.getSetOrderDetailList().size() > 0 && type != Type.ORDER) {
            for (int i = 0; i < currentOrderDetail.getSetOrderDetailList().size(); i++) {
                // add ingredient if any
                TextView textView = new TextView(context);
                textView.setBackgroundColor(Color.parseColor("#D5D5D5"));
                android.widget.LinearLayout.LayoutParams textParams = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1);
                ingredientLayoutContainer.addView(textView, textParams);

                OrderDetail setOrder = currentOrderDetail.getSetOrderDetailList().get(i);
                LinearLayout setOrderLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_new, null);

                //套餐配菜不能显示选择框
                CheckBox checkBox = (CheckBox) setOrderLayout.findViewById(R.id.check);
                checkBox.setVisibility(View.INVISIBLE);

                TextView orderRowNo = (TextView) setOrderLayout.findViewById(R.id.textViewRowNo);
                orderRowNo.setVisibility(View.INVISIBLE);

                TextView orderQuantity = (TextView) setOrderLayout.findViewById(R.id.textViewQuantity);
                orderQuantity.setText("×" + String.valueOf(setOrder.getQuantity() - setOrder.getVoid_quantity()));

                TextView orderDishName = (TextView) setOrderLayout.findViewById(R.id.textViewDishName);
                if (setOrder.getUnitName() != null && !setOrder.getUnitName().isEmpty() && setOrder.isMultiUnitProduct) {
                    orderDishName.setText("    " + setOrder.getName() + "(" + setOrder.getUnitName() + ")");
                } else {
                    orderDishName.setText("    " + setOrder.getName());
                }
                orderDishName.setTextColor(R.color.Gray);
                TextView orderDishPrice = (TextView) setOrderLayout.findViewById(R.id.textViewPrice);
                TextView orderDishRealPrice = (TextView) setOrderLayout.findViewById(R.id.textViewRealPrice);

                double price = setOrder.getCurrentPrice();
                if (setOrder.isFree()) {
                    price = setOrder.getOriginPrice();
                }
                double realPrice = 0;
                if (type == Type.ORDER) {
                    realPrice = setOrder.getOriginPrice();
                } else {
                    realPrice = setOrder.getRealCurrentPrice();
                }

                if (price == realPrice) {
                    orderDishPrice.setTextColor(context.getResources().getColor(R.color.check_price_color));
                    orderDishRealPrice.setVisibility(View.GONE);
                } else {
                    orderDishPrice.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
                    orderDishPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    orderDishRealPrice.setVisibility(View.VISIBLE);
                }
                orderDishPrice.setText(OrderUtil.dishPriceFormatter.format(price));
                orderDishRealPrice.setText(OrderUtil.dishPriceFormatter.format(realPrice));

                setOrderLayout.findViewById(R.id.imageView_arrows).setVisibility(View.GONE);

                ingredientLayoutContainer.addView(setOrderLayout);
                LinearLayout remarkLayout = (LinearLayout) setOrderLayout.findViewById(R.id.remarkLayout);
                remarkLayout.removeAllViews();
                if (setOrder.isFree()) {
                    addRemarkTextWithRed(remarkLayout, "赠");
                }
                if (setOrder.getCouponId() > 0) {
                    addRemarkTextWithRed(remarkLayout, "券");
                }
//                if (setOrder.getIsSpecial()) {
//                    addRemarkTextWithRed(remarkLayout, "特");
//                }
                if (setOrder.isPriceChanged() && !currentOrderDetail.isMarket()) {
                    addRemarkTextWithRed(remarkLayout, "改");
                }
                if (setOrder.isVip()) {
                    addRemarkTextWithRed(remarkLayout, "会");
                }
                if (setOrder.isVoucher()) {
                    addRemarkTextWithRed(remarkLayout, "代");
                }
                if (setOrder.isDiscount()) {
                    addRemarkTextWithRed(remarkLayout, "折");
                }
                if (setOrder.isMarket()) {
                    addRemarkTextWithRed(remarkLayout, "时");
                }
                if (setOrder.getVoid_quantity() > 0) {
                    addRemarkTextWithBlue(remarkLayout, "退");
                }
                if (setOrder.isHold()) {
                    addRemarkTextWithBlue(remarkLayout, "挂");
                }

                if (setOrder.getIngredient().size() > 0) {
                    for (int j = 0; j < setOrder.getIngredient().size(); j++) {
                        OrderDetail setIngredient = setOrder.getIngredient().get(j);
                        LinearLayout setIngredientLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);

//                        if(type == Type.PREORDER) {
//                            setIngredientLayout.findViewById(R.id.check).setVisibility(View.INVISIBLE);
//                        } else {
//                            setIngredientLayout.findViewById(R.id.check).setVisibility(View.GONE);
//                        }
                        // set ingredient name
                        TextView setIngredientName = (TextView) setIngredientLayout.findViewById(R.id.textViewOrderListDishName);
                        setIngredientName.setText("-加料: " + setIngredient.getName());// + "<" + setIngredient.getUnitCount() + ">");
                        // set ingredient price
                        // set ingredient price
                        TextView setIngredientPriceText = (TextView) setIngredientLayout.findViewById(R.id.textViewPrice);
                        TextView setIngredientRealPriceText = (TextView) setIngredientLayout.findViewById(R.id.textViewRealPrice);
                        double setIngredientPrice = setIngredient.getCurrentPrice();
                        if (setIngredient.isFree()) {
                            setIngredientPrice = setIngredient.getOriginPrice();
                        }
                        double setIngredientRealPrice = setIngredient.getRealCurrentPrice();
                        if (setIngredientPrice == setIngredientRealPrice) {
                            setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.check_price_color));
                            setIngredientRealPriceText.setVisibility(View.GONE);
                        } else {
                            setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
                            setIngredientPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                            setIngredientRealPriceText.setVisibility(View.VISIBLE);
                        }
                        setIngredientPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientPrice));
                        setIngredientRealPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientRealPrice));

                        // set ingredient quantity
                        TextView setIngredientQuantity = (TextView) setIngredientLayout.findViewById(R.id.textViewOrderListQuantity);
                        int setIngreDientQuantity = setOrder.getQuantity() - setOrder.getVoid_quantity();
                        setIngredientQuantity.setText("x" + String.valueOf(setIngreDientQuantity * setIngredient.getUnitCount()));
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
                        LinearLayout cookLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
//                        if(type == Type.PREORDER) {
//                            cookLayout.findViewById(R.id.check).setVisibility(View.INVISIBLE);
//                        } else {
//                            cookLayout.findViewById(R.id.check).setVisibility(View.GONE);
//                        }
                        // set ingredient name
                        TextView cookName = (TextView) cookLayout.findViewById(R.id.textViewOrderListDishName);
                        cookName.setText("-做法: " + order.getName());
                        // set ingredient price
                        TextView setIngredientPriceText = (TextView) cookLayout.findViewById(R.id.textViewPrice);
                        TextView setIngredientRealPriceText = (TextView) cookLayout.findViewById(R.id.textViewRealPrice);
                        double setIngredientPrice = order.getCurrentPrice();
                        if (order.isFree()) {
                            setIngredientPrice = order.getOriginPrice();
                        }
                        double setIngredientRealPrice = order.getRealCurrentPrice();
                        if (setIngredientPrice == setIngredientRealPrice) {
                            setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.check_price_color));
                            setIngredientRealPriceText.setVisibility(View.GONE);
                        } else {
                            setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
                            setIngredientPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                            setIngredientRealPriceText.setVisibility(View.VISIBLE);
                        }
                        setIngredientPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientPrice));
                        setIngredientRealPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientRealPrice));

                        // set ingredient quantity
                        TextView cookQuantity = (TextView) cookLayout.findViewById(R.id.textViewOrderListQuantity);
                        int cooksquantity = order.getQuantity() - order.getVoid_quantity();
                        cookQuantity.setText("x" + String.valueOf(cooksquantity));

                        ingredientLayoutContainer.addView(cookLayout);
                    }
                }
                if (setOrder.getRemark() != null && setOrder.getRemark().length() != 0) {
                    LinearLayout remark_layout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
                    TextView remarkName = (TextView) remark_layout.findViewById(R.id.textViewOrderListDishName);
                    remarkName.setText("-备注: " + setOrder.getRemark());
                    ingredientLayoutContainer.addView(remark_layout);
                }

            }
        }
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
                LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
//                if(type == Type.PREORDER) {
//                    ingredientLayout.findViewById(R.id.check).setVisibility(View.INVISIBLE);
//                } else {
//                    ingredientLayout.findViewById(R.id.check).setVisibility(View.GONE);
//                }
                // set ingredient name
                TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListDishName);
                ingredientName.setText("-做法: " + order.getName());
                // set ingredient price
                TextView setIngredientPriceText = (TextView) ingredientLayout.findViewById(R.id.textViewPrice);
                TextView setIngredientRealPriceText = (TextView) ingredientLayout.findViewById(R.id.textViewRealPrice);
                double setIngredientPrice = order.getCurrentPrice();
                if (order.isFree()) {
                    setIngredientPrice = order.getOriginPrice();
                }
                double setIngredientRealPrice = order.getRealCurrentPrice();
                if (setIngredientPrice == setIngredientRealPrice) {
                    setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.check_price_color));
                    setIngredientRealPriceText.setVisibility(View.GONE);
                } else {
                    setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
                    setIngredientPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    setIngredientRealPriceText.setVisibility(View.VISIBLE);
                }
                setIngredientPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientPrice));
                setIngredientRealPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientRealPrice));


                // set ingredient quantity
                TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListQuantity);
                int cookQuantity = order.getQuantity() - order.getVoid_quantity();
                ingredientQuantity.setText("x" + String.valueOf(cookQuantity));

                ingredientLayoutContainer.addView(ingredientLayout);
            }
        }
        if (currentOrderDetail.isWeight()) {
            LinearLayout weightLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
            // set ingredient name
            TextView remarkName = (TextView) weightLayout.findViewById(R.id.textViewOrderListDishName);
            remarkName.setText("-称重:" + String.valueOf(currentOrderDetail.getWeight()) + " " + currentOrderDetail.getUnitName());
            TextView setIngredientPriceText = (TextView) weightLayout.findViewById(R.id.textViewPrice);
            setIngredientPriceText.setVisibility(View.GONE);
            TextView setIngredientRealPriceText = (TextView) weightLayout.findViewById(R.id.textViewRealPrice);

            setIngredientRealPriceText.setVisibility(View.GONE);
            ingredientLayoutContainer.addView(weightLayout);
        }
        if (currentOrderDetail.getRemark() != null && currentOrderDetail.getRemark().length() != 0) {
            LinearLayout remarkLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
            // set ingredient name
            TextView remarkName = (TextView) remarkLayout.findViewById(R.id.textViewOrderListDishName);
            remarkName.setText("-备注: " + currentOrderDetail.getRemark());
            TextView reamrkPriceText = (TextView) remarkLayout.findViewById(R.id.textViewPrice);
            TextView reamrkRealPriceText = (TextView) remarkLayout.findViewById(R.id.textViewRealPrice);
            TextView reamrkQuantity = (TextView) remarkLayout.findViewById(R.id.textViewOrderListQuantity);
            reamrkPriceText.setVisibility(View.GONE);
            reamrkRealPriceText.setVisibility(View.GONE);
            reamrkQuantity.setVisibility(View.GONE);
            ingredientLayoutContainer.addView(remarkLayout);
        }
        if (currentOrderDetail.getChildrenOrderDetail().size() > 0) {
            for (int i = 0; i < currentOrderDetail.getChildrenOrderDetail().size(); i++) {
                // add ingredient if any
                TextView textView = new TextView(context);
                textView.setBackgroundColor(Color.parseColor("#D5D5D5"));
                android.widget.LinearLayout.LayoutParams textParams = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1);
                ingredientLayoutContainer.addView(textView, textParams);

                OrderDetail setOrder = currentOrderDetail.getChildrenOrderDetail().get(i);
                LinearLayout setOrderLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_new, null);

                //套餐配菜不能显示选择框
                CheckBox checkBox = (CheckBox) setOrderLayout.findViewById(R.id.check);
                checkBox.setVisibility(View.INVISIBLE);

                TextView orderRowNo = (TextView) setOrderLayout.findViewById(R.id.textViewRowNo);
                orderRowNo.setVisibility(View.INVISIBLE);

                TextView orderQuantity = (TextView) setOrderLayout.findViewById(R.id.textViewQuantity);
                orderQuantity.setText("×" + String.valueOf(setOrder.getQuantity() - setOrder.getVoid_quantity()));

                TextView orderDishName = (TextView) setOrderLayout.findViewById(R.id.textViewDishName);
                if (setOrder.getUnitName() != null && !setOrder.getUnitName().isEmpty() && setOrder.isMultiUnitProduct) {
                    orderDishName.setText("    " + setOrder.getName() + "(" + setOrder.getUnitName() + ")");
                } else {
                    orderDishName.setText("    " + setOrder.getName());
                }
                orderDishName.setTextColor(R.color.Gray);
                TextView orderDishPrice = (TextView) setOrderLayout.findViewById(R.id.textViewPrice);
                TextView orderDishRealPrice = (TextView) setOrderLayout.findViewById(R.id.textViewRealPrice);

                double price = setOrder.getCurrentPrice();
                if (setOrder.isFree()) {
                    price = setOrder.getOriginPrice();
                }
                double realPrice = 0;
                if (type == Type.ORDER) {
                    realPrice = setOrder.getOriginPrice();
                } else {
                    realPrice = setOrder.getRealCurrentPrice();
                }

                if (price == realPrice) {
                    orderDishPrice.setTextColor(context.getResources().getColor(R.color.check_price_color));
                    orderDishRealPrice.setVisibility(View.GONE);
                } else {
                    orderDishPrice.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
                    orderDishPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    orderDishRealPrice.setVisibility(View.VISIBLE);
                }
                orderDishPrice.setText(OrderUtil.dishPriceFormatter.format(price));
                orderDishRealPrice.setText(OrderUtil.dishPriceFormatter.format(realPrice));

                setOrderLayout.findViewById(R.id.imageView_arrows).setVisibility(View.GONE);

                ingredientLayoutContainer.addView(setOrderLayout);
                LinearLayout remarkLayout = (LinearLayout) setOrderLayout.findViewById(R.id.remarkLayout);
                remarkLayout.removeAllViews();
                if (setOrder.isFree()) {
                    addRemarkTextWithRed(remarkLayout, "赠");
                }
                if (setOrder.getCouponId() > 0) {
                    addRemarkTextWithRed(remarkLayout, "券");
                }
//                if (setOrder.getIsSpecial()) {
//                    addRemarkTextWithRed(remarkLayout, "特");
//                }
                if (setOrder.isPriceChanged() && !currentOrderDetail.isMarket()) {
                    addRemarkTextWithRed(remarkLayout, "改");
                }
                if (setOrder.isVip()) {
                    addRemarkTextWithRed(remarkLayout, "会");
                }
                if (setOrder.isVoucher()) {
                    addRemarkTextWithRed(remarkLayout, "代");
                }
                if (setOrder.isDiscount()) {
                    addRemarkTextWithRed(remarkLayout, "折");
                }
                if (setOrder.isMarket()) {
                    addRemarkTextWithRed(remarkLayout, "时");
                }
                if (setOrder.getVoid_quantity() > 0) {
                    addRemarkTextWithBlue(remarkLayout, "退");
                }
                if (setOrder.isHold()) {
                    addRemarkTextWithBlue(remarkLayout, "挂");
                }

                if (setOrder.getIngredient().size() > 0) {
                    for (int j = 0; j < setOrder.getIngredient().size(); j++) {
                        OrderDetail setIngredient = setOrder.getIngredient().get(j);
                        LinearLayout setIngredientLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);

//                        if(type == Type.PREORDER) {
//                            setIngredientLayout.findViewById(R.id.check).setVisibility(View.INVISIBLE);
//                        } else {
//                            setIngredientLayout.findViewById(R.id.check).setVisibility(View.GONE);
//                        }
                        // set ingredient name
                        TextView setIngredientName = (TextView) setIngredientLayout.findViewById(R.id.textViewOrderListDishName);
                        setIngredientName.setText("-加料: " + setIngredient.getName());// + "<" + setIngredient.getUnitCount() + ">");
                        // set ingredient price
                        // set ingredient price
                        TextView setIngredientPriceText = (TextView) setIngredientLayout.findViewById(R.id.textViewPrice);
                        TextView setIngredientRealPriceText = (TextView) setIngredientLayout.findViewById(R.id.textViewRealPrice);
                        double setIngredientPrice = setIngredient.getCurrentPrice();
                        if (setIngredient.isFree()) {
                            setIngredientPrice = setIngredient.getOriginPrice();
                        }
                        double setIngredientRealPrice = setIngredient.getRealCurrentPrice();
                        if (setIngredientPrice == setIngredientRealPrice) {
                            setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.check_price_color));
                            setIngredientRealPriceText.setVisibility(View.GONE);
                        } else {
                            setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
                            setIngredientPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                            setIngredientRealPriceText.setVisibility(View.VISIBLE);
                        }
                        setIngredientPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientPrice));
                        setIngredientRealPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientRealPrice));

                        // set ingredient quantity
                        TextView setIngredientQuantity = (TextView) setIngredientLayout.findViewById(R.id.textViewOrderListQuantity);
                        int setIngreDientQuantity = setOrder.getQuantity() - setOrder.getVoid_quantity();
                        setIngredientQuantity.setText("x" + String.valueOf(setIngreDientQuantity * setIngredient.getUnitCount()));
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
                        LinearLayout cookLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
//                        if(type == Type.PREORDER) {
//                            cookLayout.findViewById(R.id.check).setVisibility(View.INVISIBLE);
//                        } else {
//                            cookLayout.findViewById(R.id.check).setVisibility(View.GONE);
//                        }
                        // set ingredient name
                        TextView cookName = (TextView) cookLayout.findViewById(R.id.textViewOrderListDishName);
                        cookName.setText("-做法: " + order.getName());
                        // set ingredient price
                        TextView setIngredientPriceText = (TextView) cookLayout.findViewById(R.id.textViewPrice);
                        TextView setIngredientRealPriceText = (TextView) cookLayout.findViewById(R.id.textViewRealPrice);
                        double setIngredientPrice = order.getCurrentPrice();
                        if (order.isFree()) {
                            setIngredientPrice = order.getOriginPrice();
                        }
                        double setIngredientRealPrice = order.getRealCurrentPrice();
                        if (setIngredientPrice == setIngredientRealPrice) {
                            setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.check_price_color));
                            setIngredientRealPriceText.setVisibility(View.GONE);
                        } else {
                            setIngredientPriceText.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
                            setIngredientPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                            setIngredientRealPriceText.setVisibility(View.VISIBLE);
                        }
                        setIngredientPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientPrice));
                        setIngredientRealPriceText.setText(OrderUtil.dishPriceFormatter.format(setIngredientRealPrice));

                        // set ingredient quantity
                        TextView cookQuantity = (TextView) cookLayout.findViewById(R.id.textViewOrderListQuantity);
                        int cooksquantity = order.getQuantity() - order.getVoid_quantity();
                        cookQuantity.setText("x" + String.valueOf(cooksquantity));

                        ingredientLayoutContainer.addView(cookLayout);
                    }
                }
                if (setOrder.getRemark() != null && setOrder.getRemark().length() != 0) {
                    LinearLayout remark_layout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
                    TextView remarkName = (TextView) remark_layout.findViewById(R.id.textViewOrderListDishName);
                    remarkName.setText("-备注: " + setOrder.getRemark());
                    ingredientLayoutContainer.addView(remark_layout);
                }

            }
        }

        TextView orderRowNo = (TextView) vi.findViewById(R.id.textViewRowNo);

        boolean isNotSetItem = false;
        int sortOrder = -1;
        for (int j = 0; j < orderDetailList.size(); j++) {
            if (orderDetailList.get(j) == currentOrderDetail) {
                sortOrder = j;
                isNotSetItem = true;
                break;
            }
        }
        if (isNotSetItem) {
            orderRowNo.setVisibility(View.VISIBLE);
//            orderRowNo.setBackgroundColor(Color.parseColor(getRowNoColor(sortOrder)));
            orderRowNo.setText(Integer.toString(sortOrder + 1));
        } else {
            orderRowNo.setVisibility(View.INVISIBLE);
        }
        LinearLayout remarkLayout = (LinearLayout) vi.findViewById(R.id.remarkLayout);
        remarkLayout.removeAllViews();
        if (currentOrderDetail.isFree()) {
            addRemarkTextWithRed(remarkLayout, "赠");
        }
        if (currentOrderDetail.getCouponId() > 0) {
            addRemarkTextWithRed(remarkLayout, "券");
        }
        if (currentOrderDetail.getIsSpecial()) {
            addRemarkTextWithRed(remarkLayout, "特");
        }
        if (currentOrderDetail.isPriceChanged() && !currentOrderDetail.isMarket()) {
            addRemarkTextWithRed(remarkLayout, "改");
        }
        if (currentOrderDetail.isVip()) {
            addRemarkTextWithRed(remarkLayout, "会");
        }
        if (currentOrderDetail.isVoucher()) {
            addRemarkTextWithRed(remarkLayout, "代");
        }
        if (currentOrderDetail.isDiscount()) {
            addRemarkTextWithRed(remarkLayout, "折");
        }
        if (currentOrderDetail.isMarket()) {
            addRemarkTextWithRed(remarkLayout, "时");
        }
        if (currentOrderDetail.getVoid_quantity() > 0) {
            addRemarkTextWithBlue(remarkLayout, "退");
        }
        if (currentOrderDetail.isHold()) {
            addRemarkTextWithBlue(remarkLayout, "挂");
        }
        if (currentOrderDetail.isWeight()) {
            addRemarkTextWithBlue(remarkLayout, "称");
        }
        TextView orderDishName = (TextView) vi.findViewById(R.id.textViewDishName);
        if (currentOrderDetail.getUnitName() != null && !currentOrderDetail.getUnitName().isEmpty() && currentOrderDetail.isMultiUnitProduct) {
            orderDishName.setText(currentOrderDetail.getName() + "(" + currentOrderDetail.getUnitName() + ")");
        } else {
            orderDishName.setText(currentOrderDetail.getName());
        }
        TextView orderDishPrice = (TextView) vi.findViewById(R.id.textViewPrice);
        TextView orderDishRealPrice = (TextView) vi.findViewById(R.id.textViewRealPrice);
        double price = currentOrderDetail.getOriginPrice();
        if (currentOrderDetail.isFree() || (currentOrderDetail.isPriceChanged() && !currentOrderDetail.isMarket())) {
            orderDishPrice.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
            orderDishPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            orderDishRealPrice.setVisibility(View.VISIBLE);
        } else {
            orderDishPrice.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            orderDishPrice.getPaint().setTypeface(Typeface.DEFAULT);
            orderDishPrice.setTextColor(context.getResources().getColor(R.color.check_price_color));
            orderDishRealPrice.setVisibility(View.GONE);
        }
        double realPrice = 0;
        realPrice = currentOrderDetail.getRealCurrentPrice();

        orderDishPrice.setText(OrderUtil.dishPriceFormatter.format(price));
        orderDishRealPrice.setText(OrderUtil.dishPriceFormatter.format(realPrice));

        TextView orderQuantity = (TextView) vi.findViewById(R.id.textViewQuantity);
        orderQuantity.setText("×" + String.valueOf(currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity()));
        ImageView imageView = (ImageView) vi.findViewById(R.id.imageView_arrows);
        CheckBox checkBox = (CheckBox) vi.findViewById(R.id.check);
        checkBox.setTag(new Integer(position));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked.set(((Integer) buttonView.getTag()).intValue(), isChecked);
            }
        });
        switch (type) {
            case CHECK:
                imageView.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                break;
            case ORDER:
                if (currentOrderDetail.isPlaced()) {
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                }
                checkBox.setVisibility(View.GONE);
                break;
            case PREORDER:
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(checked.get(position).booleanValue());
                imageView.setVisibility(View.GONE);
                break;
        }
        return vi;

    }

    public void addRemarkTextWithRed(LinearLayout layout, String remarkText) {
        TextView textView = new TextView(context);
        textView.setText(remarkText);
        textView.setTextSize(context.getResources().getDimension(R.dimen.app_info_text_size));
        textView.setPadding(2, 0, 2, 0);
        textView.setTextColor(Color.parseColor("#FF0000"));
        textView.setBackgroundResource(R.drawable.red_remark_bg);
        layout.addView(textView, params);
    }


    public void addRemarkTextWithBlue(LinearLayout layout, String remarkText) {
        TextView textView = new TextView(context);
        textView.setText(remarkText);
        textView.setTextColor(context.getResources().getColor(R.color.title_table_background));
        textView.setTextSize(context.getResources().getDimension(R.dimen.app_info_text_size));
        textView.setPadding(2, 0, 2, 0);
        textView.setBackgroundResource(R.drawable.remark_bg);
        layout.addView(textView, params);
    }

    private String getRowNoColor(int position) {
        return colors[position % colors.length];
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
