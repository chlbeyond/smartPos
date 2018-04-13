package com.rainbow.smartpos.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BaseAlertDialog<T extends BaseAlertDialog<T>> extends BaseDialog<T> {
    /**
     * container
     */
    protected LinearLayout mLlContainer;
    //title
    /**
     * title
     */
    /**
     * title content(标题)
     */
    protected String mTitle;
    /**
     * title view
     */

    protected View mTitleView;
    /**
     * bottom text
     */
    protected String mBottom;
    /**
     * title textsize(标题字体大小,单位sp)
     */
    protected float mTitleTextSize;
    /**
     * enable title show(是否显示标题)
     */
    protected boolean mIsTitleShow = true;

    protected boolean mIsHasConfirm = true;
    //content
    /**
     * content
     */
    protected View mTvContent;
    protected TextView mTxContent;
    protected boolean mContentIsView = false;
    /**
     * content text
     */
    protected String mContent;
    /**
     * show gravity of content(正文内容显示位置)
     */
    protected int mContentGravity = Gravity.CENTER_VERTICAL;
    /**
     * content textcolor(正文字体颜色)
     */
    protected int mContentTextColor;
    /**
     * content textsize(正文字体大小)
     */
    protected float mContentTextSize;


    protected View mBottomView;

    /**
     * corner radius,dp(圆角程度,单位dp)
     */
    protected float mCornerRadius = 3;
    /**
     * background color(背景颜色)
     */
    protected int mBgColor = Color.parseColor("#EEEEEE");

    /**
     * method execute order:
     * show:constrouctor---show---oncreate---onStart---onAttachToWindow
     * dismiss:dismiss---onDetachedFromWindow---onStop
     *
     * @param context
     */
    public BaseAlertDialog(Context context) {
        super(context);
        widthScale(0.88f);

        mLlContainer = new LinearLayout(context);
        mLlContainer.setOrientation(LinearLayout.VERTICAL);

        /** content */
        mTvContent = new View(context);

        /** context text */
        mTxContent = new TextView(context);


    }

    @Override
    public void setUiBeforShow() {

        /** content */
        mTxContent.setGravity(mContentGravity);
        mTxContent.setText(mContent);
        mTxContent.setTextColor(mContentTextColor);
        mTxContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContentTextSize);
        mTxContent.setLineSpacing(0, 1.3f);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        setUiBeforShow();

        int width;
        if (mWidthScale == 0) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            width = (int) (mDisplayMetrics.widthPixels * mWidthScale);
        }

        int height;
        if (mHeightScale == 0) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (mHeightScale == 1) {
            height = ViewGroup.LayoutParams.MATCH_PARENT;
            height = (int) mMaxHeight;
        } else {
            height = (int) (mMaxHeight * mHeightScale);
        }

        mTvContent.setLayoutParams(new LinearLayout.LayoutParams(width, height));
    }

    public T content(View view) {
        mTvContent = view;
        mContentIsView = true;
        return (T) this;
    }


    /**
     * set title text(设置标题内容) @return MaterialDialog
     */
    public T title(String title) {
        mTitle = title;
        return (T) this;
    }

    public T bottomText (String string) {
        mBottom = string;
        return (T) this;
    }

    /**
     * set title textsize(设置标题字体大小)
     */
    public T titleTextSize(float titleTextSize_SP) {
        mTitleTextSize = titleTextSize_SP;
        return (T) this;
    }

    /**
     * enable title show(设置标题是否显示)
     */
    public T isTitleShow(boolean isTitleShow) {
        mIsTitleShow = isTitleShow;
        return (T) this;
    }

    /**
     * 是否有确定按钮
     *
     * @param isHasConfirm
     * @return
     */
    public T isHasConfirm(boolean isHasConfirm) {
        mIsHasConfirm = isHasConfirm;
        return (T) this;
    }

    /**
     * set content text(设置正文内容)
     */
    public T content(String content) {
        mContent = content;
        return (T) this;
    }

    /**
     * set content gravity(设置正文内容,显示位置)
     */
    public T contentGravity(int contentGravity) {
        mContentGravity = contentGravity;
        return (T) this;
    }

    /**
     * set content textcolor(设置正文字体颜色)
     */
    public T contentTextColor(int contentTextColor) {
        mContentTextColor = contentTextColor;
        return (T) this;
    }

    /**
     * set content textsize(设置正文字体大小,单位sp)
     */
    public T contentTextSize(float contentTextSize_SP) {
        mContentTextSize = contentTextSize_SP;
        return (T) this;
    }


    /**
     * set corner radius (设置圆角程度)
     */
    public T cornerRadius(float cornerRadius_DP) {
        mCornerRadius = cornerRadius_DP;
        return (T) this;
    }

    /**
     * set backgroud color(设置背景色)
     */
    public T bgColor(int bgColor) {
        mBgColor = bgColor;
        return (T) this;
    }

}
