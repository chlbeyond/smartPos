package com.rainbow.smartpos.slidingtutorial;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.rainbow.smartpos.ExitManager;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.event.EventMessage;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiCloudRequests;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.inters.PushRequestListener;
import com.sanyipos.sdk.api.services.cloud._GetXPShopListRequest;
import com.sanyipos.sdk.core.AgentRequests;
import com.sanyipos.sdk.core.PosAgent;
import com.sanyipos.sdk.core.PosAgentRequest;
import com.sanyipos.sdk.model.cloud.ShopList;
import com.sanyipos.sdk.utils.ConstantsUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import kprogresshud.KProgressHUD;

/**
 * Created by ss on 2016/3/18.
 */
public class SlidingTutorial extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.button_sliding_experience_immediately)
    Button mButtonExperienceImmediately;
    public KProgressHUD progressHUD;
    public ThirdCustomPageFragment presentationFragment;
    public LogingFragment logingFragment;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

                PosAgentRequest request = ((PosAgentRequest) msg.obj);
                if (request != null) {
                    if (request.getResponseCode() == ConstantsUtil.RESPONSE_SUCCESS) {
                        request.onRequestSuccess();
                    } else {
                        request.onRequestException();
                    }
                }
                if (progressHUD != null)
                    progressHUD.dismiss();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitManager.getInstance().addActivity(this);
        if (SharePreferenceUtil.getPreference(this, SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, null) != null) {
            Intent intent = new Intent(this, MainScreenActivity.class);
            startActivity(intent);
//            handler.sendEmptyMessage(MY_GO_TO_MAINACTIVITY);
            return;
        }
        setContentView(R.layout.activity_slidingtutorail);
        ButterKnife.bind(this);
        mButtonExperienceImmediately.setOnClickListener(this);
        presentationFragment = new ThirdCustomPageFragment();
        disPresentation();
        initKProgress();
        initSDK();
        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CAMERA:
//            case MY_PERMISSIONS_REQUEST_STORAGE:
            case MY_PERMISSIONS_REQUEST_PERMS: {
                if (grantResults.length > 0 ) {
                        for(int i = 0; i < grantResults.length; ++i) {
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                //show tip to user
                                new AlertDialog.Builder(this).setTitle("权限不足")
                                        .setMessage("请重新运行程序并授权！")//设置显示的内容
                                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                        finish();
                                    }

                                }).show();
                            }
                        }
                }
                return;
            }
        }
    }

    private void requestPermissions()
    {
        ArrayList<String> perms = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.CAMERA);
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA},
//                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_REQUEST_STORAGE);
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!perms.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    perms.toArray(new String[perms.size()]),
                    MY_PERMISSIONS_REQUEST_PERMS);
        }
    }

    public void initSDK() {
        SanyiSDK.getSDK().initRequestListener(new PushRequestListener() {
            @Override
            public void startRequest(PosAgentRequest request) {
                if (request instanceof AgentRequests.CheckAgentRequest_) return;
                if (request instanceof AgentRequests.POSPollingRequest) return;
                if (request instanceof AgentRequests.LeaveSeatRequest) return;
                if (progressHUD != null) progressHUD.show();
                EventBus.getDefault().post(new EventMessage(Restaurant.EVENT_REQUEST));
            }
        });

        SanyiSDK.getSDK().initRequestCompleteListener(new PosAgent.PosAgentRequestListener() {

            @Override
            public void requestComplete(PosAgentRequest request) {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.obj = request;
                handler.sendMessage(message);
                EventBus.getDefault().post(new EventMessage(Restaurant.EVENT_REQUEST_COMPLETE));

            }
        });
    }

    public void disPresentation() {
        displayView(presentationFragment);
    }

    private void initKProgress() {
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("请稍候...")
                .setCancellable(false)
                .setDimAmount((float) 0.5);
    }

    public void displayView(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sliding_experience_immediately:
                SanyiCloudRequests.getXPShopListRequest(new _GetXPShopListRequest.IGetShopListListener() {
                    @Override
                    public void onSuccess(ShopList resp) {
                        if (logingFragment == null) {
                            logingFragment = new LogingFragment();
                        }
                        logingFragment.setShops(resp);
                        displayView(logingFragment);
                    }


                    @Override
                    public void onFail(String error) {

                    }
                });
                break;
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_PERMS = 2;
    private static final int MY_GO_TO_MAINACTIVITY = 4;
}
