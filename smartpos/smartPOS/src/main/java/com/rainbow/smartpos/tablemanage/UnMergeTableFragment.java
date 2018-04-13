package com.rainbow.smartpos.tablemanage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.VirtualTable;

import java.util.ArrayList;
import java.util.List;

public class UnMergeTableFragment extends Fragment implements OnClickListener, OnItemClickListener, OnPageChangeListener {
    private View view;

    private ViewPager mViewPager;
    private ListView mListView;
    private static UnMergeTableGroupAdapter listAdapter;
    private static MyFragmentPagerAdapter pagerAdapter;
    private List<Fragment> listFragment;
    private List<VirtualTable> virtualTables;

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
        view = inflater.inflate(R.layout.fragment_unmerge_table, container, false);
        activity = (MainScreenActivity) getActivity();
        initData();
        initView();
        return view;
    }

    private void initData() {
        // TODO Auto-generated method stub

        if (virtualTables == null) {
            virtualTables = new ArrayList<VirtualTable>();
        } else {
            virtualTables.clear();
        }
        virtualTables.addAll(SanyiSDK.rest.virtualTables);

        if (listFragment == null) {
            listFragment = new ArrayList<Fragment>();
        } else {
            listFragment.clear();
        }
        for (int i = 0; i < SanyiSDK.rest.combineTables.size(); i++) {
            List<SeatEntity> mCmTables = SanyiSDK.rest.combineTables.get(i).get(virtualTables.get(i).tag);
            UnMergeVPItemFragment fragment = new UnMergeVPItemFragment();
            fragment.setArguments(bundle);
            fragment.setData(mCmTables);
            listFragment.add(fragment);
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        Bundle bundle = getArguments();
        int pos = bundle.getInt("position", 0);
        mListView = (ListView) view.findViewById(R.id.listViewGroup);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPagerTables);

        listAdapter = new UnMergeTableGroupAdapter(getActivity(), virtualTables);
        mListView.setAdapter(listAdapter);
        mListView.setOnItemClickListener(this);

        pagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), listFragment);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        view.findViewById(R.id.back).setOnClickListener(this);
        view.findViewById(R.id.sure).setOnClickListener(this);

        mListView.setEmptyView(view.findViewById(R.id.emptyText));
        if (listAdapter.getCount() == 0) {
            view.findViewById(R.id.sure).setEnabled(false);
        } else {
            view.findViewById(R.id.sure).setEnabled(true);
        }
        if (listAdapter.getCount() > 0) {
            if (listAdapter.getCount() >= pos) {
                mViewPager.setCurrentItem(pos);
            } else {
                mViewPager.setCurrentItem(listAdapter.getCount());
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                activity.displayView(MainScreenActivity.TABLE_MANAGE_FRAGMENT, bundle);
                break;
            case R.id.sure:
                List<Long> seats = new ArrayList<Long>();
                List<SeatEntity> selectTables = ((UnMergeVPItemFragment) (listFragment.get(listAdapter.getSelectIndex()))).getUnSelectSeat();
                if (selectTables == null) {
                    return;
                }
                if (selectTables.size() == 0) {
                    Toast.makeText(activity,"请选择餐桌",Toast.LENGTH_LONG).show();
                    return;
                }
                for (SeatEntity table : selectTables) {
                    seats.add(table.seat);
                }
                SanyiScalaRequests.unBatchTableRequest(seats, new ICallBack() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(activity,error,Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onSuccess(String status) {
                        // TODO Auto-generated method stub
                        activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                    }
                });
                break;
            default:
                break;
        }
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> list;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        listAdapter.setSelectIndex(arg0);
        clearTableSelect();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        listAdapter.setSelectIndex(position);
        clearTableSelect();
        listAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(position);
    }

    public void clearTableSelect() {
        for (Fragment fragment : listFragment) {
            ((UnMergeVPItemFragment) (fragment)).clearSelect();
        }
    }

    public int getLvSelectIndex() {
        int pos = 0;
        if (null != listAdapter) {
            pos = listAdapter.getSelectIndex();
        }
        return pos;
    }
}
