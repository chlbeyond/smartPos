package com.rainbow.smartpos.member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.readystatesoftware.viewbadger.BadgeView;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.scala.check.CashierPaymentMode;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class PaymentModeAdapter extends BaseAdapter {
    private List<CashierPaymentMode> data = new ArrayList<CashierPaymentMode>();
    private Context mContext;
    private LayoutInflater inflater;
    private boolean isInit;
    private int currentPos = 0;


    public PaymentModeAdapter(Context context, boolean isInit) {
        this.mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isInit = isInit;
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
        } else if(SanyiSDK.rest.config.isAlipay) {
            CashierPaymentMode weipay = new CashierPaymentMode();
            weipay.paymentType = ConstantsUtil.PAYMENT_WECHAT;
            weipay.name = mContext.getString(R.string.payment_alipay);
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
//        } else {
//            CashierPaymentMode alipay = new CashierPaymentMode();
//            alipay.paymentType = ConstantsUtil.PAYMENT_ALIPAY;
//            alipay.name = mContext.getString(R.string.payment_alipay);
//            data.add(alipay);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public CashierPaymentMode getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final CashierPaymentMode paymentMode = data.get(position);
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
        payment_amount.setText(OrderUtil.dishPriceFormatter.format(paymentMode.value));
        payment_amount.setVisibility(View.GONE);
        badgeView.setVisibility(View.GONE);
        if (!isInit) {
            l.setBackground(mContext.getResources().getDrawable(R.drawable.member_recharge_payment_mode_not_press));
            payment_name_normal.setTextColor(mContext.getResources().getColor(R.color.return_dish_text_enabled_false));
        } else {
            l.setBackground(mContext.getResources().getDrawable(R.drawable.member_recharge_payment_mode_normal));
            payment_name_normal.setTextColor(mContext.getResources().getColor(R.color.Black));
            if (position == currentPos) {
                l.setBackground(mContext.getResources().getDrawable(R.drawable.member_recharge_payment_mode_select));
                payment_name_normal.setTextColor(mContext.getResources().getColor(R.color.White));
            }
        }
        return l;
    }

    public void refresh(boolean isInit) {
        this.isInit = isInit;
        notifyDataSetChanged();
    }

    public void setSelect(int pos) {
        this.currentPos = pos;
        notifyDataSetChanged();
    }

    public CashierPaymentMode getSelect() {
        return data.get(currentPos);
    }
}
