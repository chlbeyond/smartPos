package com.rainbow.smartpos.manage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.SmartPosApplication;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.rest.GoodsGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class AddSlodOutFragment extends Fragment {
	private View view;
	//private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;
	public CategoryPagerAdapter mPagerAdapter;
	SmartPosApplication application;
	MainScreenActivity activity;
	private RadioGroup cateogry_sliding_group;
	public int currentFragment = 0;
	public ArrayList<SlodOutDishItemFragment> _category = new ArrayList<SlodOutDishItemFragment>();
	RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_slodout_dish, container, false);
		initView();
		return view;
	}

	private void initView() {
		cateogry_sliding_group = (RadioGroup) view.findViewById(R.id.cateogry_sliding_group);
		activity = (MainScreenActivity) getActivity();
		application = (SmartPosApplication) activity.getApplication();
		
		cateogry_sliding_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				for (int i = 0; i < group.getChildCount(); i++) {
					RadioButton button = (RadioButton) group.getChildAt(i);
					if (button.getId() == checkedId) {
						currentFragment = i;
						button.setTextColor(Color.parseColor("#ffffff"));
						mViewPager.setCurrentItem(i);
						((SlodOutDishItemFragment)(_category.get(i))).setClick();
					}else{
						button.setTextColor(Color.parseColor("#000000"));
					}
				}
			}
		});
		
		mViewPager = (ViewPager) view.findViewById(R.id.category_viewpager);
		mPagerAdapter = new CategoryPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (mPagerAdapter != null) {
			mPagerAdapter.refreshDish();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public class CategoryPagerAdapter extends FragmentPagerAdapter {

		public CategoryPagerAdapter(FragmentManager fm) {

			super(fm);
			initFragments();
		}

		private void initFragments() {
			for (int i = 0; i < SanyiSDK.rest.getGoodsGroup().size(); ++i) {
				GoodsGroup goodsGroup = SanyiSDK.rest.getGoodsGroup().get(i);
				initRadioButton(goodsGroup);
				SlodOutDishItemFragment f = new SlodOutDishItemFragment();
				Bundle args = new Bundle();
				args.putInt("number", i);
				f.setArguments(args);
				_category.add(f);




			}
			((RadioButton)(cateogry_sliding_group.getChildAt(0))).setTextColor(Color.parseColor("#ffffff"));
			cateogry_sliding_group.check(cateogry_sliding_group.getChildAt(0).getId());
		}

		/**
		 * @return the number of pages to display
		 */
		@Override
		public int getCount() {
			return _category.size();
		}

//		@Override
//		public CharSequence getPageTitle(int position) {
//			return categoryName.get(position);
//		}

		@Override
		public int getItemPosition(Object object) {
			// return POSITION_NONE;
			return POSITION_UNCHANGED;
		}

		@Override
		public Fragment getItem(int index) {
			// return OrderDishItemFragment.newInstance(index);
			return _category.get(index);
		}

		public void refreshDish() {
			for (SlodOutDishItemFragment f : _category) {
				f.refreshDish();
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			return super.instantiateItem(container, position);
		}

	}
	private void initRadioButton(GoodsGroup goodsGroup){
		RadioButton radioButton = new RadioButton(getActivity());
		radioButton.setText("    " + goodsGroup.name + "    ");
		radioButton.setTextSize(getResources().getDimensionPixelOffset(R.dimen.table_status_bottom_text));
		radioButton.setTextColor(Color.parseColor("#000000"));
		radioButton.setButtonDrawable(android.R.color.transparent);
		radioButton.setBackgroundResource(R.drawable.cat_button_selector);
		radioButton.setPadding(20, 10, 20, 10);
		params.setMargins(5, 10, 5, 10);
		radioButton.setLayoutParams(params);
		radioButton.setId((int) goodsGroup.id);
		radioButton.setGravity(Gravity.CENTER);
		cateogry_sliding_group.addView(radioButton);
	}
}
