package com.rainbow.smartpos.member;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;

public class NumberPopWindow {
	private String value = "";
	static Button btn1;
	static Button btn2;
	static Button btn3;
	static Button btn4;
	static Button btn5;
	static Button btn6;
	static Button btn7;
	static Button btn8;
	static Button btn9;
	static Button btn0;
	static ImageButton btnClear;
	static TextView btnSure;
	static ImageButton btnDelete;

	private View parentView;
	private Activity activity;
	public OnSureListener listener;
	private PopupWindow mPopupWindow;

	public interface OnSureListener {
		void onSureClick(String value);
		void onNumBtnClick(String value);
	}

	public NumberPopWindow(View v, Activity activity, String number,OnSureListener onSureListener) {
		this.parentView = v;
		value = number;
		this.activity = activity;
		this.listener = onSureListener;
	}

	public void show() {
		View contentView = LayoutInflater.from(activity).inflate(R.layout.number_dialog_layout, null);
		initView(contentView);
		int width = (int) activity.getResources().getDimension(R.dimen.member_create_number_pad_width);
		int height = (int) activity.getResources().getDimension(R.dimen.member_create_number_pad_height);
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
		mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.voucher_count_bg_left));
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
		mPopupWindow.getBackground().setAlpha(190);
		int[] location = new int[2];
		parentView.getLocationOnScreen(location);
		if (location[1] + parentView.getHeight()+height > MainScreenActivity.getScreenHeight()){
			mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bg_left_top));
			mPopupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
			mPopupWindow.getBackground().setAlpha(190);
			mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, location[0], location[1] - height);
		}else{
			mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.voucher_count_bg_left));
			mPopupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
			mPopupWindow.getBackground().setAlpha(190);
			mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, location[0], location[1] + parentView.getHeight());
		}

	}
	public void dismiss(){
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
		}
	}
	
	public boolean isShow(){
		if (null != mPopupWindow) {
			return mPopupWindow.isShowing();
		}
		return false;
	}

	private void initView(View view) {
		btn1 = (Button) view.findViewById(R.id.buttonNumPad1);
		btn2 = (Button) view.findViewById(R.id.buttonNumPad2);
		btn3 = (Button) view.findViewById(R.id.buttonNumPad3);
		btn4 = (Button) view.findViewById(R.id.buttonNumPad4);
		btn5 = (Button) view.findViewById(R.id.buttonNumPad5);
		btn6 = (Button) view.findViewById(R.id.buttonNumPad6);
		btn7 = (Button) view.findViewById(R.id.buttonNumPad7);
		btn8 = (Button) view.findViewById(R.id.buttonNumPad8);
		btn9 = (Button) view.findViewById(R.id.buttonNumPad9);
		btn0 = (Button) view.findViewById(R.id.buttonNumPad0);
		btnClear = (ImageButton) view.findViewById(R.id.buttonNumPadClear);
		btnSure = (TextView) view.findViewById(R.id.sure_btn);
		btnDelete = (ImageButton) view.findViewById(R.id.buttonNumPadDelete);
		btnClear.setOnClickListener(onClickListener);
		btnDelete.setOnClickListener(onClickListener);
		btnSure.setOnClickListener(onClickListener);
		btn1.setOnClickListener(onClickListener);
		btn2.setOnClickListener(onClickListener);
		btn3.setOnClickListener(onClickListener);
		btn4.setOnClickListener(onClickListener);
		btn5.setOnClickListener(onClickListener);
		btn6.setOnClickListener(onClickListener);
		btn7.setOnClickListener(onClickListener);
		btn8.setOnClickListener(onClickListener);
		btn9.setOnClickListener(onClickListener);
		btn0.setOnClickListener(onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.buttonNumPadDelete:
				if (value.length() > 0) {
					value = value.substring(0, value.length() - 1);
					listener.onNumBtnClick(value);
				}
				break;
			case R.id.sure_btn:
				buttonSureClick();
				break;
			case R.id.buttonNumPad0:
				appendNumber("0");
				break;
			case R.id.buttonNumPad1:
				appendNumber("1");
				break;
			case R.id.buttonNumPad2:
				appendNumber("2");
				break;
			case R.id.buttonNumPad3:
				appendNumber("3");
				break;
			case R.id.buttonNumPad4:
				appendNumber("4");
				break;
			case R.id.buttonNumPad5:
				appendNumber("5");
				break;
			case R.id.buttonNumPad6:
				appendNumber("6");
				break;
			case R.id.buttonNumPad7:
				appendNumber("7");
				break;
			case R.id.buttonNumPad8:
				appendNumber("8");
				break;
			case R.id.buttonNumPad9:
				appendNumber("9");
				break;
			case R.id.buttonNumPadClear:
				value = "";
				listener.onNumBtnClick(value);
				break;

			default:
				break;
			}
		}
	};
	
	private void buttonSureClick(){
		try{
			listener.onSureClick(value);
			mPopupWindow.dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void appendNumber(String inNumb) {
		value = value + inNumb;
		listener.onNumBtnClick(value);
	}
}
