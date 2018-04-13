package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.Units;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderDishAdapter extends BaseAdapter {
    private Context mContext;
    private List<Units> allDishes = new ArrayList<Units>();
    public LayoutInflater inflater;
    static OrderFragment orderFragment;
    public int selection = -1;

    public OrderDishAdapter(Context c) {
        mContext = c;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setOrderFragment(OrderFragment fragmet) {
        orderFragment = fragmet;
    }

    public int getCount() {
        return allDishes.size();
    }

    public Units getItem(int position) {
        return allDishes.get(position);

    }

    public void setDish(List<Units> dishs) {
        this.allDishes = dishs;
        notifyDataSetChanged();
    }

    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        public TextView style;
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    public void setSelection(int position) {
        this.selection = position;
        notifyDataSetChanged();
    }


    public View getView(int position, View convertView, ViewGroup parent) {


        ProductRest dish;
        final Units unit;

        unit = allDishes.get(position);

        dish = unit.products.get(0);

        View l;

        if (convertView == null) {
            l = inflater.inflate(R.layout.fragment_order_dish_detail, parent, false);

        } else {
            l = convertView;
        }

        TextView textViewDishName = (TextView) l.findViewById(R.id.textViewDishDetailName);

        TextView textViewDishSlodOut = (TextView) l.findViewById(R.id.textViewDishSliodOut);

        TextView textViewDishPrice = (TextView) l.findViewById(R.id.textViewDishPrice);
        if (dish.isMultiUnitProduct && unit.products.size() == 1) {
            textViewDishName.setText(dish.name + "(" + dish.unitName + ")");
        } else {
            textViewDishName.setText(dish.name);
        }


        if (unit.products.size() > 1) {
            textViewDishPrice.setText(getUnitPriceExtent(unit));
        } else {
            textViewDishPrice.setText(Restaurant.currentcyFormatter.format(dish.price));
        }

        textViewDishSlodOut.setVisibility(View.GONE);
        l.setBackgroundResource(R.drawable.dish_detail_bg_selector);
        if (unit.products.size() == 1) {
            if (dish.isSoldout()) {
                l.setBackgroundResource(R.drawable.slod_out_dish_background_s);
                textViewDishSlodOut.setVisibility(View.VISIBLE);
            }
            if (dish.isLongterm()) {
                textViewDishSlodOut.setText("停售");
//                l.setNameTextColor("#646464");
            } else if (dish.getSoldoutCount() == 0) {
                textViewDishSlodOut.setText("沽清");
//                l.setNameTextColor("#646464");
            } else if (dish.getSoldoutCount() > 0) {
                l.setBackgroundResource(R.drawable.orderdish_background_ser);
//					l.setTextColor("#ffffff");
                if (dish.productType.isIsWeight()) {
                    textViewDishSlodOut.setText(new BigDecimal(dish.getSoldoutCount()).setScale(2, BigDecimal.ROUND_HALF_UP) + dish.unitName);
                } else {
                    textViewDishSlodOut.setText(dish.getSoldoutCount().intValue() + dish.unitName);
                }

            }
        }

        return l;
    }

    /**
     * 获取多规格菜品的价格区间
     *
     * @param unit
     * @return
     */

    public String getUnitPriceExtent(Units unit) {
        List<Double> prices = new ArrayList<Double>();
        for (int i = 0; i < unit.products.size(); i++) {
            ProductRest productRest = unit.products.get(i);
            prices.add(productRest.price);
        }
        Collections.sort(prices, new Comparator<Double>() {

            @Override
            public int compare(Double o1, Double o2) {
                if (o1 > o2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return Restaurant.currentcyFormatter.format(prices.get(0)) + "-" + Restaurant.currentcyFormatter.format(prices.get(prices.size() - 1));
    }


}