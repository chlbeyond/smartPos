package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.Units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by administrator on 2017/11/22.
 */

public class SecondDiscountAdapter extends BaseAdapter {


    private Context mContext;
    private List<Units> list = new ArrayList<>();
    private int maxnum;
    private int hasNum = 0;

    public HashMap<Long, Integer> getHasMap() {
        return hasMap;
    }

    public HashMap<Long, Integer> hasMap = new HashMap<>();

    public SecondDiscountAdapter(Context mContext, List<Units> list, int maxnum) {
        this.mContext = mContext;
        this.list.clear();
        this.maxnum = maxnum;
        this.list.addAll(list);
        initMap();
    }

    private void initMap() {
        for (int i = 0; i < list.size(); i++) {
            hasMap.put(list.get(i).products.get(0).getId(), 0);
        }
        hasNum = 0;
    }

    public void update(List<Units> list, int maxnum) {
        this.list.clear();
        this.maxnum = maxnum;
        this.list.addAll(list);
        initMap();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();

    }


    @Override
    public Object getItem(int position) {
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
            convertView = View.inflate(mContext, R.layout.item_choose_second_discount, null);
            holder = new ViewHolder();
            holder.itemSecondDiscountNameTv = (TextView) convertView.findViewById(R.id.tv_item_choose_second_disconut_name);
            holder.itemSecondDiscountNumTv = (TextView) convertView.findViewById(R.id.tv_item_choose_second_disconut_num);
            holder.itemSecondDiscountMinuesIv = (ImageView) convertView.findViewById(R.id.iv_item_choose_second_disconut_minues);
            holder.itemSecondDiscountAddIv = (ImageView) convertView.findViewById(R.id.iv_item_choose_second_disconut_add);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final Units unit;
        final ProductRest dish;

        unit = list.get(position);

        dish = unit.products.get(0);


        holder.itemSecondDiscountNameTv.setText(dish.getName());
        holder.itemSecondDiscountNumTv.setText(hasMap.get(list.get(position).products.get(0).getId()) + "");
        holder.itemSecondDiscountAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasNum < maxnum ) {
                    int num = hasMap.get(dish.getId());
                    if (num < 9999) {
                        //可继续增加
                        hasMap.put(list.get(position).products.get(0).getId(), num + 1);
                    }
                    checkHasNum();
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "最多可选" + maxnum + "道菜品", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.itemSecondDiscountMinuesIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = hasMap.get(dish.getId());
                if (num > 0) {
                    //可继续减少
                    hasMap.put(list.get(position).products.get(0).getId(), num - 1);
                }
                checkHasNum();
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    private void checkHasNum() {
        hasNum = 0;
        for (int i = 0; i < hasMap.size(); i++) {
            if (hasMap.get(list.get(i).products.get(0).getId()) > 0)
                hasNum = hasNum + hasMap.get(list.get(i).products.get(0).getId());
        }
    }


    class ViewHolder {
        private TextView itemSecondDiscountNameTv;
        private TextView itemSecondDiscountNumTv;
        private ImageView itemSecondDiscountMinuesIv;
        private ImageView itemSecondDiscountAddIv;

    }
}
