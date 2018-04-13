package com.rainbow.smartpos.presentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rainbow.common.view.AutoViewpager.AutoScrollViewPager;
import com.rainbow.smartpos.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/5/20.
 */
public class AutoScrollViewPagerFragment extends Fragment {
    public View view;
    public AutoScrollViewPager mViewPager;
    public List<File> mFiles;

    public void setFiles(List<File> files) {
        this.mFiles = files;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        mViewPager = (AutoScrollViewPager) view.findViewById(R.id.autoScrollViewPager);
        mViewPager.setAdapter(new ViewPagerAdapter(paseFileToImage()));
        mViewPager.setOffscreenPageLimit(mFiles.size());
        return view;
    }

    public List<ImageView> paseFileToImage() {
        List<ImageView> imageViews = new ArrayList<>();
        for (File file : mFiles) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageBitmap(bitmap);
            imageViews.add(imageView);
        }
        return imageViews;
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


        public Object instantiateItem(ViewGroup view, int position) {

            view.addView(list.get(position));
            return list.get(position);

        }

    }
}
