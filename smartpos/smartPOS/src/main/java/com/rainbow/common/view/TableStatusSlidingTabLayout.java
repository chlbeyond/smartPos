/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rainbow.common.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.tablestatus.TableAdapter;
import com.rainbow.smartpos.tablestatus.TableGroupItemFragment;
import com.rainbow.smartpos.tablestatus.TableViewPagerFragment.TableLocationPagerAdapter;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;

/**
 * To be used with ViewPager to provide a tab indicator component which give
 * constant feedback as to the user's scroll progress.
 * <p>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is
 * being used for.
 * <p>
 * The colors can be customized in two ways. The first and simplest is to
 * provide an array of colors via {@link #setSelectedIndicatorColors(int...)}
 * and {@link #setDividerColors(int...)}. The alternative is via the
 * {@link TabColorizer} interface which provides you complete control over which
 * color is used for any individual position.
 * <p>
 * The views used as tabs can be customized by calling
 * {@link #setCustomTabView(int, int)}, providing the layout ID of your custom
 * layout.
 */
public class TableStatusSlidingTabLayout extends HorizontalScrollView {

	/**
	 * Allows complete control over the colors drawn in the tab layout. Set with
	 * {@link #setCustomTabColorizer(TabColorizer)}.
	 */
	public interface TabColorizer {

		/**
		 * @return return the color of the indicator used when {@code position}
		 *         is selected.
		 */
		int getIndicatorColor(int position);

		/**
		 * @return return the color of the divider drawn to the right of
		 *         {@code position}.
		 */
		int getDividerColor(int position);

	}

	private static final int TITLE_OFFSET_DIPS = 24;
	private static final int TAB_VIEW_PADDING_DIPS = 16;
	private static final int TAB_VIEW_TEXT_SIZE_SP = 12;

	private int mTitleOffset;

	private int mTabViewLayoutId;
	private int mTabViewTextViewId;
	private int mTabViewFreeTable;

	private List<TextView> textViewFreeList;
	private List<TextView> textViewTabList;

	private TextView tabTitleView;
	private TextView tabTitleFreeTable;
	private ViewPager mViewPager;
	private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

	private final TableStatusSlidingTabStrip mTabStrip;

	public TableStatusSlidingTabLayout(Context context) {
		this(context, null);
	}

	public TableStatusSlidingTabLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TableStatusSlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// Disable the Scroll Bar
		setHorizontalScrollBarEnabled(false);
		// Make sure that the Tab Strips fills this View
		setFillViewport(true);

		mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

		mTabStrip = new TableStatusSlidingTabStrip(context);
		addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	/**
	 * Set the custom {@link TabColorizer} to be used.
	 * 
	 * If you only require simple custmisation then you can use
	 * {@link #setSelectedIndicatorColors(int...)} and
	 * {@link #setDividerColors(int...)} to achieve similar effects.
	 */
	public void setCustomTabColorizer(TabColorizer tabColorizer) {
		mTabStrip.setCustomTabColorizer(tabColorizer);
	}

	/**
	 * Sets the colors to be used for indicating the selected tab. These colors
	 * are treated as a circular array. Providing one color will mean that all
	 * tabs are indicated with the same color.
	 */
	public void setSelectedIndicatorColors(int... colors) {
		mTabStrip.setSelectedIndicatorColors(colors);
	}

	/**
	 * Sets the colors to be used for tab dividers. These colors are treated as
	 * a circular array. Providing one color will mean that all tabs are
	 * indicated with the same color.
	 */
	public void setDividerColors(int... colors) {
		mTabStrip.setDividerColors(colors);
	}

	/**
	 * Set the {@link ViewPager.OnPageChangeListener}. When using
	 * {@link TableStatusSlidingTabLayout} you are required to set any
	 * {@link ViewPager.OnPageChangeListener} through this method. This is so
	 * that the layout can update it's scroll position correctly.
	 * 
	 * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
	 */
	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		mViewPagerPageChangeListener = listener;
	}

	/**
	 * Set the custom layout to be inflated for the tab views.
	 * 
	 * @param layoutResId
	 *            Layout id to be inflated
	 * @param textViewId
	 *            id of the {@link TextView} in the inflated view
	 */
	public void setCustomTabView(int layoutResId, int textViewId, int freeTable) {
		mTabViewLayoutId = layoutResId;
		mTabViewTextViewId = textViewId;
		mTabViewFreeTable = freeTable;
	}

	/**
	 * Sets the associated view pager. Note that the assumption here is that the
	 * pager content (number of tabs and tab titles) does not change after this
	 * call has been made.
	 */
	public void setViewPager(ViewPager viewPager) {
		mTabStrip.removeAllViews();

		mViewPager = viewPager;
		if (viewPager != null) {
			viewPager.setOnPageChangeListener(new InternalViewPagerListener());
			populateTabStrip();
		}
	}

	/**
	 * Create a default view to be used for tabs. This is called if a custom tab
	 * view is not set via {@link #setCustomTabView(int, int)}.
	 */
	protected TextView createDefaultTabView(Context context) {
		TextView textView = new TextView(context);
		textView.setGravity(Gravity.CENTER);
		// textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
		// TAB_VIEW_TEXT_SIZE_SP);
		DisplayMetrics dm = new DisplayMetrics();
		textView.setTextSize(TypedValue.DENSITY_DEFAULT, 30);
		textView.setTypeface(Typeface.DEFAULT_BOLD);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// If we're running on Honeycomb or newer, then we can use the
			// Theme's
			// selectableItemBackground to ensure that the View has a pressed
			// state
			TypedValue outValue = new TypedValue();
			getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
			textView.setBackgroundResource(outValue.resourceId);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// If we're running on ICS or newer, enable all-caps to match the
			// Action Bar tab style
			textView.setAllCaps(true);
		}

		int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
		textView.setPadding(padding, 0, padding, padding);
		textView.setTextSize(30);
		return textView;
	}

	private void populateTabStrip() {
		final PagerAdapter adapter = mViewPager.getAdapter();
		final View.OnClickListener tabClickListener = new TabClickListener();
		textViewFreeList = new ArrayList<TextView>();
		textViewTabList = new ArrayList<TextView>();
		for (int i = 0; i < adapter.getCount(); i++) {
			View tabView = null;
			if (mTabViewLayoutId != 0) {
				// If there is a custom tab view layout id set, try and inflate
				// it
				tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip, false);
				tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);

				tabTitleFreeTable = (TextView) tabView.findViewById(mTabViewFreeTable);
				textViewTabList.add(tabTitleView);
				textViewFreeList.add(tabTitleFreeTable);
			}

			if (tabView == null) {
				tabView = createDefaultTabView(getContext());
			}

			if (tabTitleView == null && TextView.class.isInstance(tabView)) {
				tabTitleView = (TextView) tabView;
			}

			if (tabTitleFreeTable == null && TextView.class.isInstance(tabView)) {
				tabTitleFreeTable = (TextView) tabView;
			}

			/**
			 * 设置空台信息
			 */
			if (i == 0) {
				tabTitleFreeTable.setBackground(getResources().getDrawable(R.drawable.tablestatustabsecbac));
				tabTitleView.setTextColor(getResources().getColor(R.color.tablestutstabsel));
			}

			int availableTables = 0;
			for (int k = 0; k < SanyiSDK.rest.operationData.shopTables.size(); k++) {
				SeatEntity obj = SanyiSDK.rest.operationData.shopTables.get(k);
				if (obj != null && obj.state == TableAdapter.AVAILABLE) {
					if (i == 0) {
						availableTables++;
					} else if (SanyiSDK.rest.tableGroups.get(i - 1).id == obj.seat) {
						availableTables++;
					}
				}
			}

			tabTitleFreeTable.setText(Integer.toString(availableTables));

			tabTitleView.setText(adapter.getPageTitle(i));
			tabView.setOnClickListener(tabClickListener);
			mTabStrip.addView(tabView);
		}
	}

	private void updateTabStrip(int position) {
		final TableLocationPagerAdapter adapter = (TableLocationPagerAdapter) mViewPager.getAdapter();

		refreshFreeTable(position, adapter);

		if (position > 0) {
			refreshFreeTable(position - 1, adapter);
		}
		if (position < adapter.getCount() - 1) {
			refreshFreeTable(position + 1, adapter);
		}
	}

	private void refreshFreeTable(int position, final TableLocationPagerAdapter adapter) {
		View tabView = mTabStrip.getChildAt(position);
		TableGroupItemFragment item = (TableGroupItemFragment) adapter.getItem(position);
		if (item != null) {
			tabTitleFreeTable = (TextView) tabView.findViewById(mTabViewFreeTable);
			tabTitleFreeTable.setText(Integer.toString(getFreeTable(position)));
		}
	}

	private Integer getFreeTable(int position) {
		long tableGroup = -1;
		if (position > 0) {
			tableGroup = SanyiSDK.rest.tableGroups.get(position - 1).id;
		}
		int availabels = 0;
		for (int i = 0; i < SanyiSDK.rest.operationData.shopTables.size(); ++i) {
			SeatEntity obj = SanyiSDK.rest.operationData.shopTables.get(i);
			if (obj.state == TableAdapter.AVAILABLE && (position == 0 || obj.tableGroup == tableGroup)) {
				availabels++;
			}
		}
		return availabels;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (mViewPager != null) {
			scrollToTab(mViewPager.getCurrentItem(), 0);
		}
	}

	private void scrollToTab(int tabIndex, int positionOffset) {
		final int tabStripChildCount = mTabStrip.getChildCount();
		if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
			return;
		}

		View selectedChild = mTabStrip.getChildAt(tabIndex);
		if (selectedChild != null) {
			int targetScrollX = selectedChild.getLeft() + positionOffset;

			if (tabIndex > 0 || positionOffset > 0) {
				// If we're not at the first child and are mid-scroll, make sure
				// we obey the offset
				targetScrollX -= mTitleOffset;
			}

			scrollTo(targetScrollX, 0);
		}
	}

	private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
		private int mScrollState;

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			int tabStripChildCount = mTabStrip.getChildCount();
			if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
				return;
			}

			mTabStrip.onViewPagerPageChanged(position, positionOffset);

			View selectedTitle = mTabStrip.getChildAt(position);
			int extraOffset = (selectedTitle != null) ? (int) (positionOffset * selectedTitle.getWidth()) : 0;
			scrollToTab(position, extraOffset);

			if (mViewPagerPageChangeListener != null) {
				mViewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
			updateTabStrip(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			mScrollState = state;

			if (mViewPagerPageChangeListener != null) {
				mViewPagerPageChangeListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
				mTabStrip.onViewPagerPageChanged(position, 0f);
				scrollToTab(position, 0);
			}

			if (mViewPagerPageChangeListener != null) {
				mViewPagerPageChangeListener.onPageSelected(position);
			}

			for (int i = 0; i < mTabStrip.getChildCount(); i++) {
				if (position == i) {
					textViewFreeList.get(i).setBackground(getResources().getDrawable(R.drawable.tablestatustabsecbac));
					textViewTabList.get(i).setTextColor(getResources().getColor(R.color.tablestutstabsel));
				} else {
					textViewFreeList.get(i).setBackground(getResources().getDrawable(R.drawable.tablestatustabnorbac));
					textViewTabList.get(i).setTextColor(getResources().getColor(R.color.tablestutstabnor));
				}

			}
			updateTabStrip(position);
		}
	}

	private class TabClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			for (int i = 0; i < mTabStrip.getChildCount(); i++) {
				if (v.equals(mTabStrip.getChildAt(i))) {
					mViewPager.setCurrentItem(i);
					updateTabStrip(i);
					return;
				}
			}
		}
	}

}
