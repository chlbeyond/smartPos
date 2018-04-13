package com.rainbow.smartpos.slidingtutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.cloud.ShopList;

/**
 * Created by ss on 2016/3/19.
 */
public class LogingFragment extends Fragment {
    private TextView mTextViewTitle;
    private String mStringQRCodeUrl;
    private QRCodeFragment mQRCodeFragment;
    private ShopList shops;
    private TextView mTextViewBack;

    public void setShops(ShopList shops) {
        this.shops = shops;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sliding_login, null);
        mTextViewTitle = (TextView) view.findViewById(R.id.textView_sliding_title);
        mTextViewBack = (TextView) view.findViewById(R.id.textView_sliding_back);
        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SlidingTutorial activity = (SlidingTutorial) getActivity();
                activity.disPresentation();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shops != null) {
            displayChooseShopFragment();
            mTextViewTitle.setText("请选择门店:");
        } else {
            displayQRCodeFragment();
            mTextViewTitle.setText("微信扫码登录");
        }
    }

    public void displayQRCodeFragment() {
        mQRCodeFragment = new QRCodeFragment();
        mQRCodeFragment.setmLoginFragment(this);
        displayView(mQRCodeFragment);
    }

    public void displayChooseShopFragment() {
        displayView(new ChooseShopFragment(shops));
    }

    public void setmTextViewTitle(String title) {
        mTextViewTitle.setText(title);
    }

    public void displayView(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_slding, fragment);
        fragmentTransaction.commit();
    }
}
