package com.rainbow.smartpos.check;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cc.serialport.SerialCommand;
import com.rainbow.common.view.HorizontalListView;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.SmartPosApplication;
import com.rainbow.smartpos.SmartPosBundle;
import com.rainbow.smartpos.check.CheckView.CheckView;
import com.rainbow.smartpos.check.presenter.CheckPresenterImpl;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.place.ReturnDishReasonDialog;
import com.rainbow.smartpos.tablestatus.OpenTableDialog;
import com.rainbow.smartpos.util.AuthDialog;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.Listener.OnChoosePromotionListener;
import com.rainbow.smartpos.util.Listener.OnChooseReasonListener;
import com.rainbow.smartpos.util.Listener.OnChooseVoucherListener;
import com.rainbow.smartpos.util.SwitchTableDialog;
import com.rainbow.smartpos.util.SwitchTableDialog.ISwitchTableListener;
import com.sanyipos.android.sdk.androidUtil.CacheUtils;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.CashierRequest;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.addDetail.model.AddDetailOrder;
import com.sanyipos.sdk.model.scala.check.CashierAction;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;
import com.sanyipos.smartpos.model.OrderParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;
import sunmi.ds.DSKernel;
import sunmi.ds.SF;
import sunmi.ds.callback.ISendCallback;

import static com.cc.serialport.SerialCommand.CHARS_TOTAL_PRICE;

public class CheckFragment extends Fragment implements OnClickListener, CheckView {
    public static String TAG = "CheckFragment";


//    private CashDrawer cashDrawer = null;

    public static MainScreenActivity activity;

    public static SmartPosApplication application;
    public static final int TEN_PAY_CODE = 2;
    ListView orderList;
    CheckDetailListAdapter orderAdapter;
    PromotionAdapter mPromotionAdapter;
    VoucherAdapter mVoucherAdapter;
    PaymentModeAdapter mPaymentModeAdapter;


    TextView textViewTableName;
    //    TextView textViewPersonCount;

    AutofitTextView textViewCheckPayable;
    AutofitTextView textViewCheckUnPaid;
    AutofitTextView textViewCheckPaid;
    AutofitTextView textViewTablePromotion;
    AutofitTextView textViewBillAmount;
    //    TextView textViewTableWaiter;
    AutofitTextView textViewTableServiceCharge;
    AutofitTextView textViewTableMinPay;
    AutofitTextView textViewTax;
    TextView memberName;
    TextView memberBalance;
    TextView memberScoreNotice;
    RadioButton memberScoreRb;
    TextView memberPhone;
    TextView memberNum;
    TextView memberTotal;
    LinearLayout memberScoreLl;
    Button memberBalancePay;
    Button memberBalanceCharge;

    TextView textViewRounding;
    CheckBox checkBoxTableMinPayCancel;
    CheckBox checkBoxTableServiceChargeCancel;
    TextView member_btn;
    Button check_btn;
    Button preprint_btn;
    ImageView imageViewSawtooth;

    HorizontalListView sales_promotion_gridview;
    HorizontalListView voucher_gridview;
    GridView payment_method_gridview;

    LinearLayout sales_promotion_layout;
    LinearLayout voucher_layout;
    LinearLayout switch_table;
    LinearLayout member_info_layout;
    public List<Long> seats;
    public SeatEntity currentTable = null;
    View mainView;


    private boolean isFirstEnter;
    public CashierParamResult cashierParam;
    public CashierResult cashierResult;
    public NormalDialog scanDialog;
    public TenPayPopWindow tenPayPopWindow;
    Bundle bundle = null;
    private boolean isForMergeTable = false;
    public ChoosePromotionDialog sPromotion;
    public ChooseVoucherDialog voucherService;
    public CheckPresenterImpl checkPresenterImpl;

    private String payImgFilePath = "";
    private String payImgFileName = "";

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public CheckFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            unLockTable();
            dismissDialog();
            if (activity.presentation != null)
                activity.presentation.showOrderTotal(false);
            if (orderAdapter != null) {
                orderAdapter.orderDetailList.clear();
                orderAdapter.notifyDataSetChanged();
            }
            if (mPromotionAdapter != null) {
                mPromotionAdapter.promotions.clear();
                mPromotionAdapter.notifyDataSetChanged();
            }
            if (mVoucherAdapter != null) {
                mVoucherAdapter.voucherPayments.clear();
                mVoucherAdapter.notifyDataSetChanged();
            }
        } else {
            initData();
            if (activity.presentation != null) {
                activity.presentation.showOrderTotal(true);
                activity.presentation.initAmountView(MainScreenActivity.CHECK_FRAGMENT);
            }
        }
    }

    private void dismissDialog() {
        if (scanDialog != null) {
            if (scanDialog.isShowing()) {
                scanDialog.dismiss();
            }
        }
        if (activity.presentation != null) {
            activity.presentation.hideDialog();
        }
    }

    @Override
    public void closeDrawerLayout() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_new, container, false);
        rootView.requestFocus();
        mainView = rootView;
//        cashDrawer = new CashDrawer();
        activity = (MainScreenActivity) getActivity();
        application = (SmartPosApplication) getActivity().getApplication();

        orderList = (ListView) mainView.findViewById(R.id.listViewOrderList);
        orderList.setSelected(false);
        orderAdapter = new CheckDetailListAdapter(activity, CheckDetailListAdapter.Type.CHECK);
        orderList.setAdapter(orderAdapter);
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

        textViewTableName = (TextView) rootView.findViewById(R.id.textViewTableName);

//        textViewPersonCount = (TextView) rootView.findViewById(R.id.textViewOrderNumOfPeopleServed);
//        if (null != currentTable) {
//            textViewPersonCount.setText(String.valueOf(currentTable.personCount));
//        }
//        textViewTableWaiter = (TextView) rootView.findViewById(R.id.textViewWaitter);
        textViewTableServiceCharge = (AutofitTextView) rootView.findViewById(R.id.textViewTableServiceCharge);

        textViewTablePromotion = (AutofitTextView) rootView.findViewById(R.id.bill_promotion);
        textViewTableMinPay = (AutofitTextView) rootView.findViewById(R.id.textViewTableMinPay);
        textViewCheckPayable = (AutofitTextView) rootView.findViewById(R.id.bill_payable);
        textViewBillAmount = (AutofitTextView) mainView.findViewById(R.id.bill_amount);

        checkBoxTableMinPayCancel = (CheckBox) mainView.findViewById(R.id.checkBoxTableMinPay);
        checkBoxTableServiceChargeCancel = (CheckBox) mainView.findViewById(R.id.checkBoxTableServiceCharge);

        textViewCheckUnPaid = (AutofitTextView) mainView.findViewById(R.id.bill_unpaid);
        textViewCheckPaid = (AutofitTextView) mainView.findViewById(R.id.bill_paid);

        textViewRounding = (AutofitTextView) mainView.findViewById(R.id.textViewRounding);
        textViewTax = (AutofitTextView) mainView.findViewById(R.id.textViewTax);
        memberName = (TextView) mainView.findViewById(R.id.textView_member_name);
        memberBalance = (TextView) mainView.findViewById(R.id.textView_member_balance);
        memberScoreRb = (RadioButton) mainView.findViewById(R.id.rb_member_score);
        memberScoreLl = (LinearLayout) mainView.findViewById(R.id.ll_member_score);
        memberScoreNotice = (TextView) mainView.findViewById(R.id.textView_member_notice2);
        memberTotal = (TextView) mainView.findViewById(R.id.textView_member_total);
        memberNum = (TextView) mainView.findViewById(R.id.textView_member_num);
//        mainView.findViewById(R.id.imageView_cancel_member).setOnClickListener(this);
        mainView.findViewById(R.id.button_member_logout).setOnClickListener(this);

        member_btn = (TextView) mainView.findViewById(R.id.member_btn);
        check_btn = (Button) mainView.findViewById(R.id.check_btn);
        preprint_btn = (Button) mainView.findViewById(R.id.preprint_btn);

        sales_promotion_gridview = (HorizontalListView) mainView.findViewById(R.id.sales_promotion_gridview);
        voucher_gridview = (HorizontalListView) mainView.findViewById(R.id.voucher_gridview);
        payment_method_gridview = (GridView) mainView.findViewById(R.id.payment_method_gridview);


        sales_promotion_layout = (LinearLayout) mainView.findViewById(R.id.sales_promotion_layout);
        voucher_layout = (LinearLayout) mainView.findViewById(R.id.voucher_layout);
        member_info_layout = (LinearLayout) mainView.findViewById(R.id.include_member);
        memberBalancePay = (Button) mainView.findViewById(R.id.button_member_pay_balance);
        memberBalanceCharge = (Button) mainView.findViewById(R.id.button_member_charge);
        memberPhone = (TextView) mainView.findViewById(R.id.textView_member_phone);
        switch_table = (LinearLayout) mainView.findViewById(R.id.switch_table);

        //pay_by_member_checkbox = (CheckBox) mainView.findViewById(R.id.pay_by_member_checkbox);
        imageViewSawtooth = (ImageView) mainView.findViewById(R.id.imageView_order_sawtooth);
        imageViewSawtooth.scrollTo(0, -3);
        setListener();
        initData();

        if (activity.presentation != null) {
            activity.presentation.showOrderTotal(true);
            activity.presentation.initAmountView(MainScreenActivity.CHECK_FRAGMENT);
        }
        return rootView;
    }


    @Override
    public void clearData() {
        checkPresenterImpl = null;
        hideMemberInfo();
        bundle = null;
        seats = null;
        currentTable = null;
        orderAdapter.orderDetailList.clear();
        orderAdapter.notifyDataSetChanged();
        mPaymentModeAdapter.payments.clear();
        mPaymentModeAdapter.notifyDataSetChanged();
        textViewTableName.setText("");
        textViewBillAmount.setText("");
        textViewTablePromotion.setText("");
        textViewCheckPaid.setText("");
        textViewCheckPayable.setText("");
        textViewCheckUnPaid.setText("");
    }

    private void initData() {
        isFirstEnter = true;
        OrderParams table = (OrderParams) bundle.getSerializable(SmartPosBundle.TABLE_ID);
        seats = table.getTableIds();
        currentTable = SanyiSDK.rest.operationData.getSeat(seats.get(0));
        isForMergeTable = bundle.getBoolean(SmartPosBundle.IS_MERGE);
        checkPresenterImpl = new CheckPresenterImpl(activity, seats, bundle, this);
        checkPresenterImpl.beginCashierRequest();
    }

    private void setListener() {
        // TODO Auto-generated method stub
        member_btn.setOnClickListener(this);
        check_btn.setOnClickListener(this);
        preprint_btn.setOnClickListener(this);
        switch_table.setOnClickListener(this);
        memberBalancePay.setOnClickListener(this);
        memberBalanceCharge.setOnClickListener(this);
//        memberScoreLl.setOnClickListener(this);
        checkBoxTableMinPayCancel.setOnClickListener(this);
        checkBoxTableServiceChargeCancel.setOnClickListener(this);
        sales_promotion_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                long itemId = mPromotionAdapter.getItemId(position);
                List<CashierParamResult.Promotion> rightPromotion = new ArrayList<CashierParamResult.Promotion>();
                if (itemId == -1) {
//                    if (checkPresenterImpl.currentMemberInfo == null) {
//                        //未验证会员,从所有活动取所有人参与的活动
//                        for (CashierParamResult.Promotion promotion : cashierParam.promotions) {
//                            if (promotion.forAll) {
//                                rightPromotion.add(promotion);
//                            }
//                        }
//                    } else {
//                        for (CashierParamResult.Promotion promotion : cashierParam.promotions) {
//                            if (promotion.forAll)
//                                rightPromotion.add(promotion);
//                            else if (promotion.memberTypes != null && promotion.memberTypes.size() > 0) {
//                                for (CashierParamResult.Promotion.MemberType type : promotion.memberTypes)
//                                    if (type.id == checkPresenterImpl.currentMemberInfo.memberType)
//                                        rightPromotion.add(promotion);
//                            }
//                        }
//                    }
//                    if (rightPromotion == null || rightPromotion.size() <= 0) {
//                        Toast.makeText(activity, "没有可用活动", Toast.LENGTH_SHORT).show();
//                    } else {
                    sPromotion = new ChoosePromotionDialog(getActivity(), checkPresenterImpl, new OnChoosePromotionListener() {

                        @Override
                        public void onSure() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onCancel() {
                            // TODO Auto-generated method stub

                        }

                    });
                    sPromotion.show();
                    sPromotion.setMemberInfo(checkPresenterImpl.currentMemberInfo);
                    sPromotion.refresh(cashierParam, cashierResult);
//                    }
                }
            }
        });
        voucher_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {


                    Toast.makeText(activity, "没有权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                CashierPayment cashierPayment = mVoucherAdapter.getItem(position);
                if (-1 == cashierPayment.id) {
                    if (cashierParam.voucherTypes == null || cashierParam.voucherTypes.size() <= 0) {
                        Toast.makeText(activity, "没有可用代金券", Toast.LENGTH_SHORT).show();
                    } else {
                        voucherService = new ChooseVoucherDialog(getActivity(), checkPresenterImpl, cashierParam, cashierResult, new OnChooseVoucherListener() {

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
            }
        });
        payment_method_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View parentView, final int position, long id) {
                // TODO Auto-generated method stub
                if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
                    Toast.makeText(activity, "没有权限", Toast.LENGTH_SHORT).show();
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

    public void addCashierPayment(final View view, final CashierPayment cashierPaymentMode) {
        double value = Double.valueOf(cashierResult.bill.unpaid);
        if (value < 0 && cashierPaymentMode.paymentType != ConstantsUtil.PAYMENT_CASH) {
            Toast.makeText(activity, "非现金收银方式不允许出现负数", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cashierPaymentMode.paymentType == ConstantsUtil.PAYMENT_STORE_VALUE) {
            if (checkPresenterImpl.currentMemberInfo == null) {

                Toast.makeText(activity, "请先验证会员", Toast.LENGTH_SHORT).show();
                return;
            }
            payByBalance(view);
            return;
        }
        if (cashierPaymentMode.paymentType == ConstantsUtil.PAYMENT_POINT) {
            if (checkPresenterImpl.currentMemberInfo == null) {

                Toast.makeText(activity, "请先验证会员", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cashierResult.pointInfo != null && cashierResult.pointInfo.point > 0)
                payByPoint(view);
            else
                Toast.makeText(activity, "此单可用积分为0", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cashierPaymentMode.paymentType != ConstantsUtil.PAYMENT_WECHAT && cashierPaymentMode.paymentType != ConstantsUtil.PAYMENT_POINT) {
            final NumPadPopWindow numPadPopWindow = new NumPadPopWindow(view, getActivity(), cashierResult, cashierPaymentMode, new NumPadPopWindow.OnSureListener() {

                @Override
                public void onSureClick(final Double value, final Double change) {
                    // TODO Auto-generated method stub
//                                if (cashierPaymentMode.paymentType == ConstantsUtil.PAYMENT_DERATE) {
//                                    choosePaymentReason(value, change);
//                                }
//                    if (activity.serialPort != null) {
//                        try {
//                            activity.serialPort.getOutputStream().write(SerialCommand.initCommand());
//                            activity.serialPort.getOutputStream().write(SerialCommand.showNums(OrderUtil.decimalFormatter.format(value)));
//                            activity.serialPort.getOutputStream().write(SerialCommand.setCharsCommand(SerialCommand.CHARS_COLLECTION_PRICE));
//                            activity.serialPort.getOutputStream().flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    if (cashierPaymentMode.paymentType == ConstantsUtil.PAYMENT_WAIVE) {
                        //检测是否有免单权限
                        if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PAYMENT_WAIVE)) {

                            new AuthDialog().show(activity, ConstantsUtil.PAYMENT_WAIVE, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
                                @Override
                                public void onAuthSuccess(StaffRest staff) {
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

                                @Override
                                public void onCancel() {

                                }
                            });
                        } else {
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
                    } else {
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
                }
            });
            numPadPopWindow.show();


        } else if (cashierPaymentMode.paymentType == ConstantsUtil.PAYMENT_POINT) {
            if (cashierResult.pointInfo != null && cashierResult.pointInfo.value > 0) {

                CashierPayment payment = new CashierPayment();

                payment.paymentName = "积分抵现";
                payment.paymentType = ConstantsUtil.PAYMENT_POINT;
//                        payment.id = mPaymentModeAdapter.data.get(position).id;
                checkPresenterImpl.addPay(cashierResult.pointInfo.value, 0.0, payment);
            }
        } else {

            final NormalDialog dialog = new NormalDialog(activity);
            View weChatPayment = LayoutInflater.from(activity).inflate(R.layout.layout_wechat_payment, null);
            TextView scanText = (TextView) weChatPayment.findViewById(R.id.textView_wechat_payment_scan);
            scanText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    checkPresenterImpl.handleRequest(CashierAction.C_GETPAYURL);
                    unLockTable();
                }
            });
            TextView slotText = (TextView) weChatPayment.findViewById(R.id.textView_wechat_payment_slot_card);
            slotText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    tenPayPopWindow = new TenPayPopWindow(view, getResources().getString(R.string.please_input_2d_barcode_from_weichat), getActivity(), new TenPayPopWindow.OnSureListener() {

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
            TextView vificationText = (TextView) weChatPayment.findViewById(R.id.textView_wechat_payment_verification);
            vificationText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    tenPayPopWindow = new TenPayPopWindow(view, "请输入验证码", getActivity(), new TenPayPopWindow.OnSureListener() {

                        @Override
                        public void onSureClick(String value) {
                            // TODO Auto-generated method stub
                            tenPayPopWindow.dismiss();
                            checkPresenterImpl.vificationWeChatPayment(cashierResult.bill.unpaid, value);
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

    /**
     * 减免收银方式选择原因
     *
     * @param value
     * @param change
     */
    private void choosePaymentReason(final Double value, final Double change) {
        // TODO Auto-generated method stub
        DerateReasonDialog derateReasonDialog = new DerateReasonDialog();
        derateReasonDialog.show(getActivity(), new OnChooseReasonListener() {

            @Override
            public void onSure(long selectId, String editReason) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });
    }

    public void initPermissionButton() {
        // TODO Auto-generated method stub
        if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_CASHIER) && (cashierResult != null && cashierResult.bill.isComplete)) {
            check_btn.setEnabled(true);
        } else {
            check_btn.setEnabled(false);
        }
        if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_PRE_PRINTER)) {
            preprint_btn.setEnabled(true);
        } else {
            preprint_btn.setEnabled(false);
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
//            case R.id.imageView_cancel_member:
            case R.id.button_member_logout:
                checkPresenterImpl.cancelMember();
                break;
            case R.id.member_btn:
                checkPresenterImpl.verifyMember();
                break;
            case R.id.switch_table:
                switchTable();
                break;
            case R.id.preprint_btn:
                checkPresenterImpl.handleRequest(CashierAction.C_PREPRINT);
                break;
            case R.id.check_btn:
                if (cashierResult.bill.isComplete) {
                    checkPresenterImpl.handleRequest(CashierAction.C_END);
                } else {
                    activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                }
                break;
            case R.id.button_member_pay_balance:
                payByBalance(v);
                break;
            case R.id.button_member_charge:
                bundle.putSerializable("member_info", (Serializable) checkPresenterImpl.currentMemberInfo);
                activity.displayView(MainScreenActivity.MEMBER_FRAGMENT, bundle);
                break;

            case R.id.checkBoxTableMinPay:
                if (checkBoxTableMinPayCancel.isChecked()) {
                    checkPresenterImpl.handleRequest(CashierAction.C_USEMINCHARGE);
                } else {
                    checkPresenterImpl.handleRequest(CashierAction.C_UNDOMINCHARGE);
                }
                break;
            case R.id.checkBoxTableServiceCharge:
                if (checkBoxTableServiceChargeCancel.isChecked()) {
                    checkPresenterImpl.handleRequest(CashierAction.C_USESURCHARGE);
                } else {
                    checkPresenterImpl.handleRequest(CashierAction.C_UNDOSURCHARGE);
                }
                break;
//            case R.id.ll_member_score:
//                if (!memberScoreRb.isChecked()) {
//                    if (cashierResult.pointInfo != null && cashierResult.pointInfo.value > 0) {
//
//                        CashierPayment payment = new CashierPayment();
//
//                        payment.paymentName = "积分抵现";
//                        payment.paymentType = ConstantsUtil.PAYMENT_POINT;
////                        payment.id = mPaymentModeAdapter.data.get(position).id;
//                        checkPresenterImpl.addPay(cashierResult.pointInfo.value, 0.0, payment);
//                    }
//                } else {
//                    CashierPayment cashierPayment = null;
//                    for (CashierPayment cashierPayment1 : cashierResult.payments) {
//                        if (cashierPayment1.paymentType == ConstantsUtil.PAYMENT_POINT) {
//                            cashierPayment = cashierPayment1;
//                        }
//                    }
//                    if (cashierPayment != null) {
//                        final NormalDialog dialog = new NormalDialog(activity);
//                        dialog.content("确定取消积分抵现?");
//                        dialog.widthScale((float) 0.5);
//                        final CashierPayment finalCashierPayment = cashierPayment;
//                        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
//                            @Override
//                            public void onClickConfirm() {
//                                dialog.dismiss();
//                                SanyiScalaRequests.CashierCancelRequest(seats, SanyiSDK.currentUser.id, ConstantsUtil.PAYMENT_POINT, finalCashierPayment.id, new CashierRequest.ICashierRequestListener() {
//
//
//                                    @Override
//                                    public void onFail(String error) {
//                                        // TODO Auto-generated method stub
//                                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
//                                        initPermissionButton();
//                                    }
//
//                                    @Override
//                                    public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
//                                        // TODO Auto-generated method stub
//                                        updateUI(resp, ods);
//                                    }
//                                });
//                            }
//                        });
//                        dialog.show();
//                    }
//                }
//                break;
            default:
                break;
        }
    }

    private void payByBalance(final View v) {
        // TODO Auto-generated method stub
        if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
            Toast.makeText(activity, "没有权限", Toast.LENGTH_SHORT).show();

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

    private void switchTable() {
        // TODO Auto-generated method stub
        if (null != currentTable) {
            new SwitchTableDialog(activity, SwitchTableDialog.CHECK, currentTable, new ISwitchTableListener() {
                @Override
                public void batchOperation(long[] tableIds) {

                }

                @Override
                public void openTable(SeatEntity table, int personCount) {
                    // TODO Auto-generated method stub
                    unLockTable();
                    seats.clear();
                    seats.add(table.seat);
                    currentTable = SanyiSDK.rest.getTableById(table.seat);
                    checkPresenterImpl.beginCashierRequest();
                }

                @Override
                public void cancel() {
                    // TODO Auto-generated method stub
                }
            }).show();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    public void unLockTable() {
        activity.unLockTable(seats, false);
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
                sPromotion.setMemberInfo(checkPresenterImpl.currentMemberInfo);
                sPromotion.refresh(cashierParam, cashierResult);
            }
        }
        if (null != voucherService) {
            voucherService.refresh(cashierResult);
        }
        if (isFirstEnter) {
            if (cash.orders.get(0).remark != null) {
                final NormalDialog dialog = new NormalDialog(getContext());
                dialog.content(cash.orders.get(0).remark);
                dialog.title("备注");
                dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        }
        isFirstEnter = false;
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
        textViewTableName.setText(tableName);
        if (isForMergeTable) {
            TextPaint tp = textViewTableName.getPaint();
            tp.setFakeBoldText(true);
            String tag = SanyiSDK.rest.operationData.getCombineTag(seats.get(0));
            if (null != tag) {
                String str = String.format("%s(<font color='#FF0000'>%s</font>)", getCombineTableName(tag), tag);
                textViewTableName.setText(Html.fromHtml(str));
            }
        }
//        textViewTableWaiter.setText(cash.orders.get(0).staffName);

        textViewBillAmount.setText(OrderUtil.dishPriceFormatter.format(cash.bill.amount));
        textViewTableServiceCharge.setText(OrderUtil.dishPriceFormatter.format(cash.bill.surchagre));
        if (cash.bill.surchagre > 0) {
            checkBoxTableServiceChargeCancel.setChecked(true);
        }
        textViewTableMinPay.setText(OrderUtil.dishPriceFormatter.format(cash.bill.mincharge));
        if (cash.bill.mincharge > 0) {
            checkBoxTableMinPayCancel.setChecked(true);
        }
        textViewTablePromotion.setText(OrderUtil.dishPriceFormatter.format(cash.bill.discount + cash.bill.promotion));
        textViewCheckPayable.setText(OrderUtil.dishPriceFormatter.format(cash.bill.realValue));
        textViewCheckPaid.setText(OrderUtil.dishPriceFormatter.format(cash.bill.paid));
        textViewCheckUnPaid.setText(OrderUtil.dishPriceFormatter.format(cash.bill.unpaid));
        textViewRounding.setText(OrderUtil.dishPriceFormatter.format(cash.bill.rounding));
        textViewTax.setText(OrderUtil.dishPriceFormatter.format(cash.bill.tax));
        if (ods != null) {
            orderAdapter.setOrderDetails(ods);
            orderAdapter.notifyDataSetChanged();
        }
        String amount = OrderUtil.dishPriceFormatter.format(cash.bill.amount);
        if (activity.presentation != null) {
            activity.presentation.setListViewAdapter(orderAdapter);
            activity.presentation.refresh(cash);
        }
        if (cashierResult.member != null) {
            checkPresenterImpl.currentMemberInfo = cashierResult.member;
            showMemberInfo();
        } else {
            checkPresenterImpl.currentMemberInfo = null;
            hideMemberInfo();
        }
    }

    @Override
    public void showQRCode(final String url, double unpaid) {
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
                dismissDialog();
            }
        });
        if (activity.presentation != null) {
            activity.presentation.showQRCode(activity.presentation.getContext(), url);
        }
        //如果是商米设备,在副屏显示收款二维码和收款金额
        final JSONObject json = new JSONObject();
        try {
            json.put("title", "请支付");
            json.put("content", unpaid + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Build.BRAND.equals("SUNMI")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String state = Environment.getExternalStorageState();
                    File cacheDir = CacheUtils.getCacheDirectory(activity, state.equals(Environment.MEDIA_MOUNTED), "payimgs");
                    payImgFilePath = cacheDir.getAbsolutePath();
                    payImgFileName = "payimg";
                    Log.d("luo", payImgFilePath + "/" + payImgFileName);
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    } else {
                        // 清除掉该目录下的所有文件
                        File[] files = cacheDir.listFiles();
                        for (File file : files) {
                            if (file.canWrite()) {
                                file.delete();
                            }
                        }
                    }
//            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    Bitmap map = null;
                    try {
                        URL biturl = new URL(url);
                        URLConnection conn = biturl.openConnection();
                        conn.connect();
                        InputStream in;
                        in = conn.getInputStream();
                        map = BitmapFactory.decodeStream(in);
                        // TODO Auto-generated catch block
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FileOutputStream out = null;
                    if (map != null) {
                        try {
                            out = new FileOutputStream(new File(payImgFilePath + "/" + payImgFileName));
                            map.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    activity.mDSKernel.sendFile(DSKernel.getDSDPackageName(), json.toString(), payImgFilePath + "/" + payImgFileName, new ISendCallback() {
                        @Override
                        public void onSendSuccess(long l) {
                            //显示图片
                            try {
                                JSONObject json = new JSONObject();
                                json.put("dataModel", "QRCODE");
                                json.put("data", "default");
                                activity.mDSKernel.sendCMD(SF.DSD_PACKNAME, json.toString(), l, null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onSendFail(int i, String s) {
//                    showToast("发送二维码图片失败---->" + s);
                            dismissDialog();
                        }

                        @Override
                        public void onSendProcess(long l, long l1) {

                        }
                    });
                }
            }).start();

        }
    }

    public String getCombineTableName(String tag) {
        String name = "";
        List<SeatEntity> tables = SanyiSDK.rest.getCombineTablesByTag(tag);
        if (tables != null) {
            for (SeatEntity table : tables) {
                if (!table.tableName.isEmpty()) {
                    name = name + table.tableName + ",";
                }
            }
        }
        if (!name.isEmpty()) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }


    public void initParam(CashierParamResult param, CashierResult cashierResult) {
        // TODO Auto-generated method stub
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


    public void showMemberInfo() {
        if (checkPresenterImpl.currentMemberInfo != null) {
            member_btn.setVisibility(View.GONE);
            member_info_layout.setVisibility(View.VISIBLE);
            memberName.setText("会员名称: " + checkPresenterImpl.currentMemberInfo.name);
            memberPhone.setText("手机号:" + checkPresenterImpl.currentMemberInfo.getMobile());
            memberNum.setText("卡号:" + checkPresenterImpl.currentMemberInfo.getCard());
            double balance = checkPresenterImpl.currentMemberInfo.balance + checkPresenterImpl.currentMemberInfo.gift;
            memberBalance.setText("余额: " + OrderUtil.dishPriceFormatter.format(balance));
//            if (checkPresenterImpl.currentMemberInfo.getPoint() != null) {
//                memberScoreLl.setVisibility(View.VISIBLE);
            memberTotal.setText("积分: " + checkPresenterImpl.currentMemberInfo.point);
//            } else
//                memberScoreLl.setVisibility(View.GONE);
//            if (cashierResult.pointInfo != null && cashierResult.pointInfo.value > 0) {
//                memberScoreLl.setVisibility(View.VISIBLE);
            if (cashierResult.pointInfo != null && cashierResult.pointInfo.point > 0) {
                memberScoreNotice.setVisibility(View.VISIBLE);
                memberScoreNotice.setText("可用" + cashierResult.pointInfo.point + "积分,抵现 ¥" + cashierResult.pointInfo.value + " 元");
            } else
                memberScoreNotice.setVisibility(View.GONE);
//            } else
//                memberScoreLl.setVisibility(View.GONE);
//            memberScoreNotice.setText("本次最多可使用" + checkPresenterImpl.currentMemberInfo.get);
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

}
