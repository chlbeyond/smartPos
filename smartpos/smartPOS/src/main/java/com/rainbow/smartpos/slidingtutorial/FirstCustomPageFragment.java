package com.rainbow.smartpos.slidingtutorial;

import android.view.View;

import com.rainbow.smartpos.R;

import slidingtutorial.PageFragment;
import slidingtutorial.TransformItem;

public class FirstCustomPageFragment extends PageFragment {

	@Override
	protected int getLayoutResId() {
		return R.layout.fragment_page_first;
	}

	@Override
	protected TransformItem[] provideTransformItems() {
		return new TransformItem[]{
//				new TransformItem(R.id.ivFirstImage, true, 20),
//				new TransformItem(R.id.ivSecondImage, false, 6),
//				new TransformItem(R.id.ivThirdImage, true, 8),
//				new TransformItem(R.id.ivFourthImage, false, 10),
//				new TransformItem(R.id.ivFifthImage, false, 3),
//				new TransformItem(R.id.ivSixthImage, false, 9),
//				new TransformItem(R.id.ivSeventhImage, false, 14),
//				new TransformItem(R.id.ivEighthImage, false, 7)
		};
	}

	@Override
	protected void onCreateViewAfter(View view) {

	}
}
