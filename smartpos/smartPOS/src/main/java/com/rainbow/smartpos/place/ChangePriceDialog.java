package com.rainbow.smartpos.place;

import android.content.Context;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnChangePriceListener;
import com.rainbow.smartpos.util.Listener.OnOpenTableListener;
import com.rainbow.smartpos.util.Listener.OnSetReturnDishCountListener;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.utils.OrderUtil;

public class ChangePriceDialog {
	public static final int CHANGE_PRICE = 0;
	public static final int CHANGE_WEIGHT = 1;

	private MyDialog dialog;
	private String value = "";
	
	EditText discount_edit;
	EditText price_edit;
	TextView sure;
	ImageButton cancel;

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
	static ImageButton btnC;
	static Button btnPoint;

	Context activity;
	private int flag;
	private boolean isFirstInput = true;
	private OrderDetail mOrderDetail;
	
	private OnChangePriceListener listener;

	public void show(Context activity,  OrderDetail orderDetail, int flag, OnChangePriceListener listener) {
		this.activity = activity;
		this.mOrderDetail = orderDetail;
		this.listener = listener;
		this.flag = flag;
		
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.change_price_dialog_layout, null, false);
		dialog = new MyDialog(activity, (int) (MainScreenActivity.getScreenWidth() * 0.4), (int) (MainScreenActivity.getScreenHeight() * 0.9), view, R.style.OpDialogTheme);

		sure = (TextView) view.findViewById(R.id.sure_btn);
		cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);
		
		discount_edit = (EditText) view.findViewById(R.id.discount_edit);
		price_edit = (EditText) view.findViewById(R.id.price_edit);
		discount_edit.setInputType(InputType.TYPE_NULL);
		price_edit.setInputType(InputType.TYPE_NULL);

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
		btnC = (ImageButton) view.findViewById(R.id.buttonNumPadC);
		btnPoint = (Button) view.findViewById(R.id.buttonNumPadPoint);

		sure.setOnClickListener(onClickListener);
		cancel.setOnClickListener(onClickListener);
		btnC.setOnClickListener(onClickListener);
		btnPoint.setOnClickListener(onClickListener);
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
		
		discount_edit.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					value = "";
					price_edit.setText("");
				}
			}
		});
		price_edit.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					value = "";
					discount_edit.setText("");
				}
			}
		});

		dialog.show();
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.sure_btn:
				buttonSureClick();
				break;
			case R.id.iv_close_dialog:
				buttonCancelClick();
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
			case R.id.buttonNumPadC:
				value = "";
				if (discount_edit.isFocused()) {
					discount_edit.setText(value);
				} else {
					price_edit.setText(value);
				}
				break;
			case R.id.buttonNumPadPoint:
				appendNumber(".");
				break;

			default:
				break;
			}
		}
	};

	private void buttonSureClick() {
		// TODO Auto-generated method stub
		if (value.isEmpty()) {
			listener.onCancel();
			return;
		}
		try {
			if (discount_edit.isFocused()) {
				Double discount = Double.parseDouble(value);
				double price = mOrderDetail.getCurrentPrice()*discount/100;
				listener.onSure(price);
			}else{
				listener.onSure(Double.parseDouble(value));
			}
			dialog.dismiss();
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(activity, "请输入合法数字", 0).show();
		}
	}

	private void buttonCancelClick() {
		// TODO Auto-generated method stub
		listener.onCancel();
		dialog.dismiss();
		
	}

	void appendNumber(String inNumb) {
		if (inNumb.equals(".")) {
			if (value.length() > 0 && !value.contains(".")) {
				value = value + inNumb;
				if (discount_edit.isFocused()) {
					discount_edit.setText(value);
				} else {
					price_edit.setText(value);
				}
			}
		}else{
			if (isFirstInput) {
				value = "";
				if (discount_edit.isFocused()) {
					discount_edit.setText(value);
				} else {
					price_edit.setText(value);
				}
				isFirstInput = false;
			}
			value = value + inNumb;
			if (discount_edit.isFocused()) {
				discount_edit.setText(value);
			} else {
				price_edit.setText(value);
			}
		}
		
	}
}
