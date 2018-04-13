package com.rainbow.smartpos.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala._PendingBillsRequest;
import com.sanyipos.sdk.model.PendingBills;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;
import com.sanyipos.sdk.utils.URLUtil;

import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by ss on 2016/10/12.
 */

public class PendingBillDialog {
    public Context mContext;
    public PendingBills pendingBills;
    private volatile boolean stopCheckAnnualPay = false;

    public PendingBillDialog(Context context, PendingBills bills) {
        this.mContext = context;
        this.pendingBills = bills;
    }

    public void show() {
        final NormalDialog dialog = new NormalDialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_reminder, null);
        TextView textView = (TextView) view.findViewById(R.id.textView_dialog_reminder);
        textView.setText(pendingBills.bills.get(0).name);// + " " + OrderUtil.dishPriceFormatter.format(pendingBills.bills.get(0).amount) + " 尚未缴清，请及时缴费。");
        dialog.content(view);
        dialog.setNegativeButton(true);
        dialog.setPositiveButtonText("立即缴费");
        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                dialog.dismiss();
                if(pendingBills.bills.get(0).payUrl == null) return;
                final NormalDialog newDialog = new NormalDialog(mContext);
                newDialog.title("扫一扫看账单详情");
                newDialog.widthScale((float) 0.5);
                newDialog.heightScale((float) 0.5);
                newDialog.isHasConfirm(false);
                ImageView imageView = new ImageView(mContext);
                imageView.setBackgroundColor(Color.WHITE);
                String url = null;
                try {
                    url = URLEncoder.encode(pendingBills.bills.get(0).payUrl, "UTF-8");
                } catch (Exception e) { url = null;}

                Glide.with(mContext).load(ConstantsUtil.QRCODETOOLS + url).into(imageView);
                newDialog.content(imageView);
                newDialog.show();
                newDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        newDialog.dismiss();
                    }
                });
                newDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        stopCheckAnnualPay = true;
                    }
                });
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        while(!stopCheckAnnualPay) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SanyiScalaRequests.pendBillsRequest(new _PendingBillsRequest.IPendingBillsListener() {
                                        @Override
                                        public void onSuccess(PendingBills bills) {
                                            if (bills.bills != null && bills.bills.size() == 0)
                                                newDialog.dismiss();
                                        }

                                        @Override
                                        public void onFail(String error) {  }
                                    });
                                }
                            });

                            try {
                                Thread.sleep(5000);
                            } catch (Exception e) { break;}
                        }
//                        System.out.println("Exit loop");
                    }
                }).start();
            }
        });
        dialog.widthScale((float) 0.5);
        dialog.show();
//        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
//            @Override
//            public void onClickConfirm() {
//                dialog.dismiss();
//            }
//        });
    }
}
