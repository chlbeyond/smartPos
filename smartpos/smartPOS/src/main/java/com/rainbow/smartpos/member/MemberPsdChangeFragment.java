package com.rainbow.smartpos.member;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;

/**
 * Created by ss on 2016/2/23.
 */
public class MemberPsdChangeFragment extends Fragment implements View.OnClickListener {
    private View view;
    public EditText editTextPsd;
    private EditText editTextConfirmPsd;
    private Button confirmButton;
    public MemberFragment activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.fragment_member_psd_change, container, false);
        this.activity = (MemberFragment) getActivity();
        editTextPsd = (EditText) view.findViewById(R.id.editText_member_psd);
        editTextConfirmPsd = (EditText) view.findViewById(R.id.editText_member_confirm_psd);
        editTextConfirmPsd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    changepsd();
                    return true;
                }
                return false;
            }
        });
        confirmButton = (Button) view.findViewById(R.id.button_fragment_member_psd_confirm);
        confirmButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        editTextPsd.requestFocus();
        editTextPsd.setText("");
        editTextConfirmPsd.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_fragment_member_psd_confirm:
                if (editTextPsd.getText().length() == 0 || editTextConfirmPsd.getText().length() == 0) {
                    Toast.makeText(activity,"请输入密码",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!editTextPsd.getText().toString().equals(editTextConfirmPsd.getText().toString())) {

                    Toast.makeText(activity,"两次输入密码不一致",Toast.LENGTH_LONG).show();
                    return;
                }
                changepsd();
                break;
        }
    }

    public void changepsd() {
        SanyiScalaRequests.setMemberPasswordRequest((long) MemberFragment.res.getId(), editTextConfirmPsd.getText().toString(), new Request.ICallBack() {
            @Override
            public void onSuccess(String status) {

                Toast.makeText(activity,"操作成功",Toast.LENGTH_LONG).show();
                activity.onCheckedChanged(null, R.id.member_change_psd);
                editTextPsd.setText("");
                editTextConfirmPsd.setText("");
                editTextPsd.requestFocus();
            }


            @Override
            public void onFail(String error) {

                Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
            }
        });
    }
}
