package com.qrcode.decode;

import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

import com.qrcode.activity.CaptureActivity;

/**
 * 作�?: 陈涛(1076559197@qq.com)
 * <p>
 * 时间: 2014�?�?�?下午12:24:34
 * <p>
 * 版本: V_1.0.0
 * <p>
 * 描述: 解码线程
 */
final class DecodeThread extends Thread {

    CaptureActivity activity;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(CaptureActivity activity) {
        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(activity);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}