package com.rainbow.smartpos.tablestatus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by ss on 15/8/11.
 */
public class TableTagView extends CheckableTableLayout {

    private int DEFULT_LabeBackgroundColor = 0xffE91E63;
    private int DEFULT_LabeTextColor = 0xffffffff;
    private int DEFULT_LABEWIDTH = 20;
    private int DEFULT_TEXTSIZE = 17;
    private int DEFULT_DISTANCE = 20;
    private int mWidth;
    private int distance;
    private Rect mLabeRect;
    private int mLabeBackgroundColor = DEFULT_LabeBackgroundColor;
    private float _angel;
    private int labeWidth;
    private String labeText;
    private int textSize;
    private int textColor = DEFULT_LabeTextColor;
    private Paint mLabePaint;

    private boolean drawLabe = false;
    private Gravity mGravity;

    public enum Gravity {
        LEFT_TOP, RIGHT_TOP
    }

    public void setTableTag(String tableTag) {
        this.tableTag = tableTag;
    }

    public String tableTag;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mLabeRect = new Rect(0, 0, mWidth, labeWidth);
    }


    public TableTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mLabePaint = new Paint();
        mLabePaint.setTextAlign(Paint.Align.CENTER);

        mLabePaint.setAntiAlias(true);

        distance = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFULT_DISTANCE, getResources()
                        .getDisplayMetrics());
        labeWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFULT_LABEWIDTH, getResources()
                        .getDisplayMetrics());
        textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, DEFULT_TEXTSIZE, getResources()
                        .getDisplayMetrics());
        mGravity = Gravity.RIGHT_TOP;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (tableTag != null) {
            drawLabe(canvas);
        }
    }

    private void drawLabe(Canvas canvas) {
        canvas.save();
        int pointX = 0;
        int pointY = 0;
        calcAngel(mGravity);

        switch (mGravity) {
            case LEFT_TOP:
                pointX = (int) ((distance + mLabeRect.centerY()) / 1.414);
                pointY = pointX;
                break;

            default:
                pointX = (int) ((distance + mLabeRect.centerY()) / 1.414) + mWidth - labeWidth;
                pointY = (int) ((distance + mLabeRect.centerY()) / 1.414);
                break;
        }
        canvas.translate(pointX - mLabeRect.centerX(),
                pointY - mLabeRect.centerY());
        canvas.rotate(_angel, mLabeRect.centerX(), mLabeRect.centerY());

        mLabePaint.setColor(Color.parseColor("#277CCC"));

        canvas.drawRect(mLabeRect, mLabePaint);
        mLabePaint.setColor(Color.WHITE);
        mLabePaint.setTextSize(textSize);
        Paint.FontMetricsInt fontMetrics = mLabePaint.getFontMetricsInt();
        int baseline = mLabeRect.top
                + (mLabeRect.bottom - mLabeRect.top - fontMetrics.bottom + fontMetrics.top)
                / 2 - fontMetrics.top;
        mLabePaint.setTextAlign(Paint.Align.CENTER);
        canvas.rotate(-_angel, mLabeRect.centerX(), mLabeRect.centerY());
        canvas.drawText(tableTag, mLabeRect.centerX() - labeWidth / 2, baseline / 2, mLabePaint);
        canvas.restore();
    }


    public void setLabeBackgroundColor(int color) {
        mLabeBackgroundColor = color;

    }

    public void setLabeBackgroundResColor(int resid) {
        mLabeBackgroundColor = getResources().getColor(resid);

    }

    public void setLabeText(String text) {
        labeText = text;

    }

    public void setLabeResText(int resid) {
        labeText = getResources().getString(resid);

    }

    public void setLabeGravity(Gravity gravity) {
        mGravity = gravity;

    }

    public void setTextColor(int color) {
        textColor = color;
    }

    /**
     * @param distance dp
     * @return
     */
    public void setDistance(int distance) {
        this.distance = dip2Px(distance);
    }

    public void setTextResColor(int resid) {
        textColor = getResources().getColor(resid);

    }

    /**
     * @param width dp
     * @return
     */
    public void setLabeWidth(int width) {
        labeWidth = dip2Px(width);
    }

    public void build() {
        drawLabe = true;
        invalidate();
    }

    private void calcAngel(Gravity gravity) {
        switch (gravity) {
            case LEFT_TOP:
                _angel = -45;
                break;

            default:
                _angel = 45;
                break;
        }

    }

    private int dip2Px(float dip) {
        return (int) (dip
                * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

}
