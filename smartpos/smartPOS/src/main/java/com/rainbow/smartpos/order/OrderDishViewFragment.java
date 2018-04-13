package com.rainbow.smartpos.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rainbow.common.view.Order.OrderDish;

/**
 * Created by ss on 2016/11/23.
 */

public class OrderDishViewFragment extends Fragment {

    private View mOrderDishView;

    public OrderDish orderDish;

    public OrderFragment orderFragment;

    public OrderFragment getOrderFragment() {
        return orderFragment;
    }

    public void setOrderFragment(OrderFragment orderFragment) {
        this.orderFragment = orderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        orderDish = new OrderDish(getActivity(), getChildFragmentManager(), orderFragment.onClickDish);
        mOrderDishView = orderDish.initView();

        return mOrderDishView;
    }

}
