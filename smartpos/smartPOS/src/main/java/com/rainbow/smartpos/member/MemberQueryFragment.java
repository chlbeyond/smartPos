package com.rainbow.smartpos.member;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.utils.DataUtil;

import java.lang.reflect.Field;

public class MemberQueryFragment extends Fragment {
    public WebView webView_fragment;

    public void setMemberFragment(MemberFragment memberFragment) {
        this.memberFragment = memberFragment;
    }

    public MemberFragment memberFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        webView_fragment = (WebView) rootView.findViewById(R.id.webView_fragment);
        //reference-> http://stackoverflow.com/questions/27232275/java-util-concurrent-timeoutexception-android-view-threadedrenderer-finalize
        //http://www.cnblogs.com/-shijian/p/3421833.html
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // KITKAT
        {
            webView_fragment.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        WebSettings webSettings = webView_fragment.getSettings();
        webView_fragment.setWebViewClient(new WebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView_fragment.loadUrl(DataUtil.parserMemberOperateURL() + "memberDetail/" + MemberFragment.res.getId());
        webView_fragment.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // 结束
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 开始
                super.onPageStarted(view, url, favicon);
            }
        });
        Button button = (Button) rootView.findViewById(R.id.button_member_query_back);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberFragment.displayView(MemberFragment.QUERY_RECHARGE_MEMBER);
            }
        });
        return rootView;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        webView_fragment.clearHistory();
        webView_fragment.clearCache(true);
        CookieManager.getInstance().removeAllCookie();
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

}