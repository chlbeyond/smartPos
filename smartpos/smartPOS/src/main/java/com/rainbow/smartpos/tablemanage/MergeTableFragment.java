package com.rainbow.smartpos.tablemanage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
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

import static com.rainbow.smartpos.login.LoginActivity.mContext;

public class MergeTableFragment extends Fragment implements OnClickListener {
    private View view;

    public GridView mGridViewIsOpen;
    public GridView mGridViewChoose;
    private MergeTableAdapter mAdapterIsOpen;
    private MergeTableAdapter mAdapterChoose;
    public List<SeatEntity> mSelectTables = new ArrayList<>();
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
        view = inflater.inflate(R.layout.fragment_merge_table, container, false);
        activity = (MainScreenActivity) getActivity();
        initView();
        return view;
    }

    private void initView() {
        // TODO Auto-generated method stub
        mGridViewIsOpen = (GridView) view.findViewById(R.id.gridViewChangeOutTables);
        mGridViewChoose = (GridView) view.findViewById(R.id.gridViewChangeInTables);

        mAdapterIsOpen = new MergeTableAdapter(getActivity(), MainScreenActivity.MERGE_TABLE_FRAGMENT);
        mAdapterChoose = new MergeTableAdapter(getActivity(), MergeTableAdapter.CHOOSE_MERGE);

        mGridViewIsOpen.setAdapter(mAdapterIsOpen);
        mGridViewChoose.setAdapter(mAdapterChoose);
        mGridViewIsOpen.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                SeatEntity table = (SeatEntity) mAdapterIsOpen.getItem(position);
                setSelection(table, true);
            }
        });
        mGridViewChoose.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                SeatEntity table = (SeatEntity) mAdapterChoose.getItem(position);
                setSelection(table, false);
            }
        });

        view.findViewById(R.id.back).setOnClickListener(this);
        view.findViewById(R.id.clear_choose).setOnClickListener(this);
        view.findViewById(R.id.sure_btn).setOnClickListener(this);
    }

    public void setSelection(SeatEntity table, boolean flag) {
        if (-1 != table.seat) {
        } else {
            mSelectTables.add(table);
            VirtualTable virtualTable = SanyiSDK.rest.getVirtualTableByTag(table.tableName);
            if (null != virtualTable) {
                virtualTable.isChooseCombine = flag;
            }
        }
        refresh();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                activity.displayView(MainScreenActivity.TABLE_MANAGE_FRAGMENT, bundle);
                break;
            case R.id.sure_btn:
                List<SeatEntity> seatEntities = mAdapterChoose.getAllData();
                if (seatEntities.size() < 2) {

                    Toast.makeText(activity,"单台无法并台",Toast.LENGTH_LONG).show();
                    return;
                }
                List<Long> tableIds = new ArrayList<Long>();
                for (SeatEntity table : mAdapterChoose.getAllData()) {
                    if (-1 != table.seat) {
                        tableIds.add(table.seat);
                    } else {
                        tableIds.addAll(SanyiSDK.rest.getCombineTableIdsByTag(table.tableName));
                    }
                }
                SanyiScalaRequests.batchTableRequest(tableIds, new ICallBack() {



                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub

                        Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(String status) {
                        // TODO Auto-generated method stub
                        Toast.makeText(activity,status,Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.clear_choose:
                clearChoose();
                break;
            default:
                break;
        }
    }

    public void clearChoose() {
        for (SeatEntity table : mAdapterChoose.getAllData()) {
            setSelection(table, false);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refresh();
        } else {
            clearChoose();
        }
    }

    public void refresh() {
        if (mAdapterIsOpen != null) {
            mAdapterIsOpen.refreshData();
        }
        if (mAdapterChoose != null) {
            mAdapterChoose.refreshData();
        }
    }

}
