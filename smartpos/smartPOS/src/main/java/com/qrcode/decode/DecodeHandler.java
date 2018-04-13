package com.qrcode.decode;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.qrcode.activity.CaptureActivity;
import com.qrcode.bitmap.PlanarYUVLuminanceSource;
import com.zbar.lib.ZbarManager;

/**
 * 作�?: 陈涛(1076559197@qq.com)
 * <p>
 * 时间: 2014�?�?�?下午12:24:13
 * <p>
 * 版本: V_1.0.0
 * <p>
 * 描述: 接受消息后解�?
 */
final class DecodeHandler extends Handler {

    CaptureActivity activity = null;

    DecodeHandler(CaptureActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case Constant.decode:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case Constant.quit:
                Looper.myLooper().quit();
                break;
        }
    }

    private void decode(byte[] data, int width, int height) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;// Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        ZbarManager manager = new ZbarManager();
        String result = manager.decode(rotatedData, width, height, true, activity.getX(), activity.getY(), activity.getCropWidth(),
                activity.getCropHeight());

        if (result != null) {
            if (activity.isNeedCapture()) {
                // 生成bitmap
                PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(rotatedData, width, height, activity.getX(), activity.getY(),
                        activity.getCropWidth(), activity.getCropHeight(), false);
                int[] pixels = source.renderThumbnail();
                int w = source.getThumbnailWidth();
                int h = source.getThumbnailHeight();
                Bitmap bitmap = Bitmap.createBitmap(pixels, 0, w, w, h, Bitmap.Config.ARGB_8888);
                try {
                    String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Qrcode/";
                    File root = new File(rootPath);
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File f = new File(rootPath + "Qrcode.jpg");
                    if (f.exists()) {
                        f.delete();
                    }
                    f.createNewFile();

                    FileOutputStream out = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != activity.getHandler()) {
                Message msg = new Message();
                msg.obj = result;
                msg.what = Constant.decode_succeeded;
                activity.getHandler().sendMessage(msg);
            }
        } else {
            if (null != activity.getHandler()) {
                activity.getHandler().sendEmptyMessage(Constant.decode_failed);
            }
        }
    }

}
