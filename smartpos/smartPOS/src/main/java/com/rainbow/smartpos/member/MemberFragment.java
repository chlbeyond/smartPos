package com.rainbow.smartpos.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.rainbow.smartpos.BaseActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.member.QueryRechargeMemberFragment.OnQueryMemberButtonClickListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.utils.ConstantsUtil;

public class MemberFragment extends BaseActivity implements OnCheckedChangeListener {

    public final static int QUERY_RECHARGE_MEMBER = 0;
    public final static int MEMBER_RECHARGE = 1;
    public final static int MEMBER_CREATE = 2;
    public final static int MEMBER_QUERY = 3;
    public final static int MEMBER_QUERY_RECHARGE = 4;
    public final static int NO_PERMISSION = 5;
    public final static int MEMBER_CHANGE_PSD = 6;
    public final static int MEMBER_CHARGE_LSIT = 7;

    public RadioGroup member_manager_group;

    public MemberCreateFragment mCreateFragment;

    public MemberQueryFragment mQueryFragment;

    public MemberRechargeFragment mRechargeFragment;

    public MemberQueryRechargeFragment mQueryRechargeFragment;

    public NoPermissionFragment mPermissionFragment;

    public MemberPsdChangeFragment mChangePsdFragment;

    public MemberChargeListFragment mMemberChargeListFragment;

    public QueryRechargeMemberFragment queryRechargeMemberFragment;

    public Fragment currentFragment;
    public Type type = Type.recharge;


    public enum Type {
        recharge, changepsd, query
    }

    public Bundle bundle;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_member);
        member_manager_group = (RadioGroup) findViewById(R.id.member_manager_group);

        setCustomTitle("会员");


        initView();
    }


    public void initView() {
        // TODO Auto-generated method stub

        member_manager_group.check(member_manager_group.getChildAt(0).getId());
        member_manager_group.setOnCheckedChangeListener(this);
        if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_MEMBER_QUERY) && SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_MEMBER_RECHARGE)) {
            displayView(QUERY_RECHARGE_MEMBER);
        } else {
            displayView(NO_PERMISSION);
        }
    }


    public void hideView(FragmentTransaction ft) {
        if (mCreateFragment != null) {
            ft.detach(mCreateFragment);
        }
        if (mQueryFragment != null) {
            ft.detach(mQueryFragment);
        }
        if (mRechargeFragment != null) {
            ft.detach(mRechargeFragment);
        }
        if (mQueryRechargeFragment != null) {
            ft.detach(mQueryRechargeFragment);
        }
        if (mPermissionFragment != null) {
            ft.detach(mPermissionFragment);
        }
        if (mChangePsdFragment != null) {
            ft.detach(mChangePsdFragment);
        }
        if (queryRechargeMemberFragment != null) {
            ft.detach(queryRechargeMemberFragment);
        }
        if (mMemberChargeListFragment != null) {
            ft.detach(mMemberChargeListFragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bundle != null) {
            member_manager_group.check(R.id.member_recharge);
        }
    }

    public void displayView(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideView(ft);
        switch (position) {
            case QUERY_RECHARGE_MEMBER:
                if (queryRechargeMemberFragment != null) {
                    ft.attach(queryRechargeMemberFragment);
                } else {
                    queryRechargeMemberFragment = new QueryRechargeMemberFragment();
                    ft.add(R.id.member_frame_layout, queryRechargeMemberFragment);
                }
                currentFragment = queryRechargeMemberFragment;

                break;
            case MEMBER_RECHARGE:
                if (mRechargeFragment != null) {
                    ft.attach(mRechargeFragment);
                } else {
                    mRechargeFragment = new MemberRechargeFragment();
                    ft.add(R.id.member_frame_layout, mRechargeFragment);
                }
                if (bundle != null) {
                    mRechargeFragment.setBundle(bundle);
                }
                currentFragment = mRechargeFragment;
                break;
            case MEMBER_CREATE:
                if (mCreateFragment != null) {
                    ft.attach(mCreateFragment);
                } else {
                    mCreateFragment = new MemberCreateFragment();
                    ft.add(R.id.member_frame_layout, mCreateFragment);
                }
                currentFragment = mCreateFragment;
                break;
            case MEMBER_QUERY:
                if (mQueryFragment != null) {
                    ft.attach(mQueryFragment);
                } else {
                    mQueryFragment = new MemberQueryFragment();
                    mQueryFragment.setMemberFragment(this);
                    ft.add(R.id.member_frame_layout, mQueryFragment);
                }
                currentFragment = mQueryFragment;
                break;
            case MEMBER_QUERY_RECHARGE:
                if (mQueryRechargeFragment != null) {
                    ft.attach(mQueryRechargeFragment);
                } else {
                    mQueryRechargeFragment = new MemberQueryRechargeFragment();
                    ft.add(R.id.member_frame_layout, mQueryRechargeFragment);
                }
                currentFragment = mQueryRechargeFragment;
                break;
            case NO_PERMISSION:
                if (mPermissionFragment != null) {
                    ft.attach(mPermissionFragment);
                } else {
                    mPermissionFragment = new NoPermissionFragment();
                    ft.add(R.id.member_frame_layout, mPermissionFragment);
                }
                currentFragment = mPermissionFragment;
                break;
            case MEMBER_CHANGE_PSD:
                if (mChangePsdFragment != null) {
                    ft.attach(mChangePsdFragment);
                    mChangePsdFragment.editTextPsd.requestFocus();
                } else {
                    mChangePsdFragment = new MemberPsdChangeFragment();
                    ft.add(R.id.member_frame_layout, mChangePsdFragment);
                }
                currentFragment = mChangePsdFragment;
                break;
            case MEMBER_CHARGE_LSIT:
                if (mMemberChargeListFragment != null) {
                    ft.attach(mMemberChargeListFragment);
                } else {
                    mMemberChargeListFragment = new MemberChargeListFragment();
                    ft.add(R.id.member_frame_layout, mMemberChargeListFragment);
                }
                currentFragment = mChangePsdFragment;
                break;
            default:
                break;
        }
        ft.commit();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        switch (checkedId) {
            case R.id.member_recharge:
                type = Type.recharge;
                if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_MEMBER_RECHARGE)) {
                    displayView(QUERY_RECHARGE_MEMBER);
                } else {
                    displayView(NO_PERMISSION);
                }
                break;
            case R.id.member_create:
                if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_MEMBER_HAND_OUT_CARD)) {
                    displayView(MEMBER_CREATE);
                } else {
                    displayView(NO_PERMISSION);
                }
                break;

            case R.id.member_query:
                type = Type.query;
                if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_MEMBER_QUERY)) {
                    displayView(QUERY_RECHARGE_MEMBER);
                } else {
                    displayView(NO_PERMISSION);
                }
                break;
            case R.id.member_recharge_detail:
                if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_MEMBER_QUERY)) {
                    displayView(MEMBER_CHARGE_LSIT);
                } else {
                    displayView(NO_PERMISSION);
                }
                break;
            case R.id.member_change_psd:
                type = Type.changepsd;
                if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_MEMBER_HAND_OUT_CARD)) {
                    displayView(QUERY_RECHARGE_MEMBER);
                } else {
                    displayView(NO_PERMISSION);
                }
            default:
                break;
        }
    }


    public OnQueryMemberButtonClickListener queryMemberButtonClickListener = new OnQueryMemberButtonClickListener() {

        @Override
        public void onQuerySuccess(MemberInfo memberInfo) {
            // TODO Auto-generated method stub
            res = memberInfo;
            switch (type) {
                case changepsd:
                    displayView(MEMBER_CHANGE_PSD);
                    break;
                case recharge:
                    displayView(MEMBER_RECHARGE);
                    break;
                case query:
                    displayView(MEMBER_QUERY);
                    break;
            }

        }

    };
    public static MemberInfo res;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
            switch (requestCode) {
                case Restaurant.SCAN_CODE_CHECK_WECHAT:
                    if (null != mRechargeFragment && mRechargeFragment.tenPayPopWindow.isShow()) {
                        mRechargeFragment.tenPayPopWindow.onSureListener.onSureClick(data.getExtras().getString("serial_number"));
                    }
                    break;
            }
    }
}
