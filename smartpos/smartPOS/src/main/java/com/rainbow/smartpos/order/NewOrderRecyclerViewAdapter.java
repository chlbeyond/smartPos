package com.rainbow.smartpos.order;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/9/10.
 */
public class NewOrderRecyclerViewAdapter extends RecyclerView.Adapter<NewOrderRecyclerViewAdapter.OrderHolder> {

    public List<OrderDetail> orderDetails = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public NewOrderRecyclerViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderHolder(LayoutInflater.from(context).inflate(R.layout.check_detail_list_row_new, null));
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        OrderDetail currentOrderDetail = orderDetails.get(position);

        holder.ingredientLinearLayout.removeAllViews();
        if (currentOrderDetail.getIngredient().size() > 0) {

            for (int i = 0; i < currentOrderDetail.getIngredient().size(); i++) {
                // add ingredient if any
                OrderDetail ingredient = currentOrderDetail.getIngredient().get(i);
                LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);

                // set ingredient name
                TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListDishName);
                ingredientName.setText("-加料: " + ingredient.getName() + "(" + ingredient.getUnitCount() + ")");

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
                ingredientQuantity.setText("x" + String.valueOf(quantity * ingredient.getUnitCount()));

                holder.ingredientLinearLayout.addView(ingredientLayout);
            }
        }

        if (currentOrderDetail.getSetOrderDetailList().size() > 0) {
            for (int i = 0; i < currentOrderDetail.getSetOrderDetailList().size(); i++) {
                // add ingredient if any
                TextView textView = new TextView(context);
                textView.setBackgroundColor(Color.parseColor("#D5D5D5"));
                android.widget.LinearLayout.LayoutParams textParams = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1);
                holder.ingredientLinearLayout.addView(textView, textParams);

                OrderDetail setOrder = currentOrderDetail.getSetOrderDetailList().get(i);
                LinearLayout setOrderLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_new, null);
                TextView orderRowNo = (TextView) setOrderLayout.findViewById(R.id.textViewRowNo);
                orderRowNo.setVisibility(View.INVISIBLE);

                TextView orderDishName = (TextView) setOrderLayout.findViewById(R.id.textViewDishName);
                if (setOrder.getUnitName() != null && !setOrder.getUnitName().isEmpty() && setOrder.isMultiUnitProduct) {
                    orderDishName.setText(setOrder.getName() + "(" + setOrder.getUnitName() + ")");
                } else {
                    orderDishName.setText(setOrder.getName());
                }
                TextView orderDishPrice = (TextView) setOrderLayout.findViewById(R.id.textViewPrice);
                TextView orderDishRealPrice = (TextView) setOrderLayout.findViewById(R.id.textViewRealPrice);

                double price = setOrder.getCurrentPrice();
                if (setOrder.isFree()) {
                    price = setOrder.getOriginPrice();
                }
                double realPrice = 0;
                realPrice = setOrder.getOriginPrice();


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

                holder.ingredientLinearLayout.addView(setOrderLayout);
                LinearLayout remarkLayout = (LinearLayout) setOrderLayout.findViewById(R.id.remarkLayout);
                remarkLayout.removeAllViews();
                if (setOrder.isFree()) {
                    addRemarkTextWithRed(remarkLayout, "赠");
                }
                if (setOrder.getCouponId() > 0) {
                    addRemarkTextWithRed(remarkLayout, "券");
                }
                if (setOrder.getIsSpecial()) {
                    addRemarkTextWithRed(remarkLayout, "特");
                }
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

                        // set ingredient name
                        TextView setIngredientName = (TextView) setIngredientLayout.findViewById(R.id.textViewOrderListDishName);
                        setIngredientName.setText("-加料: " + setIngredient.getName() + "<" + setIngredient.getUnitCount() + ">");
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
                        holder.ingredientLinearLayout.addView(setIngredientLayout);
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

                        holder.ingredientLinearLayout.addView(cookLayout);
                    }
                }
                if (setOrder.getRemark() != null && setOrder.getRemark().length() != 0) {
                    LinearLayout remark_layout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
                    TextView remarkName = (TextView) remark_layout.findViewById(R.id.textViewOrderListDishName);
                    remarkName.setText("-备注: " + setOrder.getRemark());
                    holder.ingredientLinearLayout.addView(remark_layout);
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

                holder.ingredientLinearLayout.addView(ingredientLayout);
            }
        }
        if (currentOrderDetail.isWeight()) {
            LinearLayout remarkLayout = (LinearLayout) inflater.inflate(R.layout.check_detail_list_row_child_new, null);
            // set ingredient name
            TextView remarkName = (TextView) remarkLayout.findViewById(R.id.textViewOrderListDishName);
            remarkName.setText("-称重:" + String.valueOf(currentOrderDetail.getWeight()) + " " + currentOrderDetail.getUnitName());
            TextView setIngredientPriceText = (TextView) remarkLayout.findViewById(R.id.textViewPrice);
            setIngredientPriceText.setVisibility(View.INVISIBLE);
            TextView setIngredientRealPriceText = (TextView) remarkLayout.findViewById(R.id.textViewRealPrice);

            setIngredientRealPriceText.setVisibility(View.INVISIBLE);
            holder.ingredientLinearLayout.addView(remarkLayout);
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
            holder.ingredientLinearLayout.addView(remarkLayout);
        }

//        TextView orderRowNo = (TextView) vi.findViewById(R.id.textViewRowNo);
//
//        boolean isNotSetItem = false;
//        int sortOrder = -1;
//        for (int j = 0; j < orderDetailList.size(); j++) {
//            if (orderDetailList.get(j) == currentOrderDetail) {
//                sortOrder = j;
//                isNotSetItem = true;
//                break;
//            }
//        }
//        if (isNotSetItem) {
//            orderRowNo.setVisibility(View.VISIBLE);
////            orderRowNo.setBackgroundColor(Color.parseColor(getRowNoColor(sortOrder)));
//            orderRowNo.setText(Integer.toString(sortOrder + 1));
//        } else {
//            orderRowNo.setVisibility(View.INVISIBLE);
//        }

        holder.remarkLinearLayout.removeAllViews();
        if (currentOrderDetail.isFree()) {
            addRemarkTextWithRed(holder.remarkLinearLayout, "赠");
        }
        if (currentOrderDetail.getCouponId() > 0) {
            addRemarkTextWithRed(holder.remarkLinearLayout, "券");
        }
        if (currentOrderDetail.getIsSpecial()) {
            addRemarkTextWithRed(holder.remarkLinearLayout, "特");
        }
        if (currentOrderDetail.isPriceChanged() && !currentOrderDetail.isMarket()) {
            addRemarkTextWithRed(holder.remarkLinearLayout, "改");
        }
        if (currentOrderDetail.isVip()) {
            addRemarkTextWithRed(holder.remarkLinearLayout, "会");
        }
        if (currentOrderDetail.isVoucher()) {
            addRemarkTextWithRed(holder.remarkLinearLayout, "代");
        }
        if (currentOrderDetail.isDiscount()) {
            addRemarkTextWithRed(holder.remarkLinearLayout, "折");
        }
        if (currentOrderDetail.isMarket()) {
            addRemarkTextWithRed(holder.remarkLinearLayout, "时");
        }
        if (currentOrderDetail.getVoid_quantity() > 0) {
            addRemarkTextWithBlue(holder.remarkLinearLayout, "退");
        }
        if (currentOrderDetail.isHold()) {
            addRemarkTextWithBlue(holder.remarkLinearLayout, "挂");
        }

        if (currentOrderDetail.getUnitName() != null && !currentOrderDetail.getUnitName().isEmpty() && currentOrderDetail.isMultiUnitProduct) {
            holder.textViewDishName.setText(currentOrderDetail.getName() + "(" + currentOrderDetail.getUnitName() + ")");
        } else {
            holder.textViewDishName.setText(currentOrderDetail.getName());
        }

        double price = currentOrderDetail.getOriginPrice();
        if (currentOrderDetail.isFree() || (currentOrderDetail.isPriceChanged() && !currentOrderDetail.isMarket())) {
            price = currentOrderDetail.getOriginPrice();
        }

        double realPrice = 0;

        realPrice = currentOrderDetail.getOriginPrice();

        if (price == realPrice) {
            holder.textViewDishPrice.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            holder.textViewDishPrice.getPaint().setTypeface(Typeface.DEFAULT);
            holder.textViewDishPrice.setTextColor(context.getResources().getColor(R.color.check_price_color));
            holder.textViewRealPrice.setVisibility(View.GONE);
        } else {
            holder.textViewDishPrice.setTextColor(context.getResources().getColor(R.color.cook_method_text_color));
            holder.textViewDishPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            holder.textViewRealPrice.setVisibility(View.VISIBLE);
        }
        holder.textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(price));
        holder.textViewRealPrice.setText(OrderUtil.dishPriceFormatter.format(realPrice));


        holder.textViewQuantity.setText("×" + String.valueOf(currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity()));


        if (currentOrderDetail.isPlaced()) {
            holder.imageViewArrow.setVisibility(View.GONE);
        } else {
            holder.imageViewArrow.setVisibility(View.VISIBLE);
        }


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

    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public void addRemarkTextWithBlue(LinearLayout layout, String remarkText) {
        TextView textView = new TextView(context);
        textView.setText(remarkText);
        textView.setTextColor(context.getResources().getColor(R.color.title_table_background));
        textView.setTextSize(context.getResources().getDimension(R.dimen.app_info_text_size));
        textView.setPadding(2, 0, 2, 0);
        textView.setBackgroundResource(R.drawable.remark_bg);
        layout.addView(textView, params);
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        public LinearLayout ingredientLinearLayout;
        public TextView textViewQuantity;
        public LinearLayout remarkLinearLayout;
        public TextView textViewDishName;
        public TextView textViewDishPrice;
        public TextView textViewRealPrice;
        public ImageView imageViewArrow;

        public OrderHolder(View itemView) {
            super(itemView);
            ingredientLinearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutCheckOrderIngredient);
            textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
            remarkLinearLayout = (LinearLayout) itemView.findViewById(R.id.remarkLayout);
            textViewDishName = (TextView) itemView.findViewById(R.id.textViewDishName);
            textViewDishPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
            textViewRealPrice = (TextView) itemView.findViewById(R.id.textViewRealPrice);
            imageViewArrow = (ImageView) itemView.findViewById(R.id.imageView_arrows);
        }
    }
}
