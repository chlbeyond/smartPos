package com.rainbow.smartpos.check;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.DisplayUtil;
import com.readystatesoftware.viewbadger.BadgeView;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.List;

/**
 * Created by ss on 2016/4/19.
 */
public class PaymentSelector {
    public Context mContext;
    public MainScreenActivity activity;
    public List<CashierPayment> payments;
    public ListView mListView;
    public View mParentView;
    public PopupWindow popupWindow;
    public ClickPayment listener;

    public interface ClickPayment {
        void clickPayment(CashierPayment payment);

        void addPayment();
    }

    public PaymentSelector(MainScreenActivity context, View view, List<CashierPayment> payments) {
        this.activity = context;
        this.mContext = context;
        this.payments = payments;
        this.mParentView = view;
    }

    public void show(final ClickPayment clickPayment) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_payment_selector, null);
        mListView = (ListView) view.findViewById(R.id.listView_payment_selector);
        mListView.setAdapter(new PaymentSelectorAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < payments.size()) {
                    clickPayment.clickPayment(payments.get(position));
                }else {
                    clickPayment.addPayment();
                }
                popupWindow.dismiss();
            }
        });

        int height = mParentView.getMeasuredHeight() * (payments.size() + 1);
        popupWindow = new PopupWindow(view, mParentView.getMeasuredWidth(), height, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        final int[] location = new int[2];
        mParentView.getLocationOnScreen(location);
        if (location[1] + height < MainScreenActivity.getScreenHeight()) {
            popupWindow.showAsDropDown(mParentView);
        } else {
            popupWindow.showAsDropDown(mParentView, Gravity.NO_GRAVITY, 0, location[1] - mParentView.getMeasuredHeight());
        }
        DisplayUtil.backgroundAlpha(activity, 0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.backgroundAlpha(activity, 1f);
            }
        });
    }

    public class PaymentSelectorAdapter extends BaseAdapter {
        public PaymentSelectorAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return payments.size() + 1;
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
            View l;
            if (position < payments.size()) {
                if (convertView == null) {
                    l = LayoutInflater.from(mContext).inflate(R.layout.payment_mode_detail, parent, false);
                } else {
                    l = convertView;
                }
                CashierPayment payment = payments.get(position);
                TextView payment_name_normal = (TextView) l.findViewById(R.id.payment_name_normal);
                TextView payment_amount = (TextView) l.findViewById(R.id.payment_amount);
                BadgeView badgeView = (BadgeView) l.findViewById(R.id.payment_size);
                badgeView.setVisibility(View.GONE);
                payment_name_normal.setText(payment.paymentName);
                payment_amount.setTextColor(Color.BLACK);
                payment_amount.setText(OrderUtil.dishPriceFormatter.format(payment.value));

                return l;
            } else

            {
                l = LayoutInflater.from(mContext).inflate(R.layout.layout_add_payment, parent, false);
                return l;
            }

        }
    }
}