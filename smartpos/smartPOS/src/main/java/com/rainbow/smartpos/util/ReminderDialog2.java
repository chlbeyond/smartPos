package com.rainbow.smartpos.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.check.MemberInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by administrator on 2017/8/29.
 */

public class ReminderDialog2 extends Dialog implements View.OnClickListener {
    public ReminderDialog2(Context context) {
        super(context);
    }

    public ReminderDialog2(FragmentActivity activity, String content,boolean can,ReminderDialog2Listener mReminderDialog2Listener) {
        super(activity);
        this.mReminderDialog2Listener = mReminderDialog2Listener;
        this.canTouch=can;
        this.activity = activity;
//        this.title=title;
        this.content=content;
    }


    public static interface ReminderDialog2Listener {
        public void cancle();

        public void confirm();
    }

    private TextView reminderDialog2CancelTv;
    private TextView reminderDialog2ConfirmTv;
//    private TextView reminderDialog2TitleTv;
//    private String title;
    private String content;
    private boolean canTouch=true;//点击外部是否可以关闭dialog
    private TextView reminderDialog2ContentTv;
    private ReminderDialog2Listener mReminderDialog2Listener;
    FragmentActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reminder2);

        Window window = getWindow();
        //window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.height = (int) MainScreenActivity.getScreenHeight()*3/10;
        params.width = (int) MainScreenActivity.getScreenWidth()*6/10;
        window.setAttributes(params);

        reminderDialog2CancelTv = (TextView) findViewById(R.id.tv_dialog_reminder2_cancel);
        reminderDialog2ConfirmTv = (TextView) findViewById(R.id.tv_dialog_reminder2_confirm);
//        reminderDialog2TitleTv= (TextView) findViewById(R.id.tv_dialog_reminder2_title);
        reminderDialog2ContentTv= (TextView) findViewById(R.id.tv_dialog_reminder2_content);

//        reminderDialog2TitleTv.setText(title);
        reminderDialog2ContentTv.setText(content);


        reminderDialog2CancelTv.setOnClickListener(this);
        reminderDialog2ConfirmTv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_dialog_reminder2_cancel:
                mReminderDialog2Listener.cancle();
                dismiss();
                break;
            case R.id.tv_dialog_reminder2_confirm:
                mReminderDialog2Listener.confirm();
                dismiss();
                break;
        }

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
        setCanceledOnTouchOutside(canTouch);
    }



}
