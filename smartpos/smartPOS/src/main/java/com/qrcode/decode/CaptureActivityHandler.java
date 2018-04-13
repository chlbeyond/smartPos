package com.qrcode.decode;

import android.os.Handler;
import android.os.Message;

import com.qrcode.activity.CaptureActivity;
import com.qrcode.camera.CameraManager;

/**
 * 作�?: 陈涛(1076559197@qq.com)
 * <p>
 * 时间: 2014�?�?�?下午12:23:32
 * <p>
 * 版本: V_1.0.0
 * <p>
 * 描述: 扫描消息转发
 */
public final class CaptureActivityHandler extends Handler {

    DecodeThread decodeThread = null;
    CaptureActivity activity = null;
    private State state;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureActivityHandler(CaptureActivity activity) {
        this.activity = activity;
        decodeThread = new DecodeThread(activity);
        decodeThread.start();
        state = State.SUCCESS;
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {

        switch (message.what) {
            case Constant.auto_focus:
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, Constant.auto_focus);
                }
                break;
            case Constant.restart_preview:
                restartPreviewAndDecode();
                break;
            case Constant.decode_succeeded:
                state = State.SUCCESS;
                activity.handleDecode((String) message.obj);// 解析成功，回�?			break;

            case Constant.decode_failed:
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
                        Constant.decode);
                break;
        }

    }

    public void quitSynchronously() {
        state = State.DONE;
        CameraManager.get().stopPreview();
        removeMessages(Constant.decode_succeeded);
        removeMessages(Constant.decode_failed);
        removeMessages(Constant.decode);
        removeMessages(Constant.auto_focus);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
                    Constant.decode);
            CameraManager.get().requestAutoFocus(this, Constant.auto_focus);
        }
    }

}
