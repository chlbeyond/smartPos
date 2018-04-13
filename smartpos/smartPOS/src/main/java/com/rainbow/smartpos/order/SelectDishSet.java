package com.rainbow.smartpos.order;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.GoodsSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectDishSet {
    ExpandableListView expListView;
    SelectDishSetAdapter listAdapter;
    OrderFragment fragment;
    private Dialog dialog;
    private Context activity;
    selectDishSetInterface postrun;


    public interface selectDishSetInterface {
        public void OnOkButtonPressed(OrderDetail detail);

        public void OnCancelButtonPressed();
    }

    public void show(final OrderFragment fragment, final String promptString, OrderDetail orderDetail, long productId, selectDishSetInterface postrun) {
        this.postrun = postrun;
        this.fragment = fragment;
        activity = fragment.getActivity();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogView = inflater.inflate(R.layout.select_set, null, false);
        dialog = new MyDialog(activity, MainScreenActivity.getScreenWidth() * 0.4, MainScreenActivity.getScreenHeight() * 0.9, dialogView, R.style.OpDialogTheme);
        expListView = (ExpandableListView) dialogView.findViewById(R.id.lvExp);
        dialogView.findViewById(R.id.iv_close_dialog).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.sure_btn).setOnClickListener(onClickListener);
        listAdapter = new SelectDishSetAdapter(fragment, productId, orderDetail);


        expListView.setAdapter(listAdapter);
        expListView.setGroupIndicator(null);
        expListView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        // expListView.setChildIndicator(childIndicator)
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            expListView.expandGroup(i);
        }

        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }

        });
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
//				listAdapter.onChildClick(groupPosition, childPosition);

                return false;
            }

        });
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            expListView.expandGroup(i);
        }
        dialog.show();
    }


    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.iv_close_dialog:
                    dialog.dismiss();
                    break;
                case R.id.sure_btn:
                    postrun.OnOkButtonPressed(listAdapter.getOrderDetail());
                    dialog.dismiss();
                    break;

                default:
                    break;
            }
        }
    };
}