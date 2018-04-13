package com.rainbow.smartpos.check;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

public class NumPadPopWindow {
    private String value = "";
    private TextView textViewCheckPaymentType;
    private TextView textViewCheckPaying;
    private TextView textViewCheckPayChange;
    static Button btn1;
    static Button btn2;
    static Button btn3;
    static Button btn4;
    static Button btn5;
    static Button btn6;
    static Button btn7;
    static Button btn8;
    static Button btn9;
    static Button btn0;
    static Button btn00;
    static ImageButton btnClear;
    static Button btnSure;
    static ImageButton btnDelete;
    static Button btnAll;
    static Button btnPoint;

    private boolean isFirstInput = true;
    private View parentView;
    private Activity activity;
    private OnSureListener onSureListener;
    private CashierPayment cashierPaymentMode;
    private CashierResult currentCashierResult;
    private PopupWindow mPopupWindow;

    public interface OnSureListener {
        void onSureClick(Double value, Double change);
    }

    public NumPadPopWindow(View v, Activity activity, CashierResult cashierResult, CashierPayment cashierPaymentMode, OnSureListener onSureListener) {
        this.parentView = v;
        this.activity = activity;
        this.currentCashierResult = cashierResult;
        this.cashierPaymentMode = cashierPaymentMode;
        this.onSureListener = onSureListener;
    }

    public void show() {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.numpad_dialog_layout, null);
        initView(contentView);
        int width = (int) activity.getResources().getDimension(R.dimen.check_fragment_cash_pay_width);
        int height = (int) activity.getResources().getDimension(R.dimen.check_fragment_cash_pay_height);
        mPopupWindow = new PopupWindow(contentView, width, height, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.choose_unit_bg));
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
        mPopupWindow.getBackground().setAlpha(190);
        int[] location = new int[2];
        parentView.getLocationOnScreen(location);
        if ((location[0] + parentView.getWidth() + width) < MainScreenActivity.getScreenWidth()) {
            mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, location[0] + parentView.getWidth(), location[1] - height + parentView.getHeight());
        } else {
            mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, location[0] - width, location[1] - height + parentView.getHeight());
        }
    }

    public void dismiss() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
        }
    }

    private void initView(View view) {
        // TODO Auto-generated method stub
        textViewCheckPaymentType = (TextView) view.findViewById(R.id.textViewCheckPaymentType);
        textViewCheckPaying = (TextView) view.findViewById(R.id.textViewCheckPaying);
        textViewCheckPayChange = (TextView) view.findViewById(R.id.textViewCheckPayChange);

        textViewCheckPaymentType.setText(cashierPaymentMode.paymentName);

        setValue();
        btn1 = (Button) view.findViewById(R.id.buttonNumPad1);
        btn2 = (Button) view.findViewById(R.id.buttonNumPad2);
        btn3 = (Button) view.findViewById(R.id.buttonNumPad3);
        btn4 = (Button) view.findViewById(R.id.buttonNumPad4);
        btn5 = (Button) view.findViewById(R.id.buttonNumPad5);
        btn6 = (Button) view.findViewById(R.id.buttonNumPad6);
        btn7 = (Button) view.findViewById(R.id.buttonNumPad7);
        btn8 = (Button) view.findViewById(R.id.buttonNumPad8);
        btn9 = (Button) view.findViewById(R.id.buttonNumPad9);
        btn0 = (Button) view.findViewById(R.id.buttonNumPad0);
        btn00 = (Button) view.findViewById(R.id.buttonNumPadTwoZero);
        btnPoint = (Button) view.findViewById(R.id.buttonNumPadPoint);
        btnClear = (ImageButton) view.findViewById(R.id.buttonNumPadClear);
        btnSure = (Button) view.findViewById(R.id.buttonNumPadSure);
        btnSure.setClickable(true);
        btnDelete = (ImageButton) view.findViewById(R.id.buttonNumPadC);
        btnAll = (Button) view.findViewById(R.id.buttonNumPadAll);

        btn00.setOnClickListener(onClickListener);
        btnPoint.setOnClickListener(onClickListener);
        btnClear.setOnClickListener(onClickListener);
        btnDelete.setOnClickListener(onClickListener);
        btnSure.setOnClickListener(onClickListener);
        btnAll.setOnClickListener(onClickListener);
        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
        btn5.setOnClickListener(onClickListener);
        btn6.setOnClickListener(onClickListener);
        btn7.setOnClickListener(onClickListener);
        btn8.setOnClickListener(onClickListener);
        btn9.setOnClickListener(onClickListener);
        btn0.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.buttonNumPadAll:
                    setValue();
                    break;
                case R.id.buttonNumPadC:
                    if(!value.isEmpty()) {
                        value = value.substring(0, value.length() - 1);
                        if(!value.isEmpty()) {
                            isFirstInput = false;
                            textViewCheckPaying.setText(value);
                        } else {
                            setValue();
                            isFirstInput = true;
                        }
                    }
                    break;
                case R.id.buttonNumPadSure:
                    btnSure.setClickable(false);//取消确定按钮的点击事件,防止用户短时间内多次点击,造成多次发送请求导致数据错误
                    buttonSureClick();
                    break;
                case R.id.buttonNumPadTwoZero:
                    appendNumber("00");
                    break;
                case R.id.buttonNumPad0:
                    appendNumber("0");
                    break;
                case R.id.buttonNumPad1:
                    appendNumber("1");
                    break;
                case R.id.buttonNumPad2:
                    appendNumber("2");
                    break;
                case R.id.buttonNumPad3:
                    appendNumber("3");
                    break;
                case R.id.buttonNumPad4:
                    appendNumber("4");
                    break;
                case R.id.buttonNumPad5:
                    appendNumber("5");
                    break;
                case R.id.buttonNumPad6:
                    appendNumber("6");
                    break;
                case R.id.buttonNumPad7:
                    appendNumber("7");
                    break;
                case R.id.buttonNumPad8:
                    appendNumber("8");
                    break;
                case R.id.buttonNumPad9:
                    appendNumber("9");
                    break;
                case R.id.buttonNumPadClear:
                    setValue();
                    isFirstInput = true;
                    break;
                case R.id.buttonNumPadPoint:
                    appendNumber(".");
                    break;

                default:
                    break;
            }
            setPayChange();
        }
    };

    private void buttonSureClick() {
        try {
            Double paying = Double.parseDouble(value);
            if (cashierPaymentMode.paymentType != ConstantsUtil.PAYMENT_CASH && paying > currentCashierResult.bill.unpaid) {
                Toast.makeText(activity, cashierPaymentMode.paymentName + "的收款金额不能大于待收金额", 0).show();
                btnAll.performClick();
                return;
            }
            Double change = Double.parseDouble(textViewCheckPayChange.getText().toString());
            onSureListener.onSureClick(paying, change);
            dismiss();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private void appendNumber(String inNumb) {
        if (inNumb.equals(".")) {
            if (value.length() > 0 && !value.contains(".")) {
                value = value + inNumb;
                textViewCheckPaying.setText(value);
            }
        } else {
            if (isFirstInput) {
                value = "";
                textViewCheckPaying.setText("");
                isFirstInput = false;
            }
            value = value + inNumb;
            textViewCheckPaying.setText(value);
        }
    }

    private void setValue() {
        if (currentCashierResult.bill.unpaid < 0) {
            textViewCheckPaying.setText(OrderUtil.decimalFormatter.format(0));
            textViewCheckPayChange.setText(OrderUtil.decimalFormatter.format(-currentCashierResult.bill.unpaid));
//            value = Integer.toString(0);
//            setPayChange();
        } else {
            textViewCheckPaying.setText(OrderUtil.decimalFormatter.format(currentCashierResult.bill.unpaid));
//            value = String.valueOf(currentCashierResult.bill.unpaid);
        }
        value = textViewCheckPaying.getText().toString();
        setPayChange();
    }

    private void setPayChange() {
        try {
            double paying = Double.parseDouble(value);
            if (paying > currentCashierResult.bill.unpaid) {
                textViewCheckPayChange.setText(OrderUtil.decimalFormatter.format(paying - currentCashierResult.bill.unpaid));
            } else {
                textViewCheckPayChange.setText(OrderUtil.decimalFormatter.format(0));
            }
        } catch (Exception e) {
            textViewCheckPayChange.setText(OrderUtil.decimalFormatter.format(0));
            e.printStackTrace();
        }
    }

}
