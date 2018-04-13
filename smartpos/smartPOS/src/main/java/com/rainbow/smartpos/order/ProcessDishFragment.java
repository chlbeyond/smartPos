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
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.bean.TableOrderInfo;
import com.sanyipos.sdk.api.inters.IBatchHandleDetailListener;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.scala.addDetail.model.AddDetaiAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessDishFragment extends Fragment implements OnClickListener {
    public TextView textView_fragment_order_dish_title;
    public GridView gridView_order_fragment_process;
    public Button buttonOrderItemOptionAllProcessConfirm;
    public Button buttonOrderItemOptionProcessConfirm;
    public Button buttonContinueOrder;
    public List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
    public List<OrderDetail> orderDetailPlaceList = new ArrayList<OrderDetail>();
    public ProcessDishAdapter processDishAdapter;
    public List<Integer> sList = new ArrayList<Integer>();
    public MainScreenActivity activity;

    public void setOrderList(List<OrderDetail> orderList) {
        this.orderDetailList = orderList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_order_dish_process, container, false);
        activity = (MainScreenActivity) getActivity();
        initView(v);
        return v;
    }

    private void initView(View v) {
        textView_fragment_order_dish_title = (TextView) v.findViewById(R.id.textView_fragment_order_dish_title);
        buttonOrderItemOptionAllProcessConfirm = (Button) v.findViewById(R.id.buttonOrderItemOptionAllProcessConfirm);
        gridView_order_fragment_process = (GridView) v.findViewById(R.id.gridView_order_fragment_process);
        buttonOrderItemOptionProcessConfirm = (Button) v.findViewById(R.id.buttonOrderItemOptionProcessConfirm);
        buttonContinueOrder = (Button) v.findViewById(R.id.buttonContinueOrder);

        buttonContinueOrder.setOnClickListener(this);
        buttonOrderItemOptionAllProcessConfirm.setOnClickListener(this);
        buttonOrderItemOptionProcessConfirm.setOnClickListener(this);
        processDishAdapter = new ProcessDishAdapter();

        initData();
        initButton();

        gridView_order_fragment_process.setAdapter(processDishAdapter);
        gridView_order_fragment_process.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (!processDishAdapter.getSeclected().contains(position)) {
                    processDishAdapter.addSeclected(position);
                } else {
                    processDishAdapter.removeSelected(position);
                }
                processDishAdapter.notifyDataSetChanged();
                initButton();
            }
        });
    }

    private void initButton() {
        // TODO Auto-generated method stub
        if (processDishAdapter.getSeclected().size() > 0) {
            buttonOrderItemOptionProcessConfirm.setEnabled(true);
        } else {
            buttonOrderItemOptionProcessConfirm.setEnabled(false);
        }
        if (orderDetailPlaceList.size() > 0) {
            buttonOrderItemOptionAllProcessConfirm.setEnabled(true);
        } else {
            buttonOrderItemOptionAllProcessConfirm.setEnabled(false);
        }
    }

    public void initData() {
        // TODO Auto-generated method stub
        for (OrderDetail order : orderDetailList) {
            if (order.isHold() && order.isPlaced() && order.getQuantity() > order.getVoid_quantity()) {
                orderDetailPlaceList.add(order);
            }
        }

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        orderDetailPlaceList.clear();
        sList.clear();
    }

    public class ProcessDishAdapter extends BaseAdapter {
        public LayoutInflater inflater;

        public ProcessDishAdapter() {
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

        public void addSeclected(int position) {
            sList.add(position);
        }

        public void removeSelected(int position) {
            Iterator<Integer> it = sList.iterator();
            while (it.hasNext()) {
                if (it.next() == position) {
                    it.remove();
                }
            }
        }

        public List<Integer> getSeclected() {
            return sList;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
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
            textViewDishPrice.setText(Double.toString(orderDetail.getCurrentPrice()));
//            TextView textViewDishOrdered = (TextView) view.findViewById(R.id.textViewDishOrdered);
//            for (Integer s : sList) {
//                if (s == position) {
//                    textViewDishOrdered.setText("√");
//                    textViewDishOrdered.setVisibility(View.VISIBLE);
//                }
//            }
            return view;

        }

    }

    @Override
    public void onClick(final View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.buttonOrderItemOptionAllProcessConfirm:
            case R.id.buttonOrderItemOptionProcessConfirm:
                final List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
                if (v.getId() == R.id.buttonOrderItemOptionAllProcessConfirm) {
                    if (orderDetailPlaceList.size() > 0) {
                        for (int i = 0; i < orderDetailPlaceList.size(); i++) {
                            orderDetailList.add(orderDetailPlaceList.get(i));
                        }
                    } else {
                        Toast.makeText(getActivity(), "没有可叫起菜品", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    int y = 0;
                    if (processDishAdapter.getSeclected().size() > 0) {
                        for (int i = 0; i < orderDetailPlaceList.size(); i++) {
                            for (int j = 0; j < processDishAdapter.getSeclected().size(); j++) {
                                if (i == Integer.valueOf(processDishAdapter.getSeclected().get(j))) {
                                    orderDetailList.add(orderDetailPlaceList.get(i));
                                }
                            }

                        }
                    } else {
                        Toast.makeText(getActivity(), "请选择菜品", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                SanyiScalaRequests.changeDishRequest(orderDetailList, AddDetaiAction.ACTION_COOK, new IBatchHandleDetailListener() {



                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub

                        Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(TableOrderInfo info, List<OrderDetail> ods) {
                        // TODO Auto-generated method stub
                        for (OrderDetail od : orderDetailList) {
                            od.setHold(false);
                        }

                        Toast.makeText(activity,"叫起成功",Toast.LENGTH_LONG).show();
//                        activity.orderFragment.removeOrderitemOptionFragment();
                    }
                });
                break;
            case R.id.buttonContinueOrder:
//                activity.orderFragment.orderListAdpater.notifyDataSetChanged();
//                activity.orderFragment.removeOrderitemOptionFragment();
                break;
            default:
                break;
        }
    }
}
