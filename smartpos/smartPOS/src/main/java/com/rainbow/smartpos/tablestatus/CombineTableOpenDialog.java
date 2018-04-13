package com.rainbow.smartpos.tablestatus;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.util.Listener.OnChooseOrderTypeListener;

public class CombineTableOpenDialog implements OnClickListener {
    FragmentActivity activity;
    OnChooseOrderTypeListener chooseOrderTypeListener;
    NormalDialog normalDialog;

    public CombineTableOpenDialog(FragmentActivity activity, OnChooseOrderTypeListener chooseOrderTypeListener) {
        this.activity = activity;
        this.chooseOrderTypeListener = chooseOrderTypeListener;
    }

    public void show() {
        View view = LayoutInflater.from(activity).inflate(R.layout.combine_table_open_dialog, null);
        LinearLayout order_with_self = (LinearLayout) view.findViewById(R.id.order_with_self);
        LinearLayout order_with_all = (LinearLayout) view.findViewById(R.id.order_with_all);
        order_with_self.setOnClickListener(this);
        order_with_all.setOnClickListener(this);
        normalDialog = new NormalDialog(activity);
        normalDialog.content(view);
        normalDialog.widthScale((float) 0.5);
        normalDialog.heightScale((float) 0.3);
        normalDialog.isHasConfirm(false);
        normalDialog.show();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.order_with_self:
                chooseOrderTypeListener.onOrderSelf();
                break;
            case R.id.order_with_all:
                chooseOrderTypeListener.onOrderWithAll();
                break;
            case R.id.iv_close_dialog:
                chooseOrderTypeListener.cancel();
                break;

            default:
                break;
        }
        normalDialog.dismiss();
    }
}
