package com.rainbow.smartpos.manage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.api.services.scala.DaySettlePreviewRequest;
import com.sanyipos.sdk.model.DisplayItems;

import java.util.ArrayList;
import java.util.List;

public class DaySettle {
    MainScreenActivity activity;
    private LayoutInflater mInflater;
    private Dialog timeDialog;
    private boolean isAllShopClose;
    private AlertDialog dailyStatDialog;

    public DaySettle(Activity activity) {
        this.activity = (MainScreenActivity) activity;
        mInflater = LayoutInflater.from(activity);
    }

//	public void showDaySettleDialog(final DaySettleResult result) {
//		if (result.businessDays.isEmpty()) {
//			Toast.makeText(activity, "没有未日结账单", Toast.LENGTH_LONG).show();
//			return;
//		}
//		// final Date now = new Date();
//		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//		View view = mInflater.inflate(R.layout.dialog_shop_close, null);
//		builder.setTitle(Html.fromHtml("<font color='#16a4fa'>日结</font>"));
//		final TextView textViewDailyStateStart = (TextView) view.findViewById(R.id.textView2);
//		textViewDailyStateStart.setText(DateHelper.noSecondFormater.format(result.beginDate));
//		final TextView textViewDailyStatDatePicker = (TextView) view.findViewById(R.id.textView_DailyStat_DatePicker);
//		textViewDailyStatDatePicker.setText(DateHelper.noSecondFormater.format(result.endDate));
//		final ArrayAdapter<String> bizDayArrayAdapter = new ArrayAdapter<String>(activity, R.layout.dailystat_spinner);
//		for (int i = 0; i < result.businessDays.size(); i++) {
//			bizDayArrayAdapter.add(DateHelper.dateFormater.format(result.businessDays.get(i)));
//		}
//		textViewDailyStatDatePicker.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//				View view = mInflater.inflate(R.layout.dailystat_datepicker, null);
//				builder.setTitle(Html.fromHtml("<font color='#16a4fa'>选择日结时间</font>"));
//				final DatePicker datePicker = (DatePicker) view.findViewById(R.id.day_settlement_datePicker);
//				final TimePicker timePicker = (TimePicker) view.findViewById(R.id.day_settlement_timePicker);
//				datePicker.init(result.beginDate.getYear() + 1900, result.beginDate.getMonth(), result.beginDate.getDate(), null);
//				long time = result.endDate.getTime();
//				datePicker.setMaxDate(time);
//				timePicker.setCurrentHour(result.endDate.getHours());
//				timePicker.setCurrentMinute(result.endDate.getMinutes());
//				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						timeDialog.dismiss();
//					}
//				});
//				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//					@SuppressWarnings("deprecation")
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						@SuppressWarnings("deprecation")
//						Date pickEndTime = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
//						pickEndTime.setSeconds(59);
//						textViewDailyStatDatePicker.setText(DateHelper.noSecondFormater.format(pickEndTime));
//						timeDialog.dismiss();
//					}
//				});
//				builder.setView(view);
//				timeDialog = builder.create();
//				timeDialog.show();
//			}
//		});
//		final Spinner bizDaySpinner = (Spinner) view.findViewById(R.id.spinner_DailyStat);
//		bizDaySpinner.setAdapter(bizDayArrayAdapter);
//		for (int i = 0; i < result.businessDays.size(); i++) {
//			if (result.businessDays.get(i).equals(result.businessDay)) {
//				bizDaySpinner.setSelection(i);
//			}
//		}
//		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				progressDialog.dismiss();
//			}
//		});
//		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				String endTime = null;
//				// boolean validEndTime = true;
//				try {
//					endTime = textViewDailyStatDatePicker.getText().toString() + ":00";
//				} catch (Exception e) {
//					e.printStackTrace();
//					// validEndTime = false;
//				}
//				String businessDate = (String) bizDaySpinner.getSelectedItem();
//				getDaySettlePreview(endTime, businessDate);
//			}
//		});
//		builder.setView(view);
//		Dialog dialog = builder.create();
//		dialog.show();
//	}

    public void getDaySettlePreview(final String endTime, final String businessDay) {
        SanyiScalaRequests.daySettlePreviewRequest(endTime + ":00", businessDay, new DaySettlePreviewRequest.IDaySettlePreviewListener() {



            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity,error,Toast.LENGTH_LONG).show();


            }

            public void onSuccess(final DisplayItems.HandoverInformationList resp) {
                // TODO Auto-generated method stub
                if (resp.handoverInfos.size() > 0) {
                    AlertDialog.Builder builderDialog = new AlertDialog.Builder(activity);
                    builderDialog.setTitle(Html.fromHtml("<font color='#000000'>日结清单</font>" + "<font color='#571614' >(请仔细核对，一旦日结后将不能执行反操作)</font>"));
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View iView = inflater.inflate(R.layout.handover_preview, null, false);
                    ExpandableListView detailView = (ExpandableListView) iView.findViewById(R.id.listViewHandoverInfo);
                    List<String> groupList = new ArrayList<String>();
                    List<List<DisplayItems.DisplayAmountItem>> childList = new ArrayList<List<DisplayItems.DisplayAmountItem>>();
                    groupList.add("收银");
                    groupList.add("减免");
                    childList.add(resp.handoverInfos);
                    childList.add(resp.waiveInfos);
                    HandoverAdapter adapter = new HandoverAdapter(groupList, childList, activity);
                    detailView.setAdapter(adapter);
                    for (int i = 0; i < adapter.getGroupCount(); i++) {
                        detailView.expandGroup(i);
                    }
                    builderDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dailyStatDialog.dismiss();
                        }
                    });
                    builderDialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dailyStatDialog.dismiss();
                            shopClosedNow(endTime, businessDay);
                        }
                    });
                    builderDialog.setView(iView);
                    dailyStatDialog = builderDialog.create();
                    dailyStatDialog.setCancelable(false);
                    dailyStatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dailyStatDialog.show();
                } else {
                    Toast.makeText(activity, "日结清单获取有误，请重试", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    public void shopClosedNow(String endTime, String businessDay) {
        SanyiScalaRequests.daySettleRequest(endTime, businessDay, new ICallBack() {

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
    }
}
