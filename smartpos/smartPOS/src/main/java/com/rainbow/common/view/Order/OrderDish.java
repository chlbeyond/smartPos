package com.rainbow.common.view.Order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.order.OrderDishItemFragment;
import com.rainbow.smartpos.order.OrderFragment;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.rest.GoodsGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/1/21.
 */
public class OrderDish {
    public Context mContext;
    public View mView;
//    public ProhibitViewPager mViewPager;
//    public CategoryPagerAdapter mPagerAdapter;
//    public SlidingTabLayout mSlidingTabLayout;
    public FragmentManager mFragmentManager;
//    public ArrayList<OrderDishItemFragment> fragments = new ArrayList<OrderDishItemFragment>();
//    public RelativeLayout linearLayout_KITKAT;

    private Button dishTypes[] = null;
    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
    TableRow tableRowSubCatetory = null;
    OrderDishItemFragment dishItemFragment = null;
//    Button curDishType = null;
    int currDishTypeIndex = -1;
    int scrollBeginIndex = 0;//菜品大类窗口显示范围的起始偏移量
    HorizontalScrollView scrollView = null;

    public OrderFragment orderFragment;
    private OrderDishItemFragment.OnClickDish clickDishListener;

    public OrderDish(Context context, FragmentManager fragmentManager, OrderDishItemFragment.OnClickDish listener) {
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        this.clickDishListener = listener;
    }

//    private int getViewWidth(View v)
//    {
//        return v.getPaddingRight() + v.getWidth() + v.getPaddingLeft();
//    }
//    //计算菜品大类按钮的起始位置
//    private int getDishTypeButtonOffset(int index)
//    {
//        if(index < 0 || index > dishTypes.length - 1) return 0;
//        int offset = 0;
//        for(int i = 0; i < index; ++i)
//            offset += getViewWidth(dishTypes[i]);//dishTypes[i].getWidth() + dishTypes[i].getPaddingLeft() + dishTypes[i].getPaddingRight();
//        return offset;
//    }

    private void adjustDishTypeBtn()
    {
        adjustDishTypeBtn(currDishTypeIndex);
    }

    private void adjustDishTypeBtn(int i) {
//        System.out.println("scroll width = " + tableRowSubCatetory.getWidth());
//        System.out.println("scrollview width = " + scrollView.getWidth());
//        System.out.println("selected item offset = " + getDishTypeButtonOffset(index) + "  item width = " + dishTypes[index].getWidth());
//        if(getDishTypeButtonOffset(index) < scrollBeginIndex) {//隐藏在左侧
//            scrollBeginIndex = Math.max(0, getDishTypeButtonOffset(index));
//            System.out.println("left = "+scrollBeginIndex);
//        } else if(getDishTypeButtonOffset(index + 1) - scrollBeginIndex > scrollView.getWidth()) { //隐藏在右侧
//            scrollBeginIndex += getDishTypeButtonOffset(index + 1) - scrollView.getWidth();
//            System.out.println("right = "+scrollBeginIndex);
//        }
//        scrollView.scrollTo(scrollBeginIndex , 0);
//        System.out.println("right = "+dishTypes[i].getRight());
        if(dishTypes[i].getRight() > scrollView.getWidth()) {
            scrollView.scrollTo(dishTypes[i].getRight() + dishTypes[i].getPaddingRight() - scrollView.getWidth(), 0);
        } else if(dishTypes[i].getLeft() < scrollView.getScrollX()) {
            scrollView.scrollTo(dishTypes[i].getLeft() - dishTypes[i].getPaddingLeft(), 0);
        }
    }

    private void initDishTypes()
    {
        if(!SanyiSDK.rest.getGoodsGroup().isEmpty()) {
            if(tableRowSubCatetory != null && tableRowSubCatetory.getChildCount() > 0)
                tableRowSubCatetory.removeAllViews();
            dishTypes = new Button[SanyiSDK.rest.getGoodsGroup().size()];
            for (int j = 0; j < dishTypes.length; ++j) {

                final GoodsGroup dishType = SanyiSDK.rest.getGoodsGroup().get(j);
                String dishTypeName = dishType.name;
                Button btn1 = new Button(mView.getContext());
                dishTypes[j] = btn1;
                btn1.setText(dishTypeName);
                btn1.setTextSize(mView.getContext().getResources().getDimensionPixelOffset(R.dimen.table_status_bottom_text));
                btn1.setBackgroundResource(R.drawable.subcat_trip_button_selector);
                btn1.setTextColor(Color.WHITE);
                btn1.setPadding(20, 10, 20, 10);
                params.setMargins(5, 10, 5, 10);

                btn1.setLayoutParams(params);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickDishType(((Integer)v.getTag()).intValue());
                    }
                });
                btn1.setId(R.id.tableRowCategory);
                btn1.setGravity(Gravity.CENTER);
                btn1.setTag(j);

                tableRowSubCatetory.addView(btn1);
            }

            currDishTypeIndex = -1;
        }
    }

    public View initView() {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_order_dish_module, null);
//        linearLayout_KITKAT = (RelativeLayout) mView.findViewById(R.id.linearLayout_order_dish_4_4);
//        mViewPager = (ProhibitViewPager) mView.findViewById(R.id.order_dish_category_viewpager);
//        mPagerAdapter = new CategoryPagerAdapter(mFragmentManager);
//        mViewPager.setAdapter(mPagerAdapter);
//        mViewPager.setNoScroll(false);
//        mSlidingTabLayout = (SlidingTabLayout) mView.findViewById(R.id.cateogry_sliding_tabs);
//        mSlidingTabLayout.setViewPager(mViewPager);

        tableRowSubCatetory = (TableRow) mView.findViewById(R.id.tableRowCategory);
        tableRowSubCatetory.setFocusable(false);
        tableRowSubCatetory.setFocusableInTouchMode(false);
        scrollView = (HorizontalScrollView) mView.findViewById(R.id.order_horizontalScrollView);
        initDishTypes();

        if(dishItemFragment == null) {
            dishItemFragment = new OrderDishItemFragment();
//            dishItemFragment.setDishTypePos(currDishTypeIndex);
            dishItemFragment.setClickDishListener(clickDishListener);
            dishItemFragment.setFlingListener(new OrderDishItemFragment.FlingListener() {
                @Override
                public void onLeft() {
                    if(currDishTypeIndex > 0) {
                        clickDishType(currDishTypeIndex - 1);
                        adjustDishTypeBtn();
                    }
                }

                @Override
                public void onRight() {
                    if(currDishTypeIndex < dishTypes.length - 1) {
                        clickDishType(currDishTypeIndex + 1);
                        adjustDishTypeBtn();
                    }
                }
            });
        }
        if(dishTypes.length > 0) {
            if(scrollView!=null)
                scrollView.smoothScrollTo(0,0);
            clickDishType(0);
        }

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.dishFragmentContainer, dishItemFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        return mView;
    }

    public void clickDishType(int v) {
        if(v < 0) return;
        if(currDishTypeIndex != v) {
            if(v==0&&scrollView!=null)
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0,0);
                    }
                }, 200);//暂停200毫秒让scrollview滚动到最前面
            if (currDishTypeIndex >= 0)
                dishTypes[currDishTypeIndex].setSelected(false);
            dishTypes[v].setSelected(true);
            currDishTypeIndex = v;
            dishItemFragment.setDishTypePos(v);
        }
    }

    public void notifyDataChanged() {

//        if (mPagerAdapter != null) {
//            mPagerAdapter.refreshDish();
//
//        }
//        initDishTypes();
//        if(dishTypes != null && dishTypes.length > 0)
//            clickDishType(0);
        dishItemFragment.refreshDishState();
    }

    public void initLayout() {

//        mViewPager.setCurrentItem(0);
        if(dishTypes != null && dishTypes.length > 0)
            if(scrollView!=null)
                scrollView.smoothScrollTo(0,0);
            clickDishType(0);
    }

//    private class CategoryPagerAdapter extends FragmentPagerAdapter {
//
//        public List<String> categoryName = new ArrayList<String>();
//
//        public CategoryPagerAdapter(FragmentManager fm) {
//
//            super(fm);
//            initFragments();
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//
//        public void initFragments() {
//            fragments.clear();
//            for (int i = 0; i < SanyiSDK.rest.getGoodsGroup().size(); ++i) {
//                GoodsGroup goodsGroup = SanyiSDK.rest.getGoodsGroup().get(i);
////                com.rainbow.smartpos.util.Logger.i("info", goodsGroup.getName());
//                categoryName.add(goodsGroup.name);
//                OrderDishItemFragment f = new OrderDishItemFragment();
//                f.setClickDishListener(clickDishListener);
//                Bundle args = new Bundle();
//                args.putInt("number", i);
//                f.setArguments(args);
//                fragments.add(f);
//            }
//        }
//
//        /**
//         * @return the number of pages to display
//         */
//        @Override
//        public int getCount() {
//            return fragments.size();
////            return SanyiSDK.rest.getGoodsGroup().size();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return categoryName.get(position);
//        }
//
//        @Override
//        public int getItemPosition(Object object) {
//            // return POSITION_NONE;
//            return POSITION_UNCHANGED;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//        }
//
//
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            // TODO Auto-generated method stub
//            return super.instantiateItem(container, position);
//        }
//
////        @Override
////        public OrderDishItemFragment getItem(int index) {
////            // return OrderDishItemFragment.newInstance(index);
////            return fragments.get(index);
////        }
//
//        public void refreshDish() {
//            for (OrderDishItemFragment f : fragments) {
//                f.refreshDish();
//            }
//        }
//
//    }
//
//    public abstract class AbstractTabPagerAdapter extends PagerAdapter {
//
//        private final String TAG = AbstractTabPagerAdapter.class.getCanonicalName();
//
//        private final FragmentManager mFragmentManager;
//
//        private FragmentTransaction mCurTransaction;
//
//        private Fragment mCurrentPrimaryItem = null;
//
//        public AbstractTabPagerAdapter(FragmentManager fragmentManager) {
//            mFragmentManager = fragmentManager;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            if (mCurTransaction == null) {
//                throw new IllegalArgumentException("current transaction must not be null");
//            }
//            String fragmentTag = makeFragmentName(container.getId(), position);
//            Fragment fragment = (Fragment) mFragmentManager.findFragmentByTag(fragmentTag);
//            if (fragment != null) {
//                mCurTransaction.attach(fragment);
//                Log.d(TAG, "Attaching existing fragment " + fragment + " at position " + position);
//                //mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), position));
//            } else {
//                fragment = getItem(position);
//                mCurTransaction.add(container.getId(), fragment, fragmentTag);
//                Log.d(TAG, "Attaching new fragment " + fragment + " at position " + position);
//            }
//
//            if (fragment != mCurrentPrimaryItem) {
//                fragment.setMenuVisibility(false);
//                //fragment.setUserVisibleHint(false);
//            }
//
//            return fragment;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            if (mCurTransaction == null) {
//                throw new IllegalArgumentException("current transaction must not be null");
//            }
//            mCurTransaction.detach((Fragment) object);
//            //mCurTransaction.remove((Fragment)object);
//        }
//
//        @Override
//        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            //super.setPrimaryItem(container, position, object);
//            Fragment fragment = (Fragment) object;
//            if (fragment != mCurrentPrimaryItem) {
//                Log.d(TAG, "set Primary item " + position + " to " + fragment);
//                if (mCurrentPrimaryItem != null) {
//                    mCurrentPrimaryItem.setMenuVisibility(false);
//                    // this command unexpectedly changes the state of the fragment which leads to a warning message and possible some strange behaviour
//                    //mCurrentPrimaryItem.setUserVisibleHint(false);
//                }
//                if (fragment != null) {
//                    fragment.setMenuVisibility(true);
//                    //fragment.setUserVisibleHint(true);
//                }
//                mCurrentPrimaryItem = fragment;
//            }
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object fragment) {
//            return ((Fragment) fragment).getView() == view;
//        }
//
//        public abstract Fragment getItem(int position);
//
//        @Override
//        public void startUpdate(ViewGroup container) {
//            super.startUpdate(container);
//            if (mCurTransaction != null) {
//                throw new IllegalArgumentException("current transaction must not be null");
//            }
//            mCurTransaction = mFragmentManager.beginTransaction();
//            Log.d(TAG, "FragmentTransaction started");
//        }
//
//        @Override
//        public void finishUpdate(ViewGroup container) {
//            if (mCurTransaction != null) {
//                mCurTransaction.commit();
//                mCurTransaction = null;
//                //mFragmentManager.executePendingTransactions();
//                Log.d(TAG, "FragmentTransaction committed");
//            } else {
//                throw new IllegalArgumentException("current transaction must not be null");
//            }
//        }
//
//        private String makeFragmentName(int viewId, int position) {
//            if (viewId <= 0)
//                throw new IllegalArgumentException("viewId " + viewId);
//            return "tabpageradptr:" + getPageTitle(position) + ":" + viewId + ":" + position;
//        }
//
//    }

}
