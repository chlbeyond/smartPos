package com.zbar.lib;

/**
 * 作�?: 陈涛(1076559197@qq.com)
 * <p>
 * 时间: 2014�?�?�?下午12:25:46
 * <p>
 * 版本: V_1.0.0
 * <p>
 * 描述: zbar调用�?
 */
public class ZbarManager {

    static {
        System.loadLibrary("zbar");
    }

    public native String decode(byte[] data, int width, int height, boolean isCrop, int x, int y, int cwidth, int cheight);
}
