package com.rainbow.smartpos;

import android.app.Activity;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rainbow.smartpos.event.EventMessage;
import com.rainbow.smartpos.presentation.DisplayPresentation;
import com.zhy.autolayout.AutoLayoutActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import kprogresshud.KProgressHUD;

import static com.rainbow.smartpos.login.LoginActivity.mContext;

/**
 * Created by ss on 2016/9/5.
 */
public class BaseActivity extends AutoLayoutActivity {
    private Activity activity;
    public DisplayPresentation presentation;
    private ActionBar actionBar;
    public KProgressHUD progressHUD;


    @Subscribe
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        activity = this;
        ExitManager.getInstance().addActivity(activity);
        actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_layout);
        actionBar.getCustomView().findViewById(R.id.imageView_actionbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.primarycolor));
        initPresent();
        initProgressDialog();
        initRequestListener();
    }

    public void setCustomTitle(String title) {
        View view = actionBar.getCustomView();
        TextView textViewTitle = (TextView) view.findViewById(R.id.textView_actionbar_title);
        textViewTitle.setText(title);
    }

    public void initProgressDialog() {
        // TODO Auto-generated method stub
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("请稍候...")
                .setCancellable(false)
                .setDimAmount((float) 0.5);
    }

    private void initRequestListener() {

    }


    public void initPresent() {
        DisplayManager manager = (DisplayManager) mContext.getSystemService(DISPLAY_SERVICE);
        Display[] displays = manager.getDisplays();
        if (displays.length > 1) {
            presentation = new DisplayPresentation(activity, displays[1]);
            presentation.show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onUserEvent(EventMessage event) {
        switch (event.eventType) {
            case Restaurant.EVENT_REQUEST:

                if (progressHUD != null) progressHUD.show();
                break;
            case Restaurant.EVENT_REQUEST_COMPLETE:
                if (progressHUD != null)
                    progressHUD.dismiss();

                break;
        }
    }
}
