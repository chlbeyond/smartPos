package com.rainbow.smartpos.check;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.install.Constants;
import com.readystatesoftware.viewbadger.BadgeView;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.PaymentMode;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.model.scala.check.CashierPaymentMode;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentModeAdapter extends BaseAdapter {
    public List<CashierPaymentMode> data = new ArrayList<>();
    public Map<Long, List<CashierPayment>> payments = new HashMap<>();
    private Context mContext;
    private LayoutInflater inflater;
    private List<PaymentMode> paymentModes;
    private MainScreenActivity activity;

    public PaymentModeAdapter(Context context, List<PaymentMode> paymentModes) {
        this.mContext = context;
        this.activity = (MainScreenActivity) context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paymentModes = paymentModes;
        initData();
    }

    private void initData() {
        data.clear();
        CashierPaymentMode cash = new CashierPaymentMode();
        cash.paymentType = ConstantsUtil.PAYMENT_CASH;
        cash.name = mContext.getString(R.string.payment_cash);
        data.add(cash);

        CashierPaymentMode bank_card = new CashierPaymentMode();
        bank_card.paymentType = ConstantsUtil.PAYMENT_BANK_CARD;
        bank_card.name = mContext.getString(R.string.payment_bankcard);
        data.add(bank_card);

        //注意
        //前台对于支付宝类型只做记账处理
        //如果门店开通了支付宝，需使用微信类型，界面显示为支付宝，后台会自动判断支付类型
        if(SanyiSDK.rest.config.isAlipay && SanyiSDK.rest.config.isWeixin) {
            CashierPaymentMode weipay = new CashierPaymentMode();
            weipay.paymentType = ConstantsUtil.PAYMENT_WECHAT;
            weipay.name = mContext.getString(R.string.payment_unipay);
            data.add(weipay);
        } else if(SanyiSDK.rest.config.isWeixin) {
            CashierPaymentMode weipay = new CashierPaymentMode();
            weipay.paymentType = ConstantsUtil.PAYMENT_WECHAT;
            weipay.name = mContext.getString(R.string.payment_weipay);
            data.add(weipay);

//            CashierPaymentMode alipay = new CashierPaymentMode();
//            alipay.paymentType = ConstantsUtil.PAYMENT_ALIPAY;
//            alipay.name = mContext.getString(R.string.payment_alipay);
//            data.add(alipay);
        } else if(SanyiSDK.rest.config.isAlipay) {
            CashierPaymentMode weipay = new CashierPaymentMode();
            weipay.paymentType = ConstantsUtil.PAYMENT_WECHAT;
            weipay.name = mContext.getString(R.string.payment_alipay);
            data.add(weipay);
        }
//        else {
//            CashierPaymentMode alipay = new CashierPaymentMode();
//            alipay.paymentType = ConstantsUtil.PAYMENT_ALIPAY;
//            alipay.name = mContext.getString(R.string.payment_alipay);
//            data.add(alipay);
//        }

        CashierPaymentMode store = new CashierPaymentMode();
        store.paymentType = ConstantsUtil.PAYMENT_STORE_VALUE;
        store.name = mContext.getString(R.string.rechargeable_card);
        data.add(store);

        CashierPaymentMode derate = new CashierPaymentMode();
        derate.paymentType = ConstantsUtil.PAYMENT_WAIVE;
        derate.name = mContext.getString(R.string.payment_derate);
        data.add(derate);

        CashierPaymentMode point = new CashierPaymentMode();
        point.paymentType = ConstantsUtil.PAYMENT_POINT;
        point.name = mContext.getString(R.string.payment_point);
        data.add(point);

        for (int i = 0; i < paymentModes.size(); i++) {
            PaymentMode paymentMode = paymentModes.get(i);
            CashierPaymentMode cashierPaymentMode = new CashierPaymentMode();
            cashierPaymentMode.id = paymentMode.id;
            cashierPaymentMode.paymentType = ConstantsUtil.PAYMENT_CUSTOM;
            cashierPaymentMode.name = paymentMode.name;
            cashierPaymentMode.remark = paymentMode.remark;
            data.add(cashierPaymentMode);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }


    @Override
    public List<CashierPayment> getItem(int position) {
        // TODO Auto-generated method stub
        if(data.get(position).paymentType != ConstantsUtil.PAYMENT_CUSTOM) {
            return payments.get(data.get(position).paymentType.longValue());
        }
        else {
            return payments.get(data.get(position).id);
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return -1;
    }

    public Double getPaymentsTotal(List<CashierPayment> payments) {
        Double amount = 0.0;
        for (CashierPayment payment : payments) {
            amount += (payment.value - payment.change);
        }
        return amount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final CashierPaymentMode paymentMode = data.get(position);
        List<CashierPayment> cashPayment;
        if(paymentMode.paymentType != ConstantsUtil.PAYMENT_CUSTOM)
            cashPayment = payments.get(paymentMode.paymentType.longValue());
        else
            cashPayment = payments.get(paymentMode.id);
        View l;

        if (convertView == null) {
            l = inflater.inflate(R.layout.payment_mode_detail, parent, false);
        } else {
            l = convertView;
        }
        TextView payment_name_normal = (TextView) l.findViewById(R.id.payment_name_normal);
        TextView payment_amount = (TextView) l.findViewById(R.id.payment_amount);
        BadgeView badgeView = (BadgeView) l.findViewById(R.id.payment_size);
        payment_name_normal.setText(paymentMode.name);
        if (cashPayment != null && cashPayment.size() > 0) {
            payment_amount.setText(OrderUtil.dishPriceFormatter.format(getPaymentsTotal(cashPayment)));
            payment_name_normal.setTextColor(Color.WHITE);
            payment_amount.setVisibility(View.VISIBLE);
            if (cashPayment.size() > 1) {
                badgeView.setVisibility(View.VISIBLE);
                badgeView.setText(Integer.toString(cashPayment.size()));
            } else {
                badgeView.setVisibility(View.GONE);
            }
            l.setBackground(mContext.getResources().getDrawable(R.drawable.payment_mode_choose_selector));
        } else {
            payment_amount.setVisibility(View.GONE);
            badgeView.setVisibility(View.GONE);
            payment_name_normal.setTextColor(Color.BLACK);
            l.setBackground(mContext.getResources().getDrawable(R.drawable.payment_mode_normal_selector));
        }

        return l;
    }

    public void setPayment(List<CashierPayment> cashierPayments) {
        refreshData(cashierPayments);
        notifyDataSetChanged();
    }


    private void refreshData(List<CashierPayment> cashierPayments) {
                                                                     payments.clear();
//        for (int i = 0; i < data.size(); i++) {
//            CashierPaymentMode paymentMode = data.get(i);
//            List<CashierPayment> lists = new ArrayList<>();
//            for (CashierPayment cashPayment : cashierPayments) {
//                if (paymentMode.paymentType == cashPayment.paymentType && paymentMode.paymentType != ConstantsUtil.PAYMENT_CUSTOM) {
//                    lists.add(cashPayment);
//                }
//                if (cashPayment.paymentType == paymentMode.paymentType && paymentMode.paymentType == ConstantsUtil.PAYMENT_CUSTOM) {
//                    if (paymentMode.name.equals(cashPayment.paymentName)) {
//                        lists.add(cashPayment);
//                    }
//                }
//            }
//            payments.put(i, lists);
//        }

        for(int i = 0; i < data.size(); ++i) {
            if(data.get(i).paymentType != ConstantsUtil.PAYMENT_CUSTOM) {
                payments.put(data.get(i).paymentType.longValue(), new ArrayList<CashierPayment>());
            }
            else {
                payments.put(data.get(i).id, new ArrayList<CashierPayment>());
            }
        }
        for(CashierPayment cashPayment : cashierPayments) {
            List<CashierPayment> paymentList;
            if(cashPayment.paymentType != ConstantsUtil.PAYMENT_CUSTOM)
                paymentList = payments.get(cashPayment.paymentType.longValue());
            else
                paymentList = payments.get(cashPayment.paymentMode.longValue());
            if(paymentList != null)
                paymentList.add(cashPayment);
            else {
                if(cashPayment.paymentType == ConstantsUtil.PAYMENT_ALIPAY) {
                    paymentList = payments.get((long)ConstantsUtil.PAYMENT_WECHAT); //微信支付是默认的移动支付方式
                    if(paymentList != null)
                        paymentList.add(cashPayment);
//                    else
//                        System.out.println("Weixin payment list not found");
                }
            }
        }
    }


}