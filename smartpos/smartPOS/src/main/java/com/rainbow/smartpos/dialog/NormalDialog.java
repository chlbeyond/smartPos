package com.rainbow.smartpos.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;

import butterknife.ButterKnife;


@SuppressWarnings("deprecation")
public class NormalDialog extends BaseAlertDialog<NormalDialog> {
    /**
     * horizontal line above btns
     */
    private View mVLineHorizontal;

    /**
     * btn divider line color(对话框之间的分割线颜色(水平+垂直))
     */
    private int mDividerColor = Color.parseColor("#DCDCDC");

    public static final int STYLE_ONE = 0;
    public static final int STYLE_TWO = 1;
    private int mStyle = STYLE_ONE;

    private boolean isNegativeButton = false; //是否显示“取消”按钮

    public NormalDialog(Context context) {
        super(context);

        mTitleTextSize = 22f;
        mContentTextColor = Color.parseColor("#383838");
        mContentTextSize = 17f;
        /** default value*/
    }

    TextView mTextTitle;
    ImageView mImageCancel;
    Button mButtonBottom;
    Button mNegativeButton;
    public INormailDialogListener mListener;

    public static interface INormailDialogListener {
        void onClickConfirm();
    }

    public void setNormalListener(INormailDialogListener listener) {
        this.mListener = listener;
    }

    public void setNegativeButton(boolean show)
    {
        isNegativeButton = show;
    }

    public void setPositiveButtonText(String text)
    {
        mBottom = text;
    }

    @Override
    public View onCreateView() {
        /** title */
        mTitleView = LayoutInflater.from(mContext).inflate(R.layout.dialog_title_view, null);
        mTextTitle = ButterKnife.findById(mTitleView, R.id.textView_dialog_title);
        mTextTitle.setText(TextUtils.isEmpty(mTitle) ? "提示" : mTitle);

        mImageCancel = ButterKnife.findById(mTitleView, R.id.imageView_dialog_cancel);
        mImageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                superDismiss();
            }
        });
        mLlContainer.addView(mTitleView);

        /** content */
        if (mContentIsView) {
            mTvContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            mLlContainer.addView(mTvContent);
        } else {
            mTxContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mTxContent.setBackgroundColor(Color.parseColor("#ffffff"));
            mLlContainer.addView(mTxContent);
        }
        mVLineHorizontal = new View(mContext);
        mVLineHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        mLlContainer.addView(mVLineHorizontal);
        if (mIsHasConfirm) {
            mBottomView = LayoutInflater.from(mContext).inflate(R.layout.dialog_btn_view, null);
            mButtonBottom = ButterKnife.findById(mBottomView, R.id.button_dialog_bottom);
            if (mBottom != null)
                mButtonBottom.setText(mBottom);
            mButtonBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.onClickConfirm();
                }
            });
            mLlContainer.addView(mBottomView);
        }
        if(mBottomView == null) mBottomView = LayoutInflater.from(mContext).inflate(R.layout.dialog_btn_view, null);
        mNegativeButton = ButterKnife.findById(mBottomView, R.id.cancelButton_dialog_bottom);
        if (!isNegativeButton) {
            mNegativeButton.setVisibility(View.GONE);
            ButterKnife.findById(mBottomView, R.id.middle_space).setVisibility(View.GONE);
        } else {
            mNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    superDismiss();
                }
            });
        }
        mLlContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return mLlContainer;
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();

        /** content */
        if (mStyle == STYLE_ONE) {
            mTxContent.setPadding(dp2px(15), dp2px(10), dp2px(15), dp2px(10));
            mTxContent.setMinHeight(dp2px(68));
            mTxContent.setGravity(mContentGravity);
        } else if (mStyle == STYLE_TWO) {
            mTvContent.setPadding(dp2px(15), dp2px(7), dp2px(15), dp2px(20));
            mTxContent.setMinHeight(dp2px(56));
            mTxContent.setGravity(Gravity.CENTER);
        }

        /** btns */
        mVLineHorizontal.setBackgroundColor(mDividerColor);

        /**set background color and corner radius */
        float radius = dp2px(mCornerRadius);
        float[] topRadii = {radius, radius, radius, radius, 0, 0, 0, 0};
        mTitleView.setBackgroundDrawable(CornerUtils.cornerDrawable(mBgColor, topRadii));
        float[] bottomRadii = {0, 0, 0, 0, radius, radius, radius, radius};
        if (mIsHasConfirm) {
            mBottomView.setBackgroundDrawable(CornerUtils.cornerDrawable(mBgColor, bottomRadii));
        } else {
            if (mContentIsView) {
                mTvContent.setBackgroundDrawable(CornerUtils.cornerDrawable(mBgColor, bottomRadii));
            } else {
                mTxContent.setBackgroundDrawable(CornerUtils.cornerDrawable(mBgColor, bottomRadii));
            }
        }
    }

    // --->属性设置

    /**
     * set style(设置style)
     */
    public NormalDialog style(int style) {
        this.mStyle = style;
        return this;
    }

    /**
     * set divider color between btns(设置btn分割线的颜色)
     */
    public NormalDialog dividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        return this;
    }


}
