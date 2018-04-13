package com.rainbow.smartpos.tablestatus;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.manage.HandoverAdapter;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.model.DisplayItems;
import com.sanyipos.sdk.model.DisplayItems.DisplayAmountItem;
import com.sanyipos.sdk.model.DisplayItems.HandoverInformationList;

import java.util.ArrayList;
import java.util.List;

import static com.rainbow.smartpos.login.LoginActivity.mContext;

public class HandOverDialog {
    MainScreenActivity activity;
    AlertDialog handoverInfoDlg;
    private List<String> groupList;
    private List<List<DisplayAmountItem>> childList;

    public HandOverDialog(Context context, HandoverInformationList handoverInformationList) {
        this.activity = (MainScreenActivity) context;
        onHandOverItemList(handoverInformationList);
    }

    private void onHandOverItemList(final HandoverInformationList handoverInformationList) {

        final NormalDialog normalDialog = new NormalDialog(activity);
        normalDialog.title("交接清单");
        normalDialog.widthScale((float) 0.5);
        normalDialog.heightScale((float) 0.7);
        final View iView = LayoutInflater.from(activity).inflate(R.layout.handover_preview, null, false);
        normalDialog.content(iView);
        initData(handoverInformationList);
        ExpandableListView detailView = (ExpandableListView) iView.findViewById(R.id.listViewHandoverInfo);
        HandoverAdapter adapter = new HandoverAdapter(groupList, childList, activity);
        detailView.setAdapter(adapter);
        detailView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            detailView.expandGroup(i);
        }
        normalDialog.show();
        normalDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                normalDialog.dismiss();
                SanyiScalaRequests.handOverRequest(new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {

                        Toast.makeText(mContext,status,Toast.LENGTH_LONG).show();
                        activity.displayView(MainScreenActivity.LOGIN_FRAGMENT, null);
                    }

                    @Override
                    public void onFail(String error) {

                        Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void initData(HandoverInformationList handoverInformationList) {
        groupList = new ArrayList<String>();
        childList = new ArrayList<List<DisplayItems.DisplayAmountItem>>();
        groupList.add("实收");
        groupList.add("会员");
        groupList.add("虚收");
        childList.add(handoverInformationList.handoverInfos);
        childList.add(handoverInformationList.memberInfos);
        childList.add(handoverInformationList.waiveInfos);
    }
}
