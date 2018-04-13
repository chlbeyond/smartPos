package com.rainbow.common.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ProhibitViewPager extends ViewPager {
    private String TAG = "ProhibitViewPager";
    private boolean noScroll = false;

    public ProhibitViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ProhibitViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setNoScroll(boolean isNoScroll) {
        this.noScroll = isNoScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        /* return false;//super.onTouchEvent(arg0); */
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {

        getParent().requestDisallowInterceptTouchEvent(true);
        if (getCurrentItem() == 0) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                    getParent().requestDisallowInterceptTouchEvent(true);

                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        return super.onInterceptTouchEvent(arg0);

    }


    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}
