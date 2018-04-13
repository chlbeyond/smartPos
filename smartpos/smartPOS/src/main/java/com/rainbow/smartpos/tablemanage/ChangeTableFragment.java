package com.rainbow.smartpos.tablemanage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.SeatEntity;

public class ChangeTableFragment extends Fragment implements OnClickListener {
    public static final int FRIST_STEP = 0;
    public static final int SECOND_STEP = 1;
    public static final int THREE_STEP = 2;
    private View view;

    private FirstStepFragment mFirstStepFragment;
    private SecondStepFragment mSecondStepFragment;
    private ThreeStepFragment mThreeStepFragment;

    private RadioGroup radio_group;
    public Bundle bundle;
    public MainScreenActivity activity;
    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.fragment_change_table, container, false);
        activity = (MainScreenActivity) getActivity();
        initView();
        return view;
    }

    private void initView() {
        // TODO Auto-generated method stub
        radio_group = (RadioGroup) view.findViewById(R.id.radio_group);
//		radio_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				// TODO Auto-generated method stub
//				switch (checkedId) {
//				case R.id.text1:
//				case R.id.text2:
//					group.check(R.id.change_in_table);
//				case R.id.change_in_table:
//					displayView(FRIST_STEP);
//					break;
//				case R.id.text3:
//				case R.id.text4:
//					group.check(R.id.sure_change_table);
//				case R.id.sure_change_table:
//					displayView(SECOND_STEP);
//					break;
//
//				default:
//					break;
//				}
//			}
//		});
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getString(R.string.change_table));
        view.findViewById(R.id.back).setOnClickListener(this);
        displayView(FRIST_STEP);
    }

    public void displayView(int position) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        hideView(ft);
        switch (position) {
            case FRIST_STEP:
                if (mFirstStepFragment != null) {
                    ft.attach(mFirstStepFragment);
                } else {
                    mFirstStepFragment = new FirstStepFragment();
                    mFirstStepFragment.setBundle(bundle);
                    mFirstStepFragment.setChangeTableFragment(this);
                    ft.add(R.id.frameLayoutChange, mFirstStepFragment);
                }
                break;
            case SECOND_STEP:
                if (mSecondStepFragment != null) {
                    ft.attach(mSecondStepFragment);
                } else {
                    mSecondStepFragment = new SecondStepFragment();
                    mSecondStepFragment.setBundle(bundle);
                    mSecondStepFragment.setChangeTableFragment(this);
                    ft.add(R.id.frameLayoutChange, mSecondStepFragment);
                }
                radio_group.check(R.id.change_in_table);
                break;
            case THREE_STEP:
                SeatEntity changeInTable = null;
                SeatEntity changeOutTable = null;
                if (null != mFirstStepFragment) {
                    changeOutTable = mFirstStepFragment.getSelectTable();
                }
                if (null != mSecondStepFragment) {
                    changeInTable = mSecondStepFragment.getSelectTable();
                }
                radio_group.check(R.id.sure_change_table);

                mThreeStepFragment = new ThreeStepFragment();
                mThreeStepFragment.setChangeInTable(changeInTable);
                mThreeStepFragment.setChangeOutTable(changeOutTable);
                ft.add(R.id.frameLayoutChange, mThreeStepFragment);

                break;
            default:
                break;
        }
        ft.commit();
    }

    public void hideView(FragmentTransaction ft) {
        if (mFirstStepFragment != null) {
            ft.detach(mFirstStepFragment);
        }
        if (mSecondStepFragment != null) {
            ft.detach(mSecondStepFragment);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                activity.displayView(MainScreenActivity.TABLE_MANAGE_FRAGMENT, bundle);
                break;
            default:
                break;
        }
    }
}
