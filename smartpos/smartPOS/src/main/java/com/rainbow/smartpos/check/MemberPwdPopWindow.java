package com.rainbow.smartpos.check;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rainbow.smartpos.R;

public class MemberPwdPopWindow {
	private View parentView;
	private Context activity;
	public OnSureListener onSureListener;
	private PopupWindow mPopupWindow;
	
	TextView sure_btn;
	EditText member_pwd_edit;
	

	public interface OnSureListener {
		void onSureClick(String pwd);
	}

	public MemberPwdPopWindow(View v, Context activity, OnSureListener onSureListener) {
		this.parentView = v;
		this.activity = activity;
		this.onSureListener = onSureListener;
	}

	public void show() {
		View contentView = LayoutInflater.from(activity).inflate(R.layout.member_pwd_dialog_layout, null);
		initView(contentView);
		int width = (int) activity.getResources().getDimension(R.dimen.check_member_pwd_width);
		int height = (int) activity.getResources().getDimension(R.dimen.check_member_pwd_height);
		mPopupWindow = new PopupWindow(contentView, width, height, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
		int[] location = new int[2];
		parentView.getLocationOnScreen(location);
		mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.voucher_count_bg_left));
		mPopupWindow.getBackground().setAlpha(190);
		mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, location[0], location[1]+parentView.getHeight());
	}
	public void dismiss(){
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
		}
	}

	private void initView(View view) {
		sure_btn = (TextView) view.findViewById(R.id.sure_btn);
		member_pwd_edit = (EditText) view.findViewById(R.id.member_pwd_edit);
		sure_btn.setOnClickListener(onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.sure_btn:
				buttonSureClick();
				break;
			default:
				break;
			}
		}
	};
	
	private void buttonSureClick(){
		try{
			onSureListener.onSureClick(member_pwd_edit.getText().toString());
			dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
