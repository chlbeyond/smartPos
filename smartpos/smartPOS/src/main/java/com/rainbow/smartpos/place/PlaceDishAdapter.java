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

import java.util.List;

public class PlaceDishAdapter extends BaseAdapter {
    private static final String TAG = "PlaceDishAdapter";
    private Context context;
    private List<OrderDetail> orderDetails;
    private List<OrderDetail> selectedDetails;
    private LayoutInflater mInflater;
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public PlaceDishAdapter(Context context, List<OrderDetail> orderDetails, List<OrderDetail> selectedDetail) {
        super();
        this.context = context;
        this.orderDetails = orderDetails;
        this.selectedDetails = selectedDetail;
        mInflater = LayoutInflater.from(context);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(1, 1, 1, 1);
    }

    public void setSelectedDetails(List<OrderDetail> orderDetails) {
        this.selectedDetails = orderDetails;
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

    public void updateView(View view, int itemIndex) {

        getView(itemIndex, view, null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        OrderDetail orderDetail = orderDetails.get(position);
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.place_single_dish_item, null);
            viewHolder.remarkLayout = (LinearLayout) convertView.findViewById(R.id.remarkLayout);
            viewHolder.textViewDishName = (TextView) convertView.findViewById(R.id.textViewDishName);
            viewHolder.textViewDishWeight = (TextView) convertView.findViewById(R.id.textViewDishWeight);
            viewHolder.textViewDishPrice = (TextView) convertView.findViewById(R.id.textViewDishPrice);
            viewHolder.textViewDishCount = (TextView) convertView.findViewById(R.id.textViewDishCount);
            viewHolder.textViewDishCurrPrice = (TextView) convertView.findViewById(R.id.currPriceTextView);
            viewHolder.textViewHold = (TextView) convertView.findViewById(R.id.holdTextView);
            viewHolder.textViewSend = (TextView) convertView.findViewById(R.id.sendTextView);
            viewHolder.textViewChanged = (TextView) convertView.findViewById(R.id.changedTextView);
            viewHolder.textViewQuan = (TextView) convertView.findViewById(R.id.quanTextView);
            viewHolder.textViewDishReturnCount = (TextView) convertView.findViewById(R.id.textViewDishReturnCount);
            viewHolder.cookHint = (TextView) convertView.findViewById(R.id.cook_hint);
            viewHolder.ingredientHint = (TextView) convertView.findViewById(R.id.ingredient_hint);
            viewHolder.remarkHint = (TextView) convertView.findViewById(R.id.remark_hint);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (orderDetail.getUnitName() != null && !orderDetail.getUnitName().isEmpty() && orderDetail.isMultiUnitProduct) {
            viewHolder.textViewDishName.setText(orderDetail.getName() + "(" + orderDetail.getUnitName() + ")");
        } else {
            viewHolder.textViewDishName.setText(orderDetail.getName());
        }
        viewHolder.textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(orderDetail.getCurrentPrice()));
        if (orderDetail.isFree()) {
            viewHolder.textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(0));
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
        viewHolder.remarkHint.setVisibility(View.GONE);
//        viewHolder.remarkLayout.removeAllViews();
        convertView.setBackgroundResource(R.drawable.place_dish_bg_selector);
        for (OrderDetail order : selectedDetails) {
            if (order == orderDetail) {
                convertView.setBackgroundResource(R.drawable.place_dish_bg_selected);
                viewHolder.textViewDishName.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.textViewDishPrice.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.textViewDishCount.setTextColor(context.getResources().getColor(R.color.white));
            }
        }
        if (orderDetail.getQuantity() == orderDetail.getVoid_quantity()) {
            viewHolder.textViewDishName.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishPrice.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishCount.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishReturnCount.setVisibility(View.VISIBLE);
            viewHolder.textViewDishReturnCount.setText(context.getString(R.string.has_return_dish));
            viewHolder.textViewDishName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else if (orderDetail.getQuantity() > orderDetail.getVoid_quantity() && orderDetail.getVoid_quantity() > 0) {
            viewHolder.textViewDishReturnCount.setVisibility(View.VISIBLE);
            viewHolder.textViewDishReturnCount.setText("退量 " + orderDetail.getVoid_quantity());
        }

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
//            addRemarkText(viewHolder.remarkLayout, "挂");
            viewHolder.textViewHold.setVisibility(View.VISIBLE);
        }else
            viewHolder.textViewHold.setVisibility(View.GONE);
        if (orderDetail.isFree()) {
//            addRemarkText(viewHolder.remarkLayout, "赠");
            viewHolder.textViewSend.setVisibility(View.VISIBLE);
        }else
            viewHolder.textViewSend.setVisibility(View.GONE);
        if (orderDetail.getCouponId() > 0) {
//            addRemarkText(viewHolder.remarkLayout, "劵");
            viewHolder.textViewQuan.setVisibility(View.VISIBLE);
        }else
            viewHolder.textViewQuan.setVisibility(View.GONE);
        if (orderDetail.isPriceChanged() && !orderDetail.isMarket()) {
//            addRemarkText(viewHolder.remarkLayout, "改");
            viewHolder.textViewChanged.setVisibility(View.VISIBLE);
        }else
            viewHolder.textViewChanged.setVisibility(View.GONE);
        if (orderDetail.isMarket()) {
//            addRemarkText(viewHolder.remarkLayout, "时");
            viewHolder.textViewDishCurrPrice.setVisibility(View.VISIBLE);
        }else
            viewHolder.textViewDishCurrPrice.setVisibility(View.GONE);
        return convertView;
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

    public void remove(int position) {
        orderDetails.remove(position);
    }

    private class ViewHolder {
        TextView textViewDishName;
        TextView textViewDishWeight;
        TextView textViewDishPrice;
        TextView textViewDishCount;
        TextView textViewDishCurrPrice;
        TextView textViewHold;
        TextView textViewSend;
        TextView textViewChanged;
        TextView textViewQuan;
        TextView textViewDishReturnCount;
        TextView cookHint;
        TextView ingredientHint;
        TextView remarkHint;
        LinearLayout remarkLayout;
    }

}
