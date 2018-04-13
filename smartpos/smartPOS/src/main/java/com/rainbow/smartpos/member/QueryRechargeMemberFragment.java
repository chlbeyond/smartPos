package com.rainbow.smartpos.member;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.ChooseMemberDialog;
import com.rainbow.smartpos.util.ChooseMemberDialog2;
import com.rainbow.smartpos.util.KeyboardUtil;
import com.rainbow.smartpos.util.Listener.OnChooseMemberListener;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.MemberQueryRequest.IMemberQueryListener;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.model.scala.check.MembersResult;
import com.sanyipos.sdk.utils.ConstantsUtil;

public class QueryRechargeMemberFragment extends Fragment implements OnClickListener {
    private EditText recharge_number;
    private TextView query;
    private TextView query_null;
    private View view;
    private MemberFragment activity;

    public static interface OnQueryMemberButtonClickListener {
        public void onQuerySuccess(MemberInfo memberInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.fragment_member_recharge_query, container, false);
        activity = (MemberFragment) getActivity();
        initView();
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        recharge_number.setText("");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {
            recharge_number.setText("");
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        recharge_number = (EditText) view.findViewById(R.id.member_query_condition);
        query_null = (TextView) view.findViewById(R.id.query_null);
        query = (TextView) view.findViewById(R.id.query);
//        recharge_number.setInputType(InputType.TYPE_CLASS_NUMBER);
        boolean isSum = SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_INPUT_TYPE,true);
        new KeyboardUtil(view, getActivity(), recharge_number, isSum).showKeyboard();
        recharge_number.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (query_null.getVisibility() == View.VISIBLE) {
                    query_null.setVisibility(View.INVISIBLE);
                }
            }
        });
        recharge_number.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    queryMemberInfo();
                    closeInputMethod();
                    return true;
                }
                return false;
            }
        });
        recharge_number.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    queryMemberInfo();
                    closeInputMethod();
                }
                return false;
            }
        });
        query.setOnClickListener(this);
    }

    public void closeInputMethod() {
        if (getActivity().getCurrentFocus() != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void queryMemberInfo() {
        String query_value = recharge_number.getText().toString();
        if (query_value.isEmpty()) {
            Toast.makeText(activity, "查询条件不能为空", Toast.LENGTH_LONG).show();

            return;
        }
        SanyiScalaRequests.memberQueryRequest(query_value, ConstantsUtil.Member.all, new IMemberQueryListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onSuccess(MembersResult result) {
                // TODO Auto-generated method stub
                if (result != null && result.members != null && result.members.size() > 0) {
                    closeInputMethod();

                    Toast.makeText(activity, "查询成功", Toast.LENGTH_LONG).show();

                    if (result.members.size() == 1) {
//                        if (activity.type == MemberFragment.Type.changepsd) {
//                            activity.displayView(MemberFragment.MEMBER_CHANGE_PSD);
//                            activity.mChangePsdFragment.setMemberInfo(result.members.get(0));
//                            return;
//                        }
//                        if(activity.type == MemberFragment.Type.recharge){
//
//                        }
                        activity.queryMemberButtonClickListener.onQuerySuccess(result.members.get(0));
                        return;
                    }
                    new ChooseMemberDialog2(getActivity(), result.members, new ChooseMemberDialog2.ChooseMemberListener() {

                        @Override
                        public void ChooseMember(MemberInfo memberInfo) {
                            activity.queryMemberButtonClickListener.onQuerySuccess(memberInfo);
                        }
                    }).show();
                } else {

                    Toast.makeText(activity, "查询失败",Toast.LENGTH_LONG).show();
                    query_null.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.query:
                queryMemberInfo();
                break;

            default:
                break;
        }
    }

}
