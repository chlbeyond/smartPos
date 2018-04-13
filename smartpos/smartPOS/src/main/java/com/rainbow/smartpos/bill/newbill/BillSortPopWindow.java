package com.rainbow.smartpos.bill.newbill;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.Payment;
import com.sanyipos.sdk.model.scala.ClosedBill;
import com.sanyipos.sdk.model.scala.ClosedBillListResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/1/14.
 */
public class BillSortPopWindow implements View.OnClickListener {
    private View mView;
    private Context mContext;
    private PopupWindow mPopupWindow;
    private TextView mTextViewSortPayment;
    private TextView mTextViewSortPromotion;
    private TextView mTextViewSortDiscount;
    private TextView mTextViewSortMember;
    private TextView mTextViewSortCancel;

    private View parentView;
    private CustomSortListener listener;
    private ClosedBillListResult closedBillListResult;


    public static final int B_PAYMENT = 1;
    public static final int B_PROMOTION = 2;
    public static final int B_DISCOUNT = 3;
    public static final int B_MEMBER = 4;
    public int currentType = 0;
    List<ClosedBill.BillWrapper> wrappers;

    public interface CustomSortListener {
        void paymentCash();

        void paymentBankCard();

        void paymentStoreValue();

        void paymentAlipay();

        void paymentWechat();

        void paymentVoucher();

        void paymentDiscount(long id);

        void paymentWaive();

        void paymentDebit();

        void paymentNoCent();

        void paymentMinCharge();

        void paymentPromotion(long id);

        void paymentCustom();

        void cancelPayment();
    }

    public BillSortPopWindow() {

    }

    private void initView() {
        mTextViewSortPayment = (TextView) mView.findViewById(R.id.bill_fragment_sort_payment);
        mTextViewSortPayment.setOnClickListener(this);
        mTextViewSortPromotion = (TextView) mView.findViewById(R.id.bill_fragment_sort_promotion);
        mTextViewSortPromotion.setOnClickListener(this);
        mTextViewSortDiscount = (TextView) mView.findViewById(R.id.bill_fragment_sort_discount);
        mTextViewSortDiscount.setOnClickListener(this);
        mTextViewSortMember = (TextView) mView.findViewById(R.id.bill_fragment_sort_member);
        mTextViewSortMember.setOnClickListener(this);
        mTextViewSortCancel = (TextView) mView.findViewById(R.id.bill_fragment_sort_cancel);
        mTextViewSortCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.bill_fragment_sort_cancel:
                listener.cancelPayment();
                mPopupWindow.dismiss();
                return;
            case R.id.bill_fragment_sort_payment:
                currentType = B_PAYMENT;
                break;
            case R.id.bill_fragment_sort_promotion:
                currentType = B_PROMOTION;
                break;
            case R.id.bill_fragment_sort_discount:
                currentType = B_DISCOUNT;
                break;
            case R.id.bill_fragment_sort_member:
                currentType = B_MEMBER;
                break;
        }
        getPaymentString();
        SortChildWindow sortWindow = new SortChildWindow();
        sortWindow.show(mContext, wrappers, parentView, mView, new SortChildWindow.ISelectedChildSortListener() {

            @Override
            public void selectedPositon(int position) {
                switch (currentType) {
                    case B_PAYMENT:
                        switch ((int) wrappers.get(position).getType()) {
                            case Payment.PAYMENT_CASH:
                                listener.paymentCash();
                                break;
                            case Payment.PAYMENT_BANK_CARD:
                                listener.paymentBankCard();
                                break;
                            case Payment.PAYMENT_STORE_VALUE:
                                listener.paymentStoreValue();
                                break;
                            case Payment.PAYMENT_ALIPAY:
                                listener.paymentAlipay();
                                break;
                            case Payment.PAYMENT_WECHAT:
                                listener.paymentWechat();
                                break;
                            case Payment.PAYMENT_VOUCHER:
                                listener.paymentVoucher();
                                break;
                            case Payment.PAYMENT_WAIVE:
                                listener.paymentWaive();
                                break;
                            case Payment.PAYMENT_DEBIT:
                                listener.paymentDebit();
                                break;
                            case Payment.PAYMENT_CUSTOM:
                                listener.paymentCustom();
                                break;
                        }
                        break;
                    case B_PROMOTION:
                        listener.paymentPromotion(wrappers.get(position).getType());
                        break;
                    case B_DISCOUNT:
                        listener.paymentDiscount(wrappers.get(position).getType());
                        break;
                    case B_MEMBER:
                        break;
                }
                mPopupWindow.dismiss();
            }
        });

    }

    public void show(Context context, View parentView, ClosedBillListResult billListResult, CustomSortListener listener) {
        this.mContext = context;
        this.parentView = parentView;
        this.listener = listener;
        this.closedBillListResult = billListResult;

        mView = LayoutInflater.from(context).inflate(R.layout.bill_fragment_sort, null);

        initView();

        mPopupWindow = new PopupWindow(mView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.showAsDropDown(parentView);


    }

    public boolean isContains(String wrapper) {
        for (ClosedBill.BillWrapper billWrapper : wrappers) {
            if (billWrapper.getTypeName().equals(wrapper)) {
                return true;
            }
        }
        return false;
    }

    public List<ClosedBill.BillWrapper> getPaymentString() {
        wrappers = new ArrayList<>();
        if (closedBillListResult != null)
            for (ClosedBill bill : closedBillListResult.bills) {
                switch (currentType) {
                    case B_PAYMENT:
                        for (ClosedBill.BillPayment payment : bill.payments) {
                            if (!isContains(payment.getTypeName()))
                                wrappers.add((ClosedBill.BillWrapper) payment);
                        }
                        break;
                    case B_DISCOUNT:
                        for (ClosedBill.BillDiscount payment : bill.discounts) {
                            if (!isContains(payment.getTypeName()))
                                wrappers.add((ClosedBill.BillWrapper) payment);
                        }
                        break;
                    case B_PROMOTION:
                        for (ClosedBill.BillPromotion payment : bill.promotions) {
                            if (!isContains(payment.getTypeName()))
                                wrappers.add((ClosedBill.BillWrapper) payment);
                        }
                        break;

                }

            }
        return wrappers;
    }


}
