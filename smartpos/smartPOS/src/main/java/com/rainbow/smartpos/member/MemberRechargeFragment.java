package com.rainbow.smartpos.member;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.HorizontalListView;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.TenPayPopWindow;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.util.ChooseMemberDialog;
import com.rainbow.smartpos.util.Listener.OnChooseMemberListener;
import com.rainbow.smartpos.util.Listener.OnChooseStaffListener;
import com.rainbow.smartpos.util.Listener.OnVerifyMemberListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.MemberChargeRequest.IMemberChargeListener;
import com.sanyipos.sdk.model.MemberPromotion;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.model.scala.member.MemberChargeParam;
import com.sanyipos.sdk.model.scala.member.MemberChargeResult;
import com.sanyipos.sdk.model.scala.member.MemberChargeResult.ChargeResult;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MemberRechargeFragment extends Fragment implements OnClickListener {
    private TextView member_sn;
    private TextView member_type;
    private TextView member_name;
    private TextView recharge_mobile;
    private TextView member_card_number;
    private TextView member_account_balance;

    private TextView switch_activity;

    private HorizontalListView recharge_promotion_gridview;
    private GridView payment_method_gridview;

    private LinearLayout member_activity_layout;
    private LinearLayout member_amount_layout;

    private TextView charge_value;
    private TextView gift_value;
    private TextView edit_sale_staff;
    private StaffRest saleStaff;

    private Button button_sure;
    private Button button_cancel;

    private boolean isInit = false;
    private PaymentModeAdapter mPaymentModeAdapter;

    private StaffRest currentStaff = null;

    private MemberInfo currentMemberInfo = null;

    public Bundle bundle;

    public MemberFragment activity;

    private boolean isPromotion = false;

    private int promotionId = -1;

    private int vip_charge_id = -1;

    private List<MemberPromotion> promotions;

    private MemberPromotionAdapter memberPromotionAdapter;

    public TenPayPopWindow tenPayPopWindow;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    private interface OnChargeSureInterface {
        public void sure();

        public void cancel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_member_recharge, container, false);
        activity = (MemberFragment) getActivity();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden) {

        }
    }

    public void initView(View view) {
        member_sn = (TextView) view.findViewById(R.id.edittext_number1);
        member_type = (TextView) view.findViewById(R.id.edittext_number2);
        member_name = (TextView) view.findViewById(R.id.member_name);
        recharge_mobile = (TextView) view.findViewById(R.id.recharge_mobile);
        member_card_number = (TextView) view.findViewById(R.id.member_card_number);
        member_account_balance = (TextView) view.findViewById(R.id.account_balance);

        switch_activity = (TextView) view.findViewById(R.id.switch_activity);

        recharge_promotion_gridview = (HorizontalListView) view.findViewById(R.id.recharge_promotion_gridview);
        recharge_promotion_gridview.setOnItemClickListener(onItemClickListener);
        payment_method_gridview = (GridView) view.findViewById(R.id.payment_method_gridview);

        member_activity_layout = (LinearLayout) view.findViewById(R.id.member_activity_layout);
        member_amount_layout = (LinearLayout) view.findViewById(R.id.member_amount_layout);
        member_activity_layout.setVisibility(View.GONE);
        member_amount_layout.setVisibility(View.VISIBLE);
        charge_value = (TextView) view.findViewById(R.id.charge_value);
        gift_value = (TextView) view.findViewById(R.id.gift_value);
        edit_sale_staff = (TextView) view.findViewById(R.id.edit_sale_staff);

        button_sure = (Button) view.findViewById(R.id.button_sure);
        button_cancel = (Button) view.findViewById(R.id.button_member_recharge_cancel);
        mPaymentModeAdapter = new PaymentModeAdapter(getActivity(), isInit);
        payment_method_gridview.setAdapter(mPaymentModeAdapter);
        if (null != bundle) {
            MemberInfo memberInfo = (MemberInfo) bundle.getSerializable("member_info");
            if (null != memberInfo) {
                currentMemberInfo = memberInfo;
                if (SanyiSDK.rest.staffs.size() > 0) {
                    currentStaff = SanyiSDK.rest.staffs.get(0);
                }
                edit_sale_staff.setText(currentStaff != null ? currentStaff.name : "无");
                refreshData();
            } else {
                initData();
//                readMember();
            }
        } else {
            initData();
//            readMember();
        }

        if (MemberFragment.res != null) {
            currentMemberInfo = MemberFragment.res;
            refreshData();
        }
        setListener();
    }

    public void initData() {
        isInit = false;
        currentMemberInfo = null;
        member_sn.setText(getString(R.string.not_has));
        member_type.setText(getString(R.string.not_has));
        member_name.setText(getString(R.string.not_has));
        recharge_mobile.setText(getString(R.string.not_has));
        member_card_number.setText(getString(R.string.not_has));
        member_account_balance.setText(getString(R.string.not_has));

        member_sn.setTextColor(getResources().getColor(R.color.return_dish_text_enabled_false));
        member_type.setTextColor(getResources().getColor(R.color.return_dish_text_enabled_false));
        member_name.setTextColor(getResources().getColor(R.color.return_dish_text_enabled_false));
        recharge_mobile.setTextColor(getResources().getColor(R.color.return_dish_text_enabled_false));
        member_card_number.setTextColor(getResources().getColor(R.color.return_dish_text_enabled_false));
        member_account_balance.setTextColor(getResources().getColor(R.color.return_dish_text_enabled_false));
        if (SanyiSDK.rest.staffs.size() > 0) {
            currentStaff = SanyiSDK.rest.staffs.get(0);
        }
        edit_sale_staff.setText(currentStaff != null ? currentStaff.name : "无");
        switch_activity.setEnabled(false);
        charge_value.setEnabled(false);
        gift_value.setEnabled(false);
        edit_sale_staff.setEnabled(false);
        button_sure.setEnabled(false);
        mPaymentModeAdapter.refresh(isInit);
    }

    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            charge_value.setText(Double.toString(promotions.get(position).getCharge()));
            gift_value.setText(Double.toString(promotions.get(position).getGift()));
            promotionId = promotions.get(position).getPromotion();
            vip_charge_id = promotions.get(position).vipCharge;
            memberPromotionAdapter.selected = position;
            memberPromotionAdapter.notifyDataSetChanged();
        }
    };

    public void refreshData() {
        isInit = true;
        if (null != currentMemberInfo) {
            member_sn.setText(currentMemberInfo.sn);
            member_type.setText(currentMemberInfo.memberTypeName);
            member_name.setText(currentMemberInfo.name);
            recharge_mobile.setText(currentMemberInfo.mobile);
            member_card_number.setText(currentMemberInfo.card);
            member_account_balance.setText(OrderUtil.dishPriceFormatter.format(currentMemberInfo.balance + currentMemberInfo.gift) + "(" + OrderUtil.dishPriceFormatter.format(currentMemberInfo.pendingBalance) + ")");

        }
        member_sn.setTextColor(getResources().getColor(R.color.Black));
        member_type.setTextColor(getResources().getColor(R.color.Black));
        member_name.setTextColor(getResources().getColor(R.color.Black));
        recharge_mobile.setTextColor(getResources().getColor(R.color.Black));
        member_card_number.setTextColor(getResources().getColor(R.color.Black));
        member_account_balance.setTextColor(getResources().getColor(R.color.Black));

        switch_activity.setEnabled(true);
        charge_value.setEnabled(true);
        gift_value.setEnabled(true);
        edit_sale_staff.setEnabled(true);
        button_sure.setEnabled(true);
        mPaymentModeAdapter.refresh(isInit);
    }

    private void weChatCharge(View view) {
        if (charge_value.getText().toString().isEmpty()) {
            Toast.makeText(activity, "请输入充值金额", Toast.LENGTH_LONG).show();
            return;
        }
        final double charge_value_money = Double.parseDouble(charge_value.getText().toString());
        final double gift_value_money = gift_value.getText().toString().isEmpty() ? 0 : Double.parseDouble(gift_value.getText().toString());
        tenPayPopWindow = new TenPayPopWindow(view, getResources().getString(R.string.please_input_2d_barcode_from_weichat), getActivity(), new TenPayPopWindow.OnSureListener() {

            @Override
            public void onSureClick(String value) {
                // TODO Auto-generated method stub
                tenPayPopWindow.dismiss();
                MemberChargeParam param = new MemberChargeParam();
                param.setMember_id(currentMemberInfo.id);
                param.setCharge_value(charge_value_money);
                param.setGift_value(gift_value_money);
                if (promotionId != -1) {
                    param.setPromotion_id(promotionId);
                } else {
                    param.setPromotion_id(null);
                }
                param.setStaff_id(SanyiSDK.currentUser.id);
                if (saleStaff != null) {
                    param.setSale_staff_id(saleStaff.id);
                } else {
                    param.setSale_staff_id(SanyiSDK.currentUser.id);
                }
                if (vip_charge_id != -1) {
                    param.setVip_charge_id(vip_charge_id);
                } else {
                    param.setVip_charge_id(null);
                }
                MemberChargeParam.ChargePayment payment = new MemberChargeParam.ChargePayment();
                payment.setAmount(charge_value_money);
                payment.setChange((double) 0);
                payment.setPayment_type_id(mPaymentModeAdapter.getSelect().paymentType);
                payment.setRefid(value);
                List<MemberChargeParam.ChargePayment> payments = new ArrayList<MemberChargeParam.ChargePayment>();
                payments.add(payment);
                param.setCharge_payments(payments);
                SanyiScalaRequests.memberChargeRequest(param, new IMemberChargeListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub

                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onSuccess(MemberChargeResult result) {
                        // TODO Auto-generated method stub
                        gift_value.setText("");
                        charge_value.setText("");

                        Toast.makeText(activity, "充值成功", Toast.LENGTH_LONG).show();

                        promotionId = -1;
                        vip_charge_id = -1;
//                            if(Restaurant.isFastMode) {
//                                activity.displayView(MainScreenActivity.ORDER_FRAGMENT,bundle);
//                                activity.orderFragment.isChargeMember = true;
//                                activity.orderFragment.checkPresenterImpl.cancelMember();
//                                return;
//                            }
//                            if (bundle != null) {
//                                activity.displayView(MainScreenActivity.CHECK_FRAGMENT, bundle);
//                                activity.checkFragment.checkPresenterImpl.cancelMember();
//                                return;
//                            }
                        showChargeResultDialog(result.result);

                    }
                });
            }
        });
        tenPayPopWindow.show();
    }

    private void setListener() {
        // TODO Auto-generated method stub
        switch_activity.setOnClickListener(this);
        charge_value.setOnClickListener(this);
        gift_value.setOnClickListener(this);
        edit_sale_staff.setOnClickListener(this);
        button_sure.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        payment_method_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                mPaymentModeAdapter.setSelect(position);
                if (mPaymentModeAdapter.getSelect().paymentType == ConstantsUtil.PAYMENT_WECHAT) {
                    weChatCharge(view);
                }
            }
        });

    }

    private void initPromotions() {
        promotions = new ArrayList<>();
        if (SanyiSDK.rest.vipCharges != null)
            for (MemberPromotion promotion : SanyiSDK.rest.vipCharges) {
                if (promotion.getMemberType() == currentMemberInfo.memberType) {
                    promotions.add(promotion);
                }
            }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_activity:
                switchActivity();
                break;
//            case R.id.switch_amount:
//                switchAmount();
//                break;
            case R.id.charge_value:
                if (promotionId != -1) return;
                inputChargeValue();
                break;
            case R.id.gift_value:
                if (promotionId != -1) return;
                inputGiftValue();
                break;
            case R.id.edit_sale_staff:
                chooseStaff();
                break;
            case R.id.button_sure:
                if (mPaymentModeAdapter.getSelect().paymentType == ConstantsUtil.PAYMENT_WECHAT) {
                    weChatCharge(payment_method_gridview.getChildAt(2));
                    return;
                }
                addPayment();
                break;
            case R.id.button_member_recharge_cancel:
                activity.displayView(MemberFragment.QUERY_RECHARGE_MEMBER);
                break;
            default:
                break;
        }

    }

    private void chooseStaff() {
        // TODO Auto-generated method stub
        ChooseRechargeStaffDialog chooseRechargeStaffDialog = new ChooseRechargeStaffDialog(getActivity(), ChooseRechargeStaffDialog.CHOOSE_STAFF, SanyiSDK.rest.staffs, new OnChooseStaffListener() {

            @Override
            public void onSure(StaffRest staff) {
                // TODO Auto-generated method stub
                edit_sale_staff.setText(staff.name);
                saleStaff = staff;
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });
        chooseRechargeStaffDialog.show();
    }

    private void inputGiftValue() {
        // TODO Auto-generated method stub
        RechargeNumberPopWindow numberPopWindow = new RechargeNumberPopWindow(gift_value, getActivity(), getActivity().getString(R.string.please_input_gift_value), gift_value.getText().toString(),
                new RechargeNumberPopWindow.OnSureListener() {

                    @Override
                    public void onSureClick(Double value) {
                        // TODO Auto-generated method stub
                        gift_value.setText(String.valueOf(value));
                    }
                });
        numberPopWindow.show();
    }

    private void inputChargeValue() {
        // TODO Auto-generated method stub
        RechargeNumberPopWindow numberPopWindow = new RechargeNumberPopWindow(charge_value, getActivity(), getActivity().getString(R.string.please_input_charge_value), charge_value.getText().toString(),
                new RechargeNumberPopWindow.OnSureListener() {

                    @Override
                    public void onSureClick(Double value) {
                        // TODO Auto-generated method stub
                        charge_value.setText(String.valueOf(value));
                    }
                });
        numberPopWindow.show();
    }


    private void readMember() {
        // TODO Auto-generated method stub
        ReadMemberDialog readMemberDialog = new ReadMemberDialog((MemberFragment) getActivity(), new OnVerifyMemberListener() {

            @Override
            public void onQuerySuccess(List<MemberInfo> memberInfos) {
                // TODO Auto-generated method stub


                Toast.makeText(activity, "查询成功", Toast.LENGTH_LONG).show();

                if (memberInfos.size() == 1) {
                    currentMemberInfo = memberInfos.get(0);
                    refreshData();
                    SanyiScalaRequests.setMemberPasswordRequest(Long.valueOf("88899"), "999999", new Request.ICallBack() {
                        @Override
                        public void onSuccess(String status) {

                            Toast.makeText(activity, status, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFail(String error) {

                        }
                    });
                } else {
                    new ChooseMemberDialog(getActivity(), memberInfos, new OnChooseMemberListener() {

                        @Override
                        public void sure(MemberInfo memberInfo) {
                            // TODO Auto-generated method stub
                            currentMemberInfo = memberInfo;
                            refreshData();
                        }

                        @Override
                        public void cancel() {
                            // TODO Auto-generated method stub
                        }
                    }).show();
                }
            }

            @Override
            public void onQueryCanceled() {
                // TODO Auto-generated method stub

            }
        });
        readMemberDialog.show();
    }

    private void switchAmount() {
        // TODO Auto-generated method stub
        member_activity_layout.setVisibility(View.GONE);
        member_amount_layout.setVisibility(View.VISIBLE);
    }

    private void switchActivity() {
        // TODO Auto-generated method stub
        initPromotions();
        if (promotions == null || promotions.size() == 0) {
            Toast.makeText(getActivity(), "没有可用充值活动", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isPromotion) {
            member_activity_layout.setVisibility(View.VISIBLE);
            recharge_promotion_gridview.setAdapter(memberPromotionAdapter = new MemberPromotionAdapter(getActivity(), promotions));
            isPromotion = true;
        } else {
            member_activity_layout.setVisibility(View.GONE);
            isPromotion = false;
        }
        charge_value.setText("");
        gift_value.setText("");
        promotionId = -1;
        vip_charge_id = -1;
        if (memberPromotionAdapter != null) {
            memberPromotionAdapter.selected = -1;
        }

    }

    private void addPayment() {
        try {
            final double charge_value_money = Double.parseDouble(charge_value.getText().toString());
            final double gift_value_money = gift_value.getText().toString().isEmpty() ? 0 : Double.parseDouble(gift_value.getText().toString());
            if ((0 == charge_value_money) && (0 == gift_value_money)) {

                Toast.makeText(activity, "充值金额和赠送金额不能同时为0元", Toast.LENGTH_LONG).show();

                button_sure.setEnabled(true);
                charge_value.setText("");
                gift_value.setText("");
                return;
            }
            showChargeSureDialog(charge_value_money, gift_value_money, new OnChargeSureInterface() {

                @Override
                public void sure() {
                    // TODO Auto-generated method stub
                    MemberChargeParam param = new MemberChargeParam();
                    param.setMember_id(currentMemberInfo.id);
                    param.setCharge_value(charge_value_money);
                    param.setGift_value(gift_value_money);
                    if (promotionId != -1) {
                        param.setPromotion_id(promotionId);
                    } else {
                        param.setPromotion_id(null);
                    }
                    param.setStaff_id(SanyiSDK.currentUser.id);
                    if (saleStaff != null) {
                        param.setSale_staff_id(saleStaff.id);
                    } else {
                        param.setSale_staff_id(SanyiSDK.currentUser.id);
                    }
                    if (vip_charge_id != -1) {
                        param.setVip_charge_id(vip_charge_id);
                    } else {
                        param.setVip_charge_id(null);
                    }
                    MemberChargeParam.ChargePayment payment = new MemberChargeParam.ChargePayment();
                    payment.setAmount(charge_value_money);
                    payment.setChange((double) 0);
                    payment.setPayment_type_id(mPaymentModeAdapter.getSelect().paymentType);
                    List<MemberChargeParam.ChargePayment> payments = new ArrayList<MemberChargeParam.ChargePayment>();
                    payments.add(payment);
                    param.setCharge_payments(payments);
                    SanyiScalaRequests.memberChargeRequest(param, new IMemberChargeListener() {


                        @Override
                        public void onFail(String error) {
                            // TODO Auto-generated method stub

                            Toast.makeText(activity, error, Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onSuccess(MemberChargeResult result) {
                            // TODO Auto-generated method stub
                            gift_value.setText("");
                            charge_value.setText("");

                            Toast.makeText(activity, "充值成功", Toast.LENGTH_LONG).show();

                            promotionId = -1;
                            vip_charge_id = -1;
//                            if(Restaurant.isFastMode) {
//                                activity.displayView(MainScreenActivity.ORDER_FRAGMENT,bundle);
//                                activity.orderFragment.isChargeMember = true;
//                                activity.orderFragment.checkPresenterImpl.cancelMember();
//                                return;
//                            }
//                            if (bundle != null) {
//                                activity.displayView(MainScreenActivity.CHECK_FRAGMENT, bundle);
//                                activity.checkFragment.checkPresenterImpl.cancelMember();
//                                return;
//                            }
                            showChargeResultDialog(result.result);

                        }
                    });
                }

                @Override
                public void cancel() {
                    // TODO Auto-generated method stub
//                    if(Restaurant.isFastMode) {
//                        activity.displayView(MainScreenActivity.ORDER_FRAGMENT,bundle);
//                        activity.orderFragment.isChargeMember = true;
//                        return;
//                    }
//                    if (bundle != null) {
//                        activity.displayView(MainScreenActivity.CHECK_FRAGMENT, bundle);
//                        return;
//                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(activity, "请输入正确的金额", Toast.LENGTH_LONG).show();

            charge_value.setText("");
            gift_value.setText("");
        } finally {
            button_sure.setEnabled(true);
        }

    }

    /**
     * 充值成功后显示会员账户详情
     *
     * @param result
     */
    public void showChargeResultDialog(final ChargeResult result) {
        final NormalDialog normalDialog = new NormalDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.query_member_dialog_item, null);
        TextView member_sn = (TextView) view.findViewById(R.id.member_sn);
        TextView member_name = (TextView) view.findViewById(R.id.member_name);
        TextView member_type = (TextView) view.findViewById(R.id.member_type);
        TextView member_moblie = (TextView) view.findViewById(R.id.member_moblie);
        view.findViewById(R.id.member_cardNo).setVisibility(View.GONE);
        view.findViewById(R.id.member_balance).setVisibility(View.GONE);
        view.findViewById(R.id.member_recharge_balance).setVisibility(View.GONE);
        view.findViewById(R.id.member_gift_balance).setVisibility(View.GONE);
        member_sn.setText("会员充值账户:    " + OrderUtil.decimalFormatter.format(result.balance));
        member_name.setText("会员赠送账户:    " + OrderUtil.decimalFormatter.format(result.gift_balance));
        member_type.setText("本次充值金额:    " + OrderUtil.decimalFormatter.format(result.charge_value));
        member_moblie.setText("本次赠送金额:    " + OrderUtil.decimalFormatter.format(result.gift_value));
        normalDialog.content(view);
        normalDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                initData();
                activity.displayView(MemberFragment.QUERY_RECHARGE_MEMBER);
                normalDialog.dismiss();
            }
        });
        normalDialog.widthScale((float) 0.5);
        normalDialog.show();
    }

    /**
     * 充值确认
     */
    public void showChargeSureDialog(double charge, double gift, final OnChargeSureInterface chargeSureInterface) {
        final NormalDialog normalDialog = new NormalDialog(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.member_charge_sure_dialog, null);
        normalDialog.content(view);
        TextView member_type = (TextView) view.findViewById(R.id.member_type);
        TextView member_moblie = (TextView) view.findViewById(R.id.member_moblie);
        EditText edit = (EditText) view.findViewById(R.id.edit);
        member_type.setText("本次充值金额:    " + OrderUtil.decimalFormatter.format(charge));
        member_moblie.setText("本次赠送金额:    " + OrderUtil.decimalFormatter.format(gift));
        edit.setInputType(InputType.TYPE_NULL);
        edit.requestFocus();
        edit.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
//                    sure.performClick();
                    return true;
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ESCAPE)) {
//                    cancel.performClick();
                    return true;
                }
                return false;
            }
        });
        normalDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                chargeSureInterface.sure();
                normalDialog.dismiss();
            }
        });
        normalDialog.widthScale((float) 0.5);
        normalDialog.show();
    }

    /**
     * 关闭软键盘
     */
    public void closeInputMethod(EditText edit) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    public void closeInputMethod() {
        if (getActivity().getCurrentFocus() != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
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

}
