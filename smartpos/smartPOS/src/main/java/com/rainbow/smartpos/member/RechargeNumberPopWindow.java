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

import com.rainbow.smartpos.R;

public class RechargeNumberPopWindow {
	private String value = "";
	private TextView textViewCheckPaymentType;
	private TextView textViewCheckPaying;
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
	static Button btn00;
	static ImageButton btnClear;
	static Button btnSure;
	static ImageButton btnDelete;
	static Button btnPoint;

	private boolean isFirstInput = true;
	private View parentView;
	private Activity activity;
	private OnSureListener onSureListener;
	private PopupWindow mPopupWindow;
	private String inputType;

	public interface OnSureListener {
		void onSureClick(Double value);
	}

	public RechargeNumberPopWindow(View v, Activity activity, String str, String number,OnSureListener onSureListener) {
		this.parentView = v;
		this.activity = activity;
		this.inputType = str;
		value = number;
		this.onSureListener = onSureListener;
	}

	public void show() {
		View contentView = LayoutInflater.from(activity).inflate(R.layout.recharge_number_dialog_layout, null);
		initView(contentView);
		int width = (int) activity.getResources().getDimension(R.dimen.recharge_num_pad_width);
		int height = (int) activity.getResources().getDimension(R.dimen.recharge_num_pad_height);
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
		mPopupWindow.getBackground().setAlpha(190);
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
		int[] location = new int[2];
		parentView.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, location[0], location[1] + parentView.getHeight());
	}
	public void dismiss(){
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
		}
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		textViewCheckPaymentType = (TextView) view.findViewById(R.id.textViewCheckPaymentType);
		textViewCheckPaying = (TextView) view.findViewById(R.id.textViewCheckPaying);

		textViewCheckPaymentType.setText(inputType);
		if (value.isEmpty()) {
			value = "0";
		}
		textViewCheckPaying.setText(value);
		
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
		btn00 = (Button) view.findViewById(R.id.buttonNumPadTwoZero);
		btnPoint = (Button) view.findViewById(R.id.buttonNumPadPoint);
		btnClear = (ImageButton) view.findViewById(R.id.buttonNumPadClear);
		btnSure = (Button) view.findViewById(R.id.buttonNumPadSure);
		btnDelete = (ImageButton) view.findViewById(R.id.buttonNumPadC);

		btn00.setOnClickListener(onClickListener);
		btnPoint.setOnClickListener(onClickListener);
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
			case R.id.buttonNumPadAll:
				break;
			case R.id.buttonNumPadC:
				if (value.length() > 0) {
					value = value.substring(0, value.length() - 1);
					textViewCheckPaying.setText(value);
				}
				break;
			case R.id.buttonNumPadSure:
				buttonSureClick();
				break;
			case R.id.buttonNumPadTwoZero:
				appendNumber("00");
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
				textViewCheckPaying.setText("");
				break;
			case R.id.buttonNumPadPoint:
				appendNumber(".");
				break;

			default:
				break;
			}
		}
	};
	
	private void buttonSureClick(){
		try{
			Double paying = Double.parseDouble(value);
			onSureListener.onSureClick(paying);
			dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void appendNumber(String inNumb) {
		if (inNumb.equals(".")) {
			if (value.length() > 0 && !value.contains(".")) {
				value = value + inNumb;
				textViewCheckPaying.setText(value);
			}
		} else {
			if (isFirstInput) {
				value = "";
				textViewCheckPaying.setText("");
				isFirstInput = false;
			}
			value = value + inNumb;
			textViewCheckPaying.setText(value);
		}
	}

}
