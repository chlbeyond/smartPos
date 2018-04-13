package slidingtutorial;

import com.sanyi.androidsdk.R;

/**
 * Simple implementation of {@link PresentationPagerFragment}
 */
public abstract class SimplePagerFragment extends PresentationPagerFragment {

    @Override
    public int getLayoutResId() {
        return R.layout.st_fragment_presentation;
    }

    @Override
    public int getViewPagerResId() {
        return R.id.viewPager;
    }

    @Override
    public int getIndicatorResId() {
        return R.id.indicator;
    }

}
