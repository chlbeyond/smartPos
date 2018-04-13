package com.rainbow.common.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ss on 2016/6/6.
 */
public class AutoRecyclerView extends RecyclerView {
    private int m_gridMinSpans;
    private int m_gridItemLayoutId;
    private LayoutRequester m_layoutRequester = new LayoutRequester();

    public AutoRecyclerView(Context context) {
        super(context);
    }

    public AutoRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    public void setGridLayoutManager(int orientation, int itemLayoutId, int minSpans) {
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), minSpans, orientation, false);
//        m_gridItemLayoutId = itemLayoutId;
//        m_gridMinSpans = minSpans;
//        setLayoutManager(layoutManager);
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

//        if (changed) {
//            LayoutManager layoutManager = getLayoutManager();
//            if (layoutManager instanceof GridLayoutManager) {
//                final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
//                LayoutInflater inflater = LayoutInflater.from(getContext());
//                View item = inflater.inflate(m_gridItemLayoutId, this, false);
//                int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                item.measure(measureSpec, measureSpec);
//                int itemWidth = item.getMeasuredWidth();
//                int recyclerViewWidth = getMeasuredWidth();
//                int spanCount = Math.max(m_gridMinSpans, recyclerViewWidth / itemWidth)/2;
//
//                gridLayoutManager.setSpanCount(spanCount);
//
//                // if you call requestLayout() right here, you'll get ArrayIndexOutOfBoundsException when scrolling
//                post(m_layoutRequester);
//            }
//        }
    }

    private class LayoutRequester implements Runnable {
        @Override
        public void run() {
            requestLayout();
        }
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;

            // Add top margin only for the first item to avoid double space between items
//          if(parent.getChildLayoutPosition(view) == 0)
        }
    }
}
