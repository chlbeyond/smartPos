package com.sanyipos.android.sdk.checkServices;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sanyi.androidsdk.R;

/**
 * 加载对话框
 *
 * @author ming.cheng
 * @date 2014/10/28
 *
 */
public class SmallLoadingDialog extends Dialog {

	private FragmentActivity mContextAct;
	private String loadingtext;
	private String title = "初始化中...";

	private TextView mToolTipText;

	public SmallLoadingDialog(FragmentActivity context) {
		super(context, R.style.dialog);
		this.mContextAct = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading_dialog);


		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.height = 300;
		params.width = 300;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);

		mToolTipText = (TextView) findViewById(R.id.tooltipText);

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		setCanceledOnTouchOutside(false);
	}

	public void setToolTipText(final String text) {
		this.mContextAct.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mToolTipText.setText(text);
			}
		});
	}

}
