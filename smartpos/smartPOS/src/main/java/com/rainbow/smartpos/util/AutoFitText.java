package com.rainbow.smartpos.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ss on 2016/3/19.
 */
public class AutoFitText extends TextView {

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    private int textColor = Color.WHITE;
    /**
     * Minimum limit size.
     */
    private static final float MIN_TEXT_SIZE = 2f;
    /**
     * Objects for determining the size.
     */
    private Paint paint = new Paint();
    /**
     * First specified size.
     */
    private float originalFontSize = 0f;

    /**
     * Constructor.
     */
    public AutoFitText(Context c) {
        super(c);
        setLines(1);
    }

    /**
     * Constructor.
     */
    public AutoFitText(Context c, AttributeSet attrs) {
        super(c, attrs);
        setLines(1);
    }

    /**
     * Constructor.
     */
    public AutoFitText(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        setLines(1);
    }

    /**
     * onMeasure.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (originalFontSize == 0f) {
            originalFontSize = getTextSize();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        resize(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Resize!!
     */
    private void resize(int widthMeasureSpec, int heightMeasureSpec) {
        // Get the original font height.
        paint.setAntiAlias(true);
        paint.setTextSize(originalFontSize);
        Paint.FontMetrics origFm = paint.getFontMetrics();
        float origFontHaigh = origFm.bottom - origFm.top + getPaddingTop() + getPaddingBottom();

        // Get the now font size.
        float fontSize = getTextSize();

        // Get the view width.
        float contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        // Do loop if text width bigger than view width.
        while (contentWidth < getTextWidth(fontSize)) {
            // Gradually reduce the font size.
            fontSize--;

            // End the loop if font size smaller than minimum limit size.
            if (fontSize <= MIN_TEXT_SIZE) {
                fontSize = MIN_TEXT_SIZE;
                break;
            }
        }

        // Setting resized font size.
        setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

        // Setting view size.
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int measureSpecH = MeasureSpec.makeMeasureSpec((int) origFontHaigh, MeasureSpec.AT_MOST);
            setMeasuredDimension(widthMeasureSpec, measureSpecH);
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * onDraw.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // Get the font height.

        paint.setTextSize(getTextSize());
        paint.setColor(textColor);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float fontHaight = fm.bottom - fm.top;

        // Text of the draw target.
        String text = getText().toString();
        float textSize = getTextWidth(getTextSize());
        canvas.drawText(text, (getWidth() - textSize) / 2, -fm.top + ((getHeight() - fontHaight) / 2), paint);
    }


    /**
     * Get the text width from font size and text.
     *
     * @param fontSize Font size of the check target.
     * @return Text width
     */
    float getTextWidth(float textSize) {
        paint.setTextSize(textSize);
        return paint.measureText(getText().toString());
    }
}

