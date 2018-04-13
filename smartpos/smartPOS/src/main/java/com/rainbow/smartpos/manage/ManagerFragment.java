package com.rainbow.smartpos.manage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.cashdrawer.CashDrawer;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.SmartPosApplication;
import com.rainbow.smartpos.tablestatus.HandOverDialog;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.api.services.scala.DaySettleBillListRequest.IGetClosedBillListsListener;
import com.sanyipos.sdk.api.services.scala._HandoverPreviewRequest.IHandOverPreviewListener;
import com.sanyipos.sdk.model.DaySettleResult;
import com.sanyipos.sdk.model.DisplayItems.HandoverInformationList;
import com.sanyipos.sdk.utils.ConstantsUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ManagerFragment extends Fragment implements OnClickListener {
	public LinearLayout button_fragment_manager_bill_query;
	public LinearLayout button_fragment_manager_handover;
	public LinearLayout button_fragment_manager_dailystat;
	public LinearLayout button_fragment_manager_change_password;
	public LinearLayout button_fragment_manager_about;
	public LinearLayout button_fragment_manager_cash_box;
	public LinearLayout button_fragment_manager_slod_out_manage;
	public LinearLayout button_fragment_manager_reprint_daily_detail;
	public LinearLayout button_fragment_manager_print_slodout_list;
	private boolean isAllShopClose = false;
	ProgressDialog progressDialog;
	AlertDialog dailyStatDialog;
	private CashDrawer cashDrawer = null;

	 MainScreenActivity activity;
	 SmartPosApplication application;

	private LayoutInflater mInflater;
	private Field field;;
	private Dialog dialog;
	private Dialog timeDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_manager, container, false);
		activity = (MainScreenActivity) getActivity();
		cashDrawer = new CashDrawer();
		mInflater = LayoutInflater.from(getActivity());
		application = (SmartPosApplication) getActivity().getApplication();
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(null);
		progressDialog.setMessage("请等候...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setIndeterminateDrawable(activity.getResources().getDrawable(R.drawable.spinner));

		button_fragment_manager_bill_query = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_bill_query);
		button_fragment_manager_bill_query.setOnClickListener(this);
		button_fragment_manager_handover = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_handover);
		button_fragment_manager_handover.setOnClickListener(this);
		button_fragment_manager_dailystat = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_dailystat);
		button_fragment_manager_dailystat.setOnClickListener(this);
		button_fragment_manager_change_password = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_change_password);
		button_fragment_manager_change_password.setOnClickListener(this);
		button_fragment_manager_about = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_about);
		button_fragment_manager_about.setOnClickListener(this);
		button_fragment_manager_cash_box = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_cash_box);
		button_fragment_manager_cash_box.setOnClickListener(this);
		button_fragment_manager_slod_out_manage = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_slod_out_manage);
		button_fragment_manager_slod_out_manage.setOnClickListener(this);
		button_fragment_manager_reprint_daily_detail = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_reprint_daily_detail);
		button_fragment_manager_reprint_daily_detail.setOnClickListener(this);
		button_fragment_manager_print_slodout_list = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_print_slodout_list);
		button_fragment_manager_print_slodout_list.setOnClickListener(this);
		return rootView;
	}

	public void parserMemberURL() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_fragment_manager_bill_query:
			if (SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
				activity.displayView(MainScreenActivity.BILL_FRAGMENT, new Bundle());
			} else {
				Toast.makeText(activity, "只有收银权限才能执行此操作", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.button_fragment_manager_handover:
			if (SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
				SanyiScalaRequests.handOverPreviewRequest(new IHandOverPreviewListener() {


					@Override
					public void onFail(String error) {
						// TODO Auto-generated method stub
						Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(HandoverInformationList resp) {
						// TODO Auto-generated method stub
						HandOverDialog handOverDialog = new HandOverDialog(activity, resp);
					}
				});
			} else {
				Toast.makeText(activity, "只有收银权限才能执行此操作", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.button_fragment_manager_dailystat:
			if (SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_SHOP_CLOSE)) {
				SanyiScalaRequests.daySettleListRequest(new IGetClosedBillListsListener() {



					@Override
					public void onFail(String error) {
						// TODO Auto-generated method stub

						Toast.makeText(activity,error,Toast.LENGTH_LONG).show();

					}

					@Override
					public void onSuccess(DaySettleResult resp) {
						// TODO Auto-generated method stub
						DaySettle d = new DaySettle(activity);
//						d.showDaySettleDialog(resp);
					}
				});
			} else {
				Toast.makeText(activity, "只有日结权限才能执行此操作", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.button_fragment_manager_change_password:
			changePassword();
			break;
		case R.id.button_fragment_manager_about:
			activity.displayView(MainScreenActivity.SETTING, new Bundle());
			break;
		case R.id.button_fragment_manager_cash_box:
			if (SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
				if (null != cashDrawer && cashDrawer.getCashDrawerStatus() == CashDrawer.STATUS_CLOSED) {
					cashDrawer.openCashDrawer();
				}
				SanyiScalaRequests.openDrawerRequest(new ICallBack() {

					
					@Override
					public void onFail(String error) {
						// TODO Auto-generated method stub

						Toast.makeText(activity,error,Toast.LENGTH_LONG).show();

					}
					
					@Override
					public void onSuccess(String status) {
						// TODO Auto-generated method stub

						Toast.makeText(activity,status,Toast.LENGTH_LONG).show();

					}
				});
			} else {
				Toast.makeText(getActivity(), "只有收银权限才能执行此操作", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.button_fragment_manager_slod_out_manage:
			activity.displayView(MainScreenActivity.SLOD_OUT, new Bundle());
			break;
		case R.id.button_fragment_manager_reprint_daily_detail:
			if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_SHOP_CLOSE)) {

				Toast.makeText(activity, "只有日结权限才能执行此操作", Toast.LENGTH_SHORT).show();
				break;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			View view = mInflater.inflate(R.layout.dailystat_datepicker, null);
			builder.setTitle(Html.fromHtml("<font color='#16a4fa'>选择重打时间</font>"));
			final DatePicker datePicker = (DatePicker) view.findViewById(R.id.day_settlement_datePicker);
//			TimePicker timePicker = (TimePicker) view.findViewById(R.id.day_settlement_timePicker);
//			timePicker.setVisibility(View.GONE);
//			Calendar c = Calendar.getInstance();
//			datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
//			datePicker.setMaxDate(c.getTimeInMillis());
//
//			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//					dialog.dismiss();
//				}
//			});
//			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					@SuppressWarnings("deprecation")
//					Date date = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());
//					SanyiScalaRequests.reprintDailyDetailRequest(date, new ICallBack() {
//
//						@Override
//						public void request_timeout() {
//							// TODO Auto-generated method stub
//							MainScreenActivity.toastRequestTimeOut();
//						}
//
//						@Override
//						public void request_fail() {
//							// TODO Auto-generated method stub
//							MainScreenActivity.toastRequestFail();
//						}
//
//						@Override
//						public void onFail(String error) {
//							// TODO Auto-generated method stub
//							MainScreenActivity.toastText(error);
//						}
//
//						@Override
//						public void onSuccess(String status) {
//							// TODO Auto-generated method stub
//							MainScreenActivity.toastText(status);
//						}
//					});
//				}
//			});
//			builder.setView(view);
//			Dialog dayDialog = builder.create();
//			dayDialog.show();
			break;
		case R.id.button_fragment_manager_print_slodout_list:
			SanyiScalaRequests.printSoldoutListRequest(new ICallBack() {

				@Override
				public void onFail(String error) {
					// TODO Auto-generated method stub
					Toast.makeText(activity,error,Toast.LENGTH_LONG).show();

				}

				@Override
				public void onSuccess(String status) {
					// TODO Auto-generated method stub
					Toast.makeText(activity,status,Toast.LENGTH_LONG).show();

				}
			});
			break;
		default:
			break;
		}
	}

	public void changePassword() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View view = mInflater.inflate(R.layout.change_password, null);

		TextView textView_current_staff = (TextView) view.findViewById(R.id.textView_current_staff);
		textView_current_staff.setText(SanyiSDK.currentUser.name);
		TextView textView_current_sn = (TextView) view.findViewById(R.id.textView_current_sn);
		textView_current_sn.setText(Long.toString(SanyiSDK.currentUser.sn));

		final TextView textView_old_password_reminder = (TextView) view.findViewById(R.id.textView_old_password_reminder);
		final TextView textView_new_password_reminder = (TextView) view.findViewById(R.id.textView_new_password_reminder);
		final TextView textView_new_password_reminder_confirm = (TextView) view.findViewById(R.id.textView_new_password_reminder_confirm);

		final EditText textView_old_password = (EditText) view.findViewById(R.id.editText_old_password);
		final EditText textView_input_new_password = (EditText) view.findViewById(R.id.editText_new_password);
		final EditText textView_input_new_password_confirm = (EditText) view.findViewById(R.id.editText_new_password_confirm);
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, int which) {
				try {
					field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
					field.setAccessible(true);
					// 设置mShowing值，欺骗android系统
					field.set(dialog, false);// 如果为true则会推出
				} catch (Exception e) {
					e.printStackTrace();
				}
				String old_password = textView_old_password.getText().toString();
				String new_password = textView_input_new_password.getText().toString();
				String sure_new_password = textView_input_new_password_confirm.getText().toString();
				if (old_password.isEmpty() && new_password.isEmpty() && sure_new_password.isEmpty()) {
					closeInputMethod(view);
					try {
						// 设置mShowing值，欺骗android系统
						field.set(dialog, true);// 如果为true则会推出
					} catch (Exception e) {
						e.printStackTrace();
					}
					dialog.dismiss();
				}
				if (old_password.length() < 3) {
					textView_old_password_reminder.setText("请输入三位数的密码");
					textView_old_password_reminder.setVisibility(View.VISIBLE);
					textView_old_password.setFocusable(true);
					openInputMethod(textView_old_password);
					return;
				}
				if (old_password.length() == 3) {
					// Staff user =
					// SanyiSDK.getStaffByAccessCode(SanyiSDK.currentUser.sn +
					// old_password, true);
					// if (user == SanyiSDK.currentUser) {
					// textView_old_password_reminder.setVisibility(View.INVISIBLE);
					// } else {
					// textView_old_password.setFocusable(true);
					// openInputMethod(textView_old_password);
					// textView_old_password_reminder.setText("您输入的密码不正确");
					// textView_old_password_reminder.setVisibility(View.VISIBLE);
					// return;
					// }
				}
				if (new_password.length() < 3) {
					textView_new_password_reminder.setText("请输入三位数的密码");
					textView_new_password_reminder.setVisibility(View.VISIBLE);
					textView_input_new_password.setFocusable(true);
					openInputMethod(textView_input_new_password);
					return;
				}
				if (sure_new_password.length() < 3) {
					textView_new_password_reminder_confirm.setText("请输入三位数的密码");
					textView_new_password_reminder_confirm.setVisibility(View.VISIBLE);
					textView_input_new_password_confirm.setFocusable(true);
					openInputMethod(textView_input_new_password_confirm);
					return;
				}
				if (new_password.equals(sure_new_password)) {
					progressDialog.show();
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("old-password", old_password));
					nameValuePairs.add(new BasicNameValuePair("new-password", new_password));
					SanyiScalaRequests.changePasswordRequest(old_password, new_password, new Request.ICallBack() {



						@Override
						public void onFail(String error) {
							// TODO Auto-generated method stub
							progressDialog.dismiss();
							Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
						}

						@Override
						public void onSuccess(String status) {
							// TODO Auto-generated method stub
							Toast.makeText(getActivity(), status, Toast.LENGTH_LONG).show();
							progressDialog.dismiss();
							closeInputMethod(view);
							try {
								// 设置mShowing值，欺骗android系统
								field.set(dialog, true);// 如果为true则会推出
							} catch (Exception e) {
								e.printStackTrace();
							}
							dialog.dismiss();
						}
					});
				} else {
					textView_new_password_reminder.setVisibility(View.VISIBLE);
					textView_new_password_reminder.setText("两次输入密码不一致");
					textView_input_new_password.setFocusable(true);
					openInputMethod(textView_input_new_password);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				closeInputMethod(view);
				try {
					// 设置mShowing值，欺骗android系统
					field.set(dialog, true);// 如果为true则会推出
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		});
		textView_old_password.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				textView_old_password_reminder.setVisibility(View.INVISIBLE);
				return false;
			}
		});
		textView_input_new_password.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				textView_new_password_reminder.setVisibility(View.INVISIBLE);
				String str = textView_input_new_password.getText().toString();
				if (str.length() == 3) {
					textView_new_password_reminder.setVisibility(View.INVISIBLE);
				}
				return false;
			}
		});

		textView_input_new_password_confirm.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				textView_new_password_reminder_confirm.setVisibility(View.INVISIBLE);
				textView_new_password_reminder.setVisibility(View.INVISIBLE);
				String str = textView_input_new_password_confirm.getText().toString();
				if (str.length() == 3) {
					textView_new_password_reminder_confirm.setVisibility(View.INVISIBLE);
				}
				return false;
			}
		});
		builder.setView(view);
		final Dialog dialog = builder.create();
		dialog.setTitle("修改密码");
		dialog.show();
	}

	public void closeInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void openInputMethod(View view) {
		InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanger.showSoftInput(view, 0);
	}

}
