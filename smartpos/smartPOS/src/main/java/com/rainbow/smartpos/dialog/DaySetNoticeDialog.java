package com.rainbow.smartpos.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;

/**
 * 下载apk对话框
 *
 * @author ming.cheng
 * @date 2014/11/14
 */
public class DaySetNoticeDialog extends Dialog {

    public static interface IPaySuccessListener {

        public void onComfirm();

    }


    private TextView dialogPaySuccessTitleTv;
    private ImageView dialogPaySuccessCloseIv;
    private TextView dialogPaySuccessContentTv;
    private TextView dialogPaySuccessConfirmTv;
    private TextView dialogPaySuccessCancelTv;

    private IPaySuccessListener listener;

    private Context mContext;

    private int progress;
    private boolean cancelUpdate;
    private String content="";

    public DaySetNoticeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public DaySetNoticeDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    public DaySetNoticeDialog(Context context, String content, IPaySuccessListener listener) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        this.listener = listener;
        this.content=content;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_set_notice_dialog);

        dialogPaySuccessTitleTv = (TextView) findViewById(R.id.dialog_pay_success_title);
        dialogPaySuccessCloseIv = (ImageView) findViewById(R.id.dialog_pay_success_close);
        dialogPaySuccessContentTv = (TextView) findViewById(R.id.dialog_pay_success_content);
        dialogPaySuccessConfirmTv = (TextView) findViewById(R.id.dialog_pay_success_confirm);
        dialogPaySuccessCancelTv = (TextView) findViewById(R.id.dialog_pay_success_cancel);

        dialogPaySuccessContentTv.setText(content);

        dialogPaySuccessCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialogPaySuccessConfirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onComfirm();
            }
        });
        dialogPaySuccessCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void show() {
        super.show();

        setCanceledOnTouchOutside(false);
    }



}
