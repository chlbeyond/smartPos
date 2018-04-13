package com.rainbow.smartpos.tablestatus;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.utils.DataUtil;
import com.rainbow.smartpos.Restaurant;

public class WebViewFragment extends Fragment {
	public WebView webView_fragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
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
		webView_fragment.loadUrl(DataUtil.parserMemberURL(SanyiSDK.rest.config.webUrl, SanyiSDK.rest.operationData.shop.brandId, (long) SanyiSDK.registerData.getShopId(), SanyiSDK.currentUser,
				Restaurant.uuid, SanyiSDK.registerData.getSalt()));
		return rootView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		webView_fragment.clearHistory();
		webView_fragment.clearCache(true);
	}

}
