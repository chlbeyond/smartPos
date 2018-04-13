package com.rainbow.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * radiobutton文字和图片同时居中
 *
 * @author zz
 */
public class TDMRadioButton extends RadioButton {

    public TDMRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public TDMRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public TDMRadioButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub  
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //获取设置的图片  
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            //第一个是left  
            Drawable drawabletop = drawables[1];
            if (drawabletop != null) {
                int drawableHeight = 0;
                drawableHeight = drawabletop.getIntrinsicHeight();
                canvas.translate(0, drawableHeight / 2);
            }
        }
        super.onDraw(canvas);
    }
}
