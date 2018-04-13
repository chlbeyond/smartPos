package com.rainbow.smartpos.slidingtutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;

import butterknife.Bind;

public class ThirdCustomPageFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.button_third_pro_version)
    Button mButtonProVersion;

    @Bind(R.id.button_third_free_version)
    Button mButtonFreeVersion;

    public LogingFragment logingFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_third, null);
        mButtonProVersion = (Button) view.findViewById(R.id.button_third_pro_version);
        mButtonFreeVersion = (Button) view.findViewById(R.id.button_third_free_version);
        mButtonProVersion.setOnClickListener(this);
        mButtonFreeVersion.setOnClickListener(this);
        logingFragment = new LogingFragment();
        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_third_pro_version:
                Intent intent = new Intent();
                intent.setClass(getActivity(), MainScreenActivity.class);
                startActivity(intent);
                break;
            case R.id.button_third_free_version:
                SlidingTutorial activity = (SlidingTutorial) getActivity();
                activity.displayView(logingFragment);
                break;
        }
    }

}
