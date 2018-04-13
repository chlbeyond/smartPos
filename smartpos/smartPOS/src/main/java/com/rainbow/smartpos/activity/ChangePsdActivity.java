package com.rainbow.smartpos.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.BaseActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;

import butterknife.ButterKnife;

/**
 * Created by ss on 2016/9/5.
 */
public class ChangePsdActivity extends BaseActivity implements View.OnClickListener {
    public Context mContext;

    private EditText mOldPsd;
    private EditText mNewPsd;
    private EditText mConfirmPsd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_safe);

        ButterKnife.bind(this);
        mContext = this;
        setCustomTitle("安全");

        TextView mCurrentUser = (TextView) findViewById(R.id.textView_current_staff);
        mCurrentUser.setText(SanyiSDK.currentUser.getName());
        final TextView mCurrentNumber = (TextView) findViewById(R.id.textView_current_sn);
        mCurrentNumber.setText(Long.toString(SanyiSDK.currentUser.getSn()));
        mOldPsd = (EditText) findViewById(R.id.editText_old_password);
        mNewPsd = (EditText) findViewById(R.id.editText_new_password);
        mConfirmPsd = (EditText) findViewById(R.id.editText_new_password_confirm);
        mNewPsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mConfirmPsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        findViewById(R.id.button_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOldPsd.getText().toString().length() < 3) {

                    Toast.makeText(mContext, "请输入三位数旧密码",Toast.LENGTH_LONG).show();

                    return;
                }
                if (mNewPsd.getText().toString().length() < 3) {

                    Toast.makeText(mContext, "请输入三位数旧密码",Toast.LENGTH_LONG).show();
                    return;
                }
                if (mConfirmPsd.getText().toString().length() < 3) {

                    Toast.makeText(mContext, "请输入三位数新密码",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mNewPsd.getText().toString().equals(mConfirmPsd.getText().toString())) {

                    Toast.makeText(mContext, "两次输入密码不一致",Toast.LENGTH_LONG).show();
                    return;
                }
                SanyiScalaRequests.changePasswordRequest(mOldPsd.getText().toString(), mNewPsd.getText().toString(), new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {
                        Toast.makeText(mContext, status,Toast.LENGTH_LONG).show();
                    }


                    @Override
                    public void onFail(String error) {
                        Toast.makeText(mContext, error,Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

}
