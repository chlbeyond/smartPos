package com.rainbow.smartpos.tablemanage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.scala.changeTable.ChangeTableAction;

public class ThreeStepFragment extends Fragment {
    private View view;

    private TextView changeTableHint;
    private MainScreenActivity activity;
    public SeatEntity changeInTable;
    public SeatEntity currentTable;

    public void setChangeInTable(SeatEntity changeInTable) {
        this.changeInTable = changeInTable;
    }

    public void setChangeOutTable(SeatEntity currentTable) {
        this.currentTable = currentTable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.second_step_layout, container, false);
        activity = (MainScreenActivity) getActivity();
        initView();
        return view;
    }

    private void initView() {
        // TODO Auto-generated method stub
        changeTableHint = (TextView) view.findViewById(R.id.changeTableHint);
        if (null != changeInTable && null != currentTable) {
            changeTableHint.setText("转出台: " + currentTable.tableName + " - " + "转入台: " + changeInTable.tableName);
        }
        view.findViewById(R.id.sure_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (changeInTable == null || currentTable == null) {
                    Toast.makeText(activity,"请选择餐桌",Toast.LENGTH_LONG).show();
                    return;
                }
                SanyiScalaRequests.ChangeTableRequest(ChangeTableAction.TURNTABLE, currentTable.seat, SanyiSDK.getStaffId(), changeInTable.seat, new ICallBack() {


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
            }
        });
    }
}
