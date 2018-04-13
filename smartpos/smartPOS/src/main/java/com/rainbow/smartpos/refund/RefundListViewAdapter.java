package com.rainbow.smartpos.refund;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.QPayInfo;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.DateHelper;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.List;

/**
 * Created by Admin on 2017/7/6.
 */

public class RefundListViewAdapter extends BaseAdapter {

    public RefundListViewAdapter(Context ctx)
    {
        super();
        this.context = ctx;
    }

    public void setmSelection(int mSelection) {
        this.mSelection = mSelection;
        notifyDataSetChanged();
    }

    public QPayInfo getSelectedQPayItem()
    {
        if(this.mSelection >= 0)
            return qpayList.get(mSelection);
        else return null;
    }

    public void setQPayList(List<QPayInfo> qpayList) {
        this.qpayList = qpayList;
        if(this.qpayList == null || this.qpayList.isEmpty())
            this.mSelection = -1;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (qpayList == null)? 0 : qpayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {

            view = LayoutInflater.from(context).inflate(R.layout.layout_refund_table_head, parent, false);
        } else {
            view = convertView;
        }
        TextView billSn, tableno, payment_method, amount, state, time, operator;
        billSn = (TextView) view.findViewById(R.id.textView_bill_number);
        tableno = (TextView) view.findViewById(R.id.textView_table_number);
        payment_method = (TextView) view.findViewById(R.id.textView_payment_method);
        amount = (TextView) view.findViewById(R.id.textView_amount);
        state = (TextView) view.findViewById(R.id.textView_payment_state);
        time = (TextView) view.findViewById(R.id.textView_time);
        operator = (TextView) view.findViewById(R.id.textView_operator);

        QPayInfo qpay = qpayList.get(position);
        billSn.setText(qpay.billsn);
        tableno.setText(qpay.orderName);
        if(qpay.paymentType == ConstantsUtil.PAYMENT_ALIPAY)
            payment_method.setText("支付宝");
        else
            payment_method.setText("微信");
        amount.setText(OrderUtil.dishPriceFormatter.format(qpay.amount));
        if(qpay.state == 0) {
            state.setText("已退款");
        }
        else {
            state.setText("支付正常");
        }
        if(qpay.refundon != null) {
            time.setText(DateHelper.hmsFormater.format(qpay.refundon));
        } else
            time.setText("");
        if(qpay.refundStaffName != null) {
            operator.setText(qpay.refundStaffName);
        } else
            operator.setText("");

        if (position == mSelection) {
//            view.setBackgroundColor(Color.parseColor("#D8D8D8"));
            view.setBackgroundColor(Color.parseColor("#E8E8E8"));
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }


    int mSelection = -1;
    List<QPayInfo> qpayList;
    Context context;
}
