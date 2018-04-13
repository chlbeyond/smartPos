package com.rainbow.smartpos.order;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.common.Converter;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.AppendProductGoodsSpec;
import com.sanyipos.sdk.model.rest.AppendProductGroup;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by administrator on 2018/3/5.
 */

public class ChildFoodGvAdapter extends BaseAdapter {

    private Context mContext;
    private List<AppendProductGoodsSpec> list;
    private List<ChildFoodAdapter.TempAppend> tempAppends;
    private ChildFoodGvListener childFoodGvListener;


    public ChildFoodGvAdapter(Context mContext, List<AppendProductGoodsSpec> list, List<ChildFoodAdapter.TempAppend> appends) {
        this.mContext = mContext;
        this.list = list;
        this.tempAppends = appends;
    }

    public void setChildFoodGvListener(ChildFoodGvListener listener) {
        childFoodGvListener = listener;
    }

    public interface ChildFoodGvListener {
        void add(int position);

        void min(int position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public AppendProductGoodsSpec getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.child_food_gv_layout, null);
            holder = new ViewHolder();
            holder.setView(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        ProductRest productRest = SanyiSDK.rest.getProductByGoodsId(list.get(position).goods);
        holder.childFoodGvNameTv.setText(productRest.getName());


        if (tempAppends.get(position).getNum() > 0) {
//            holder.childFoodGvLl.setBackground(mContext.getDrawable(R.drawable.bg_corner_child_blue));
//            holder.childFoodGvNameTv.setTextColor(Color.WHITE);
//            holder.childFoodGvPriceTv.setTextColor(Color.WHITE);
            holder.childFoodGvPriceTv.setText("¥ " + productRest.getOriginPrice() + " * " + tempAppends.get(position).getNum());
            holder.childFoodGvAddminLl.setVisibility(View.VISIBLE);
            holder.childFoodGvPriceTv.setTextSize(12);
        } else {
//            holder.childFoodGvLl.setBackground(mContext.getDrawable(R.drawable.bg_corner_child_empty));
//            holder.childFoodGvNameTv.setTextColor(mContext.getResources().getColor(R.color.bill_fragment_mid_right_text));
//            holder.childFoodGvPriceTv.setTextColor(mContext.getResources().getColor(R.color.bill_fragment_mid_right_text));
            holder.childFoodGvPriceTv.setText("¥ " + productRest.getOriginPrice());
            holder.childFoodGvAddminLl.setVisibility(View.GONE);
            holder.childFoodGvPriceTv.setTextSize(15);
        }

        holder.childFoodGvContainLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(childFoodGvListener!=null)
                    childFoodGvListener.add(position);
            }
        });

        holder.childFoodGvMinuseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(childFoodGvListener!=null)
                    childFoodGvListener.min(position);
            }
        });


        return convertView;
    }


    class ViewHolder {
        TextView childFoodGvNameTv;
        LinearLayout childFoodGvAddminLl;
        ImageView childFoodGvMinuseIv;
        ImageView childFoodGvAddIv;
        TextView childFoodGvPriceTv;
        LinearLayout childFoodGvLl;
        LinearLayout childFoodGvContainLl;


        public void setView(View v) {
            childFoodGvNameTv = (TextView) v.findViewById(R.id.tv_child_food_gv_name);
            childFoodGvLl = (LinearLayout) v.findViewById(R.id.ll_child_food_gv);
            childFoodGvAddminLl = (LinearLayout) v.findViewById(R.id.ll_child_food_gv_addmin);
            childFoodGvMinuseIv = (ImageView) v.findViewById(R.id.iv_child_food_gv_minuse);
            childFoodGvAddIv = (ImageView) v.findViewById(R.id.iv_child_food_gv_add);
            childFoodGvPriceTv = (TextView) v.findViewById(R.id.tv_child_food_gv_price);
            childFoodGvContainLl= (LinearLayout) v.findViewById(R.id.ll_child_food_gv_contain);
        }
    }


}
