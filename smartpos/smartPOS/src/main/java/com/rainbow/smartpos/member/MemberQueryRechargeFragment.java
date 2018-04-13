package com.rainbow.smartpos.member;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.utils.DataUtil;

import java.lang.reflect.Field;

public class MemberQueryRechargeFragment extends Fragment {
	public WebView webView_fragment;
	ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
		initProgressDialog();
		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		webView_fragment = (WebView) rootView.findViewById(R.id.webView_fragment);
		//reference-> http://stackoverflow.com/questions/27232275/java-util-concurrent-timeoutexception-android-view-threadedrenderer-finalize
		//http://www.cnblogs.com/-shijian/p/3421833.html
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // KITKAT
		{
			webView_fragment.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		WebSettings webSettings = webView_fragment.getSettings();
		webView_fragment.setWebViewClient(new WebViewClient());
		webSettings.setJavaScriptEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView_fragment.loadUrl(DataUtil.parserMemberRechargeRecordURL());
		webView_fragment.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				// 结束
				super.onPageFinished(view, url);
				progressDialog.dismiss();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// 开始
				super.onPageStarted(view, url, favicon);
				progressDialog.setMessage("加载中......");
				progressDialog.show();
			}
		});
		return rootView;
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle(null);
		progressDialog.setMessage("请等候...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setIndeterminateDrawable(getActivity().getResources().getDrawable(R.drawable.spinner));
		progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		progressDialog.setCancelable(false);
	}


	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
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
