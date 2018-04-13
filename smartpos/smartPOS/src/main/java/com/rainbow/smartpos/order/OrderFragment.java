package com.rainbow.smartpos.order;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cc.serialport.SerialCommand;
import com.rainbow.common.view.HackyDrawerLayout;
import com.rainbow.common.view.HorizontalListView;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.SmartPosBundle;
import com.rainbow.smartpos.check.CheckDetailListAdapter;
import com.rainbow.smartpos.check.CheckView.CheckView;
import com.rainbow.smartpos.check.ChoosePromotionDialog;
import com.rainbow.smartpos.check.ChooseVoucherDialog;
import com.rainbow.smartpos.check.MemberPwdPopWindow;
import com.rainbow.smartpos.check.NumPadPopWindow;
import com.rainbow.smartpos.check.PaymentModeAdapter;
import com.rainbow.smartpos.check.PaymentSelector;
import com.rainbow.smartpos.check.PromotionAdapter;
import com.rainbow.smartpos.check.TenPayPopWindow;
import com.rainbow.smartpos.check.VoucherAdapter;
import com.rainbow.smartpos.check.presenter.CheckPresenterImpl;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.order.OtherFragment.OtherFragmentInterface;
import com.rainbow.smartpos.place.ChangePriceDialog;
import com.rainbow.smartpos.place.ReturnDishReasonDialog;
import com.rainbow.smartpos.place.WeightDialog;
import com.rainbow.smartpos.place.presenter.PlaceDetailPresenterImpl;
import com.rainbow.smartpos.tablestatus.OpenTableDialog;
import com.rainbow.smartpos.util.AuthDialog;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.NoRepeatClickUtils;
import com.rainbow.smartpos.util.SwitchTableDialog;
import com.rainbow.smartpos.util.SwitchTableDialog.ISwitchTableListener;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.bean.TableOrderInfo;
import com.sanyipos.sdk.api.inters.IBatchHandleDetailListener;
import com.sanyipos.sdk.api.inters.IHandleDetailListener;
import com.sanyipos.sdk.api.services.scala.NewBillRequest.INewBillRequestListener;
import com.sanyipos.sdk.api.services.scala.OpenTablesRequest;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.rest.Units;
import com.sanyipos.sdk.model.scala.addDetail.model.AddDetaiAction;
import com.sanyipos.sdk.model.scala.addDetail.model.AddDetailOrder;
import com.sanyipos.sdk.model.scala.check.CashierAction;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;
import com.sanyipos.smartpos.model.OrderParams;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OrderFragment extends Fragment implements OnClickListener, OtherFragmentInterface, CheckView {

    static final String LOG_TAG = "OrderFragment";
    public static final int ORDER_DISH_FRAGMENT = 100;
    public static final int SEARCH_DISH_FRAGMENT = 101;
    ListView orderList;
    public CheckDetailListAdapter orderListAdpater;
    public CheckPresenterImpl checkPresenterImpl;
    View mainView;

    TextView order_remark;
    LinearLayout order_left_layout;

    TextView textViewTableName;
    TextView textViewSwitchTable;
    TextView textViewNotOrderPlace;
    RelativeLayout textViewSecondDiscount;
    TextView memberScoreNotice;
    LinearLayout memberScoreLl;

    OrderFragment fragment;
    public TableOrderInfo tableOrderInfo;
    public SearchDishFragment searchDishFragment;
    public Fragment currentFragment;
    public OrderDishFragment orderDishFragment;
    public List<Long> tableIds;
    public SeatEntity currentTable;
    public ImageView imageViewSawtooth;
    public HackyDrawerLayout drawerLayout;
    MainScreenActivity activity;

    public int numOfPeople = 0;
    public String tableName;
    Bundle bundle;

    /***
     * CHECK
     */
    HorizontalListView sales_promotion_gridview;
    HorizontalListView voucher_gridview;
    GridView payment_method_gridview;


    public CashierParamResult cashierParam;
    public CashierResult cashierResult;

    public NormalDialog scanDialog;
    LinearLayout sales_promotion_layout;
    LinearLayout voucher_layout;
    LinearLayout switch_table;
    LinearLayout member_info_layout;
    LinearLayout check_view;
    TextView textViewCheckPayable;
    TextView textViewCheckUnPaid;
    TextView memberPhone;
    TextView memberNum;
    TextView textViewCheckPaid;
    TextView textViewTablePromotion;
    TextView textViewBillAmount;

    TextView member_btn;

    TextView memberName;

    TextView memberBalance;

    TextView memberTotal;

    Button memberBalanceCharge;
    Button memberBalancePayBalance;
    PromotionAdapter mPromotionAdapter;
    VoucherAdapter mVoucherAdapter;
    PaymentModeAdapter mPaymentModeAdapter;


    public OrderOptionDialog mOrderOptionDialog;
    private boolean isForMergeTable = false;
    private MemberInfo currentMemberInfo = null;
    public ChoosePromotionDialog sPromotion;
    public ChooseVoucherDialog voucherService;


    public ProductRest dish;
    public TenPayPopWindow tenPayPopWindow;
    public RelativeLayout relativeLayoutCheckBottom;

    private boolean isFirstEnter;

    public boolean isChargeMember = false;

    /**
     * CHECK
     *
     * @return
     */

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public OrderFragment() {
        fragment = this;
    }

    public void initData() {
        if (!Restaurant.isFastMode) {
            if (bundle == null) {
                activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                return;
            }
            numOfPeople = bundle.getInt(SmartPosBundle.PEOPLE_NUMBER);

            OrderParams table = (OrderParams) bundle.getSerializable(SmartPosBundle.TABLE_ID);
            tableIds = table.getTableIds();

            final boolean newbill = bundle.getBoolean(SmartPosBundle.NEW_BILL);
            isForMergeTable = bundle.getBoolean(SmartPosBundle.IS_MERGE);

            currentTable = SanyiSDK.rest.operationData.getSeat(tableIds.get(0));
            if (null != currentTable) {
                textViewTableName.setText(currentTable.tableName);
            }
            if (newbill) {
                startNewBill(tableIds, numOfPeople);
            } else {
                openTableRequest();
            }
            if (isForMergeTable) {
                TextPaint tp = textViewTableName.getPaint();
                tp.setFakeBoldText(true);
                //textViewTableName.setTextColor(Color.parseColor("#ff0000"));
                String combineTableName = getCombineTableName();
                String tag = (bundle.getString(SmartPosBundle.TAG) != null) ? bundle.getString(SmartPosBundle.TAG) : null;
                String str = String.format("%s(<font color='#FF0000'>%s</font>)", combineTableName, (tag == null) ? currentTable.order.tag : tag);
                textViewTableName.setText(Html.fromHtml(str));

            }
        } else {
            textViewTableName.setText(R.string.empty);
        }
    }

    public OrderDishItemFragment.OnClickDish onClickDish = new OrderDishItemFragment.OnClickDish() {
        @Override
        public void onClickDish(Units unit, View view) {

            if (unit.products.size() > 1) {
                final ChooseUnitDialog chooseUnitDialog = new ChooseUnitDialog((MainScreenActivity) getActivity(), unit, view, new ChooseUnitDialog.IChooseUnitListener() {

                    @Override
                    public void sure(ProductRest productRest) {
                        // TODO Auto-generated method stub
                        addDish(productRest, null, 1);

                        if (!productRest.productType.isIsSet() && productRest.mixed == null) {
                            if (productRest.productType.isIsMarket()) {
//                                if (isDialogShow()) {
//                                    return;
//                                }
//                                mOrderOptionDialog = new OrderOptionDialog(getActivity(), orderListAdpater.getCurrentSelectOrder(), new Listener.OnOrderOpBtnClickListener() {
//
//                                    @Override
//                                    public void sure() {
//                                        // TODO Auto-generated method stub\
//                                        onOrderUpdate();
//                                        orderListAdpater.notifyDataSetChanged();
//                                    }
//
//                                    @Override
//                                    public void cancel() {
//                                        // TODO Auto-generated method stub
//
//                                    }
//
//                                });
//                                mOrderOptionDialog.show();
                                new WeightDialog().show(getActivity(), getString(R.string.customer_price),
                                        String.valueOf(orderListAdpater.getCurrentSelectOrder().getCurrentPrice()), WeightDialog.CUSTOMER_PRICE, new Listener.OnChangePriceListener() {
                                            @Override
                                            public void onSure(Double count) {
                                                OrderDetail od = orderListAdpater.getCurrentSelectOrder();
                                                od.setOriginPrice(count);
                                                od.setCurrentPrice(count);
                                                od.setRealCurrentPrice(count);
                                                od.setPriceChanged(true);
                                                onOrderUpdate();
                                                orderListAdpater.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancel() {

                                            }
                                        });
                            }
                            if (productRest.productType.isIsWeight()) {
                                new WeightDialog().show(getActivity(), getString(R.string.weight_dish), String.valueOf(orderListAdpater.getCurrentSelectOrder().getWeight()), ChangePriceDialog.CHANGE_WEIGHT, new Listener.OnChangePriceListener() {
                                    @Override
                                    public void onSure(Double count) {
                                        orderListAdpater.getCurrentSelectOrder().setWeight(count);
                                        orderListAdpater.notifyDataSetChanged();
                                        onOrderUpdate();
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                            }
                            if (SanyiSDK.rest.config.autoGotoCooking) {
                                if (productRest.subProducts.size() > 0) {
                                    if (isDialogShow()) {
                                        return;
                                    }
                                    mOrderOptionDialog = new OrderOptionDialog(getActivity(), orderListAdpater.getCurrentSelectOrder(), productRest.mixed != null ? productRest.mixed : 0, new Listener.OnOrderOpBtnClickListener() {

                                        @Override
                                        public void sure() {
                                            // TODO Auto-generated method stub
                                            onOrderUpdate();
                                            orderListAdpater.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void sure(OrderDetail orderDetail) {

                                        }

                                        @Override
                                        public void cancel() {
                                            // TODO Auto-generated method stub

                                        }

                                    });
                                    mOrderOptionDialog.show();
                                }
                            }
                        }

                    }
                });
                chooseUnitDialog.show();
            } else {
                dish = unit.products.get(0);

                if (!dish.soldout || (dish.getSoldoutCount() > 0 && !dish.isLongterm())) {
//                    if (dish.mixed == null || dish.mixed != null && dish.mixed == 0)
                    addDish(dish, null, 1);

                    if (!dish.productType.isIsSet()) {
                        if (dish.productType.isIsMarket()) {
//                            if (isDialogShow()) {
//                                return;
//                            }
//                            mOrderOptionDialog = new OrderOptionDialog(getActivity(), orderListAdpater.getCurrentSelectOrder(), new Listener.OnOrderOpBtnClickListener() {
//
//                                @Override
//                                public void sure() {
//                                    // TODO Auto-generated method stub
//                                    onOrderUpdate();
//                                    orderListAdpater.notifyDataSetChanged();
//                                }
//
//                                @Override
//                                public void cancel() {
//                                    // TODO Auto-generated method stub
//
//                                }
//
//                            });
//                            mOrderOptionDialog.show();
                            new WeightDialog().show(getActivity(), getString(R.string.customer_price),
                                    String.valueOf(orderListAdpater.getCurrentSelectOrder().getCurrentPrice()), WeightDialog.CUSTOMER_PRICE, new Listener.OnChangePriceListener() {
                                        @Override
                                        public void onSure(Double count) {
                                            OrderDetail od = orderListAdpater.getCurrentSelectOrder();
                                            od.setOriginPrice(count);
                                            od.setCurrentPrice(count);
                                            od.setRealCurrentPrice(count);
                                            od.setPriceChanged(true);
                                            onOrderUpdate();
                                            orderListAdpater.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    });
                        }
                        if (dish.productType.isIsWeight()) {
                            new WeightDialog().show(getActivity(), getString(R.string.weight_dish), String.valueOf(orderListAdpater.getCurrentSelectOrder().getWeight()), ChangePriceDialog.CHANGE_WEIGHT, new Listener.OnChangePriceListener() {
                                @Override
                                public void onSure(Double count) {
                                    orderListAdpater.getCurrentSelectOrder().setWeight(count);
                                    onOrderUpdate();
                                    orderListAdpater.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }
                        if (SanyiSDK.rest.config.autoGotoCooking) {
                            if (dish.subProducts.size() > 0) {
                                if (isDialogShow()) {
                                    return;
                                }
                                if (dish.mixed != null) {
                                    mOrderOptionDialog = new OrderOptionDialog(getActivity(), OrderUtil.createOrderDetail(dish, 1), dish.mixed != null ? dish.mixed : 0, new Listener.OnOrderOpBtnClickListener() {

                                        @Override
                                        public void sure() {
                                            // TODO Auto-generated method stub
                                            onOrderUpdate();
                                            orderListAdpater.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void sure(OrderDetail orderDetail) {
//                                            orderListAdpater.addOrderDetail(orderDetail);
//                                            orderListAdpater.setSelectedIndex(orderListAdpater.getCount() - 1);
//                                            orderList.setSelection(orderListAdpater.getCount());
//                                            orderListAdpater.notifyDataSetChanged();
//                                            onOrderUpdate();
                                        }

                                        @Override
                                        public void cancel() {
                                            // TODO Auto-generated method stub

                                        }

                                    });
                                    mOrderOptionDialog.show();
                                } else {
                                    mOrderOptionDialog = new OrderOptionDialog(getActivity(),orderListAdpater.getCurrentSelectOrder() , 0, new Listener.OnOrderOpBtnClickListener() {

                                        @Override
                                        public void sure() {
                                            // TODO Auto-generated method stub
                                            orderListAdpater.notifyDataSetChanged();
                                            onOrderUpdate();
                                        }

                                        @Override
                                        public void sure(OrderDetail orderDetail) {

                                        }

                                        @Override
                                        public void cancel() {
                                            // TODO Auto-generated method stub

                                        }

                                    });
                                    mOrderOptionDialog.show();
                                }
                            }

                        }
                    }
                } else {
                    if (dish.isLongterm()) {
                        Toast.makeText(getActivity(), getString(R.string.orderfragment_dish_void), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.orderfragment_dish_sold), Toast.LENGTH_LONG).show();
                    }
                }
            }

        }
    };

    @Override
    public void closeDrawerLayout() {
        drawerLayout.closeDrawers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_fragment_new, container, false);
        fragment = this;
        mainView = rootView;
        activity = (MainScreenActivity) getActivity();
        textViewTableName = (TextView) rootView.findViewById(R.id.textViewTableName);
//        textViewOrderNumOfPeopleServed = (TextView) rootView.findViewById(R.id.textViewOrderNumOfPeopleServed);
        textViewNotOrderPlace = (TextView) rootView.findViewById(R.id.textViewNotOrderPlace);
        textViewSecondDiscount = (RelativeLayout) rootView.findViewById(R.id.order_can_second);
        switch_table = (LinearLayout) rootView.findViewById(R.id.switch_table);
        order_remark = (TextView) rootView.findViewById(R.id.order_remark);
        memberScoreLl = (LinearLayout) mainView.findViewById(R.id.ll_member_score);
        memberScoreNotice = (TextView) mainView.findViewById(R.id.textView_member_notice2);
        drawerLayout = (HackyDrawerLayout) rootView.findViewById(R.id.drawerlayout_order);
        imageViewSawtooth = (ImageView) rootView.findViewById(R.id.imageView_order_sawtooth);
        imageViewSawtooth.scrollTo(0, -3);
        order_left_layout = (LinearLayout) rootView.findViewById(R.id.order_left_layout);
        textViewSwitchTable = (TextView) rootView.findViewById(R.id.textView_switch_table);
        if (Restaurant.isFastMode) {
            textViewSwitchTable.setText(R.string.table_fastmode_set_no);
        }
        check_view = (LinearLayout) rootView.findViewById(R.id.view_check);
        check_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) check_view.getLayoutParams();
                params.width = activity.metric.widthPixels / 3 * 2;
                check_view.setLayoutParams(params);
            }
        });
        switch_table.setOnClickListener(this);
        order_remark.setOnClickListener(this);
        textViewSecondDiscount.setOnClickListener(this);

        orderList = (ListView) rootView.findViewById(R.id.listViewOrderList);
        orderList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (activity.presentation != null) {
                    if (firstVisibleItem == 0) {
                        activity.presentation.scollListView(0);
                        return;
                    }
                    activity.presentation.scollListView(firstVisibleItem + visibleItemCount);
                }
            }
        });
        orderListAdpater = new CheckDetailListAdapter(getActivity(), CheckDetailListAdapter.Type.ORDER);
        orderListAdpater.setListView(orderList);
        orderList.setAdapter(orderListAdpater);
        if (activity.presentation != null) {
            activity.presentation.showOrderTotal(true);
            activity.presentation.initAmountView(MainScreenActivity.ORDER_FRAGMENT);
            activity.presentation.setListViewAdapter(orderListAdpater);
        }
        if (Restaurant.isFastMode) {
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                }
            });
        }
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NoRepeatClickUtils.isFastClick()) {
                    return;
                }
                orderListAdpater.setSelectedIndex(position);
                if (orderListAdpater.getCurrentSelectOrder() == null) return;
                if (!Restaurant.isFastMode && orderListAdpater.getCurrentSelectOrder().isPlaced())
                    return;
                new OrderDetailDialog(activity, orderListAdpater.getCurrentSelectOrder(), view, new OrderDetailDialog.OrderDetailDialogListener() {

                    @Override
                    public void ondismiss() {

                        orderListAdpater.notifyDataSetChanged();
                        onOrderUpdate();
                    }

                    @Override
                    public void placeReturnDish() {
                        final OrderDetail orderDetail = orderListAdpater.getCurrentSelectOrder();
                        new AuthDialog().show(activity, ConstantsUtil.PERMISSION_RETURN_DISH, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
                            @Override
                            public void onAuthSuccess(StaffRest staff) {
                                orderDetail.setAuth_staff_id(staff.id);
                                if (orderDetail.getQuantity() - orderDetail.getVoid_quantity() > 1) {

                                    OpenTableDialog returnDishDialog = new OpenTableDialog(activity, getContext().getString(R.string.orderfragment_voiddish_num),
                                            "1", orderDetail.getQuantity() - orderDetail.getVoid_quantity(), OpenTableDialog.RETURN_DISH_COUNT, new Listener.OnSetReturnDishCountListener() {
                                        @Override
                                        public void sure(final int returncount) {
                                            new ReturnDishReasonDialog().show(activity, new Listener.OnChooseReasonListener() {
                                                @Override
                                                public void onSure(long selectId, String editReason) {
                                                    returnDish(orderDetail, returncount, selectId, editReason);
                                                }

                                                @Override
                                                public void onCancel() {

                                                }
                                            });
                                        }

                                        @Override
                                        public void cancel() {

                                        }
                                    });
                                    returnDishDialog.show();
                                } else {
                                    new ReturnDishReasonDialog().show(activity, new Listener.OnChooseReasonListener() {
                                        @Override
                                        public void onSure(long selectId, String editReason) {
                                            returnDish(orderDetail, 1, selectId, editReason);
                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }

                    @Override
                    public void placeFreeDish() {
                        AuthDialog freeauthDialog = new AuthDialog();
                        freeauthDialog.show(activity, ConstantsUtil.PERMISSION_FREE_DISH, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
                            @Override
                            public void onAuthSuccess(StaffRest staff) {
                                SanyiScalaRequests.changeDishRequest(new PlaceDetailPresenterImpl.ListParams(orderListAdpater.getCurrentSelectOrder()).getOrderDetails(), AddDetaiAction.ACTION_GIFT, new IBatchHandleDetailListener() {
                                    @Override
                                    public void onSuccess(TableOrderInfo info, List<OrderDetail> ods) {
                                        orderListAdpater.getCurrentSelectOrder().setFree(true);
                                        orderListAdpater.notifyDataSetChanged();
                                        onOrderUpdate();
                                    }


                                    @Override
                                    public void onFail(String error) {

                                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }

                    @Override
                    public void placeChangePrice() {
                        final OrderDetail orderDetail = orderListAdpater.getCurrentSelectOrder();
                        final AuthDialog authDialog = new AuthDialog();
                        authDialog.show(activity, ConstantsUtil.PERMISSION_CHANGE_PRICE, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {

                            @Override
                            public void onCancel() {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAuthSuccess(StaffRest staff) {
                                new ChangePriceDialog().show(activity, orderDetail, ChangePriceDialog.CHANGE_PRICE, new Listener.OnChangePriceListener() {
                                    @Override
                                    public void onSure(final Double count) {
                                        SanyiScalaRequests.changeDishPriceAWeightReqeust(orderDetail, AddDetaiAction.ACTION_CHANGEPRICE, count, new IHandleDetailListener() {

                                            @Override
                                            public void onSuccess(TableOrderInfo info, OrderDetail orderDetail) {
                                                orderListAdpater.getCurrentSelectOrder().setPriceChanged(true);
                                                orderListAdpater.getCurrentSelectOrder().setCurrentPrice(count);
                                                orderListAdpater.getCurrentSelectOrder().setRealCurrentPrice(count);
                                                onOrderUpdate();
                                                orderListAdpater.notifyDataSetChanged();
                                            }


                                            @Override
                                            public void onFail(String error) {
                                                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });

                            }

                        });


                    }

                    @Override
                    public void remark() {
                        OrderRemarklDialog remark = new OrderRemarklDialog(activity, orderListAdpater.getCurrentSelectOrder(), new Listener.OnOrderOpBtnClickListener() {
                            @Override
                            public void sure() {
                                orderListAdpater.notifyDataSetChanged();
                            }

                            @Override
                            public void sure(OrderDetail orderDetail) {

                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        remark.show();
                    }

                    @Override
                    public void deleteDish() {
                        OrderDetail order = orderListAdpater.getCurrentSelectOrder();
                        if (order == null) return;
                        orderListAdpater.orderDetailList.remove(order);
                        orderListAdpater.orderDetailsListForDisPlay.remove(order);
                        if (order.isSet()) {
                            for (OrderDetail serOrder : order.setOrders) {
                                orderListAdpater.orderDetailsListForDisPlay.remove(serOrder);
                            }
                        }
                        orderListAdpater.notifyDataSetChanged();
                        onOrderUpdate();
                    }

                    @Override
                    public void freedish() {
                        AuthDialog freeauthDialog = new AuthDialog();
                        freeauthDialog.show(activity, ConstantsUtil.PERMISSION_FREE_DISH, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
                            @Override
                            public void onAuthSuccess(StaffRest staff) {
                                if (orderListAdpater.getCurrentSelectOrder().isFree()) {
                                    orderListAdpater.getCurrentSelectOrder().setFree(false);
                                } else {
                                    orderListAdpater.getCurrentSelectOrder().setFree(true);
                                }
                                orderListAdpater.notifyDataSetChanged();
                                onOrderUpdate();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });

                    }

                    @Override
                    public void changePrice() {
                        AuthDialog authDialog = new AuthDialog();
                        authDialog.show(activity, ConstantsUtil.PERMISSION_CHANGE_PRICE, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
                            @Override
                            public void onAuthSuccess(StaffRest staff) {
                                new ChangePriceDialog().show(activity, orderListAdpater.getCurrentSelectOrder(), ChangePriceDialog.CHANGE_PRICE, new Listener.OnChangePriceListener() {
                                    @Override
                                    public void onSure(Double count) {
                                        orderListAdpater.getCurrentSelectOrder().setPriceChanged(true);
                                        orderListAdpater.getCurrentSelectOrder().setCurrentPrice(count);
                                        orderListAdpater.getCurrentSelectOrder().setRealCurrentPrice(count);
                                        orderListAdpater.notifyDataSetChanged();
                                        onOrderUpdate();
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }

                    @Override
                    public void other() {
                        if (orderListAdpater.getCurrentSelectOrder().isSet() && !orderListAdpater.getCurrentSelectOrder().isPlaced()) {
                            SelectDishSet dishSet = new SelectDishSet();
                            dishSet.show(fragment, getContext().getString(R.string.orderfragment_dishset), orderListAdpater.getCurrentSelectOrder(), orderListAdpater.getCurrentSelectOrder().getProductId(), new SelectDishSet.selectDishSetInterface() {

                                @Override
                                public void OnCancelButtonPressed() {
                                    // TODO Auto-generated method stub
                                }

                                @Override
                                public void OnOkButtonPressed(OrderDetail detail) {
                                    orderListAdpater.notifyDataSetChanged();
                                    onOrderUpdate();
                                }
                            });
                        } else {
                            new OrderOptionDialog(getActivity(), orderListAdpater.getCurrentSelectOrder(), 0, new Listener.OnOrderOpBtnClickListener() {

                                @Override
                                public void sure() {
                                    // TODO Auto-generated method stub
                                    orderListAdpater.notifyDataSetChanged();
                                    onOrderUpdate();
                                }

                                @Override
                                public void sure(OrderDetail orderDetail) {
                                    orderListAdpater.addOrderDetail(orderDetail);
                                    orderListAdpater.setSelectedIndex(orderListAdpater.getCount() - 1);
                                    orderList.setSelection(orderListAdpater.getCount());
                                    orderListAdpater.notifyDataSetChanged();
                                    onOrderUpdate();
                                }

                                @Override
                                public void cancel() {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
                        }
                    }
                }).show();


            }
        });
        rootView.findViewById(R.id.empty_layout).setVisibility(View.GONE);
        orderList.setEmptyView(rootView.findViewById(R.id.empty_layout));
        initData();
        initDrawerLayout();
        Restaurant.isSpellOrder = SharePreferenceUtil.getBooleanPreference(activity, SmartPosPrivateKey.ST_SPELL_ORDER, false);
        initOrderMode();
        return rootView;
    }

    private void returnDish(final OrderDetail order, final int returncount, long selectId, String editReason) {
        SanyiScalaRequests.returnDishRequest(order, returncount, selectId, editReason, new IHandleDetailListener() {
            @Override
            public void onSuccess(TableOrderInfo info, OrderDetail orderDetail) {
                order.setVoid_quantity(returncount);
                orderListAdpater.notifyDataSetChanged();

                Toast.makeText(activity, activity.getString(R.string.operation_success), Toast.LENGTH_LONG).show();
            }


            @Override
            public void onFail(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initDrawerLayout() {


        textViewCheckPayable = (TextView) mainView.findViewById(R.id.bill_payable);
        textViewBillAmount = (TextView) mainView.findViewById(R.id.bill_amount);
        textViewCheckUnPaid = (TextView) mainView.findViewById(R.id.bill_unpaid);
        textViewCheckPaid = (TextView) mainView.findViewById(R.id.bill_paid);
        textViewTablePromotion = (TextView) mainView.findViewById(R.id.bill_promotion);

        sales_promotion_gridview = (HorizontalListView) mainView.findViewById(R.id.sales_promotion_gridview);
        voucher_gridview = (HorizontalListView) mainView.findViewById(R.id.voucher_gridview);
        payment_method_gridview = (GridView) mainView.findViewById(R.id.payment_method_gridview);
        sales_promotion_layout = (LinearLayout) mainView.findViewById(R.id.sales_promotion_layout);
        voucher_layout = (LinearLayout) mainView.findViewById(R.id.voucher_layout);
        member_info_layout = (LinearLayout) mainView.findViewById(R.id.include_member);
        memberName = (TextView) mainView.findViewById(R.id.textView_member_name);
        memberBalance = (TextView) mainView.findViewById(R.id.textView_member_balance);
        memberTotal = (TextView) mainView.findViewById(R.id.textView_member_total);
        member_btn = (TextView) mainView.findViewById(R.id.member_btn);
        memberPhone = (TextView) mainView.findViewById(R.id.textView_member_phone);
        memberScoreLl = (LinearLayout) mainView.findViewById(R.id.ll_member_score);
        memberScoreNotice = (TextView) mainView.findViewById(R.id.textView_member_notice2);
        memberNum = (TextView) mainView.findViewById(R.id.textView_member_num);
        mainView.findViewById(R.id.button_member_logout).setOnClickListener(this);


        member_btn.setOnClickListener(this);
        mainView.findViewById(R.id.check_btn).setVisibility(View.INVISIBLE);
        mainView.findViewById(R.id.preprint_btn).setVisibility(View.INVISIBLE);
        mainView.findViewById(R.id.imageView_cancel_member).setOnClickListener(this);
//        mainView.findViewById(R.id.relativeLayout_check_bottom_button).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
        memberBalanceCharge = (Button) mainView.findViewById(R.id.button_member_charge);
        memberBalanceCharge.setOnClickListener(this);
        memberBalancePayBalance = (Button) mainView.findViewById(R.id.button_member_pay_balance);
        memberBalancePayBalance.setOnClickListener(this);
        sales_promotion_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                long itemId = mPromotionAdapter.getItemId(position);
                List<CashierParamResult.Promotion> rightPromotion = new ArrayList<CashierParamResult.Promotion>();
                if (itemId == -1) {
                    if (checkPresenterImpl.currentMemberInfo == null) {
                        //未验证会员,从所有活动取所有人参与的活动
                        for (CashierParamResult.Promotion promotion : cashierParam.promotions) {
                            if (!promotion.forAll) {
                                rightPromotion.add(promotion);
                            }
                        }
                    } else {
                        rightPromotion.addAll(cashierParam.promotions);
                    }
                    if (rightPromotion == null || rightPromotion.size() <= 0) {
                        Toast.makeText(activity, "没有可用活动", Toast.LENGTH_SHORT).show();
                    } else {
                        if (null == sPromotion) {
                            sPromotion = new ChoosePromotionDialog(getActivity(), checkPresenterImpl, new Listener.OnChoosePromotionListener() {

                                @Override
                                public void onSure() {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onCancel() {
                                    // TODO Auto-generated method stub

                                }

                            });
                        }
                        sPromotion.show();
                        sPromotion.setMemberInfo(currentMemberInfo);
                        sPromotion.refresh(cashierParam, cashierResult);
                    }
                }
            }
        });

        voucher_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
                    Toast.makeText(activity, getString(R.string.str_common_no_privilege), Toast.LENGTH_LONG).show();
                    return;
                }
                CashierPayment cashierPayment = mVoucherAdapter.getItem(position);
                if (-1 == cashierPayment.id) {
                    voucherService = new ChooseVoucherDialog(getActivity(), checkPresenterImpl, cashierParam, cashierResult, new Listener.OnChooseVoucherListener() {

                        @Override
                        public void onSure() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onCancel() {
                            // TODO Auto-generated method stub

                        }

                    });
                    voucherService.show();
                }
            }
        });

        payment_method_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View parentView, final int position, long id) {
                // TODO Auto-generated method stub
                if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {

                    Toast.makeText(activity, getString(R.string.str_common_no_privilege), Toast.LENGTH_LONG).show();
                    return;
                }

                final List<CashierPayment> cashierPaymentModes = mPaymentModeAdapter.getItem(position);
                if (cashierPaymentModes.size() > 0) {
                    PaymentSelector paymentSelector = new PaymentSelector(activity, parentView, mPaymentModeAdapter.getItem(position));
                    paymentSelector.show(new PaymentSelector.ClickPayment() {
                        @Override
                        public void clickPayment(CashierPayment payment) {
                            checkPresenterImpl.removePayment(payment.paymentName, payment.value - payment.change, payment.paymentType, payment.id);
                        }

                        @Override
                        public void addPayment() {
                            CashierPayment payment = new CashierPayment();
                            payment.paymentName = mPaymentModeAdapter.data.get(position).name;
                            payment.paymentType = mPaymentModeAdapter.data.get(position).paymentType;
                            payment.id = mPaymentModeAdapter.data.get(position).id;
                            addCashierPayment(parentView, payment);
                        }
                    });
                } else {
                    CashierPayment payment = new CashierPayment();
                    payment.paymentName = mPaymentModeAdapter.data.get(position).name;
                    payment.paymentType = mPaymentModeAdapter.data.get(position).paymentType;
                    payment.id = mPaymentModeAdapter.data.get(position).id;
                    addCashierPayment(parentView, payment);
                }

            }
        });
    }

    private void payByBalance(final View v) {
        // TODO Auto-generated method stub
        if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {


            Toast.makeText(activity, getString(R.string.str_common_no_privilege), Toast.LENGTH_LONG).show();

            return;
        }
        final CashierPayment paymentMode = new CashierPayment();
        paymentMode.paymentType = ConstantsUtil.PAYMENT_STORE_VALUE;
        paymentMode.paymentName = getString(R.string.rechargeable_card);
        if (SanyiSDK.rest.config.isMemberUsePassword) {
            MemberPwdPopWindow memberPwdPopWindow = new MemberPwdPopWindow(v, activity, new MemberPwdPopWindow.OnSureListener() {

                @Override
                public void onSureClick(final String pwd) {
                    // TODO Auto-generated method stub
                    final NumPadPopWindow numPadPopWindow = new NumPadPopWindow(v, getActivity(), cashierResult, paymentMode, new NumPadPopWindow.OnSureListener() {

                        @Override
                        public void onSureClick(Double value, Double change) {
                            // TODO Auto-generated method stub
                            checkPresenterImpl.addMemberPayment(value, pwd);
                        }
                    });
                    numPadPopWindow.show();


                }
            });
            memberPwdPopWindow.show();
            return;
        }
        final NumPadPopWindow numPadPopWindow = new NumPadPopWindow(v, getActivity(), cashierResult, paymentMode, new NumPadPopWindow.OnSureListener() {

            @Override
            public void onSureClick(Double value, Double change) {
                // TODO Auto-generated method stub
                checkPresenterImpl.addMemberPayment(value, null);
            }
        });
        numPadPopWindow.show();
    }

    private void payByPoint(final View v) {
        // TODO Auto-generated method stub
        if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
            Toast.makeText(activity, "没有权限", Toast.LENGTH_SHORT).show();

            return;
        }
//        final CashierPayment paymentMode = new CashierPayment();
//        paymentMode.paymentType = ConstantsUtil.PAYMENT_POINT;
//        paymentMode.paymentName = getString(R.string.payment_point);
        if (SanyiSDK.rest.config.isMemberUsePassword) {
            MemberPwdPopWindow memberPwdPopWindow = new MemberPwdPopWindow(v, activity, new MemberPwdPopWindow.OnSureListener() {

                @Override
                public void onSureClick(final String pwd) {

                    // TODO Auto-generated method stub
//                    CashierPayment payment = new CashierPayment();
//
//                    payment.paymentName = "积分抵现";
//                    payment.paymentType = ConstantsUtil.PAYMENT_POINT;
//                        payment.id = mPaymentModeAdapter.data.get(position).id;

                    checkPresenterImpl.addPointPayment(cashierResult.pointInfo.value, pwd);

                }
            });
            memberPwdPopWindow.show();
            return;
        } else {
            checkPresenterImpl.addPointPayment(cashierResult.pointInfo.value, null);
//            CashierPayment payment = new CashierPayment();
//
//            payment.paymentName = "积分抵现";
//            payment.paymentType = ConstantsUtil.PAYMENT_POINT;
//            checkPresenterImpl.addPay(cashierResult.pointInfo.value, 0.0, payment);
        }
    }

    public void addCashierPayment(final View view, final CashierPayment cashierPaymentMode) {
        double value = Double.valueOf(cashierResult.bill.unpaid);
        if (value < 0 && cashierPaymentMode.paymentType != ConstantsUtil.PAYMENT_CASH) {

            Toast.makeText(activity, getString(R.string.orderfragment_verify_payment_value), Toast.LENGTH_LONG).show();
            return;
        }
        if (cashierPaymentMode.paymentType == ConstantsUtil.PAYMENT_STORE_VALUE) {
            if (currentMemberInfo == null) {

                Toast.makeText(activity, getString(R.string.orderfragment_verify_vip), Toast.LENGTH_LONG).show();
                return;
            }
            payByBalance(view);
            return;
        }
        if (cashierPaymentMode.paymentType == ConstantsUtil.PAYMENT_POINT) {
            if (currentMemberInfo == null) {

                Toast.makeText(activity, "请先验证会员", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cashierResult.pointInfo != null && cashierResult.pointInfo.point > 0)
                payByPoint(view);
            else
                Toast.makeText(activity, "此单可用积分为0", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cashierPaymentMode.paymentType != ConstantsUtil.PAYMENT_WECHAT) {
            final NumPadPopWindow numPadPopWindow = new NumPadPopWindow(view, getActivity(), cashierResult, cashierPaymentMode, new NumPadPopWindow.OnSureListener() {

                @Override
                public void onSureClick(Double value, Double change) {
                    // TODO Auto-generated method stub
//                                if (cashierPaymentMode.paymentType == ConstantsUtil.PAYMENT_DERATE) {
//                                    choosePaymentReason(value, change);
//                                }
                    checkPresenterImpl.addPay(value, change, cashierPaymentMode);
                    if (activity.serialPort != null) {
                        try {
                            activity.serialPort.getOutputStream().write(SerialCommand.initCommand());
                            activity.serialPort.getOutputStream().write(SerialCommand.showNums(OrderUtil.decimalFormatter.format(change)));
                            activity.serialPort.getOutputStream().write(SerialCommand.setCharsCommand(SerialCommand.CHARS_CHANGE_PRICE));
                            activity.serialPort.getOutputStream().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            numPadPopWindow.show();
        } else {
            final NormalDialog dialog = new NormalDialog(activity);
            View weChatPayment = LayoutInflater.from(activity).inflate(R.layout.layout_wechat_payment, null);
            TextView scanText = (TextView) weChatPayment.findViewById(R.id.textView_wechat_payment_scan);
            scanText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    checkPresenterImpl.handleRequest(CashierAction.C_GETPAYURL);

                }
            });
            TextView slotText = (TextView) weChatPayment.findViewById(R.id.textView_wechat_payment_slot_card);
            slotText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    tenPayPopWindow = new TenPayPopWindow(view, getString(R.string.please_input_2d_barcode_from_weichat), getActivity(), new TenPayPopWindow.OnSureListener() {

                        @Override
                        public void onSureClick(String value) {
                            // TODO Auto-generated method stub
                            tenPayPopWindow.dismiss();
                            checkPresenterImpl.addTenPayPayment(cashierResult.bill.unpaid, value);
                        }
                    });
                    tenPayPopWindow.show();

                }
            });
            dialog.content(weChatPayment);
            dialog.isHasConfirm(false);
            dialog.widthScale((float) 0.5);
            dialog.show();

        }
    }

    public void openTableRequest() {
        SanyiScalaRequests.openTableRequest(tableIds, new OpenTablesRequest.OnOpenTableListener() {
            @Override
            public void onSuccess(List<OpenTableDetail> resp) {
            }


            @Override
            public void onFail(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
            }
        });
    }

    public void initOrderMode() {
//        if (Restaurant.isSpellOrder) {
//            if (searchDishFragment != null) {
//                if (currentFragment == searchDishFragment) return;
//            }
//            displayView(SEARCH_DISH_FRAGMENT);
//        } else {
//            if (orderDishFragment != null) {
//                if (currentFragment == orderDishFragment) return;
//            }
//            displayView(ORDER_DISH_FRAGMENT);
//        }
        displayView(ORDER_DISH_FRAGMENT);
    }

    public void hideView(FragmentTransaction ft) {
        if (orderDishFragment != null) {
            ft.hide(orderDishFragment);
        }
        if (searchDishFragment != null) {
            ft.hide(searchDishFragment);
        }

    }

    public void displayView(int position) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        hideView(ft);
        switch (position) {
            case ORDER_DISH_FRAGMENT:
                if (orderDishFragment != null) {
                    ft.show(orderDishFragment);
                } else {
//                    System.out.println("create dish fragment");
                    orderDishFragment = new OrderDishFragment();
                    orderDishFragment.setArguments(bundle);
                    orderDishFragment.setOrderFragment(fragment);
//                    orderDishFragment.setOrderListAdapter(orderListAdpater);
                    orderDishFragment.setParent(this);
                    ft.add(R.id.dishContainer, orderDishFragment);

                }
                currentFragment = orderDishFragment;
                break;
            case SEARCH_DISH_FRAGMENT:
                if (searchDishFragment != null) {
                    ft.show(searchDishFragment);
                } else {
                    searchDishFragment = new SearchDishFragment();
                    searchDishFragment.setOrderFragment(fragment);
                    ft.add(R.id.dishContainer, searchDishFragment);
                }
                currentFragment = searchDishFragment;
                break;
            default:
                break;
        }
//        System.out.println("commit transaction");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isChargeMember) {
            isChargeMember = false;
            return;
        }
        if (!hidden) {
            initData();
            initOrderMode();
//            if (orderDishFragment != null) {
//                orderDishFragment.initView();
//                orderDishFragment.initOrderView();
//            }
            if (activity.presentation != null) {
                activity.presentation.showOrderTotal(true);
                activity.presentation.initAmountView(MainScreenActivity.ORDER_FRAGMENT);
                activity.presentation.setListViewAdapter(orderListAdpater);
            }
            if (Restaurant.isFastMode) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        } else {
            unLockTable();
            closeDialog();
            if (activity.presentation != null) {
                activity.presentation.showOrderTotal(false);
            }
            orderListAdpater.orderDetailList.clear();
            orderListAdpater.orderDetailsListForDisPlay.clear();
            orderListAdpater.notifyDataSetChanged();
            onOrderUpdate();
            if (Restaurant.isFastMode)
                clearData();
        }

    }

    private void closeDialog() {
        if (scanDialog != null) {
            if (scanDialog.isShowing()) {
                scanDialog.dismiss();
            }
        }
    }

    public void unLockTable() {
        activity.unLockTable(tableIds, false);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub

        super.onResume();
        orderDishFragment.refreshDish();//refresh soldout dish state when it is commit by myself
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

    public void removeOrderitemOptionFragment() {
        if (currentFragment instanceof OrderDishFragment) {
            return;
        }
        displayView(ORDER_DISH_FRAGMENT);
    }


    public void startNewBill(final List<Long> tables, final int numOfPeople) {
        SanyiScalaRequests.addBillRequest(tables, numOfPeople, new INewBillRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(List<OpenTableDetail> result) {
                // TODO Auto-generated method stub
                OpenTableDetail openTableDetail = result.get(0);
                if (openTableDetail != null) {
                    updateBillInfo(openTableDetail.info);
                    List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
                    for (OrderDetail orderDetail : openTableDetail.ods) {
                        orderDetail.setSortOrder(orderDetails.size() + 1);
                        orderDetails.add(orderDetail);
                    }
                    orderListAdpater.setOrderDetails(orderDetails);
                    orderListAdpater.notifyDataSetChanged();
                    onOrderUpdate();
                    Toast.makeText(activity, getString(R.string.orderfragment_opentable_success), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity, getString(R.string.orderfragment_getbill_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * @return the isForMergeTable
     */
    public boolean isForMergeTable() {
        return isForMergeTable;
    }

    /**
     * @return the tableIds
     */
    public List<Long> getTableIds() {
        return tableIds;
    }

    /**
     * @return list of order details which may belongs to different order
     */

    public List<OrderDetail> getCurrentOrderDetails() {
        return orderListAdpater.orderDetailList;
    }


    public void updateBillInfo(TableOrderInfo info) {
        if (info != null) {
            if (Restaurant.isFastMode) {
                textViewTableName.setText(info.getOrderName());
            } else {
                textViewTableName.setText(info.getTableName());
            }
        }
    }

    public String getCombineTableName() {
        String name = "";
        for (int i = 0; i < tableIds.size(); i++) {
            name = name + SanyiSDK.rest.getTableById(tableIds.get(i)).tableName + ",";
        }
        if (!name.isEmpty()) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    public void onOrderUpdate() {
        Double noPlaceToal = orderListAdpater.getOrderTotal();
        String total = OrderUtil.dishPriceFormatter.format(noPlaceToal);
        textViewNotOrderPlace.setText(total);
        if (activity.presentation != null) {
            activity.presentation.refresh(total);
        }
        if (tableOrderInfo != null)
            if (Restaurant.isFastMode) {
                textViewTableName.setText(tableOrderInfo.getOrderName());
            } else {
                textViewTableName.setText(tableOrderInfo.getTableName());
            }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button_member_logout:
                checkPresenterImpl.cancelMember();
                break;
            case R.id.button_member_charge:
                if (bundle == null) bundle = new Bundle();
                bundle.putSerializable("member_info", (Serializable) checkPresenterImpl.currentMemberInfo);
                activity.displayView(MainScreenActivity.MEMBER_FRAGMENT, bundle);
                isChargeMember = true;
                break;
            case R.id.button_member_pay_balance:
                payByBalance(v);
                break;
            case R.id.imageView_cancel_member:
                if (checkPresenterImpl != null)
                    checkPresenterImpl.cancelMember();
                break;
            case R.id.member_btn:
                if (checkPresenterImpl != null)
                    checkPresenterImpl.verifyMember();
                break;
            case R.id.switch_table:
                if (!Restaurant.isFastMode) {
                    if (!orderListAdpater.orderDetailList.isEmpty()) {
                        Iterator<OrderDetail> itr = orderListAdpater.orderDetailList.iterator();
                        while (itr.hasNext()) {
                            if (!itr.next().isPlaced()) {
                                Toast.makeText(activity, getString(R.string.orderfragment_dishnotplaced_not_switch), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                    if (null != currentTable) {
                        new SwitchTableDialog(activity, SwitchTableDialog.ORDER, currentTable, new ISwitchTableListener() {
                            @Override
                            public void batchOperation(long[] tableIds) {

                            }

                            @Override
                            public void openTable(SeatEntity table, int personCount) {
                                // TODO Auto-generated method stub
                                activity.unLockTable(tableIds, false);
                                tableIds.clear();
                                tableIds.add(table.seat);
                                openTableRequest();
                                currentTable = SanyiSDK.rest.operationData.getSeat(table.seat);
                                numOfPeople = currentTable.order.personCount;
                                textViewTableName.setText(currentTable.tableName);
                                orderListAdpater.setOrderDetails(new ArrayList<OrderDetail>());
                                orderListAdpater.notifyDataSetChanged();
                            }

                            @Override
                            public void cancel() {
                                // TODO Auto-generated method stub
                            }
                        }).show();
                    }
                } else {
                    if (orderListAdpater.isHasPlaceDetail()) {

                        Toast.makeText(activity, getString(R.string.orderfragment_dishplaced_not_switch), Toast.LENGTH_LONG).show();
                        return;
                    }
                    new OpenTableDialog(activity, getString(R.string.open_table_dialog_fast_mode), "", OpenTableDialog.FAST_MODE, new Listener.OnFastModeNumberListener() {
                        @Override
                        public void onFastNumber(String number) {
                            if (number != null) {
                                tableName = number;
                                textViewTableName.setText(tableName);
                            }
                        }

                        @Override
                        public void cancel() {

                        }
                    }).show();

                }
                break;
            case R.id.order_can_second:
                final List<Units> secondDiscountList = new ArrayList<>();
                Units units1 = new Units();
                List<ProductRest> list1 = new ArrayList<>();
                ProductRest pr = new ProductRest();
                pr.setName("葱花炒蛋");
                pr.setId(1111);
                list1.add(pr);
                units1.products = list1;

                Units units2 = new Units();
                List<ProductRest> list2 = new ArrayList<>();
                ProductRest pr2 = new ProductRest();
                pr2.setName("野山椒牛肉");
                pr2.setId(1112);
                list2.add(pr2);
                units2.products = list2;

                Units units3 = new Units();
                List<ProductRest> list3 = new ArrayList<>();
                ProductRest pr3 = new ProductRest();
                pr3.setName("西红柿蛋汤");
                pr3.setId(1113);
                list3.add(pr3);
                units3.products = list3;

                Units units4 = new Units();
                List<ProductRest> list4 = new ArrayList<>();
                ProductRest pr4 = new ProductRest();
                pr4.setName("提拉米苏");
                pr4.setId(1114);
                list4.add(pr4);
                units4.products = list4;

                Units units5 = new Units();
                List<ProductRest> list5 = new ArrayList<>();
                ProductRest pr5 = new ProductRest();
                pr5.setName("冬瓜汤");
                pr5.setId(1115);
                list5.add(pr5);
                units5.products = list5;

                Units units6 = new Units();
                List<ProductRest> list6 = new ArrayList<>();
                ProductRest pr6 = new ProductRest();
                pr6.setName("家庭套餐");
                pr6.setId(1116);
                list6.add(pr6);
                units6.products = list6;

                Units units7 = new Units();
                List<ProductRest> list7 = new ArrayList<>();
                ProductRest pr7 = new ProductRest();
                pr7.setName("蒜蓉金针菇");
                pr7.setId(1117);
                list7.add(pr7);
                units7.products = list7;

                Units units8 = new Units();
                List<ProductRest> list8 = new ArrayList<>();
                ProductRest pr8 = new ProductRest();
                pr8.setName("泡椒凤爪");
                pr8.setId(1118);
                list8.add(pr8);
                units8.products = list8;

                Units units9 = new Units();
                List<ProductRest> list9 = new ArrayList<>();
                ProductRest pr9 = new ProductRest();
                pr9.setName("四季奶青");
                pr9.setId(1119);
                list9.add(pr9);
                units9.products = list9;

                Units units10 = new Units();
                List<ProductRest> list10 = new ArrayList<>();
                ProductRest pr10 = new ProductRest();
                pr10.setName("焦糖玛奇朵燕麦巧克力");
                pr10.setId(1110);
                list10.add(pr10);
                units10.products = list10;

                secondDiscountList.add(units1);
                secondDiscountList.add(units2);
                secondDiscountList.add(units3);
                secondDiscountList.add(units4);
                secondDiscountList.add(units5);
                secondDiscountList.add(units6);
                secondDiscountList.add(units7);
                secondDiscountList.add(units8);
                secondDiscountList.add(units9);
                secondDiscountList.add(units10);

                final List<Units> selectList = new ArrayList<>();
                ChooseSecondDiscountDialog dialog = new ChooseSecondDiscountDialog(activity, secondDiscountList, 3, new ChooseSecondDiscountDialog.ChooseSecondDiscountListener() {
                    @Override
                    public void confirm(HashMap<Long, Integer> map) {
                        selectList.clear();
                        for (Units units : secondDiscountList) {
                            if (map.get(units.products.get(0).getId()) != null && map.get(units.products.get(0).getId()) > 0) {
                                units.products.get(0).count = map.get(units.products.get(0).getId());
                                selectList.add(units);
                            }
                        }
                        Log.e("选中的菜", selectList.toString());
                    }


                });

                dialog.show();
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

//    public void beginSearchDish() {
//        if (currentFragment instanceof SearchDishFragment) {
//            searchDishFragment.setKeyBoardInput(keyBoard);
//        } else {
//        displayView(SEARCH_DISH_FRAGMENT);
//        }
//    }

    public void addDish(final ProductRest dish, List<Long> cookins, final int quantity) {
        // check if it is a set order, we need display Dialog
        // to ask /show user what set order include
        if (dish != null) {
            if (activity.serialPort != null)
                try {

                    activity.serialPort.getOutputStream().write(SerialCommand.initCommand());

                    activity.serialPort.getOutputStream().write(SerialCommand.showNums(OrderUtil.decimalFormatter.format(dish.getProductType().isIsSpecial() ? dish.originPrice
                            : dish.price)));

                    activity.serialPort.getOutputStream().write(SerialCommand.setCharsCommand(SerialCommand.CHARS_UNIT_PRICE));

                    activity.serialPort.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (dish.productType.isIsSet()) {
                SelectDishSet dishSet = new SelectDishSet();
                dishSet.show(this, getString(R.string.dish_set), null, dish.id, new SelectDishSet.selectDishSetInterface() {

                    @Override
                    public void OnCancelButtonPressed() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void OnOkButtonPressed(OrderDetail detail) {
                        // TODO Auto-generated method stub
                        orderListAdpater.addOrderDetail(detail);
                        orderListAdpater.setSelectedIndex(orderListAdpater.getCount() - 1);
                        orderList.setSelection(orderListAdpater.getCount());

//                        if (orderListAdpater.orderDetailList.size() == 1) {
                            orderListAdpater.notifyDataSetChanged();
//                        }
                        onOrderUpdate();

                    }
                });
            } else if (dish.mixed != null && dish.mixed > 0) {
                mOrderOptionDialog = new OrderOptionDialog(getActivity(), OrderUtil.createOrderDetail(dish, 1), dish.mixed != null ? dish.mixed : 0, new Listener.OnOrderOpBtnClickListener() {

                    @Override
                    public void sure() {
                        // TODO Auto-generated method stub
                        orderListAdpater.notifyDataSetChanged();
                        onOrderUpdate();
                    }

                    @Override
                    public void sure(OrderDetail orderDetail) {
                        orderListAdpater.addOrderDetail(orderDetail);
                        orderListAdpater.setSelectedIndex(orderListAdpater.getCount() - 1);
                        orderList.setSelection(orderListAdpater.getCount());
                        orderListAdpater.notifyDataSetChanged();
                        onOrderUpdate();
                    }

                    @Override
                    public void cancel() {
                        // TODO Auto-generated method stub

                    }

                });
                mOrderOptionDialog.show();
            } else {
                OrderDetail orderDetail = OrderUtil.createOrderDetail(dish, quantity);
                if (orderDetail.getIsSpecial()) {
                    orderDetail.setCurrentPrice(orderDetail.getOriginPrice());
                }
                if (cookins != null) {
                    for (Long integer : cookins) {
                        OrderDetail cook = OrderUtil.createOrderDetail(SanyiSDK.rest.getProductById(integer), quantity);
                        orderDetail.addPublicCookMethod(cook);
                    }
                }
                orderListAdpater.addOrderDetail(orderDetail);
                orderListAdpater.setSelectedIndex(orderListAdpater.getCount() - 1);
//                orderListAdpater.getView(orderListAdpater.getCount() - 1, null, null);
                orderListAdpater.notifyDataSetChanged();

                orderList.setSelection(orderListAdpater.getCount());
                onOrderUpdate();

            }
        }
    }

    @Override
    public void initParam(CashierParamResult param, CashierResult cashierResult) {
        cashierParam = param;
        if ((null != cashierParam && null != cashierParam.promotions && cashierParam.promotions.size() > 0) || (cashierParam.discountPlans != null && cashierParam.discountPlans.size() > 0)) {
            sales_promotion_layout.setVisibility(View.VISIBLE);
            mPromotionAdapter = new PromotionAdapter(getActivity(), checkPresenterImpl, cashierResult.promotions, cashierResult.discounts);
            sales_promotion_gridview.setAdapter(mPromotionAdapter);
        } else {
            sales_promotion_layout.setVisibility(View.GONE);
        }
        if (null != cashierParam && null != cashierParam.voucherTypes && cashierParam.voucherTypes.size() > 0) {
            voucher_layout.setVisibility(View.VISIBLE);
            mVoucherAdapter = new VoucherAdapter(getActivity(), checkPresenterImpl, cashierResult.payments);
            voucher_gridview.setAdapter(mVoucherAdapter);
        } else {
            voucher_layout.setVisibility(View.GONE);
        }
        mPaymentModeAdapter = new PaymentModeAdapter(getActivity(), cashierParam.paymentModes);
        payment_method_gridview.setAdapter(mPaymentModeAdapter);

    }

    @Override
    public void clearData() {
        tableIds = null;
        tableName = null;
        cashierParam = null;
        isFirstEnter = true;
        currentMemberInfo = null;
        tableOrderInfo = null;
        textViewTableName.setText(getString(R.string.table_empty));
        orderListAdpater.orderDetailList.clear();
        onOrderUpdate();
        orderListAdpater.orderDetailsListForDisPlay.clear();
        orderListAdpater.notifyDataSetChanged();
    }

    public void updateUI(CashierResult cash, List<OrderDetail> ods) {
        cashierResult = cash;
        if (cash.bill.isComplete) {
            if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_CASHIER)) {
                if (!isFirstEnter) {
                    if (null != voucherService && voucherService.isShow()) {
                        voucherService.dismiss();
                    }
                    checkPresenterImpl.handleRequest(CashierAction.C_END);
                    return;
                }
            }
        }
        if (sPromotion != null) {
            if (sPromotion.isShow()) {
                sPromotion.setMemberInfo(currentMemberInfo);
                sPromotion.refresh(cashierParam, cashierResult);
            }
        }
        if (null != voucherService) {
            voucherService.refresh(cashierResult);
        }
        if (activity.serialPort != null) {
            try {
                activity.serialPort.getOutputStream().write(SerialCommand.initCommand());
                activity.serialPort.getOutputStream().write(SerialCommand.showNums(OrderUtil.decimalFormatter.format(cash.bill.unpaid)));
                activity.serialPort.getOutputStream().write(SerialCommand.setCharsCommand(SerialCommand.CHARS_TOTAL_PRICE));
                activity.serialPort.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isFirstEnter = false;
        String tableName = "";
        for (AddDetailOrder order : cash.orders) {
            tableName += order.tableName;
        }
        if (null != mPaymentModeAdapter) {
            mPaymentModeAdapter.setPayment(cashierResult.payments);
        }
        if (null != mPromotionAdapter) {
            mPromotionAdapter.refresh(cashierResult.promotions, cashierResult.discounts);
        }
        if (null != mVoucherAdapter) {
            mVoucherAdapter.refresh(cashierResult.payments);
        }
        if (cash.discounts.size() > 0) {
            checkPresenterImpl.discountId = cash.discounts.get(0).discountPlanId;
        } else {
            checkPresenterImpl.discountId = -1;
        }
        initPermissionButton();
//        textViewTableName.setText(tableName);
        if (isForMergeTable) {
            TextPaint tp = textViewTableName.getPaint();
            tp.setFakeBoldText(true);
            String tag = SanyiSDK.rest.operationData.getCombineTag(tableIds.get(0));
            if (null != tag) {
                String str = String.format("%s(<font color='#FF0000'>%s</font>)", getCombineTableName(), tag);
                textViewTableName.setText(Html.fromHtml(str));
            }
        }
//        textViewTableWaiter.setText(cash.orders.get(0).staffName);
        textViewBillAmount.setText(OrderUtil.dishPriceFormatter.format(cash.bill.amount));
//        textViewTableServiceCharge.setText(OrderUtil.dishPriceFormatter.format(cash.bill.surchagre));
//        if (cash.bill.surchagre > 0) {
//            checkBoxTableServiceChargeCancel.setChecked(true);
//        }
//        textViewTableMinPay.setText(OrderUtil.dishPriceFormatter.format(cash.bill.mincharge));
//        if (cash.bill.mincharge > 0) {
//            checkBoxTableMinPayCancel.setChecked(true);...............................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................

//        }
        textViewTablePromotion.setText(OrderUtil.dishPriceFormatter.format(cash.bill.discount + cash.bill.promotion));
        textViewCheckPayable.setText(OrderUtil.dishPriceFormatter.format(cash.bill.realValue));
        textViewCheckPaid.setText(OrderUtil.dishPriceFormatter.format(cash.bill.paid));
        textViewCheckUnPaid.setText(OrderUtil.dishPriceFormatter.format(cash.bill.unpaid));
//        textViewRounding.setText(OrderUtil.dishPriceFormatter.format(cash.bill.rounding));


//        if (ods != null) {
//            orderAdapter.setOrderDetails(ods);
//            orderAdapter.notifyDataSetChanged();
//        }
        if (cashierResult.member != null) {
            currentMemberInfo = cashierResult.member;
            showMemberInfo();
        } else {


            currentMemberInfo = null;
            hideMemberInfo();
        }
    }

    @Override
    public void showQRCode(String url, double unpaid) {
        scanDialog = new NormalDialog(getContext());
        ImageView imageView = new ImageView(getActivity());
        imageView.setBackgroundColor(Color.WHITE);
        Glide.with(getActivity()).load(url).into(imageView);
        scanDialog.content(imageView);
        scanDialog.isHasConfirm(false);
        scanDialog.widthScale((float) 0.5);
        scanDialog.heightScale((float) 0.5);
        scanDialog.show();
        scanDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (scanDialog != null) {
                    if (scanDialog.isShowing()) {
                        scanDialog.dismiss();
                    }
                }
                if (activity.presentation != null) {
                    activity.presentation.hideDialog();
                }
            }
        });
        if (activity.presentation != null) {
            activity.presentation.showQRCode(activity.presentation.getContext(), url);
        }
    }

    @Override
    public void initPermissionButton() {

    }

    public void showMemberInfo() {
        if (currentMemberInfo != null) {
            member_btn.setVisibility(View.GONE);
            member_info_layout.setVisibility(View.VISIBLE);
            memberName.setText(getString(R.string.vip_name_label) + currentMemberInfo.name + "(" + currentMemberInfo.getMemberTypeName() + ")");
            memberPhone.setText("手机号:" + currentMemberInfo.getMobile());
            memberNum.setText("卡号:" + currentMemberInfo.getCard());
            double balance = currentMemberInfo.balance + currentMemberInfo.gift;
            memberBalance.setText("余额: " + OrderUtil.dishPriceFormatter.format(balance));
            memberTotal.setText(getString(R.string.vip_point_label) + currentMemberInfo.point);
            if (cashierResult.pointInfo != null && cashierResult.pointInfo.point > 0) {
                memberScoreNotice.setVisibility(View.VISIBLE);
                memberScoreNotice.setText("可用" + cashierResult.pointInfo.point + "积分,抵现 ¥" + cashierResult.pointInfo.value + " 元");
            } else
                memberScoreNotice.setVisibility(View.GONE);
//            if (balance < cashierResult.bill.unpaid) {
//                memberBalanceCharge.setVisibility(View.VISIBLE);
//            } else {
            memberBalanceCharge.setVisibility(View.GONE);
//            }
        }

    }


    public void hideMemberInfo() {
        member_btn.setVisibility(View.VISIBLE);
        member_info_layout.setVisibility(View.GONE);
    }

    public boolean isDialogShow() {
        if (null != mOrderOptionDialog && mOrderOptionDialog.isShowing()) {
            return true;
        }
        return false;
    }


}
