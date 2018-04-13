package com.rainbow.smartpos.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.GenericHelper;
import com.sanyipos.sdk.api.SanyiSDK;
import com.rainbow.smartpos.Restaurant;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CouponService {

	private static final int SUCESS = 1;
	private static final int FAIL = 0;

	// flag values
	public static int NOFLAGS = 0;
	public static int HIDE_INPUT = 1;
	public static int HIDE_PROMPT = 2;

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
	private CouponService me;

	private int flag_hideInput = 0;
	private int flag_hidePrompt = 0;
	Activity activity;

	private ProgressBar progressBar;
	private Dialog progressDialog;
	CouponInterface postrun;

	TextView progressStatus;

	private ImageButton buttonNumPadDelete;
	private ImageButton buttonNumPadCancel;
	private ProgressBar progressBarVerify;
	AlertDialog alertDlg;
	Button buttonOk;
	Button buttonCancel;
	boolean scannerInput = false;

	public interface CouponInterface {
		public String couponOK(String data);

		public String couponCanceled();
	}

	private Handler handler = new Handler() {// 跟心ui

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			switch ((Integer) msg.obj) {
			case SUCESS:
				alertDlg.dismiss();
				postrun.couponOK(bundle.getString("data"));
				Toast.makeText(activity, "优惠劵验证成功", Toast.LENGTH_LONG).show();
				break;
			case FAIL:
				buttonOk.setEnabled(true);
				Toast.makeText(activity, "优惠劵验证失败 - " + bundle.getString("errorCode") + " : " + bundle.getString("errorMessage"), Toast.LENGTH_LONG).show();
				if (scannerInput) {
					editTextInputValue.setText("");
				}
				progressDialog.dismiss();
				break;

			default:
				break;
			}
		}

	};

	public void setAdditionalText(String inTxt) {
		addl_text = inTxt;
	}

	public String getCouponNo() {
		return editTextInputValue.getText().toString();
	}

	public void show(final Activity a, final String promptString, int inFlags, final CouponInterface postrun) {
		activity = a;
		me = this;
		this.postrun = postrun;
		flag_hideInput = inFlags % 2;
		flag_hidePrompt = (inFlags / 2) % 2;

		Builder dlg = new AlertDialog.Builder(a, R.style.DialogTheme);
		String str = "<font color='#33B5E5'>" + promptString + "</font>";
		if (flag_hidePrompt == 0) {
			dlg.setTitle(Html.fromHtml(str));
		}

		// Inflate the Dialog layout
		LayoutInflater inflater = a.getLayoutInflater();
		View iView = inflater.inflate(R.layout.coupon_service, null, false);

		progressBarVerify = (ProgressBar) iView.findViewById(R.id.progressBarVerify);
		buttonOk = (Button) iView.findViewById(R.id.buttonNumberPadOk);
		buttonOk.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				v.setEnabled(false);
				scannerInput = false;
				useCoupon(activity, editTextInputValue.getText().toString(), postrun);
			}
		});
		buttonCancel = (Button) iView.findViewById(R.id.buttonNumberPadCancel);
		buttonCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				v.setEnabled(false);
				alertDlg.dismiss();
				postrun.couponCanceled();
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
					useCoupon(activity, editTextInputValue.getText().toString(), postrun);
					return true;
				}
				return false;

			}
		});
		editTextInputValue.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				closeInputMethod(a);
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
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (alertDlg.isShowing()) {
					closeInputMethod(a);
				}
			}
		}).start();
	}

	public void closeInputMethod(Activity activity) {
		View view = activity.getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	void appendNumber(String inNumb) {
		if (flag_hideInput == 1) {
			editTextInputValue.setText(editTextInputValue.getText() + "*");
		} else {
			editTextInputValue.setText(editTextInputValue.getText() + inNumb);
		}
	}

	public void useCoupon(Activity activity, String couponId, final CouponInterface postrun) {

		{
			this.activity = activity;
			this.postrun = postrun;
			// 构造软件下载对话框
			AlertDialog.Builder builder = new Builder(activity);
			builder.setTitle("正在验证优惠劵");
			// 给下载对话框增加进度条
			final LayoutInflater inflater = LayoutInflater.from(activity);
			View v = inflater.inflate(R.layout.voucher_coupon_service_progress, null);
			progressBar = (ProgressBar) v.findViewById(R.id.updateProgress);
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
			progressDialog.show();
			// 现在文件

			try {
				JSONObject obj = new JSONObject();
				obj.put("sn", couponId);
				ValidateCouponTask service = new ValidateCouponTask();
				service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private class ValidateCouponTask extends AsyncTask<JSONObject, Void, Integer> {

		// protected String url = Restaurant.agentUrl;
		protected String responseText;

		@Override
		protected Integer doInBackground(JSONObject... params) {
			int responseCode = 0;
			HttpParams parms = new BasicHttpParams();
			parms.setParameter("charset", HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(parms, 8 * 1000);
			HttpConnectionParams.setSoTimeout(parms, 8 * 1000);

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient(parms);
			HttpPost httppost = new HttpPost(SanyiSDK.rest.config.apiUrl + String.valueOf(Restaurant.shopId) + "/coupon/use");

			try {
				// Add your data
				JSONObject obj = params[0];
				String millis = Long.toString(System.currentTimeMillis());

				String token = GenericHelper.SHA1(SanyiSDK.registerData.getSalt() + "-" + millis) + "|" + millis;
				httppost.addHeader("charset", HTTP.UTF_8);
				httppost.addHeader("X-SanYi-Version", "1.0");
				// TODO: could id include alpha letter? then we need to change
				// deviceId to string
				httppost.addHeader("X-SanYi-UUID", Restaurant.uuid);
				httppost.addHeader("X-SanYi-Token", token);
				httppost.addHeader("Content-Type", "application/json");
				String stringData = obj.toString();
				httppost.setEntity(new StringEntity(stringData, HTTP.UTF_8));
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				if (response != null) {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						StringBuilder sb = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							sb.append(line);
						}
						responseText = sb.toString();
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							progressDialog.dismiss();
							Message message = new Message();
							message.obj = SUCESS;
							Bundle b = new Bundle();
							b.putString("data", responseText);
							message.setData(b);
							handler.sendMessage(message);

						} else {
							JSONObject responseObj = new JSONObject(responseText);
							responseCode = responseObj.getInt("error_code");
							responseText = responseObj.getString("message");
						}

					} catch (IOException e) {
						responseCode = 408;
						Log.e("SE3", "IO Exception in reading from stream.");
						responseText = "读取服务器返回信息错误";
					}
				}
			} catch (Exception e) {
				responseCode = 408;
				responseText = "返回信息空";
				e.printStackTrace();
			}
			if (responseCode != 0) {
				Message message = new Message();
				message.obj = FAIL;
				Bundle b = new Bundle();
				b.putString("errorCode", String.valueOf(responseCode));
				b.putString("errorMessage", responseText);
				message.setData(b);
				handler.sendMessage(message);
			}
			return responseCode;
		}

	}

}