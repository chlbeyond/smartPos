package com.rainbow.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;
/**
 * radiobutton文字和图片同时居中
 * @author zz
 *
 */
public class MDMRadioButton extends RadioButton {  
	  
    public MDMRadioButton(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        // TODO Auto-generated constructor stub  
    }  
  
    public MDMRadioButton(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
    }  
  
    public MDMRadioButton(Context context) {  
        super(context);  
        // TODO Auto-generated constructor stub  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        //获取设置的图片  
        Drawable[] drawables = getCompoundDrawables();  
        if (drawables != null) {  
            //第一个是left  
            Drawable drawableLeft = drawables[0];  
            if (drawableLeft != null) {  
                //获取文字的宽度  
                float textWidth = getPaint().measureText(getText().toString());  
                int drawablePadding = getCompoundDrawablePadding();  
                int drawableWidth = 0;  
                drawableWidth = drawableLeft.getIntrinsicWidth();  
                float bodyWidth = textWidth + drawableWidth + drawablePadding;  
                int y = getWidth();  
                canvas.translate((getWidth() - bodyWidth) / 2, 0);  
            }  
        }  
        super.onDraw(canvas);  
    }  
}
