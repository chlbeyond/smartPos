package com.rainbow.smartpos.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.common.view.Order.OrderDish;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.Units;
import com.sanyipos.sdk.utils.OrderUtil;

/**
 * Created by ss on 2016/4/9.
 */
public class NewOrderFragment extends Fragment {
    RecyclerView recyclerView;
    public LinearLayout view;
    public OrderDish orderDish;
    NewOrderRecyclerViewAdapter orderAdapter;
    public LayoutInflater inflater;
    public Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_order_fragment_new, container, false);
        context = getContext();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_order_dish);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        view = (LinearLayout) rootView.findViewById(R.id.dishContainer);
        orderAdapter = new NewOrderRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(orderAdapter);


        orderDish = new OrderDish(getActivity(), getChildFragmentManager(), new OrderDishItemFragment.OnClickDish() {
            @Override
            public void onClickDish(Units unit, View view) {
                OrderDetail orderDetail = OrderUtil.createOrderDetail(unit.products.get(0), 1);
                orderAdapter.orderDetails.add(orderDetail);
                orderAdapter.notifyDataSetChanged();
            }
        });
        view.addView(orderDish.initView());

        return rootView;
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

    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

}
