package com.rainbow.smartpos.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.SmartPosApplication;
import com.rainbow.smartpos.SmartPosBundle;
import com.rainbow.smartpos.activity.reportfragment.handover.ShadowTransformer;
import com.rainbow.smartpos.check.presenter.CheckPresenterImpl;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.tablestatus.OpenTableDialog;
import com.rainbow.smartpos.util.Listener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.AddDetailRequest;
import com.sanyipos.sdk.api.services.scala.OpenTablesRequest;
import com.sanyipos.sdk.api.services.scala._AddFastDetailsRequest;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.smartpos.model.OrderParams;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OrderDishFragment extends Fragment implements OnClickListener {
    static final String LOG_TAG = "OrderFragment";
    SmartPosApplication application;
    MainScreenActivity activity;

    View mainView;
    public Button buttonOrderPlaceOrder;
    public Button buttonOrderFirstPlaceOrder;
    public Button buttonOrderCancelOrder;

    public Button buttonOrderSaveOrder;
    public Button buttonOrderFetchOrder;
    public CheckBox checkBoxEncodeOrder;
    public OrderDishViewFragment orderDishViewFragment;
    public SearchDishFragment searchDishFragment;
    public CheckBox place_no_print;
    public CheckBox hold_dish;
    private int currentPosition;

    public void setOrderFragment(OrderFragment orderFragment) {
        this.orderFragment = orderFragment;
    }

    private OrderFragment orderFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_order_dish, container, false);
        // mainView.setOnFocusChangeListener(this);

        activity = (MainScreenActivity) getActivity();
        application = (SmartPosApplication) activity.getApplication();

        buttonOrderPlaceOrder = (Button) mainView.findViewById(R.id.buttonOrderPlaceOrder);

        buttonOrderFirstPlaceOrder = (Button) mainView.findViewById(R.id.buttonOrderFirstPlaceOrder);
        buttonOrderCancelOrder = (Button) mainView.findViewById(R.id.buttonOrderCancelOrder);
        buttonOrderSaveOrder = (Button) mainView.findViewById(R.id.buttonOrderSaveOrder);
        buttonOrderFetchOrder = (Button) mainView.findViewById(R.id.buttonOrderFetchOrder);
        buttonOrderPlaceOrder.setOnClickListener(this);
        buttonOrderFirstPlaceOrder.setOnClickListener(this);
        buttonOrderCancelOrder.setOnClickListener(this);
        buttonOrderSaveOrder.setOnClickListener(this);
        buttonOrderFetchOrder.setOnClickListener(this);

        if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_ORDER)) {
            setPlaceButton(true);
        } else {
            setPlaceButton(false);
        }

        place_no_print = (CheckBox) mainView.findViewById(R.id.place_no_print);
        hold_dish = (CheckBox) mainView.findViewById(R.id.hold_dish);
        hold_dish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    orderFragment.orderListAdpater.setDishHold(true);
                } else {
                    orderFragment.orderListAdpater.setDishHold(false);
                }
            }
        });
        checkBoxEncodeOrder = (CheckBox) mainView.findViewById(R.id.checkBox_encode_order);
        checkBoxEncodeOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disPlaySearchView();
                } else {
                    disPlayOrderView();
                }
            }
        });
        place_no_print.setChecked(false);
//        disPlayOrderView();
        if (Restaurant.isSpellOrder) {
            checkBoxEncodeOrder.setChecked(true);
            disPlaySearchView();
        } else {
            checkBoxEncodeOrder.setChecked(false);
            disPlayOrderView();
        }
        initOrderView();
        return mainView;
    }

    public void disPlayOrderView() {
//        System.out.println("Show order");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (orderDishViewFragment == null) {
            orderDishViewFragment = new OrderDishViewFragment();
            orderDishViewFragment.setOrderFragment(orderFragment);
        }
        ft.replace(R.id.linearLayout_order_dish, orderDishViewFragment);
        ft.commit();
    }

    public void disPlaySearchView() {
//        System.out.println("Show search");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (searchDishFragment == null) {
            searchDishFragment = new SearchDishFragment();
            searchDishFragment.setOrderFragment(orderFragment);
        }
        ft.replace(R.id.linearLayout_order_dish, searchDishFragment);
        ft.commit();
    }

    public void initView() {
        if (null != place_no_print) {
            if (place_no_print.isChecked())
                place_no_print.setChecked(false);
        }
        if (null != hold_dish) {
            if (hold_dish.isChecked())
                hold_dish.setChecked(false);
        }
        if (orderDishViewFragment != null && orderDishViewFragment.orderDish != null) {
            orderDishViewFragment.orderDish.initLayout();
        }
        setPlaceButton(true);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!Restaurant.orderIsRefresh) {
//            disPlayOrderView();
            if (Restaurant.isSpellOrder) {
                checkBoxEncodeOrder.setChecked(true);
                disPlaySearchView();
            } else {
                checkBoxEncodeOrder.setChecked(false);
                disPlayOrderView();
            }
            Restaurant.orderIsRefresh = true;
        }
    }


    public void refreshDish() {
        if (orderDishViewFragment != null) {
            orderDishViewFragment.orderDish.notifyDataChanged();
        }
        if (searchDishFragment != null && searchDishFragment.orderDishAdapter != null) {
            searchDishFragment.orderDishAdapter.notifyDataSetChanged();
        }
    }

    public void setParent(OrderFragment parent) {
        orderFragment = parent;

    }


    public boolean isCheck() {
        for (int i = 0; i < orderFragment.getCurrentOrderDetails().size(); i++) {
            if (!orderFragment.getCurrentOrderDetails().get(i).isPlaced()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {

//            case R.id.buttonOrderCheck:
            case R.id.buttonOrderCancelOrder:
                if (orderFragment.tableOrderInfo != null) {
                    SanyiScalaRequests.cancelFastOrderRequest(orderFragment.tableOrderInfo.getOrderId(), new Request.ICallBack() {
                        @Override
                        public void onSuccess(String status) {
                            Toast.makeText(activity, status, Toast.LENGTH_LONG).show();
                            orderFragment.clearData();
                        }


                        @Override
                        public void onFail(String error) {
                            Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    orderFragment.clearData();
                }
                break;
            case R.id.buttonOrderSaveOrder:
                if (orderFragment.orderListAdpater.orderDetailList.isEmpty()) {

                    Toast.makeText(activity, "无落单菜品", Toast.LENGTH_LONG).show();
                    return;
                }
                if (orderFragment.tableOrderInfo == null && Restaurant.fastModeInputNumber) {
                    new OpenTableDialog(getActivity(), getString(R.string.open_table_dialog_fast_mode), "", OpenTableDialog.FAST_MODE, new Listener.OnFastModeNumberListener() {
                        @Override
                        public void onFastNumber(String number) {
                            addFastDetailRequest(viewId, number);
                            if (number != null) {
                                orderFragment.tableName = number;
                                orderFragment.textViewTableName.setText(orderFragment.tableName);
                            }
                        }

                        @Override
                        public void cancel() {

                        }
                    }).show();
                } else {
                    addFastDetailRequest(viewId, orderFragment.tableName);
                }
                break;
            case R.id.buttonOrderFetchOrder:
                currentPosition = 0;

                SanyiScalaRequests.getFastUnpaidOrdersRequest(new OpenTablesRequest.OnOpenTableListener() {


                    @Override
                    public void onFail(String error) {
                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(final List<OpenTableDetail> resp) {
                        if (resp.size() == 0) {
                            Toast.makeText(activity, "暂无存单", Toast.LENGTH_LONG).show();
                            return;
                        }

                        final NormalDialog dialog = new NormalDialog(activity);
                        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_handover, null);

                        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager_fragment_handover);


                        FastModeFetchAdapter mCardAdapter = new FastModeFetchAdapter(getActivity(), resp);
                        viewPager.setAdapter(mCardAdapter);

                        mCardAdapter.setClickListener(new FastModeFetchAdapter.FastModeCardViewListener() {
                            @Override
                            public void onCardViewClickListener(int position) {

                            }
                        });
                        ShadowTransformer mCardShadowTransformer = new ShadowTransformer(viewPager, mCardAdapter);
                        viewPager.setPageTransformer(false, mCardShadowTransformer);

                        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {
                                currentPosition = position;
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                        dialog.title("取单");
                        dialog.content(view);
                        dialog.widthScale((float) 0.65);
                        dialog.heightScale((float) 0.5);
                        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                            @Override
                            public void onClickConfirm() {
                                if (resp.get(currentPosition).info.getOpenStaffId() != SanyiSDK.currentUser.getId()) {
                                    Toast.makeText(activity, "不可以取其他服务员的单", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                dialog.dismiss();
                                orderFragment.clearData();
                                orderFragment.tableOrderInfo = resp.get(currentPosition).info;
                                List<Long> seats = new ArrayList<Long>();
                                seats.add(resp.get(currentPosition).info.getTableSeatId());
                                orderFragment.tableIds = seats;
                                orderFragment.orderListAdpater.setOrderDetails(resp.get(currentPosition).ods);
                                orderFragment.orderListAdpater.notifyDataSetChanged();
                                orderFragment.onOrderUpdate();
                                SanyiScalaRequests.unLockTableRequest(seats, false, new Request.ICallBack() {

                                    @Override
                                    public void onFail(String error) {
                                    }

                                    @Override
                                    public void onSuccess(String status) {
                                    }
                                });
                            }
                        });
                        dialog.show();
                    }
                });

                break;
            case R.id.buttonOrderFirstPlaceOrder:

//                SanyiScalaRequests.cancelFastOrderRequest(orderFragment.cashierResult.orders.get(0).orderId, new Request.ICallBack() {
//                    @Override
//                    public void onSuccess(String status) {
//                        ToastUtils.showShort(activity, status);
//                    }
//
//                    @Override
//                    public void request_timeout() {
//
//                    }
//
//                    @Override
//                    public void request_fail() {
//
//                    }
//
//                    @Override
//                    public void onFail(String error) {
//
//                        ToastUtils.showShort(activity, error);
//                    }
//                });
            case R.id.buttonOrderPlaceOrder: {
                if (!Restaurant.isFastMode) {
                    setPlaceButton(false);
                    List<OrderDetail> orderDetails = orderFragment.orderListAdpater.getUnPlaceDetail();
                    if (orderDetails.size() == 0) {
                        OrderParams table = (OrderParams) orderFragment.bundle.getSerializable(SmartPosBundle.TABLE_ID);
                        activity.displayView(MainScreenActivity.PLACE_FRAGMENT, SmartPosBundle.getBundle(orderFragment.bundle.getInt(SmartPosBundle.PEOPLE_NUMBER), table.getTableIds(), false, false, null));
                        return;
                    }
                    final List<Long> tableIds = orderFragment.getTableIds();
                    SanyiScalaRequests.AddDetailsRequest(!place_no_print.isChecked(), tableIds, orderDetails, new AddDetailRequest.IAddDetailListener() {


                        @Override
                        public void onFail(String error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                            setPlaceButton(true);

                        }

                        @Override
                        public void onSuccess(List<OpenTableDetail> resp) {
                            // TODO Auto-generated method stub
                            Toast.makeText(activity, "下单成功", Toast.LENGTH_LONG).show();
                            if (viewId == R.id.buttonOrderFirstPlaceOrder) {
                                orderFragment.orderListAdpater.setDishFistPlace();
                                orderFragment.orderListAdpater.notifyDataSetChanged();
                                setPlaceButton(true);
                                return;
                            }
                            if (Restaurant.isPublicDevice) {
                                activity.displayView(MainScreenActivity.LOGIN_FRAGMENT, null);
                            } else {
                                activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                            }
                        }
                    });

                } else {
                    if (orderFragment.orderListAdpater.orderDetailList.isEmpty()) {

                        Toast.makeText(activity, "无落单菜品", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (orderFragment.tableOrderInfo == null && Restaurant.fastModeInputNumber) {
                        new OpenTableDialog(getActivity(), getString(R.string.open_table_dialog_fast_mode), "", OpenTableDialog.FAST_MODE, new Listener.OnFastModeNumberListener() {
                            @Override
                            public void onFastNumber(String number) {
                                addFastDetailRequest(viewId, number);
                                if (number != null) {
                                    orderFragment.tableName = number;
                                    orderFragment.textViewTableName.setText(orderFragment.tableName);
                                }
                            }

                            @Override
                            public void cancel() {

                            }
                        }).show();
                    } else {
                        addFastDetailRequest(viewId, orderFragment.tableName);
                    }
                }
            }
        }
    }

    public void setPlaceButton(boolean b) {
        buttonOrderPlaceOrder.setEnabled(b);
        buttonOrderFirstPlaceOrder.setEnabled(b);
    }

    public void addFastDetailRequest(final int id, String tableName) {
        if (orderFragment.tableIds == null) {
            SanyiScalaRequests.AddFastDetailsRequest(tableName, orderFragment.orderListAdpater.getUnPlaceDetail(), new _AddFastDetailsRequest.IAddFastDetailListener() {
                @Override
                public void onSuccess(List<OpenTableDetail> resp) {
                    if (id == R.id.buttonOrderSaveOrder) {
                        orderFragment.clearData();

                        Toast.makeText(activity, "存单成功", Toast.LENGTH_LONG).show();
                        return;
                    }
                    orderFragment.orderListAdpater.notifyDataSetChanged();
                    List<Long> seats = new ArrayList<Long>();
                    seats.add(resp.get(0).info.getTableSeatId());
                    orderFragment.tableIds = seats;
                    orderFragment.tableOrderInfo = resp.get(0).info;
                    orderFragment.textViewTableName.setText(orderFragment.tableOrderInfo.getOrderName());
                    if (id == R.id.buttonOrderFirstPlaceOrder) {
                        orderFragment.checkPresenterImpl = new CheckPresenterImpl(activity, orderFragment.tableIds, orderFragment.bundle, orderFragment);
                        orderFragment.checkPresenterImpl.beginCashierRequest();
                        orderFragment.drawerLayout.openDrawer(Gravity.RIGHT);
                    }

                }


                @Override
                public void onFail(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            SanyiScalaRequests.AddDetailsRequest(false, orderFragment.tableIds, orderFragment.orderListAdpater.getUnPlaceDetail(), new AddDetailRequest.IAddDetailListener() {
                @Override
                public void onSuccess(List<OpenTableDetail> resp) {
                    if (id == R.id.buttonOrderSaveOrder) {
                        orderFragment.clearData();

                        Toast.makeText(activity, "存单成功", Toast.LENGTH_LONG).show();
                        final List<Long> seats = new ArrayList<>();
                        seats.add(resp.get(0).info.getTableId());
                        SanyiScalaRequests.unLockTableRequest(seats, false, new Request.ICallBack() {

                            @Override
                            public void onFail(String error) {
                            }

                            @Override
                            public void onSuccess(String status) {
                            }
                        });
                        return;

                    }
                    orderFragment.orderListAdpater.notifyDataSetChanged();
                    if (id == R.id.buttonOrderFirstPlaceOrder) {
                        orderFragment.checkPresenterImpl = new CheckPresenterImpl(activity, orderFragment.tableIds, orderFragment.bundle, orderFragment);
                        orderFragment.checkPresenterImpl.beginCashierRequest();
                        orderFragment.drawerLayout.openDrawer(Gravity.RIGHT);
                    }
                    if (id == R.id.buttonOrderSaveOrder) {
                        orderFragment.clearData();
                    }
                }


                @Override
                public void onFail(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden) {
            if(Restaurant.isSpellOrder) {
                checkBoxEncodeOrder.setChecked(true);
                disPlaySearchView();
            } else {
                checkBoxEncodeOrder.setChecked(false);
                disPlayOrderView();
            }
            initView();
            initOrderView();
        }
    }

    public void initOrderView() {
        if (Restaurant.isFastMode) {
            hold_dish.setVisibility(View.GONE);
            place_no_print.setVisibility(View.GONE);
            buttonOrderFirstPlaceOrder.setText(getString(R.string.check));
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) buttonOrderFirstPlaceOrder.getLayoutParams();
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            buttonOrderFirstPlaceOrder.setLayoutParams(params);
            buttonOrderPlaceOrder.setVisibility(View.GONE);
            buttonOrderFetchOrder.setVisibility(View.VISIBLE);
            buttonOrderSaveOrder.setVisibility(View.VISIBLE);
            buttonOrderCancelOrder.setVisibility(View.VISIBLE);
        } else {
            hold_dish.setVisibility(View.VISIBLE);
            place_no_print.setVisibility(View.VISIBLE);
            buttonOrderFirstPlaceOrder.setText(getString(R.string.first_order));
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) buttonOrderFirstPlaceOrder.getLayoutParams();
//            buttonOrderFirstPlaceOrder.setLayoutParams(params);
            buttonOrderPlaceOrder.setVisibility(View.VISIBLE);
            buttonOrderSaveOrder.setVisibility(View.GONE);
            buttonOrderFetchOrder.setVisibility(View.GONE);
            buttonOrderCancelOrder.setVisibility(View.GONE);
        }
    }

}