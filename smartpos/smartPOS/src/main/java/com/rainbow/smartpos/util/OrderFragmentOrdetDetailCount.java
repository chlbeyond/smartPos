package com.rainbow.smartpos.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;

public class OrderFragmentOrdetDetailCount {

	private static final int SUCESS = 1;
	private static final int FAIL = 0;

	// flag values
	public static int NOFLAGS = 0;
	public static int HIDE_INPUT = 1;
	public static int HIDE_PROMPT = 2;

	static Float amountDue;

	static TextView prompt;
	static TextView promptValue;
	EditText editTextInputValue;
	TextView textView_order_fragment_old_count_number;
	String value = "";
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
	static Button btnC;
	static Button btnDot;
	static Button buttonOk;
	static Button buttonCancel;

	private ImageButton buttonNumPadDelete;
	private ImageButton buttonNumPadCancel;

	private String addl_text;
	private OrderFragmentOrdetDetailCount me;

	private int flag_hideInput = 0;
	private int flag_hidePrompt = 0;
	Activity activity;

	private ProgressBar progressBar;
	private Dialog progressDialog;
	OrderFragmentOrdetailCount orderCount;

	TextView progressStatus;

	AlertDialog alertDlg;

	public interface OrderFragmentOrdetailCount {
		public String OrderCount(String count);

		public String OrderCancel();
	}

	public void setAdditionalText(String inTxt) {
		addl_text = inTxt;
	}

	public void show(final Activity a, final String promptString, int quantity, int inFlags, final OrderFragmentOrdetailCount orderCount) {
		activity = a;
		me = this;
		this.orderCount = orderCount;
		flag_hideInput = inFlags % 2;
		flag_hidePrompt = (inFlags / 2) % 2;

		final Builder dlg = new AlertDialog.Builder(a, R.style.DialogTheme);
		
		String str = "<font color='#33B5E5'>"+promptString+"</font>";
		if (flag_hidePrompt == 0) {
			dlg.setTitle(Html.fromHtml(str));
		}
		// Inflate the Dialog layout
		LayoutInflater inflater = a.getLayoutInflater();
		View iView = inflater.inflate(R.layout.orderfragmentcount_pad, null, false);

		// create code to handle the change tender
		prompt = (TextView) iView.findViewById(R.id.promptText);
		textView_order_fragment_old_count_number = (TextView) iView.findViewById(R.id.textView_order_fragment_old_count_number);
		textView_order_fragment_old_count_number.setText(Integer.toString(quantity));
		// if (addl_text == null) {
		// prompt.setVisibility(View.GONE);
		// } else {
		// prompt.setText(addl_text);
		// }
		editTextInputValue = (EditText) iView.findViewById(R.id.editTextInputValue);
		editTextInputValue.setInputType(InputType.TYPE_NULL);
		editTextInputValue.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					return true;
				}
				return false;

			}
		});

		btn1 = (Button) iView.findViewById(R.id.buttonNumPad1);
		btn2 = (Button) iView.findViewById(R.id.buttonNumPad2);
		btn3 = (Button) iView.findViewById(R.id.buttonNumPad3);
		btn4 = (Button) iView.findViewById(R.id.buttonNumPad4);
		btn5 = (Button) iView.findViewById(R.id.buttonNumPad5);
		btn6 = (Button) iView.findViewById(R.id.buttonNumPad6);
		btn7 = (Button) iView.findViewById(R.id.buttonNumPad7);
		btn8 = (Button) iView.findViewById(R.id.buttonNumPad8);
		btn9 = (Button) iView.findViewById(R.id.buttonNumPad9);
		btn0 = (Button) iView.findViewById(R.id.buttonNumPad0);
		buttonOk = (Button) iView.findViewById(R.id.buttonNumberPadOk);
		buttonOk.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				v.setEnabled(false);
				if (value.length() > 0) {
					orderCount.OrderCount(value);
					alertDlg.dismiss();
				} else {
					Toast.makeText(a, "请输入正确的数量", Toast.LENGTH_LONG).show();
					v.setEnabled(true);
				}
			}
		});
		buttonCancel = (Button) iView.findViewById(R.id.buttonNumberPadCancel);
		buttonCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				v.setEnabled(false);
				alertDlg.dismiss();
				orderCount.OrderCancel();
			}
		});

		btn1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("1");
			}
		});
		btn2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("2");
			}
		});
		btn3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("3");
			}
		});
		btn4.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("4");
			}
		});
		btn5.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("5");
			}
		});
		btn6.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("6");
			}
		});
		btn7.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("7");
			}
		});
		btn8.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("8");
			}
		});
		btn9.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("9");
			}
		});
		btn0.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber("0");
			}
		});

		buttonNumPadCancel = (ImageButton) iView.findViewById(R.id.buttonNumPadCancel);
		buttonNumPadDelete = (ImageButton) iView.findViewById(R.id.buttonNumPadDelete);
		// btnDot = (Button) padView.findViewById(R.id.buttonNumPadOk);

		buttonNumPadCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				value = "";
				editTextInputValue.setText(value);
			}
		});

		buttonNumPadDelete.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (value.length() > 0) {
					value = value.substring(0, value.length() - 1);
					editTextInputValue.setText(value);
				}

			}

		});

		dlg.setView(iView);
		dlg.setCancelable(false);

		alertDlg = dlg.create();
		alertDlg.setCancelable(false);
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDlg.show();
	}

	public String getVoucherNo() {
		return editTextInputValue.getText().toString();
	}

	void appendNumber(String inNumb) {
		value = value + inNumb;
		if (flag_hideInput == 1) {
			editTextInputValue.setText(value + "*");
		} else {
			editTextInputValue.setText(value);
		}
	}

}