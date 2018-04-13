package com.qrcode.decode;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * 作�?: 陈涛(1076559197@qq.com)
 * <p>
 * 时间: 2014�?�?�?下午12:24:51
 * <p>
 * 版本: V_1.0.0
 */
public final class FinishListener
        implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

    private final Activity activityToFinish;

    public FinishListener(Activity activityToFinish) {
        this.activityToFinish = activityToFinish;
    }

    public void onCancel(DialogInterface dialogInterface) {
        run();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        run();
    }

    public void run() {
        activityToFinish.finish();
    }

}