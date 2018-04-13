package com.rainbow.smartpos.check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.presenter.CheckPresenterImpl;
import com.rainbow.smartpos.order.CookMethodItemLayout;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class VoucherAdapter extends BaseAdapter {
    public List<CashierPayment> voucherPayments = new ArrayList<CashierPayment>();
    private Context mContext;
    private LayoutInflater inflater;
    private MainScreenActivity activity;
    private CheckPresenterImpl presenter;
    public VoucherAdapter(Context context,CheckPresenterImpl presenter,List<CashierPayment> payments) {
        this.mContext = context;
        this.presenter = presenter;
        this.activity = (MainScreenActivity) mContext;

        initData(payments);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void initData(List<CashierPayment> payments) {
        voucherPayments.clear();
        CashierPayment payment = new CashierPayment();
        payment.id = -1;
        voucherPayments.add(payment);
        for (int i = 0; i < payments.size(); i++) {
            CashierPayment cashierPayment = payments.get(i);
            if ((null != cashierPayment.paymentType) && (cashierPayment.paymentType == ConstantsUtil.PAYMENT_VOUCHER)) {
                voucherPayments.add(cashierPayment);
            }
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return voucherPayments.size();
    }

    @Override
    public CashierPayment getItem(int position) {
        // TODO Auto-generated method stub
        return voucherPayments.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final CashierPayment voucherPayment = voucherPayments.get(position);

        CookMethodItemLayout l;

        if (convertView == null) {
            l = (CookMethodItemLayout) inflater.inflate(R.layout.has_choose_promotion_dialog_detail, parent, false);

        } else {
            l = (CookMethodItemLayout) convertView;
            // i = (ImageView) l.getChildAt(0);
        }

        TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
        TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
        LinearLayout flag_name_layout = (LinearLayout) l.findViewById(R.id.flag_name_layout);
        ImageView delete_hint = (ImageView) l.findViewById(R.id.delete_hint);
        //CheckBox textViewDishSelect = (CheckBox) l.findViewById(R.id.textViewDishSelect);
        if (voucherPayment.id == -1) {
            textViewDishUnitName.setVisibility(View.GONE);
            textViewDishUnitPrice.setVisibility(View.GONE);
            flag_name_layout.setVisibility(View.VISIBLE);
            delete_hint.setVisibility(View.GONE);
            l.setBackground(mContext.getResources().getDrawable(R.drawable.dash_coners_bg));
        } else {
            flag_name_layout.setVisibility(View.GONE);
            textViewDishUnitName.setVisibility(View.VISIBLE);
            textViewDishUnitPrice.setVisibility(View.VISIBLE);
            delete_hint.setVisibility(View.VISIBLE);
            textViewDishUnitName.setText(voucherPayment.paymentName);
            textViewDishUnitPrice.setText("抵" + OrderUtil.decimalFormatter.format(Double.valueOf(voucherPayment.value) - Double.valueOf(voucherPayment.change)));
            l.setBackground(mContext.getResources().getDrawable(R.drawable.has_choose_promotion_selector));
        }
        delete_hint.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                presenter.removePayment(voucherPayment.paymentName, voucherPayment.value - voucherPayment.change
                        , voucherPayment.paymentType, voucherPayment.id);
            }
        });
        return l;
    }

    public void refresh(List<CashierPayment> payments) {
        initData(payments);
        notifyDataSetChanged();
    }
}
