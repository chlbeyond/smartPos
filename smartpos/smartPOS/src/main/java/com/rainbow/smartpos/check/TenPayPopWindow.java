package com.rainbow.smartpos.check;

import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qrcode.activity.CaptureActivity;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;

public class TenPayPopWindow {
    private String value = "";
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
    static ImageButton btnClear;
    static TextView btnSure;
    static ImageButton btnDelete;

    private boolean isFirstInput = true;
    private View parentView;
    private Activity activity;
    public OnSureListener onSureListener;
    private PopupWindow mPopupWindow;
    private EditText edit;
    private TextView richScan;
    private String remind;
    private Type type;

    public interface OnSureListener {
        void onSureClick(String value);
    }

    public enum Type {
        TENPAY, PREORDER
    }

    public TenPayPopWindow(View v, String remind, Activity activity, OnSureListener onSureListener) {
        this(v, remind, activity, Type.TENPAY, onSureListener);
    }

    public TenPayPopWindow(View v, String remind, Activity activity, Type type, OnSureListener onSureListener) {
        this.parentView = v;
        this.activity = activity;
        this.remind = remind;
        this.onSureListener = onSureListener;
        this.type = type;
    }

    public void show() {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.tenpay_dialog_layout, null);
        initView(contentView);
        int width = (int) activity.getResources().getDimension(R.dimen.check_fragment_ten_pay_width);
        int height = (int) activity.getResources().getDimension(R.dimen.check_fragment_ten_pay_height);
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

    public boolean isShow() {
        return mPopupWindow.isShowing();
    }

    private void initView(View view) {
        edit = (EditText) view.findViewById(R.id.edit);
        edit.setHint(remind);
        edit.requestFocus();
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
        btnClear = (ImageButton) view.findViewById(R.id.buttonNumPadClear);
        btnSure = (TextView) view.findViewById(R.id.sure_btn);
        btnDelete = (ImageButton) view.findViewById(R.id.buttonNumPadDelete);
        richScan = (TextView) view.findViewById(R.id.richScan);
        btnClear.setOnClickListener(onClickListener);
        btnDelete.setOnClickListener(onClickListener);
        btnSure.setOnClickListener(onClickListener);
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
        richScan.setOnClickListener(onClickListener);
        edit.setInputType(InputType.TYPE_NULL);
        edit.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    if (edit.getText().length() > 0) {
                        btnSure.performClick();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.buttonNumPadDelete:
                    if (value.length() > 0) {
                        value = value.substring(0, value.length() - 1);
                        edit.setText(value);
                    }
                    break;
                case R.id.sure_btn:
                    buttonSureClick();
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
                    value = "";
                    edit.setText("");
                    break;
                case R.id.richScan:
                    if(type == Type.TENPAY)
                        activity.startActivityForResult(new Intent(activity, CaptureActivity.class), Restaurant.SCAN_CODE_CHECK_WECHAT);
                    else
                        activity.startActivityForResult(new Intent(activity, CaptureActivity.class), Restaurant.SCAN_CODE_CHECK_PREORDER);
                    break;

                default:
                    break;
            }
        }
    };

    private void buttonSureClick() {
        try {
            onSureListener.onSureClick(edit.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void appendNumber(String inNumb) {
        if (inNumb.equals(".")) {
            if (value.length() > 0 && !value.contains(".")) {
                value = value + inNumb;
                edit.setText(value);
            }
        } else {
            if (isFirstInput) {
                value = "";
                edit.setText("");
                isFirstInput = false;
            }
            value = value + inNumb;
            edit.setText(value);
        }
    }
}
