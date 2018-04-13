package com.rainbow.smartpos.check;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.SmartPosApplication;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.VoucherType;

public class VoucherService {

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

	private String addl_text = "";

	private int flag_hideInput = 0;
	private int flag_hidePrompt = 0;
	MainScreenActivity activity;
	SmartPosApplication application;

	private Dialog progressDialog;
	VoucherInterface postrun;

	TextView progressStatus;
	Button buttonOk;
	Button buttonCancel;
	Context context;
	private ImageButton buttonNumPadDelete;
	private ImageButton buttonNumPadCancel;
	AlertDialog alertDlg;
	ProgressDialog progresDialog;

	boolean scannerInput = false;
	List<VoucherType> voucherTypeList;

	public interface VoucherInterface {
		public String voucherOk(int count, long voucherId);

		public String voucherSN(String voucherSN);

		public String voucherCanceled();
	}

	public void setAdditionalText(String inTxt) {
		addl_text = inTxt;
	}

	private Button buttonSelectVoucherType;

	public void show(final Activity a, final String promptString, int inFlags, List<VoucherType> voucherList, final VoucherInterface postrun) {
		activity = (MainScreenActivity) a;
		application = (SmartPosApplication) a.getApplication();
		this.postrun = postrun;
		flag_hideInput = inFlags % 2;
		flag_hidePrompt = (inFlags / 2) % 2;
		this.voucherTypeList = voucherList;
		Builder dlg = new AlertDialog.Builder(a, R.style.DialogTheme);
		String str = "<font color='#33B5E5'>" + promptString + "</font>";
		if (flag_hidePrompt == 0) {
			dlg.setTitle(Html.fromHtml(str));
		}

		// Inflate the Dialog layout
		LayoutInflater inflater = a.getLayoutInflater();
		View iView = inflater.inflate(R.layout.voucher_service, null, false);

		buttonOk = (Button) iView.findViewById(R.id.buttonNumberPadOk);
		buttonOk.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!editTextInputValue.getText().toString().equals("")) {
					postrun.voucherSN(editTextInputValue.getText().toString());
					editTextInputValue.getText().clear();
					editTextInputValue.requestFocus();
				}
			}
		});
		buttonCancel = (Button) iView.findViewById(R.id.buttonNumberPadCancel);
		buttonCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				v.setEnabled(false);
				alertDlg.dismiss();
				postrun.voucherCanceled();
			}
		});

		buttonSelectVoucherType = (Button) iView.findViewById(R.id.buttonSelectVoucherType);
		buttonSelectVoucherType.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				v.setEnabled(false);
				// alertDlg.dismiss();
				showSelectVoucherTypeDialog(v);
			}
		});

		// create code to handle the change tender
		prompt = (TextView) iView.findViewById(R.id.promptText);
		prompt.setText(addl_text);
		if (addl_text.equals("")) {
			prompt.setVisibility(View.GONE);
		}
		editTextInputValue = (EditText) iView.findViewById(R.id.editTextInputValue);
		editTextInputValue.setInputType(InputType.TYPE_NULL);
		editTextInputValue.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					scannerInput = true;
					buttonOk.performClick();
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
		btnC = (Button) iView.findViewById(R.id.buttonNumPadC);
		// btnDot = (Button) iView.findViewById(R.id.buttonNumPadOk);

		buttonNumPadCancel = (ImageButton) iView.findViewById(R.id.buttonNumPadCancel);
		buttonNumPadDelete = (ImageButton) iView.findViewById(R.id.buttonNumPadDelete);
		// btnDot = (Button) padView.findViewById(R.id.buttonNumPadOk);

		buttonNumPadCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				editTextInputValue.setText("");
			}
		});

		buttonNumPadDelete.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				if (editTextInputValue.getText().toString().length() > 0) {
					editTextInputValue.setText(editTextInputValue.getText().toString().substring(0, editTextInputValue.getText().toString().length() - 1));
				}

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

		dlg.setView(iView);

		alertDlg = dlg.create();
		alertDlg.setCancelable(false);
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDlg.show();
	}

	public String getVoucherNo() {
		return editTextInputValue.getText().toString();
	}

	void appendNumber(String inNumb) {
		if (flag_hideInput == 1) {
			editTextInputValue.setText(editTextInputValue.getText() + "*");
		} else {
			editTextInputValue.setText(editTextInputValue.getText() + inNumb);
		}
	}

	protected void showSelectVoucherTypeDialog(View v) {
		{

			final Dialog dialog = new Dialog(v.getContext());
			dialog.setContentView(R.layout.voucherlayout);
			dialog.setTitle("选择代金券及数量");
			dialog.setCancelable(true);
			ListView listView_voucher = (ListView) dialog.findViewById(R.id.listView_voucher);
			ListView listView_voucher_is_exclusive = (ListView) dialog.findViewById(R.id.listView_voucher_is_exclusive);
			listView_voucher.setAdapter(new VoucherAdapter(voucherTypeList));
			// listView_voucher_is_exclusive.setAdapter(new
			// VoucherAdapter(listVoucherIsExclusive));
			buttonSelectVoucherType.setEnabled(true);
			listView_voucher_is_exclusive.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					dialog.dismiss();

					// chooseQuantity(listVoucherIsExclusive.get(position));
				}
			});
			listView_voucher.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
					dialog.dismiss();
					chooseQuantity(voucherTypeList.get(position));
				}
			});
			dialog.show();
		}

	}

	public void chooseQuantity(final VoucherType voucher) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		View view = LayoutInflater.from(activity).inflate(R.layout.order_operation_option, null);
		builder.setTitle(Html.fromHtml("<font color='#16a4fa'>请选择代金券数量</font>"));
		TextView textViewOrderItemDishName = (TextView) view.findViewById(R.id.textViewOrderItemDishName);
		textViewOrderItemDishName.setText("数量");
		final EditText textNumOfPeople = (EditText) view.findViewById(R.id.editTextNumOfPeopleServed);
		textNumOfPeople.setFocusable(false);
		textNumOfPeople.setEnabled(false);
		textNumOfPeople.setText(String.valueOf(1));
		Button buttonOpenTableMinusSign = (Button) view.findViewById(R.id.buttonOpenTableMinusSign);
		buttonOpenTableMinusSign.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				String temp = textNumOfPeople.getText().toString();
				int value = Integer.parseInt(temp);
				if (value > 1) {
					value--;
					textNumOfPeople.setText(Integer.toString(value));
				}
			}
		});
		Button buttonOpenTablePlusSign = (Button) view.findViewById(R.id.buttonOpenTablePlusSign);
		buttonOpenTablePlusSign.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				String temp = textNumOfPeople.getText().toString();
				int value = Integer.parseInt(temp);
				value++;
				textNumOfPeople.setText(Integer.toString(value));
			}
		});
		Button buttonOpenTable = (Button) view.findViewById(R.id.buttonOpenTable);
		buttonOpenTable.setText("确定");
		buttonOpenTable.setVisibility(View.GONE);
		Button buttonCancelOpenTable = (Button) view.findViewById(R.id.buttonCancelOpenTable);
		buttonCancelOpenTable.setVisibility(View.GONE);

		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				postrun.voucherOk(Integer.valueOf(textNumOfPeople.getText().toString()), voucher.id);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				buttonSelectVoucherType.setEnabled(true);
				buttonOk.setEnabled(true);
				dialog.dismiss();
			}
		});
		builder.setView(view);
		Dialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (MainScreenActivity.getScreenWidth() * 0.5); // 宽度设置为屏幕的0.65
		dialog.getWindow().setAttributes(p);

	}
	public void clearEdit(){
		editTextInputValue.setText("");
	}
	public void miss(){
		if (null != alertDlg) {
			alertDlg.dismiss();
		}
	}

	public class VoucherAdapter extends BaseAdapter {
		public List<VoucherType> list;

		public VoucherAdapter(List<VoucherType> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = LayoutInflater.from(activity);
			convertView = inflater.inflate(R.layout.voucherlistitem, null);
			TextView voucher_textView_item = (TextView) convertView.findViewById(R.id.voucher_textView_item);
			voucher_textView_item.setText(list.get(position).name);
			return convertView;
		}
	}

	protected void showProgressDialog() {
		{

			AlertDialog.Builder builder = new Builder(activity, R.style.DialogTheme);
			builder.setTitle("正在验证");
			// 给下载对话框增加进度条
			final LayoutInflater inflater = LayoutInflater.from(activity);
			View v = inflater.inflate(R.layout.voucher_coupon_service_progress, null);
			progressStatus = (TextView) v.findViewById(R.id.textViewStatus);
			builder.setView(v);
			// 取消更新
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// 设置取消状态
				}
			});
			progressDialog = builder.create();
			progressDialog.show();// 现在文件
		}
	}

}