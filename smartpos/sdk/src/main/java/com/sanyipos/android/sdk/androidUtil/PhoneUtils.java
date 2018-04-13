package com.sanyipos.android.sdk.androidUtil;

import android.content.Context;

public class PhoneUtils {
	
	 /** 
     * ����ֻ�ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * ����ֻ�ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    
    /**
     * 
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue){
    	return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    /**
     * 
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue){
    	return (int) (spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }
}
