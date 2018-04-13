package com.rainbow.smartpos.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.android.sdk.androidUtil.RegisteDataUtils;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.RegisteData;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala._DeviceRegisterRequest;

public class DeviceRegistrationFragment extends Fragment {
    private static final int SUCESS = 1;
    private static final int FAIL = 0;

    static final String LOG_TAG = "DeviceRegistration";
    View mainView;
    private boolean isRegistered = false;
    static TextView prompt;
    static TextView promptValue;
    static EditText promptValueEdit;
    private String value = "";
    MainScreenActivity activity;

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
    static Button btnC;
    static Button btnBack;
    static TextView btnChange;
    static Button btnOK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_registration, container, false);
        mainView = rootView;
        activity = (MainScreenActivity) getActivity();

        value = "";
        promptValue = (TextView) mainView.findViewById(R.id.promptValue);
        promptValueEdit = (EditText) mainView.findViewById(R.id.promptValueEdit);
        promptValueEdit.requestFocus();
        btn1 = (Button) mainView.findViewById(R.id.buttonNumPad1);
        btn2 = (Button) mainView.findViewById(R.id.buttonNumPad2);
        btn3 = (Button) mainView.findViewById(R.id.buttonNumPad3);
        btn4 = (Button) mainView.findViewById(R.id.buttonNumPad4);
        btn5 = (Button) mainView.findViewById(R.id.buttonNumPad5);
        btn6 = (Button) mainView.findViewById(R.id.buttonNumPad6);
        btn7 = (Button) mainView.findViewById(R.id.buttonNumPad7);
        btn8 = (Button) mainView.findViewById(R.id.buttonNumPad8);
        btn9 = (Button) mainView.findViewById(R.id.buttonNumPad9);
        btn0 = (Button) mainView.findViewById(R.id.buttonNumPad0);
        btnC = (Button) mainView.findViewById(R.id.buttonNumPadC);
        btnBack = (Button) mainView.findViewById(R.id.buttonNumPadBack);
        btnOK = (Button) mainView.findViewById(R.id.buttonNumPadOk);
        btnChange = (TextView) mainView.findViewById(R.id.buttonChange);
        btnC.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                value = "";
                promptValue.setText("");
            }
        });
        btnBack.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (value.length() > 0) {
                    value = value.substring(0, value.length() - 1);
                    promptValue.setText(value);
                }
            }
        });
        btn1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("1");
            }
        });
        btn2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("2");
            }
        });
        btn3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("3");
            }
        });
        btn4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("4");
            }
        });
        btn5.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("5");
            }
        });
        btn6.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("6");
            }
        });
        btn7.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("7");
            }
        });
        btn8.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("8");
            }
        });
        btn9.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("9");
            }
        });
        btn0.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("0");
            }
        });
        btnOK.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                doRegistration();
            }
        });
        btnChange.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SharePreferenceUtil.saveStringPreference(getActivity(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, "");
                activity.loginFragment.showConnectHostFragment();

            }
        });
        promptValueEdit.setInputType(InputType.TYPE_NULL);
        promptValueEdit.requestFocus();
        promptValueEdit.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_0:
                        case KeyEvent.KEYCODE_NUMPAD_0:
                            btn0.performClick();
                            break;
                        case KeyEvent.KEYCODE_1:
                        case KeyEvent.KEYCODE_NUMPAD_1:
                            btn1.performClick();
                            break;
                        case KeyEvent.KEYCODE_2:
                        case KeyEvent.KEYCODE_NUMPAD_2:
                            btn2.performClick();
                            break;
                        case KeyEvent.KEYCODE_3:
                        case KeyEvent.KEYCODE_NUMPAD_3:
                            btn3.performClick();
                            break;
                        case KeyEvent.KEYCODE_4:
                        case KeyEvent.KEYCODE_NUMPAD_4:
                            btn4.performClick();
                            break;
                        case KeyEvent.KEYCODE_5:
                        case KeyEvent.KEYCODE_NUMPAD_5:
                            btn5.performClick();
                            break;
                        case KeyEvent.KEYCODE_6:
                        case KeyEvent.KEYCODE_NUMPAD_6:
                            btn6.performClick();
                            break;
                        case KeyEvent.KEYCODE_7:
                        case KeyEvent.KEYCODE_NUMPAD_7:
                            btn7.performClick();
                            break;
                        case KeyEvent.KEYCODE_8:
                        case KeyEvent.KEYCODE_NUMPAD_8:
                            btn8.performClick();
                            break;
                        case KeyEvent.KEYCODE_9:
                        case KeyEvent.KEYCODE_NUMPAD_9:
                            btn9.performClick();
                            break;
                        case KeyEvent.KEYCODE_DEL:
                            btnBack.performClick();
                            break;
                        case KeyEvent.KEYCODE_FORWARD_DEL:
                            btnC.performClick();
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_NUMPAD_ENTER:
                            btnOK.performClick();
                            break;
                        case KeyEvent.KEYCODE_ESCAPE:
                            btnChange.performClick();
                            break;

                        default:
                            break;
                    }
                    // editRFID.getText().clear();
                    return true;
                }
                return false;
            }
        });
        return rootView;
    }

    public void doRegistration() {

        SanyiScalaRequests.deviceRegisterRequest(promptValue.getText().toString(), new _DeviceRegisterRequest.IRegisterListener() {



            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub ]
                Toast.makeText(activity,error,Toast.LENGTH_LONG).show();


                promptValue.setText("");
            }

            @Override
            public void onSuccess(RegisteData registeData) {
                // TODO Auto-generated method stub
                promptValue.setText("");
                value = "";
                SanyiSDK.registerData = registeData;
                SanyiSDK.currentUser=null;
                RegisteDataUtils.savePosRegisteData(getActivity(), registeData);
                activity.loginFragment.showFragment();

            }
        });
    }

    void appendNumber(String inNumb) {
        value = value + inNumb;
        promptValue.setText(promptValue.getText() + inNumb);
    }

}
