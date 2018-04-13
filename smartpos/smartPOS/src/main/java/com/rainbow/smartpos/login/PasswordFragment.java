package com.rainbow.smartpos.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.rest.StaffRest;

public class PasswordFragment extends Fragment implements OnClickListener {
    private String password = new String();
    private RadioButton radioButtonPassWord1;
    private RadioButton radioButtonPassWord2;
    private RadioButton radioButtonPassWord3;
    private RadioButton radioButtonPassWord4;
    private RadioButton radioButtonPassWord5;
    private RadioButton radioButtonPassWord6;
    private RadioButton radioButtonPassWord7;
    private RadioButton radioButtonPassWord8;
    private Button buttonLoginDigit0;
    private Button buttonLoginDigit1;
    private Button buttonLoginDigit2;
    private Button buttonLoginDigit3;
    private Button buttonLoginDigit4;
    private Button buttonLoginDigit5;
    private Button buttonLoginDigit6;
    private Button buttonLoginDigit7;
    private Button buttonLoginDigit8;
    private Button buttonLoginDigit9;
    private ImageButton buttonLoginDelete;
    private ImageButton buttonLoginCancel;
    public EditText editRFID;
    public MainScreenActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_password_fragment, null);
        activity = (MainScreenActivity) getActivity();
        radioButtonPassWord1 = (RadioButton) rootView.findViewById(R.id.RadioButtonPassWord1);
        radioButtonPassWord2 = (RadioButton) rootView.findViewById(R.id.RadioButtonPassWord2);
        radioButtonPassWord3 = (RadioButton) rootView.findViewById(R.id.RadioButtonPassWord3);
        radioButtonPassWord4 = (RadioButton) rootView.findViewById(R.id.RadioButtonPassWord4);
        radioButtonPassWord5 = (RadioButton) rootView.findViewById(R.id.RadioButtonPassWord5);
        radioButtonPassWord6 = (RadioButton) rootView.findViewById(R.id.RadioButtonPassWord6);
        radioButtonPassWord7 = (RadioButton) rootView.findViewById(R.id.RadioButtonPassWord7);
        radioButtonPassWord8 = (RadioButton) rootView.findViewById(R.id.RadioButtonPassWord8);
        radioButtonPassWord1.setClickable(false);
        radioButtonPassWord2.setClickable(false);
        radioButtonPassWord3.setClickable(false);
        radioButtonPassWord4.setClickable(false);
        radioButtonPassWord5.setClickable(false);
        radioButtonPassWord6.setClickable(false);
        radioButtonPassWord7.setClickable(false);
        radioButtonPassWord8.setClickable(false);

        radioButtonPassWord1.setFocusable(false);
        radioButtonPassWord2.setFocusable(false);
        radioButtonPassWord3.setFocusable(false);
        radioButtonPassWord4.setFocusable(false);
        radioButtonPassWord5.setFocusable(false);
        radioButtonPassWord6.setFocusable(false);
        radioButtonPassWord7.setFocusable(false);
        radioButtonPassWord8.setFocusable(false);
        radioButtonPassWord1.setFocusableInTouchMode(false);
        radioButtonPassWord2.setFocusableInTouchMode(false);
        radioButtonPassWord3.setFocusableInTouchMode(false);
        radioButtonPassWord4.setFocusableInTouchMode(false);
        radioButtonPassWord5.setFocusableInTouchMode(false);
        radioButtonPassWord6.setFocusableInTouchMode(false);
        radioButtonPassWord7.setFocusableInTouchMode(false);
        radioButtonPassWord8.setFocusableInTouchMode(false);

        buttonLoginDigit0 = (Button) rootView.findViewById(R.id.ButtonLoginDigit0);
        buttonLoginDigit0.setOnClickListener(this);
        buttonLoginDigit0.setFocusable(false);
        buttonLoginDigit0.setFocusableInTouchMode(false);

        buttonLoginDigit1 = (Button) rootView.findViewById(R.id.ButtonLoginDigit1);
        buttonLoginDigit1.setOnClickListener(this);
        buttonLoginDigit1.setFocusable(false);
        buttonLoginDigit1.setFocusableInTouchMode(false);

        buttonLoginDigit2 = (Button) rootView.findViewById(R.id.ButtonLoginDigit2);
        buttonLoginDigit2.setOnClickListener(this);
        buttonLoginDigit2.setFocusable(false);
        buttonLoginDigit2.setFocusableInTouchMode(false);

        buttonLoginDigit3 = (Button) rootView.findViewById(R.id.ButtonLoginDigit3);
        buttonLoginDigit3.setOnClickListener(this);
        buttonLoginDigit3.setFocusable(false);
        buttonLoginDigit3.setFocusableInTouchMode(false);

        buttonLoginDigit4 = (Button) rootView.findViewById(R.id.ButtonLoginDigit4);
        buttonLoginDigit4.setOnClickListener(this);
        buttonLoginDigit4.setFocusable(false);
        buttonLoginDigit4.setFocusableInTouchMode(false);

        buttonLoginDigit5 = (Button) rootView.findViewById(R.id.ButtonLoginDigit5);
        buttonLoginDigit5.setOnClickListener(this);
        buttonLoginDigit5.setFocusable(false);
        buttonLoginDigit5.setFocusableInTouchMode(false);

        buttonLoginDigit6 = (Button) rootView.findViewById(R.id.ButtonLoginDigit6);
        buttonLoginDigit6.setOnClickListener(this);
        buttonLoginDigit6.setFocusable(false);
        buttonLoginDigit6.setFocusableInTouchMode(false);

        buttonLoginDigit7 = (Button) rootView.findViewById(R.id.ButtonLoginDigit7);
        buttonLoginDigit7.setOnClickListener(this);
        buttonLoginDigit7.setFocusable(false);
        buttonLoginDigit7.setFocusableInTouchMode(false);

        buttonLoginDigit8 = (Button) rootView.findViewById(R.id.ButtonLoginDigit8);
        buttonLoginDigit8.setOnClickListener(this);
        buttonLoginDigit8.setFocusable(false);
        buttonLoginDigit8.setFocusableInTouchMode(false);

        buttonLoginDigit9 = (Button) rootView.findViewById(R.id.ButtonLoginDigit9);
        buttonLoginDigit9.setOnClickListener(this);
        buttonLoginDigit9.setFocusable(false);
        buttonLoginDigit9.setFocusableInTouchMode(false);

        buttonLoginDelete = (ImageButton) rootView.findViewById(R.id.ButtonLoginDelete);
        buttonLoginDelete.setOnClickListener(this);
        buttonLoginDelete.setFocusable(false);
        buttonLoginDelete.setFocusableInTouchMode(false);

        buttonLoginCancel = (ImageButton) rootView.findViewById(R.id.ButtonLoginCancel);
        buttonLoginCancel.setOnClickListener(this);
        buttonLoginCancel.setFocusable(false);
        buttonLoginCancel.setFocusableInTouchMode(false);


        editRFID = (EditText) rootView.findViewById(R.id.editTextRFID);
        requestFocus();
        editRFID.setInputType(InputType.TYPE_NULL);
        editRFID.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)||keyCode==KeyEvent.KEYCODE_DPAD_CENTER)) {
                    password = editRFID.getText().toString();
                    attemptLogin(password);
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_NUMPAD_0:
                            buttonLoginDigit0.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_1:
                            buttonLoginDigit1.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_2:
                            buttonLoginDigit2.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_3:
                            buttonLoginDigit3.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_4:
                            buttonLoginDigit4.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_5:
                            buttonLoginDigit5.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_6:
                            buttonLoginDigit6.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_7:
                            buttonLoginDigit7.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_8:
                            buttonLoginDigit8.performClick();
                            return true;
                        case KeyEvent.KEYCODE_NUMPAD_9:
                            buttonLoginDigit9.performClick();
                            return true;
                        case KeyEvent.KEYCODE_DEL:
                            buttonLoginDelete.performClick();
                            return true;
                        case KeyEvent.KEYCODE_FORWARD_DEL:
                            buttonLoginCancel.performClick();
                            return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ButtonLoginDigit0: {
                digitPressed("0");
                break;
            }

            case R.id.ButtonLoginDigit1: {
                digitPressed("1");
                break;
            }
            case R.id.ButtonLoginDigit2: {
                digitPressed("2");
                break;
            }
            case R.id.ButtonLoginDigit3: {
                digitPressed("3");
                break;
            }
            case R.id.ButtonLoginDigit4: {
                digitPressed("4");
                break;
            }
            case R.id.ButtonLoginDigit5: {
                digitPressed("5");
                break;
            }
            case R.id.ButtonLoginDigit6: {
                digitPressed("6");
                break;
            }
            case R.id.ButtonLoginDigit7: {
                digitPressed("7");
                break;
            }
            case R.id.ButtonLoginDigit8: {
                digitPressed("8");
                break;
            }
            case R.id.ButtonLoginDigit9: {
                digitPressed("9");
                break;
            }
            case R.id.ButtonLoginDelete: {
                deletePressed();
                break;
            }
            case R.id.ButtonLoginCancel: {
                cancelPressed();
                break;
            }

        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    public void digitPressed(String digit) {
        switch (password.length()) {
            case 0: {
                radioButtonPassWord1.setChecked(true);
                password = password + digit;
                break;
            }
            case 1: {
                radioButtonPassWord2.setChecked(true);
                password = password + digit;
                break;
            }
            case 2: {
                radioButtonPassWord3.setChecked(true);
                password = password + digit;
                break;
            }
            case 3: {
                radioButtonPassWord4.setChecked(true);
                password = password + digit;
                break;
            }
            case 4: {
                radioButtonPassWord5.setChecked(true);
                password = password + digit;
                break;
            }
            case 5: {
                radioButtonPassWord6.setChecked(true);
                password = password + digit;
                break;
            }
            case 6: {
                radioButtonPassWord7.setChecked(true);
                password = password + digit;
                break;
            }
            case 7: {
                radioButtonPassWord8.setChecked(true);
                password = password + digit;
                attemptLogin(password);
                break;
            }
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

    public void onStart() {
        cancelPressed();
        super.onStart();
    }

    public void deletePressed() {
        switch (password.length()) {
            case 1: {
                radioButtonPassWord1.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;
            }
            case 2: {
                radioButtonPassWord2.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;
            }
            case 3: {
                radioButtonPassWord3.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;
            }
            case 4: {
                radioButtonPassWord4.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;
            }
            case 5: {
                radioButtonPassWord5.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;
            }
            case 6: {
                radioButtonPassWord6.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;
            }
            case 7: {
                radioButtonPassWord7.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;
            }
            case 8: {
                radioButtonPassWord8.setChecked(false);
                password = password.substring(0, password.length() - 1);
                break;
            }
        }
    }

    public void attemptLogin(String password) {
        if (SanyiSDK.rest.operationData.shop == null) {
            showLoginErrorDialog("餐厅资料不全，不能登录，检查网路是否正常");
        } else {
            StaffRest user = SanyiSDK.getSDK().getStaffByAccessCode(password, true);
            if (null == user) {
                showLoginErrorDialog("用户不存在");
            } else {
                SanyiSDK.currentUser = user;
                activity.setCurrentUserName();
                activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                if (null != editRFID) {
                    editRFID.getText().clear();
                    editRFID.requestFocus();
                }
                cancelPressed();
            }
        }
    }

    public void showLoginErrorDialog(String extraInfo) {
        // Resources res = getActivity().getResources();
        // final CharSequence[] items =
        // res.getStringArray(R.array.login_error_dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.login_error);
        // builder.setCancelable(false);
        builder.setMessage(extraInfo);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                dlg.dismiss();
                if (null != editRFID) {
                    editRFID.getText().clear();
                    editRFID.requestFocus();
                }
                cancelPressed();
            }
        });
        builder.show();
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
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden) {
            cancelFocus();
        } else {
            requestFocus();
        }
    }
}