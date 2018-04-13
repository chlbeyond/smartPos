package com.rainbow.smartpos.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.BaseActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala._PendingBillsRequest;
import com.sanyipos.sdk.model.PendingBills;
import com.sanyipos.sdk.utils.NetUtil;

/**
 * Created by ss on 2016/8/31.
 */

public class AboutActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_left_menu_about);
        setCustomTitle("关于");

        TextView version = (TextView) findViewById(R.id.textView_about_version);
        assert version != null;
        version.setText(Restaurant.getVersionName(this) + " - Build No." + String.valueOf(Restaurant.getVersionCode(this) + " " + SanyiSDK.rest.config.mode));

        TextView shopName = (TextView) findViewById(R.id.textView_about_shop_name);
        assert shopName != null;
        shopName.setText(SanyiSDK.rest.operationData.shop.name);

        ImageView imageViewAbout = (ImageView) findViewById(R.id.imageView_about);
        assert imageViewAbout != null;
        imageViewAbout.setImageDrawable(getResources().getDrawable(R.drawable.logo_icon));

        TextView textViewSystemVersion = (TextView) findViewById(R.id.textView_about_system_version);
        assert textViewSystemVersion != null;
        textViewSystemVersion.setText(Build.VERSION.RELEASE);

        TextView texViewLocalIp = (TextView) findViewById(R.id.textView_about_local_ip);
        assert texViewLocalIp != null;
        texViewLocalIp.setText(NetUtil.getLocalIpAddress());

        TextView accessCode = (TextView) findViewById(R.id.textView_local_setting_access_code);
        assert accessCode != null;
        accessCode.setText(SanyiSDK.registerData.getAccessCode());

        TextView deviceNumber = (TextView) findViewById(R.id.textView_local_setting_device_number);
        assert deviceNumber != null;
        deviceNumber.setText(Integer.toString(SanyiSDK.registerData.getDeviceId()));

        RelativeLayout relativeLayoutService = (RelativeLayout) findViewById(R.id.relativeLayout_service);

        relativeLayoutService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SanyiScalaRequests.pendBillsRequest(new _PendingBillsRequest.IPendingBillsListener() {
                    @Override
                    public void onSuccess(PendingBills bills) {
                        if (bills.bills.size() > 0)
                            new PendingBillDialog(AboutActivity.this, bills).show();
                    }


                    @Override
                    public void onFail(String error) {
                        Toast.makeText(AboutActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        RelativeLayout relativeLayoutEmpty = (RelativeLayout) findViewById(R.id.relativeLayout_about_empty);
        relativeLayoutEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NormalDialog normalDialog = new NormalDialog(AboutActivity.this);
                normalDialog.content("POS机将恢复到初始安装状态，请确认是否抹去数据");
                normalDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        SharePreferenceUtil.saveBooleanPreference(AboutActivity.this, SmartPosPrivateKey.SP_RD_DEVICEREGISTERED, false);
                        SharePreferenceUtil.saveStringPreference(AboutActivity.this, SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, "");
                        Toast.makeText(AboutActivity.this, "数据已经清空，重启应用程序生效", Toast.LENGTH_LONG).show();
                        normalDialog.dismiss();
                    }
                });
                normalDialog.show();


            }
        });
    }

}
