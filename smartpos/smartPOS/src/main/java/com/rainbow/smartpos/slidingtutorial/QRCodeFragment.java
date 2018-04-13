package com.rainbow.smartpos.slidingtutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiCloudRequests;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.services.cloud._ApplyPosRequest;
import com.sanyipos.sdk.api.services.cloud._GetShopListRequest;
import com.sanyipos.sdk.model.cloud.ShopList;
import com.socks.library.KLog;

/**
 * Created by ss on 2016/3/19.
 */
public class QRCodeFragment extends Fragment {
    public static String TAG = "QRCodeFragment";
    private String mStringQRCodeUrl;
    private ImageView mImageViewQRCode;

    public void setmLoginFragment(LogingFragment mLoginFragment) {
        this.mLoginFragment = mLoginFragment;
    }

    private LogingFragment mLoginFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sliding_qrcode, container, false);
        mImageViewQRCode = (ImageView) view.findViewById(R.id.imageView_sliding_login_qr_code);
        mImageViewQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        SanyiCloudRequests.getQRCodeURLRequest(new _ApplyPosRequest.IApplyPosListener() {
            @Override
            public void onSuccess(String QRCodeUrl) {
                KLog.d(TAG, "request_fail" + QRCodeUrl);
                Glide.with(getActivity()).load(QRCodeUrl).centerCrop().into(mImageViewQRCode);
            }


            @Override
            public void onFail(String error) {
                KLog.d(TAG, error);
            }
        });
        SanyiSDK.getSDK().setiGetShopListListener(new _GetShopListRequest.IGetShopListListener() {
            @Override
            public void onSuccess(ShopList resp) {
                if (mLoginFragment != null) {
                    mLoginFragment.setmTextViewTitle("请选择门店");
                    mLoginFragment.setShops(resp);
                    mLoginFragment.displayChooseShopFragment();
                }
            }


            @Override


            public void onFail(String error) {

            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SanyiSDK.getSDK().cancelScanPolling();
    }
}
