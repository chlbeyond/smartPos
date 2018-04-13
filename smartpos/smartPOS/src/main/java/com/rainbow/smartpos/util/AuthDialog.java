package com.rainbow.smartpos.util;

import android.content.Context;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnAuthListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.rest.StaffRest;

public class AuthDialog {
    private MyDialog dialog;
    private String password = "";
    private int PERMISSION;
    ImageButton cancel;

    private RadioButton radioButtonPassWord1;
    private RadioButton radioButtonPassWord2;
    private RadioButton radioButtonPassWord3;
    private RadioButton radioButtonPassWord4;
    private RadioButton radioButtonPassWord5;
    private RadioButton radioButtonPassWord6;
    private RadioButton radioButtonPassWord7;
    private RadioButton radioButtonPassWord8;

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
    static ImageButton btnC;
    static ImageButton btnBack;

    public EditText editRFID;

    Context activity;
    private OnAuthListener onAuthListener;
    Type type;

    public enum Type {
        PERMISSION, DISCOUNT
    }

    public void show(Context activity, int permission, Type type, OnAuthListener onAuthListener) {
        this.activity =  activity;
        this.PERMISSION = permission;
        this.type = type;
        this.onAuthListener = onAuthListener;
        switch (type) {
            case PERMISSION:
                if (SanyiSDK.getCurrentStaffPermissionById(permission)) {
                    onAuthListener.onAuthSuccess(SanyiSDK.currentUser);
                    return;
                }
                break;
            case DISCOUNT:
                if (SanyiSDK.currentUser.hasDiscountPermission(permission)) {
                    onAuthListener.onAuthSuccess(SanyiSDK.currentUser);
                    return;
                }
                break;
        }

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.auth_dialog_layout, null, false);
        dialog = new MyDialog(activity, (int) (MainScreenActivity.getScreenWidth() * 0.4), (int) (MainScreenActivity.getScreenHeight() * 0.9), view, R.style.OpDialogTheme);

        cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);

        radioButtonPassWord1 = (RadioButton) view.findViewById(R.id.RadioButtonPassWord1);
        radioButtonPassWord2 = (RadioButton) view.findViewById(R.id.RadioButtonPassWord2);
        radioButtonPassWord3 = (RadioButton) view.findViewById(R.id.RadioButtonPassWord3);
        radioButtonPassWord4 = (RadioButton) view.findViewById(R.id.RadioButtonPassWord4);
        radioButtonPassWord5 = (RadioButton) view.findViewById(R.id.RadioButtonPassWord5);
        radioButtonPassWord6 = (RadioButton) view.findViewById(R.id.RadioButtonPassWord6);
        radioButtonPassWord7 = (RadioButton) view.findViewById(R.id.RadioButtonPassWord7);
        radioButtonPassWord8 = (RadioButton) view.findViewById(R.id.RadioButtonPassWord8);

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
        btnC = (ImageButton) view.findViewById(R.id.buttonNumPadC);
        btnBack = (ImageButton) view.findViewById(R.id.buttonNumPadBack);

        cancel.setOnClickListener(onClickListener);
        btnC.setOnClickListener(onClickListener);
        btnBack.setOnClickListener(onClickListener);
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

        editRFID = (EditText) view.findViewById(R.id.editTextRFID);
        requestFocus();
        editRFID.setInputType(InputType.TYPE_NULL);
        editRFID.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    password = editRFID.getText().toString();
                    attemptAuth(password);
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_NUMPAD_0:
                            btn0.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_1:
                            btn1.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_2:
                            btn2.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_3:
                            btn3.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_4:
                            btn4.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_5:
                            btn5.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_6:
                            btn6.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_7:
                            btn7.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_8:
                            btn8.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_9:
                            btn9.performClick();
                            return true;
                        case KeyEvent.KEYCODE_DEL:
                            btnC.performClick();
                            return true;
                        case KeyEvent.KEYCODE_FORWARD_DEL:
                            btnBack.performClick();
                            return true;
                    }
                }
                return false;
            }
        });
        dialog.show();
    }

    public void dismiss() {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    public void cancelFocus() {
        if (editRFID != null) {
            editRFID.setFocusable(false);
        }

    }

    public void requestFocus() {
        if (null != editRFID) {
            editRFID.setFocusable(true);
            editRFID.setFocusableInTouchMode(true);
            editRFID.requestFocus();
            editRFID.getText().clear();
        }
    }

    public void afreshAuthView() {
        requestFocus();
        cancelPressed();
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.iv_close_dialog:
                    buttonCancelClick();
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
                case R.id.buttonNumPadC:
                    cancelPressed();
                    break;
                case R.id.buttonNumPadBack:
                    deletePressed();
                    break;

                default:
                    break;
            }
        }
    };

    private void buttonCancelClick() {
        // TODO Auto-generated method stub
        dialog.dismiss();
        onAuthListener.onCancel();
    }

    public void appendNumber(String inNumb) {
        switch (password.length()) {
            case 0:
                radioButtonPassWord1.setChecked(true);
                password = password + inNumb;
                break;

            case 1:
                radioButtonPassWord2.setChecked(true);
                password = password + inNumb;
                break;

            case 2:
                radioButtonPassWord3.setChecked(true);
                password = password + inNumb;
                break;

            case 3:
                radioButtonPassWord4.setChecked(true);
                password = password + inNumb;
                break;

            case 4:
                radioButtonPassWord5.setChecked(true);
                password = password + inNumb;
                break;

            case 5:
                radioButtonPassWord6.setChecked(true);
                password = password + inNumb;
                break;

            case 6:
                radioButtonPassWord7.setChecked(true);
                password = password + inNumb;
                break;
            case 7:
                radioButtonPassWord8.setChecked(true);
                password = password + inNumb;
                attemptAuth(password);
                break;
        }
    }

    public void cancelPressed() {
        password = "";
        radioButtonPassWord1.setChecked(false);
        radioButtonPassWord2.setChecked(false);
        radioButtonPassWord3.setChecked(false);
        radioButtonPassWord4.setChecked(false);
        radioButtonPassWord5.setChecked(false);
        radioButtonPassWord6.setChecked(false);
        radioButtonPassWord7.setChecked(false);
        radioButtonPassWord8.setChecked(false);
    }

    public void deletePressed() {
        switch (password.length()) {
            case 1:
                radioButtonPassWord1.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;

            case 2:
                radioButtonPassWord2.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;

            case 3:
                radioButtonPassWord3.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;

            case 4:
                radioButtonPassWord4.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;

            case 5:
                radioButtonPassWord5.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;

            case 6:
                radioButtonPassWord6.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;

            case 7:
                radioButtonPassWord7.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;

            case 8:
                radioButtonPassWord8.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;

        }
    }

    private void attemptAuth(String password) {
        // TODO Auto-generated method stub
        requestFocus();
        StaffRest staff = SanyiSDK.getSDK().getStaffByAccessCode(password, false);
        switch (type) {
            case PERMISSION:
                if (SanyiSDK.getPermissionByStaff(staff, PERMISSION)) {
                    onAuthListener.onAuthSuccess(staff);
                    dismiss();
                } else {


                    Toast.makeText(activity,"授权失败,请重试",Toast.LENGTH_LONG).show();
                }
                break;
            case DISCOUNT:
                if (SanyiSDK.getDiscountByStaff(staff, PERMISSION)) {
                    onAuthListener.onAuthSuccess(staff);
                    dismiss();
                } else {
                    Toast.makeText(activity,"授权失败,请重试",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
