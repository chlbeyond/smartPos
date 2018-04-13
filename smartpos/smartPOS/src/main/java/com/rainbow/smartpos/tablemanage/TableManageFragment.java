package com.rainbow.smartpos.tablemanage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;

public class TableManageFragment extends Fragment implements OnClickListener {
    private View view;

    public LinearLayout button_fragment_table_manage_split_table;
    public LinearLayout button_fragment_table_manage_unsplit_table;
    public LinearLayout button_fragment_table_manage_merge_table;
    public LinearLayout button_fragment_table_manage_change_table;
    public LinearLayout button_fragment_table_manage_cancel_table;
    public LinearLayout button_fragment_table_manage_unmerge_table;
    public LinearLayout button_fragment_table_manage_open_and_merge;
    public MainScreenActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.fragment_table_manage, container, false);
        activity = (MainScreenActivity) getActivity();
        initView();
        return view;
    }

    private void initView() {
        // TODO Auto-generated method stub
        button_fragment_table_manage_split_table = (LinearLayout) view.findViewById(R.id.button_fragment_table_manage_split_table);
        button_fragment_table_manage_unsplit_table = (LinearLayout) view.findViewById(R.id.button_fragment_table_manage_unsplit_table);
        button_fragment_table_manage_merge_table = (LinearLayout) view.findViewById(R.id.button_fragment_table_manage_merge_table);
        button_fragment_table_manage_change_table = (LinearLayout) view.findViewById(R.id.button_fragment_table_manage_change_table);
        button_fragment_table_manage_cancel_table = (LinearLayout) view.findViewById(R.id.button_fragment_table_manage_cancel_table);
        button_fragment_table_manage_unmerge_table = (LinearLayout) view.findViewById(R.id.button_fragment_table_manage_unmerge_table);
        button_fragment_table_manage_open_and_merge = (LinearLayout) view.findViewById(R.id.button_fragment_table_manage_open_and_merge);
        button_fragment_table_manage_split_table.setOnClickListener(this);
        button_fragment_table_manage_unsplit_table.setOnClickListener(this);
        button_fragment_table_manage_merge_table.setOnClickListener(this);
        button_fragment_table_manage_change_table.setOnClickListener(this);
        button_fragment_table_manage_cancel_table.setOnClickListener(this);
        button_fragment_table_manage_unmerge_table.setOnClickListener(this);
        button_fragment_table_manage_open_and_merge.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button_fragment_table_manage_split_table:
                activity.displayView(MainScreenActivity.SPLIT_TABLE_FRAGMENT, null);
                break;
            case R.id.button_fragment_table_manage_unsplit_table:
                activity.displayView(MainScreenActivity.UNSPLIT_TABLE_FRAGMENT, null);
                break;
            case R.id.button_fragment_table_manage_merge_table:
                activity.displayView(MainScreenActivity.MERGE_TABLE_FRAGMENT, null);
                break;
            case R.id.button_fragment_table_manage_change_table:
                activity.displayView(MainScreenActivity.CHANGE_TABLE_FRAGMENT, null);
                break;
            case R.id.button_fragment_table_manage_cancel_table:
                activity.displayView(MainScreenActivity.CANCEL_TABLE_FRAGMENT, null);
                break;
            case R.id.button_fragment_table_manage_unmerge_table:
                Bundle bundle = new Bundle();
                bundle.putInt("position", 0);
                activity.displayView(MainScreenActivity.UNMERGE_TABLE_FRAGMENT, bundle);
                break;
            case R.id.button_fragment_table_manage_open_and_merge:
                activity.displayView(MainScreenActivity.OPEN_AND_MERGE_TABLE_FRAGMENT, null);
                break;

            default:
                break;
        }
    }
}
