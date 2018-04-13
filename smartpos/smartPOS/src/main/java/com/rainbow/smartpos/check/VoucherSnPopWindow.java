package com.rainbow.smartpos.check;

import android.content.Context;
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

import com.rainbow.smartpos.R;

public class VoucherSnPopWindow {
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

	private boolean isFirstInput = true;
	private View parentView;
	private Context activity;
	public OnSureListener onSureListener;
	private PopupWindow mPopupWindow;

	public interface OnSureListener {
		void onSureClick(String value);
		void onBtnClick(String value);
	}

	public VoucherSnPopWindow(View v, Context activity, OnSureListener onSureListener) {
		this.parentView = v;
		this.activity = activity;
		this.onSureListener = onSureListener;
	}

	public void show() {
		View contentView = LayoutInflater.from(activity).inflate(R.layout.voucher_sn_dialog_layout, null);
		initView(contentView);
		int width = (int) activity.getResources().getDimension(R.dimen.check_fragment_voucher_sn_width);
		int height = (int) activity.getResources().getDimension(R.dimen.check_fragment_voucher_sn_height);
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
		mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.voucher_count_bg_left));
		mPopupWindow.getBackground().setAlpha(190);
		mPopupWindow.getContentView().measure(0, 0);
		int[] location = new int[2];
		parentView.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, 20, (int)(location[1]+activity.getResources().getDimension(R.dimen.check_fragment_voucher_sn_pad_padding)));
		
	}
	public void dismiss(){
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
		}
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
					onSureListener.onBtnClick(value);
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
				onSureListener.onBtnClick(value);
				break;

			default:
				break;
			}
		}
	};
	
	private void buttonSureClick(){
		try{
			onSureListener.onSureClick(value);
			dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void appendNumber(String inNumb) {
		if (inNumb.equals(".")) {
			if (value.length() > 0 && !value.contains(".")) {
				value = value + inNumb;
			}
		} else {
			if (isFirstInput) {
				value = "";
				isFirstInput = false;
			}
			value = value + inNumb;
		}
		onSureListener.onBtnClick(value);
	}
}
