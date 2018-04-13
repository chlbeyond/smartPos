package com.rainbow.smartpos.check;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.MemberQueryRequest.IMemberQueryListener;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.model.scala.check.MembersResult;
import com.sanyipos.sdk.utils.ConstantsUtil;

import java.util.List;

public class QueryMemberDialog {

	private static final int SUCESS = 1;
	private static final int FAIL = 0;

	public static interface QueryMemberInterface {
		public void onQuerySuccess(List<MemberInfo> memberInfos);

		public void onQueryFailed(String exception);

		public void onRequestFailed();

		public void onQueryCanceled();
	}

	private EditText editTextInputValue;
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
	private String addl_text = "";
	MainScreenActivity activity;

	TextView progressStatus;
	Button buttonOk;
	Button buttonCancel;
	Button buttonQuery;
	private ImageButton buttonNumPadDelete;
	private ImageButton buttonNumPadCancel;
	private TextView textHint;

	AlertDialog alertDlg;
	private ListView queryResult;
	private MembersResult membersResult;
	private MemberInfoAdapter adapter;
	private QueryMemberInterface queryMemberInterface;

	boolean scannerInput = false;

	public void show(final Activity a, final String promptString, final QueryMemberInterface queryMemberInterface) {
		activity = (MainScreenActivity) a;
		this.queryMemberInterface = queryMemberInterface;
		membersResult = new MembersResult();
		Builder dlg = new AlertDialog.Builder(a, R.style.DialogTheme);
		String str = "<font color='#33B5E5'>" + promptString + "</font>";
		// Inflate the Dialog layout
		dlg.setTitle(Html.fromHtml(str));
		LayoutInflater inflater = a.getLayoutInflater();
		View iView = inflater.inflate(R.layout.member_query_dialog, null, false);
		textHint = (TextView) iView.findViewById(R.id.textHint);
		textHint.setText("请输入手机号、会员卡号或刷卡");
		buttonCancel = (Button) iView.findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				queryMemberInterface.onQueryCanceled();
				alertDlg.dismiss();
			}
		});

		buttonQuery = (Button) iView.findViewById(R.id.buttonQuery);
		buttonQuery.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!editTextInputValue.getText().toString().equals("")) {
					queryMemberRquest(editTextInputValue.getText().toString());
				}
			}
		});

		editTextInputValue = (EditText) iView.findViewById(R.id.editTextInputValue);
		editTextInputValue.setInputType(InputType.TYPE_NULL);
		editTextInputValue.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					scannerInput = true;
					if (!editTextInputValue.getText().toString().equals("")) {
						queryMemberRquest(editTextInputValue.getText().toString());
					}
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

		buttonNumPadCancel = (ImageButton) iView.findViewById(R.id.buttonNumPadCancel);
		buttonNumPadDelete = (ImageButton) iView.findViewById(R.id.buttonNumPadDelete);

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

	public void queryMemberRquest(String member) {
		final ProgressDialog progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(null);
		progressDialog.setMessage("请等候");
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setIndeterminateDrawable(activity.getResources().getDrawable(R.drawable.spinner));
		progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		progressDialog.show();
		SanyiScalaRequests.memberQueryRequest(member, ConstantsUtil.Member.all, new IMemberQueryListener() {


			@Override
			public void onFail(String error) {
				// TODO Auto-generated method stub
				queryMemberInterface.onQueryFailed(error);
				editTextInputValue.setText("");
				progressDialog.dismiss();
			}

			@Override
			public void onSuccess(MembersResult result) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				if (!result.members.isEmpty()) {
					queryMemberInterface.onQuerySuccess(result.members);
					alertDlg.dismiss();
				} else {
					queryMemberInterface.onQueryFailed("找不到该会员信息，请检查是否输入正确");
					editTextInputValue.setText("");
				}
			}
		});
	}

	public String getVoucherNo() {
		return editTextInputValue.getText().toString();
	}

	void appendNumber(String inNumb) {
		editTextInputValue.setText(editTextInputValue.getText() + inNumb);
	}

//	public void showQueryResultDialog(final MemberInfo memberInfo, MainScreenActivity activity) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//		builder.setTitle("会员信息");
//		View view = LayoutInflater.from(activity).inflate(R.layout.query_member_dialog_item, null);
//		TextView member_sn = (TextView) view.findViewById(R.id.member_sn);
//		TextView member_name = (TextView) view.findViewById(R.id.member_name);
//		TextView member_type = (TextView) view.findViewById(R.id.member_type);
//		TextView member_moblie = (TextView) view.findViewById(R.id.member_moblie);
//		member_sn.setText("会员编号:    " + memberInfo.sn);
//		member_name.setText("会员姓名:    " + memberInfo.name);
//		member_type.setText("会员类型:    " + memberInfo.memberTypeName);
//		member_moblie.setText("会员手机:    " + memberInfo.mobile);
//		builder.setPositiveButton(R.string.sure, new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface Dialog, int which) {
//				// TODO Auto-generated method stub
//				Dialog.dismiss();
//				alertDlg.dismiss();
//				queryMemberInterface.onQuerySuccess(memberInfo);
//			}
//		});
//		builder.setView(view);
//		AlertDialog alertDialog = builder.create();
//		alertDialog.show();
//	}

	private class MemberInfoAdapter extends BaseAdapter {
		private MembersResult membersResult;
		private Context context;
		private LayoutInflater mInflater;
		private int currentSelectPos = -1;

		public MemberInfoAdapter(MembersResult membersResult, Context context) {
			super();
			this.membersResult = membersResult;
			this.context = context;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return membersResult.members.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.query_member_dialog_item, null);
				holder.member_sn = (TextView) convertView.findViewById(R.id.member_sn);
				holder.member_name = (TextView) convertView.findViewById(R.id.member_name);
				holder.member_type = (TextView) convertView.findViewById(R.id.member_type);
				holder.member_moblie = (TextView) convertView.findViewById(R.id.member_moblie);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MemberInfo memberInfo = membersResult.members.get(position);
			holder.member_sn.setText("会员编号:" + memberInfo.sn);
			holder.member_name.setText("会员姓名:" + memberInfo.name);
			holder.member_type.setText("会员类型:" + memberInfo.memberTypeName);
			holder.member_moblie.setText("会员手机:" + memberInfo.mobile);
			convertView.setBackgroundColor(Color.WHITE);
			if (currentSelectPos == position) {
				convertView.setBackgroundColor(Color.BLUE);
			}
			return convertView;
		}

		public void setSelect(int position) {
			currentSelectPos = position;
		}

		public int getSelect() {
			return currentSelectPos;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			TextView member_sn;
			TextView member_name;
			TextView member_type;
			TextView member_moblie;
		}

	}
}