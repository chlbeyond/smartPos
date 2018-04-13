package com.rainbow.smartpos.activity.reportfragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rainbow.smartpos.R;

/**
 * Created by ss on 2016/11/25.
 */

public class WebViewFragment extends Fragment {
    private String url;
    private WebView webView_fragment;

    public void setUrl(String url) {
        this.url = url;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webView_fragment.setWebViewClient(new WebViewClient());
//        webView_fragment.setClickable(true);
        webView_fragment.setWebChromeClient(new WebChromeClient());
        webView_fragment.loadUrl(url);
        return rootView;
    }

    public void reloadWebPage()
    {
        webView_fragment.reload();
    }
}
