package com.rainbow.smartpos.check;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qrcode.activity.CaptureActivity;
import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.util.Listener.OnVerifyMemberListener;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.MemberQueryRequest.IMemberQueryListener;
import com.sanyipos.sdk.model.scala.check.MembersResult;
import com.sanyipos.sdk.utils.ConstantsUtil;

public class VerifyMemberDialog {

    public static interface VerifyMemberListener {
        void memberNumber(String member);
    }

    public static final int CHANGE_PRICE = 0;
    public static final int CHANGE_WEIGHT = 1;

    private MyDialog dialog;
    private String value = "";

    EditText dialog_edit;
    TextView sure;
    TextView textViewMemberScan;
    ImageButton cancel;

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

    MainScreenActivity activity;

    private OnVerifyMemberListener listener;

    public void show(MainScreenActivity activity, OnVerifyMemberListener listener) {
        this.activity = activity;
        this.listener = listener;

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.verify_member_dialog_layout, null, false);
        dialog = new MyDialog(activity, (int) (MainScreenActivity.getScreenWidth() * 0.4), (int) (MainScreenActivity.getScreenHeight() * 0.9), view, R.style.OpDialogTheme);

        dialog_edit = (EditText) view.findViewById(R.id.dialog_edit);
        sure = (TextView) view.findViewById(R.id.sure_btn);
        cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);
        textViewMemberScan = (TextView) view.findViewById(R.id.textView_member_scan);
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
        textViewMemberScan.setOnClickListener(onClickListener);
        sure.setOnClickListener(onClickListener);
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

        requestFocus();
        dialog_edit.setInputType(InputType.TYPE_NULL);
        dialog_edit.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    if (!dialog_edit.getText().toString().equals("")) {
                        cancelFocus();
                        sure.performClick();
                    }
                    return true;
                }
                return false;
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    public void requestFocus() {
        if (null != dialog_edit) {
            dialog_edit.requestFocus();
            dialog_edit.setFocusable(true);
            dialog_edit.setFocusableInTouchMode(true);
        }
    }

    public void cancelFocus() {
        if (null != dialog_edit) {
            dialog_edit.clearFocus();
            dialog_edit.setFocusable(false);
            dialog_edit.setFocusableInTouchMode(false);
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.sure_btn:
                    buttonSureClick();
                    break;
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
                    value = "";
                    dialog_edit.setText(value);
                    break;
                case R.id.buttonNumPadBack:
                    if (value.length() > 0) {
                        value = value.substring(0, value.length() - 1);
                        dialog_edit.setText(value);
                    }
                    break;
                case R.id.textView_member_scan:
                    activity.startActivityForResult(new Intent(activity, CaptureActivity.class), Restaurant.SCAN_CODE_CHECK_MEMBER);
                    activity.setScanListener(new VerifyMemberListener() {
                        @Override
                        public void memberNumber(String member) {
                            queryMemberRequest(member);
                        }
                    });

                    break;
                default:
                    break;
            }
        }
    };

    private void buttonSureClick() {
        // TODO Auto-generated method stub
        String queryText = dialog_edit.getText().toString();
        if (queryText.length() == 0) {
            listener.onQueryCanceled();
            dialog.dismiss();
            return;
        }
        queryMemberRequest(queryText);
    }

    private void buttonCancelClick() {
        // TODO Auto-generated method stub
        listener.onQueryCanceled();
        dialog.dismiss();
    }

    void appendNumber(String inNumb) {
        value = value + inNumb;
        dialog_edit.setText(dialog_edit.getText() + inNumb);
    }

    public void queryMemberRequest(String value) {
        SanyiScalaRequests.memberQueryRequest(value, ConstantsUtil.Member.all, new IMemberQueryListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
                dialog_edit.setText("");
                requestFocus();
            }

            @Override
            public void onSuccess(MembersResult result) {
                // TODO Auto-generated method stub
                if (!result.members.isEmpty()) {
                    listener.onQuerySuccess(result.members);
                    dialog.dismiss();
                } else {

                    Toast.makeText(activity, "找不到该会员信",Toast.LENGTH_LONG).show();
                    dialog_edit.setText("");
                }
            }
        });
    }
}
