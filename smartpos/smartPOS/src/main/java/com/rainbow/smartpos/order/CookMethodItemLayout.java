package com.rainbow.smartpos.order;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.rainbow.smartpos.R;

public class CookMethodItemLayout extends OrderDishDetailLayout {
	private int DEFULT_LABEWIDTH = 20;
	private int DEFULT_TEXTSIZE = 17;
	private int DEFULT_DISTANCE = 20;
	private int mWidth;
	private int mHeight;
	private int labeWidth;
	private String labeText = "√";
	private int textSize;
	private Paint mLabePaint;

	private boolean drawLabe = false;
	private Gravity mGravity;
	private boolean isMultipleSelect; // 是否多选

	public enum Gravity {
		LEFT_TOP, RIGHT_TOP, RIGHT_BOTTOM, LEFT_BOTTOM
	}

	public CookMethodItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}

	private void init() {
		mLabePaint = new Paint();
		mLabePaint.setTextAlign(Paint.Align.CENTER);

		mLabePaint.setAntiAlias(true);

		labeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFULT_LABEWIDTH, getResources().getDisplayMetrics());
		textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFULT_TEXTSIZE, getResources().getDisplayMetrics());
		mGravity = Gravity.RIGHT_BOTTOM;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		if (isMultipleSelect) {
//			drawLabe(canvas);
//		}
	}

	private void drawLabe(Canvas canvas) {
		canvas.save();
		mLabePaint.setColor(getResources().getColor(R.color.title_table_background));
		Path path = new Path();
		path.moveTo(mWidth - labeWidth, mHeight);
		path.lineTo(mWidth, mHeight - labeWidth);
		path.lineTo(mWidth, mHeight);
		path.close();
		canvas.drawPath(path, mLabePaint);
		mLabePaint.setColor(Color.parseColor("#FFFFFF"));
		mLabePaint.setTextSize(textSize);
		mLabePaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(labeText, mWidth - labeWidth / 4, mHeight - labeWidth+labeWidth/4, mLabePaint);
		canvas.restore();
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

	/**
	 * @param width
	 *            dp
	 * @return
	 */
	public void setLabeWidth(int width) {
		labeWidth = dip2Px(width);
	}

	public void build() {
		drawLabe = true;
		invalidate();
	}

	private int dip2Px(float dip) {
		return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
	}

	public boolean isMultipleSelect() {
		return isMultipleSelect;
	}

	public void setMultipleSelect(boolean isMultipleSelect) {
		this.isMultipleSelect = isMultipleSelect;
	}

}
