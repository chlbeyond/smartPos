package com.rainbow.smartpos.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class HoldDishFragment extends Fragment implements OnClickListener {
    public TextView textView_order_fragment_reminder_order;
    public TextView textView_fragment_order_dish_title;
    public GridView gridView_order_fragment_hold;
    public Button buttonOrderItemOptionAllHoldConfirm;
    public Button buttonOrderItemOptionHoldConfirm;
    public Button buttonContinueOrder;
    public HoldDishAdapter holdDishAdapter;
    public List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
    public List<OrderDetail> orderDetailPlaceList = new ArrayList<OrderDetail>();
    public MainScreenActivity activity;

    public void setOrderList(List<OrderDetail> orderList) {
        this.orderDetailList = orderList;

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        orderDetailPlaceList.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_order_dish_hold, container, false);
        activity = (MainScreenActivity) getActivity();
        textView_fragment_order_dish_title = (TextView) v.findViewById(R.id.textView_fragment_order_dish_title);
        gridView_order_fragment_hold = (GridView) v.findViewById(R.id.gridView_order_fragment_hold);
        buttonOrderItemOptionAllHoldConfirm = (Button) v.findViewById(R.id.buttonOrderItemOptionAllHoldConfirm);
        buttonOrderItemOptionAllHoldConfirm.setOnClickListener(this);
        buttonOrderItemOptionHoldConfirm = (Button) v.findViewById(R.id.buttonOrderItemOptionHoldConfirm);
        buttonOrderItemOptionHoldConfirm.setOnClickListener(this);
        buttonContinueOrder = (Button) v.findViewById(R.id.buttonContinueOrder);
        buttonContinueOrder.setOnClickListener(this);
        holdDishAdapter = new HoldDishAdapter();
        initData();
        initButton();
        gridView_order_fragment_hold.setAdapter(holdDishAdapter);
        gridView_order_fragment_hold.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                holdDishAdapter.addSeclected(position);
                holdDishAdapter.notifyDataSetChanged();
                initButton();
            }

        });

        return v;
    }

    private void initButton() {
        // TODO Auto-generated method stub
        if (holdDishAdapter.getSeclected().size() > 0) {
            buttonOrderItemOptionHoldConfirm.setEnabled(true);
        } else {
            buttonOrderItemOptionHoldConfirm.setEnabled(false);
        }
        if (orderDetailPlaceList.size() > 0) {
            buttonOrderItemOptionAllHoldConfirm.setEnabled(true);
        } else {
            buttonOrderItemOptionAllHoldConfirm.setEnabled(false);
        }
    }

    public void initData() {
        // TODO Auto-generated method stub

        textView_fragment_order_dish_title.setText("挂起");
        buttonOrderItemOptionAllHoldConfirm.setText("全部挂起");
        buttonOrderItemOptionHoldConfirm.setText("挂起");
        for (OrderDetail order : orderDetailList) {
            if (!order.isPlaced() && !order.isHold()) {
                orderDetailPlaceList.add(order);
            }
        }

    }

    public class HoldDishAdapter extends BaseAdapter {
        public LayoutInflater inflater;
        public List<String> sList = new ArrayList<String>();

        public HoldDishAdapter() {
            this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return orderDetailPlaceList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        public void addSeclected(int positon) {
            if (!sList.contains(Integer.toString(positon))) {
                sList.add(Integer.toString(positon));
            } else {
                sList.remove(Integer.toString(positon));
            }
        }

        public List<String> getSeclected() {
            return sList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = inflater.inflate(R.layout.fragment_order_dish_detail, parent, false);
            view.setBackground(getResources().getDrawable(R.drawable.gridview_item_selector1));
            TextView textViewDishDetailName = (TextView) view.findViewById(R.id.textViewDishDetailName);
            OrderDetail orderDetail = orderDetailPlaceList.get(position);
            textViewDishDetailName.setText(orderDetail.getName());
            if (orderDetail.isMultiUnitProduct && null != orderDetail.getUnitName()) {
                textViewDishDetailName.setText(orderDetail.getName() + "(" + orderDetail.getUnitName() + ")");
            }
            TextView textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
            textViewDishPrice.setText(Double.toString(orderDetailPlaceList.get(position).getCurrentPrice()));
//            TextView textViewDishOrdered = (TextView) view.findViewById(R.id.textViewDishOrdered);
//            for (String s : sList) {
//                if (Integer.valueOf(s) == position) {
//                    textViewDishOrdered.setText("√");
//                    textViewDishOrdered.setVisibility(View.VISIBLE);
//                }
//            }
            return view;

        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.buttonOrderItemOptionAllHoldConfirm:
            case R.id.buttonOrderItemOptionHoldConfirm:
                if (v.getId() == R.id.buttonOrderItemOptionAllHoldConfirm) {
                    if (orderDetailPlaceList.size() > 0) {
                        for (int i = 0; i < orderDetailPlaceList.size(); i++) {
                            orderDetailPlaceList.get(i).setHold(true);
                        }
                    } else {
                        Toast.makeText(getActivity(), "尚未选择菜品", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (v.getId() == R.id.buttonOrderItemOptionHoldConfirm) {
                    if (holdDishAdapter.getSeclected().size() > 0) {
                        for (int i = 0; i < orderDetailPlaceList.size(); i++) {
                            for (int j = 0; j < holdDishAdapter.getSeclected().size(); j++) {
                                if (i == Integer.valueOf(holdDishAdapter.getSeclected().get(j))) {
                                    orderDetailPlaceList.get(i).setHold(true);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "尚未选择菜品", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //MainScreenActivity.getOrderFragment().refreshOrderList();
//                activity.orderFragment.removeOrderitemOptionFragment();
                break;

            case R.id.buttonContinueOrder:
                //MainScreenActivity.getOrderFragment().refreshOrderList();
//                activity.orderFragment.removeOrderitemOptionFragment();
                break;
            default:
                break;
        }
    }
}
