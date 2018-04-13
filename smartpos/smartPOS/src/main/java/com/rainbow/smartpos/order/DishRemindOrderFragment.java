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
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.bean.TableOrderInfo;
import com.sanyipos.sdk.api.inters.IBatchHandleDetailListener;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.scala.addDetail.model.AddDetaiAction;

import java.util.ArrayList;
import java.util.List;

public class DishRemindOrderFragment extends Fragment implements OnClickListener {
    public TextView textView_order_fragment_reminder_order;
    public TextView textView_fragment_order_dish_title;
    public GridView gridView_order_fragment_reminder;
    public Button buttonOrderItemOptionAllRemindConfirm;
    public Button buttonOrderItemOptionRemindConfirm;
    public Button buttonContinueOrder;
    public RemindeDishAdapter remindeDishAdapter;
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
        Bundle bundle = this.getArguments();
        View v = inflater.inflate(R.layout.fragment_order_dish_reminder, container, false);
        activity = (MainScreenActivity) getActivity();
        textView_fragment_order_dish_title = (TextView) v.findViewById(R.id.textView_fragment_order_dish_title);
        gridView_order_fragment_reminder = (GridView) v.findViewById(R.id.gridView_order_fragment_reminder);
        buttonOrderItemOptionAllRemindConfirm = (Button) v.findViewById(R.id.buttonOrderItemOptionAllRemindConfirm);
        buttonOrderItemOptionAllRemindConfirm.setOnClickListener(this);
        buttonOrderItemOptionRemindConfirm = (Button) v.findViewById(R.id.buttonOrderItemOptionRemindConfirm);
        buttonOrderItemOptionRemindConfirm.setOnClickListener(this);
        buttonContinueOrder = (Button) v.findViewById(R.id.buttonContinueOrder);
        buttonContinueOrder.setOnClickListener(this);
        remindeDishAdapter = new RemindeDishAdapter();
        initData();
        initButton();
        gridView_order_fragment_reminder.setAdapter(remindeDishAdapter);
        gridView_order_fragment_reminder.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                remindeDishAdapter.addSeclected(position);
                remindeDishAdapter.notifyDataSetChanged();
                initButton();
            }
        });

        return v;
    }

    private void initButton() {
        // TODO Auto-generated method stub
        if (orderDetailPlaceList.size() > 0) {
            buttonOrderItemOptionAllRemindConfirm.setEnabled(true);
        } else {
            buttonOrderItemOptionAllRemindConfirm.setEnabled(false);
        }
        if (remindeDishAdapter.getSeclected().size() > 0) {
            buttonOrderItemOptionRemindConfirm.setEnabled(true);
        } else {
            buttonOrderItemOptionRemindConfirm.setEnabled(false);
        }
    }

    public void initData() {
        // TODO Auto-generated method stub
        buttonOrderItemOptionAllRemindConfirm.setText("全部催菜");
        buttonOrderItemOptionRemindConfirm.setText("催菜");
        textView_fragment_order_dish_title.setText("催菜");
        for (OrderDetail order : orderDetailList) {
            if (order.isPlaced() && order.getQuantity() > order.getVoid_quantity()) {
                orderDetailPlaceList.add(order);
            }
            if (order.isPlaced() && order.isSet() && order.getQuantity() > order.getVoid_quantity()) {
                for (OrderDetail setOrder : order.getSetOrderDetailList()) {
                    orderDetailPlaceList.add(setOrder);
                }
            }
        }

    }

    public class RemindeDishAdapter extends BaseAdapter {
        public LayoutInflater inflater;
        public List<String> sList = new ArrayList<String>();

        public RemindeDishAdapter() {
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
            OrderDetail orderDetail = orderDetailPlaceList.get(position);
            View view = inflater.inflate(R.layout.fragment_order_dish_detail, parent, false);
            view.setBackground(getResources().getDrawable(R.drawable.gridview_item_selector1));
            TextView textViewDishDetailName = (TextView) view.findViewById(R.id.textViewDishDetailName);
            if (orderDetail.isMultiUnitProduct && null != orderDetail.getUnitName()) {
                textViewDishDetailName.setText(orderDetail.getName() + "(" + orderDetail.getUnitName() + ")");
            } else {
                textViewDishDetailName.setText(orderDetail.getName());
            }

            TextView textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
            textViewDishPrice.setText(Double.toString(orderDetail.getCurrentPrice()));
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
            case R.id.buttonOrderItemOptionAllRemindConfirm:
            case R.id.buttonOrderItemOptionRemindConfirm:
                List<OrderDetail> nameValuePairs = new ArrayList<OrderDetail>();
                if (remindeDishAdapter.getSeclected().size() > 0) {
                    int y = 0;
                    for (int i = 0; i < orderDetailPlaceList.size(); i++) {
                        for (int j = 0; j < remindeDishAdapter.getSeclected().size(); j++) {
                            if (i == Integer.valueOf(remindeDishAdapter.getSeclected().get(j))) {
                                OrderDetail orderDetail = orderDetailPlaceList.get(i);
                                orderDetail.setAuth_staff_id(SanyiSDK.getStaffId());
                                nameValuePairs.add(orderDetail);
                                y++;
                            }
                        }
                    }
                    remindeDishAdapter.getSeclected().clear();
                } else {
                    Toast.makeText(getActivity(), "尚未选择菜品", Toast.LENGTH_SHORT).show();
                    return;
                }

                SanyiScalaRequests.changeDishRequest(nameValuePairs, AddDetaiAction.ACTION_REMIND, new IBatchHandleDetailListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub

                        Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
                        buttonContinueOrder.performClick();
                    }

                    @Override
                    public void onSuccess(TableOrderInfo info, List<OrderDetail> ods) {
                        // TODO Auto-generated method stub

                        Toast.makeText(activity,"催菜成功",Toast.LENGTH_LONG).show();
                        buttonContinueOrder.performClick();
                    }
                });
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
