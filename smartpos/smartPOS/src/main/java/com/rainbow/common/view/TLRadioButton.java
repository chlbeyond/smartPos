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
public class TLRadioButton extends RadioButton {

    public TLRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public TLRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public TLRadioButton(Context context) {
        super(context);  
        // TODO Auto-generated constructor stub  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {  
            Drawable drawableLeft = drawables[1];
            if (drawableLeft != null) {  
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();  
                int drawableWidth = 0;  
                drawableWidth = drawableLeft.getIntrinsicWidth();  
                float bodyWidth = textWidth + drawableWidth + drawablePadding;  
//                int y = getWidth();
//                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }  
        }  
        super.onDraw(canvas);  
    }  
}
