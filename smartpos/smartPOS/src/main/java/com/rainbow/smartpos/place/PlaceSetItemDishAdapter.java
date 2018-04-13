package com.rainbow.smartpos.place;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
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

public class PlaceSetItemDishAdapter extends BaseAdapter {
    private Context context;
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private OrderDetail currentOrderDetail = null;
    ;
    private LayoutInflater mInflater;
    //private List<String> selectPos = new ArrayList<String>();
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public PlaceSetItemDishAdapter(Context context, OrderDetail orderDetail) {
        super();
        this.context = context;
        this.currentOrderDetail = orderDetail;
        if (currentOrderDetail.childrenOrderDetail != null && currentOrderDetail.childrenOrderDetail.size() > 0)
            this.orderDetails.addAll(currentOrderDetail.childrenOrderDetail);
        else
            this.orderDetails.addAll(currentOrderDetail.setOrders);
        mInflater = LayoutInflater.from(context);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(1, 1, 1, 1);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return orderDetails.size();
    }

    @Override
    public OrderDetail getItem(int position) {
        // TODO Auto-generated method stub
        return orderDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final OrderDetail orderDetail = orderDetails.get(position);
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.place_single_dish_item, null);
            viewHolder.remarkLayout = (LinearLayout) convertView.findViewById(R.id.remarkLayout);
            viewHolder.textViewDishName = (TextView) convertView.findViewById(R.id.textViewDishName);
            viewHolder.textViewDishWeight = (TextView) convertView.findViewById(R.id.textViewDishWeight);
            viewHolder.textViewDishPrice = (TextView) convertView.findViewById(R.id.textViewDishPrice);
            viewHolder.textViewDishCount = (TextView) convertView.findViewById(R.id.textViewDishCount);
            viewHolder.textViewDishReturnCount = (TextView) convertView.findViewById(R.id.textViewDishReturnCount);
            viewHolder.cookHint = (TextView) convertView.findViewById(R.id.cook_hint);
            viewHolder.ingredientHint = (TextView) convertView.findViewById(R.id.ingredient_hint);
            viewHolder.remarkHint = (TextView) convertView.findViewById(R.id.remark_hint);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textViewDishName.setText(orderDetail.getName());
        viewHolder.textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(orderDetail.getCurrentPrice()));
        if (orderDetail.isFree()) {
            viewHolder.textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(orderDetail.getOriginPrice()));
        }
        if (orderDetail.isWeight()) {
            viewHolder.textViewDishWeight.setVisibility(View.VISIBLE);
            viewHolder.textViewDishWeight.setText(orderDetail.getWeight() + orderDetail.getUnitName());
        }
            viewHolder.textViewDishCount.setText("x" + orderDetail.getQuantity());
        viewHolder.textViewDishName.setTextColor(context.getResources().getColor(R.color.Black));
        viewHolder.textViewDishPrice.setTextColor(context.getResources().getColor(R.color.order_fragment_price_textcolor));
        viewHolder.textViewDishCount.setTextColor(context.getResources().getColor(R.color.order_fragment_price_textcolor));
        viewHolder.textViewDishReturnCount.setVisibility(View.GONE);
        viewHolder.cookHint.setVisibility(View.GONE);
        viewHolder.ingredientHint.setVisibility(View.GONE);
        viewHolder.remarkLayout.removeAllViews();
        convertView.setBackgroundResource(R.drawable.place_setitem_dish_bg_selector);
        if (orderDetail.getQuantity() == orderDetail.getVoid_quantity()) {
            viewHolder.textViewDishName.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishPrice.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishWeight.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishCount.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishReturnCount.setVisibility(View.VISIBLE);
            viewHolder.textViewDishReturnCount.setText(context.getString(R.string.has_return_dish));
            viewHolder.textViewDishName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else if (orderDetail.getQuantity() > orderDetail.getVoid_quantity() && orderDetail.getVoid_quantity() > 0) {
            viewHolder.textViewDishReturnCount.setVisibility(View.VISIBLE);
            viewHolder.textViewDishReturnCount.setText("退量 " + orderDetail.getVoid_quantity());
        }
//		if (orderDetail.getQuantity() != orderDetail.getVoid_quantity()){
//			if (orderDetail.canChoose) {
//				convertView.setBackgroundResource(R.drawable.place_dish_bg_can_choose);
//				if (orderDetail.isChoose) {
//					convertView.setBackgroundResource(R.drawable.place_dish_bg_has_choose);
//				}
//			}
//		}


        if (orderDetail.getPublicCookMethod().size() > 0) {
            viewHolder.cookHint.setVisibility(View.VISIBLE);
        }
        if (orderDetail.getIngredient().size() > 0) {
            viewHolder.ingredientHint.setVisibility(View.VISIBLE);
        }
        if (null != orderDetail.getRemark() && !orderDetail.getRemark().isEmpty()) {
            viewHolder.remarkHint.setVisibility(View.VISIBLE);
        }
        if (orderDetail.isHold()) {
            addRemarkText(viewHolder.remarkLayout, "挂");
        }
        if (orderDetail.isFree()) {
            addRemarkText(viewHolder.remarkLayout, "赠");
        }
        if (orderDetail.getCouponId() > 0) {
            addRemarkText(viewHolder.remarkLayout, "劵");
        }
        if (orderDetail.isPriceChanged() && !orderDetail.isMarket()) {
            addRemarkText(viewHolder.remarkLayout, "改");
        }
        if (orderDetail.isMarket()) {
            addRemarkText(viewHolder.remarkLayout, "时");
        }
        return convertView;
    }

    public void addRemarkText(LinearLayout layout, String remarkText) {
        TextView textView = new TextView(context);
        textView.setText(remarkText);
        textView.setTextSize(context.getResources().getDimension(R.dimen.app_info_text_size));
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.RED);
        layout.addView(textView, params);
    }

    public boolean isAllChoose() {
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail orderDetail = orderDetails.get(i);
//			if (!orderDetail.isChoose) {
//				return false;
//			}
        }
        return true;
    }

    public int getChooseCount() {
        int count = 0;
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail orderDetail = orderDetails.get(i);
//			if (orderDetail.isChoose) {
//				count++;
//			}
        }
        return count;
    }

    public List<OrderDetail> getSelect() {
        List<OrderDetail> setOrders = new ArrayList<OrderDetail>();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail orderDetail = orderDetails.get(i);
//			if (orderDetail.isChoose) {
//				setOrders.add(orderDetail);
//			}
        }
        return setOrders;
    }

    private class ViewHolder {
        TextView textViewDishName;
        TextView textViewDishPrice;
        TextView textViewDishCount;
        TextView textViewDishWeight;
        TextView textViewDishReturnCount;
        TextView cookHint;
        TextView ingredientHint;
        TextView remarkHint;
        LinearLayout remarkLayout;
    }

}
