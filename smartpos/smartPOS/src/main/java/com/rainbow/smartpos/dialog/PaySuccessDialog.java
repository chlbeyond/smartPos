package com.rainbow.smartpos.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.android.sdk.androidUtil.CacheUtils;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 下载apk对话框
 *
 * @author ming.cheng
 * @date 2014/11/14
 */
public class PaySuccessDialog extends Dialog {

    public static interface IPaySuccessListener {
        public void onClose();

        public void onComfirm();

    }


    private TextView dialogPaySuccessTitleTv;
    private ImageView dialogPaySuccessCloseIv;
    private TextView dialogPaySuccessContentTv;
    private TextView dialogPaySuccessConfirmTv;

    private IPaySuccessListener listener;

    private Context mContext;

    private int progress;
    private boolean cancelUpdate;
    private String content="";

    public PaySuccessDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public PaySuccessDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    public PaySuccessDialog(Context context,String content, IPaySuccessListener listener) {
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
        setContentView(R.layout.pay_success_dialog);

        dialogPaySuccessTitleTv = (TextView) findViewById(R.id.dialog_pay_success_title);
        dialogPaySuccessCloseIv = (ImageView) findViewById(R.id.dialog_pay_success_close);
        dialogPaySuccessContentTv = (TextView) findViewById(R.id.dialog_pay_success_content);
        dialogPaySuccessConfirmTv = (TextView) findViewById(R.id.dialog_pay_success_confirm);

        dialogPaySuccessContentTv.setText(content);

        dialogPaySuccessCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onComfirm();
            }
        });

        dialogPaySuccessConfirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onComfirm();
            }
        });

    }

    @Override
    public void show() {
        super.show();

        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Toast.makeText(mContext, "请等待操作结束", Toast.LENGTH_LONG).show();
    }


}
