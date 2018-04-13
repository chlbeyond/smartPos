package com.rainbow.smartpos.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;

public class PricePad {
	// flag values
	public static int NOFLAGS = 0;
	public static int HIDE_INPUT = 1;
	public static int HIDE_PROMPT = 2;

	static Float amountDue;

	static TextView prompt;
	static TextView promptValue;
	EditText editTextInputValue;

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

	private ImageButton buttonNumPadCancel;

	private String value = "";
	private String addl_text = "";
	private PricePad me;

	private int flag_hideInput = 0;
	private int flag_hidePrompt = 0;
	AlertDialog alertDlg;

	public interface pricePadInterface {
		public String pricePadInputValue(String value);

		public String pricePadCanceled();
	}

	public void setAdditionalText(String inTxt) {
		addl_text = inTxt;
	}

	public void show(final Activity a, final String promptString, int inFlags, final pricePadInterface postrun) {
		me = this;
		flag_hideInput = inFlags % 2;
		flag_hidePrompt = (inFlags / 2) % 2;

		Builder inputDialog = new AlertDialog.Builder(a, R.style.DialogTheme);
		String str = "<font color='#33B5E5'>" + promptString + "</font>";
		if (flag_hidePrompt == 0) {
			inputDialog.setTitle(Html.fromHtml(str));
		}

		// Inflate the Dialog layout
		LayoutInflater inflater = a.getLayoutInflater();
		View padView = inflater.inflate(R.layout.price_pad, null, false);

		Button buttonOk = (Button) padView.findViewById(R.id.buttonNumberPadOk);
		buttonOk.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				v.setEnabled(false);
				if (value != "") {
					alertDlg.dismiss();
					postrun.pricePadInputValue(value);
				} else {
					Toast.makeText(a, "您的输入有误", Toast.LENGTH_LONG).show();
					v.setEnabled(true);

				}
			}
		});
		Button buttonCancel = (Button) padView.findViewById(R.id.buttonNumberPadCancel);
		buttonCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				v.setEnabled(false);
				alertDlg.dismiss();
				postrun.pricePadCanceled();
			}
		});
		// create code to handle the change tender
		prompt = (TextView) padView.findViewById(R.id.promptText);
		prompt.setText(addl_text);
		if (addl_text.equals("")) {
			prompt.setVisibility(View.GONE);
		}
		editTextInputValue = (EditText) padView.findViewById(R.id.editTextInputValue);
		editTextInputValue.setInputType(InputType.TYPE_NULL);

		// Defaults
		value = "";

		btn1 = (Button) padView.findViewById(R.id.buttonNumPad1);
		btn2 = (Button) padView.findViewById(R.id.buttonNumPad2);
		btn3 = (Button) padView.findViewById(R.id.buttonNumPad3);
		btn4 = (Button) padView.findViewById(R.id.buttonNumPad4);
		btn5 = (Button) padView.findViewById(R.id.buttonNumPad5);
		btn6 = (Button) padView.findViewById(R.id.buttonNumPad6);
		btn7 = (Button) padView.findViewById(R.id.buttonNumPad7);
		btn8 = (Button) padView.findViewById(R.id.buttonNumPad8);
		btn9 = (Button) padView.findViewById(R.id.buttonNumPad9);
		btn0 = (Button) padView.findViewById(R.id.buttonNumPad0);

		buttonNumPadCancel = (ImageButton) padView.findViewById(R.id.buttonNumPadCancel);
		btnDot = (Button) padView.findViewById(R.id.buttonNumPadDot);

		buttonNumPadCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				value = "";
				editTextInputValue.setText("");
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

		btnDot.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				appendNumber(".");
			}
		});

		inputDialog.setView(padView);

		alertDlg = inputDialog.create();
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDlg.show();

		// inputDialog.show();
	}

	void appendNumber(String inNumb) {
		if ((value == "" && inNumb == ".") || (value.contains(".") && inNumb == ".")) {
			return;
		}
		value = value + inNumb;
		if (flag_hideInput == 1) {
			editTextInputValue.setText(editTextInputValue.getText() + "*");
		} else {
			editTextInputValue.setText(editTextInputValue.getText() + inNumb);
		}
	}

}