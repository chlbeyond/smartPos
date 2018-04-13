package com.rainbow.smartpos.presentation;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rainbow.common.view.AutoViewpager.AutoScrollViewPager;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.CheckDetailListAdapter;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.util.MimeTypeUtil;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.utils.JsonUtil;
import com.sanyipos.sdk.utils.OrderUtil;
import com.socks.library.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import easyvideoplayer.EasyVideoCallback;
import easyvideoplayer.EasyVideoPlayer;

/**
 * Created by ss on 2016/4/26.
 */
public class DisplayPresentation extends Presentation implements EasyVideoCallback {
    public static final String TAG = "DisplayPresentation";
    public Context mContext;
    public CheckDetailListAdapter listAdapter;
    public static final int EX_FILE_PICKER_RESULT = 0;
    public ListView mListView;
    public TextView textViewBillRealV;
    public TextView textViewBillAmount;
    public TextView textViewBillPromotion;
    public TextView textViewOrderAmount;
    public TextView textViewCompany;

    public LinearLayout linearLayoutBillAmount;
    public LinearLayout linearLayoutOrderAmount;


    public List<File> mVideofiles = new ArrayList<>();
    public List<File> mPicFiles = new ArrayList<>();
    public int videoIndex = 0;
    public LinearLayout mLinearLayout;
    public AutoScrollViewPager mAutoViewPager;
    public PercentRelativeLayout mPercentRelativeLayout;
    public EasyVideoPlayer mEasyVideoPlayer;
    public NormalDialog dialog;

    public DisplayPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        this.mContext = outerContext;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_presentation);
        textViewBillRealV = (TextView) findViewById(R.id.bill_real);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_presentation_order_total);
        mPercentRelativeLayout = (PercentRelativeLayout) findViewById(R.id.percentRelativeLayout);
        mAutoViewPager = (AutoScrollViewPager) findViewById(R.id.autoScrollViewPager);
        mEasyVideoPlayer = (EasyVideoPlayer) findViewById(R.id.easyViewPlayer);
        mListView = (ListView) findViewById(R.id.listView);
        textViewCompany = (TextView) findViewById(R.id.textView_company);
        textViewBillAmount = (TextView) findViewById(R.id.bill_amount);
        textViewBillPromotion = (TextView) findViewById(R.id.bill_promotion);
        textViewOrderAmount = (TextView) findViewById(R.id.order_amount);
        linearLayoutBillAmount = (LinearLayout) findViewById(R.id.linearLayout_bill_amount);
        linearLayoutOrderAmount = (LinearLayout) findViewById(R.id.linearLayout_order_amount);
        textViewCompany.setText(mContext.getString(R.string.company_name));
        initFragment();
    }

    public void initAmountView(int mode) {
        if (mode == MainScreenActivity.ORDER_FRAGMENT) {
            linearLayoutOrderAmount.setVisibility(View.VISIBLE);
            linearLayoutBillAmount.setVisibility(View.GONE);
        } else {
            linearLayoutBillAmount.setVisibility(View.VISIBLE);
            linearLayoutOrderAmount.setVisibility(View.GONE);
        }
    }

    public void initFragment() {
        initMediaPath();
        mPercentRelativeLayout.setVisibility(View.GONE);
        if (!mVideofiles.isEmpty()) {
            mAutoViewPager.setVisibility(View.GONE);
            mEasyVideoPlayer.setVisibility(View.VISIBLE);
            playVideo();
            return;

        }
        if (!mPicFiles.isEmpty()) {
            mEasyVideoPlayer.setVisibility(View.GONE);
            mAutoViewPager.setVisibility(View.VISIBLE);
            mAutoViewPager.setAdapter(new ViewPagerAdapter(paseFileToImage()));
            mAutoViewPager.startAutoScroll();
            return;

        }
        mPercentRelativeLayout.setVisibility(View.VISIBLE);
    }

    private void initMediaPath() {
        String data = SharePreferenceUtil.getPreference(mContext, SmartPosPrivateKey.ST_LOCAL_PRESENTATION, "");
        FilePathData clipData = JsonUtil.fromJson(data, FilePathData.class);
        if (clipData != null) {
            for (int i = 0; i < clipData.filePath.size(); i++) {
                String uri = clipData.filePath.get(i);
                File file = new File(uri);
                for (File cfile : file.listFiles()) {
                    String type = MimeTypeUtil.getMimeType(cfile);
                    if (type != null) {
                        if (type.contains("image")) {
                            mPicFiles.add(cfile);
                        }
                        if (type.contains("video")) {
                            mVideofiles.add(cfile);
                        }
                    }
                }
            }
        }
    }

    public void showOrderTotal(boolean isSow) {
        mLinearLayout.setVisibility(isSow ? View.VISIBLE : View.GONE);
    }

    public void refresh(String price) {
        listAdapter.notifyDataSetChanged();
        textViewOrderAmount.setText(price);
    }


    public void refresh(CashierResult cashierResult) {
        listAdapter.notifyDataSetChanged();
        textViewBillRealV.setText(OrderUtil.dishPriceFormatter.format(cashierResult.bill.realValue));
        textViewBillAmount.setText(OrderUtil.dishPriceFormatter.format(cashierResult.bill.amount));
        textViewBillPromotion.setText(OrderUtil.dishPriceFormatter.format(cashierResult.bill.promotion + cashierResult.bill.discount));
    }

    public void setListViewAdapter(CheckDetailListAdapter adapter) {
        listAdapter = adapter;
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {
        KLog.d(TAG, "onPreparing");

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        KLog.d(TAG, "onPrepared");

    }

    @Override
    public void onBuffering(int percent) {

        KLog.d(TAG, "onBuffering" + percent);

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

        KLog.d(TAG, "onError");

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

        KLog.d(TAG, "onCompletion");

        playVideo();


    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

        KLog.d(TAG, "onRetry");

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

        KLog.d(TAG, "onSubmit");

    }

    public void playVideo() {

        mEasyVideoPlayer.setAutoPlay(true);
        mEasyVideoPlayer.setCallback(this);
        mEasyVideoPlayer.setSource(Uri.fromFile(mVideofiles.get(videoIndex++)));
    }

    public void showQRCode(Context context, String url) {
        dialog = new NormalDialog(context);
        dialog.title("微信支付");
        dialog.widthScale((float) 0.5);
        dialog.heightScale((float) 0.5);
        dialog.isHasConfirm(false);
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundColor(Color.WHITE);
        Glide.with(getContext()).load(url).into(imageView);
        dialog.content(imageView);
        dialog.show();
    }

    public void hideDialog() {
        if (dialog != null)
            if (dialog.isShowing()) dialog.dismiss();
    }

    public class ViewPagerAdapter extends PagerAdapter {
        public List<ImageView> list;

        public ViewPagerAdapter(List<ImageView> imageViews) {
            super();
            list = imageViews;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(list.get(position));
            return list.get(position);

        }

    }

    public List<ImageView> paseFileToImage() {
        List<ImageView> imageViews = new ArrayList<>();
        int size = 0;
        if (mPicFiles.size() <= 5) {
            size = mPicFiles.size();
        } else {
            size = 5;
        }
        for (int i = 0; i < size; i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(mPicFiles.get(i).getPath());
            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(bitmap);
            imageViews.add(imageView);
        }
        return imageViews;
    }

    public void scollListView(int selection) {
        mListView.setSelection(selection);
    }
}

