package com.rainbow.smartpos.member;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnVerifyMemberListener;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.MemberQueryRequest.IMemberQueryListener;
import com.sanyipos.sdk.model.scala.check.MembersResult;
import com.sanyipos.sdk.utils.ConstantsUtil;

public class ReadMemberDialog {
    private MyDialog dialog;


    MemberFragment activity;
    private EditText query_edit;

    private View mainView;
    private LayoutInflater inflater;
    private OnVerifyMemberListener listener;

    public ReadMemberDialog(MemberFragment activity, OnVerifyMemberListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void show() {
        inflater = LayoutInflater.from(activity);
        mainView = inflater.inflate(R.layout.read_member_dialog_layout, null, false);
        dialog = new MyDialog(activity, MainScreenActivity.getScreenWidth() / 2, MainScreenActivity.getScreenHeight() / 2, mainView, R.style.OpDialogTheme);

        initMainView(mainView);
        initMainListener();
        dialog.show();
    }

    private void initMainView(View view) {
        query_edit = (EditText) mainView.findViewById(R.id.query_edit);
        query_edit.requestFocus();
        query_edit.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    if (!query_edit.getText().toString().equals("")) {
                        onSureClick();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initMainListener() {
        mainView.findViewById(R.id.sure_btn).setOnClickListener(onClickListener);
        mainView.findViewById(R.id.iv_close_dialog).setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.sure_btn:
                    onSureClick();
                    break;
                case R.id.iv_close_dialog:
                    listener.onQueryCanceled();
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public boolean isShowing() {
        if (null != dialog) {
            return dialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    public void onSureClick() {
        String query = query_edit.getText().toString();
        if (query.isEmpty()) {
            listener.onQueryCanceled();
            dialog.dismiss();
            return;
        }
        queryMemberRequest(query);

    }

    public void queryMemberRequest(String value) {
        SanyiScalaRequests.memberQueryRequest(value, ConstantsUtil.Member.all, new IMemberQueryListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
                query_edit.setText("");
            }

            @Override
            public void onSuccess(MembersResult result) {
                // TODO Auto-generated method stub
                if (!result.members.isEmpty()) {
                    listener.onQuerySuccess(result.members);
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity,"找不到该会员信息，请检查是否输入正确",Toast.LENGTH_LONG).show();
                    query_edit.setText("");
                }
            }
        });
    }
}
