package com.rainbow.smartpos.order;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rainbow.common.view.AutoRecyclerView;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.Units;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ss on 2016/6/6.
 */
public class RecyclerViewOrderDishAdapter extends RecyclerView.Adapter<RecyclerViewOrderDishAdapter.DishHolder> {
    public String TAG = "RecyclerViewOrderDishAdapter";
    public Context mContext;
    private int maxCount = 0;
    private List<Units> allDishes = new ArrayList<Units>();
    private AutoRecyclerView recyclerView;

    public RecyclerViewOrderDishAdapter(Context context) {
        this.mContext = context;
    }

    public interface RecyclerItemClickListener {
        void OnItemClick(Units dish, View view);
    }

    public RecyclerItemClickListener listener;

    public void setListener(RecyclerItemClickListener listener) {
        this.listener = listener;
    }

    public void setRecyclerView(AutoRecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void updateDish() {
        allDishes = null;

    }

    public void subList() {
        if (allDishes.size() > maxCount)
            for (int i = 0; i < allDishes.size(); i++) {
                if (i >= maxCount) {
                    allDishes.remove(i);
                }
            }
    }

    @Override
    public DishHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DishHolder dishHolder = new DishHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_order_dish_detail, parent, false));


        return dishHolder;
    }

    public void refresh() {
        updateDish();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(DishHolder holder, int position) {
        final Units unit = allDishes.get(position);
        ProductRest productRest = unit.products.get(0);
        holder.textViewDishDetailName.setText(productRest.getName());
        if (unit.products.size() > 1) {

            holder.textViewDishPrice.setText(getUnitPriceExtent(unit));
        } else {
            holder.textViewDishPrice.setText(Restaurant.currentcyFormatter.format(productRest.price));
        }

        if (unit.products.size() == 1) {
            if (productRest.isSoldout()) {
                holder.itemView.setBackgroundResource(R.drawable.slod_out_dish_background_s);
                holder.textViewSlodOut.setVisibility(View.VISIBLE);
            }else {
                holder.itemView.setBackgroundResource(R.drawable.dish_detail_bg_selector);
                holder.textViewSlodOut.setVisibility(View.GONE);
            }
            if (productRest.isLongterm()) {
                holder.textViewSlodOut.setText("停售");
//                l.setNameTextColor("#646464");
            } else if (productRest.getSoldoutCount() == 0) {
                holder.textViewSlodOut.setText("沽清");
//                l.setNameTextColor("#646464");
            } else if (productRest.getSoldoutCount() > 0) {
                holder.itemView.setBackgroundResource(R.drawable.orderdish_background_ser);
//					l.setTextColor("#ffffff");
                if (productRest.productType.isIsWeight()) {
                    holder.textViewSlodOut.setText(new BigDecimal(productRest.getSoldoutCount()).setScale(2, BigDecimal.ROUND_HALF_UP) + productRest.unitName);
                } else {
                    holder.textViewSlodOut.setText(productRest.getSoldoutCount().intValue() + productRest.unitName);
                }

            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.OnItemClick(unit, v);
            }
        });
//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                        KLog.d("ACTION", " ACTION UP");
//
//                        listener.OnItemClick(unit, v);
//                        break;
//                    case MotionEvent.ACTION_DOWN:
//
//                        KLog.d("ACTION", " ACTION DOWN");
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//
//                        KLog.d("ACTION", " ACTION MOVE");
//                        break;
//                }
//
//                return false;
//            }
//
//        });

    }

    @Override
    public int getItemCount() {
        return allDishes.size();
    }

    class DishHolder extends RecyclerView.ViewHolder {
        TextView textViewDishDetailName;
        TextView textViewDishPrice;
        TextView textViewSlodOut;

        public DishHolder(View itemView) {
            super(itemView);
            textViewDishDetailName = (TextView) itemView.findViewById(R.id.textViewDishDetailName);
            textViewDishPrice = (TextView) itemView.findViewById(R.id.textViewDishPrice);
            textViewSlodOut = (TextView) itemView.findViewById(R.id.textViewDishSliodOut);
        }

    }

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
