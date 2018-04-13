package com.rainbow.smartpos.mainframework;

import android.cashdrawer.CashDrawer;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.TimePicker.view.WheelTime;
import com.rainbow.common.view.wheelview.WheelView;
import com.rainbow.common.view.wheelview.adapter.ArrayWheelAdapter;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.activity.AboutActivity;
import com.rainbow.smartpos.activity.ChangePsdActivity;
import com.rainbow.smartpos.activity.ReportActivity;
import com.rainbow.smartpos.activity.SettingActivity;
import com.rainbow.smartpos.dialog.DaySetNoticeDialog;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.manage.HandoverAdapter;
import com.rainbow.smartpos.member.MemberFragment;
import com.rainbow.smartpos.sold.SoldFragment;
import com.rainbow.smartpos.tablestatus.HandOverDialog;
import com.rainbow.smartpos.tablestatus.ShopPrinterAdapter;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.DaySettleBillListRequest;
import com.sanyipos.sdk.api.services.scala.DaySettlePreviewRequest;
import com.sanyipos.sdk.api.services.scala._HandoverPreviewRequest;
import com.sanyipos.sdk.api.services.scala._TestPrintRequest;
import com.sanyipos.sdk.model.DaySettleResult;
import com.sanyipos.sdk.model.DisplayItems;
import com.sanyipos.sdk.model.ShopPrinter;
import com.sanyipos.sdk.model.TestPrintResult;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.DateHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ss on 2016/1/19.
 */
public class LeftMenuView {
    private static final String TAG = "LeftMenuView";
    private Context mContext;
    private WheelTime wheelTime;
    private WheelView wl_ymd;
    private String pickBusinessDay;
    private TextView endTime;
    private TextView textViewPickDay;
    private EditText mOldPsd;
    private EditText mNewPsd;
    private EditText mConfirmPsd;
    private CashDrawer cashDrawer = null;
    private MainScreenActivity activity;

    public LeftMenuView(Context context, MainScreenActivity activity) {
        this.mContext = context;
        this.activity = activity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_menu_slod_out:
//                activity.displayView(MainScreenActivity.SLOD_OUT, null);
                Intent intentSlod = new Intent(activity, SoldFragment.class);
                activity.startActivity(intentSlod);
                break;
            case R.id.left_menu_log_out:
                activity.logOut();
                break;
            case R.id.left_menu_change_psd:
                Intent intentPsd = new Intent(activity, ChangePsdActivity.class);
                activity.startActivity(intentPsd);
//                final NormalDialog changepsdDialog = new NormalDialog(mContext);
//                View changepsdView = LayoutInflater.from(mContext).inflate(R.layout.layout_change_psd, null);
//                changepsdDialog.title("修改密码");
//                changepsdDialog.content(changepsdView);
//                TextView mCurrentUser = (TextView) changepsdView.findViewById(R.id.textView_change_psd_current_user);
//                mCurrentUser.setText(SanyiSDK.currentUser.getName());
//                final TextView mCurrentNumber = (TextView) changepsdView.findViewById(R.id.textView_change_psd_user_number);
//                mCurrentNumber.setText(SanyiSDK.currentUser.getSn());
//                mOldPsd = (EditText) changepsdView.findViewById(R.id.editText_change_psd_old);
//                mNewPsd = (EditText) changepsdView.findViewById(R.id.editText_change_psd_new);
//                mConfirmPsd = (EditText) changepsdView.findViewById(R.id.editText_change_psd_confirm);
//                mNewPsd.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//
//                    }
//                });
//                mConfirmPsd.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//
//                    }
//                });
//                changepsdDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
//                    @Override
//                    public void onClickConfirm() {
//                        if (mOldPsd.getText().toString().length() < 3) {
//
//                            ToastUtils.showShort(activity, "请输入三位数旧密码");
//                            return;
//                        }
//                        if (mNewPsd.getText().toString().length() < 3) {
//
//                            ToastUtils.showShort(activity, "请输入三位数新密码");
//                            return;
//                        }
//                        if (mConfirmPsd.getText().toString().length() < 3) {
//
//                            ToastUtils.showShort(activity, "请输入三位数新密码");
//                            return;
//                        }
//                        if (!mNewPsd.getText().toString().equals(mConfirmPsd.getText().toString())) {
//
//                            ToastUtils.showShort(activity, "两次输入密码不一致");
//                            return;
//                        }
//                        SanyiScalaRequests.changePasswordRequest(mOldPsd.getText().toString(), mNewPsd.getText().toString(), new Request.ICallBack() {
//                            @Override
//                            public void onSuccess(String status) {
//
//
//                                ToastUtils.showShort(activity, status);
//                                changepsdDialog.dismiss();
//                            }
//
//                            @Override
//                            public void request_timeout() {
//
//                            }
//
//                            @Override
//                            public void request_fail() {
//
//                            }
//
//                            @Override
//                            public void onFail(String error) {
//
//
//                                ToastUtils.showShort(activity, error);
//                            }
//                        });
//                    }
//                });
//                changepsdDialog.widthScale((float) 0.5);
//
//                changepsdDialog.show();
                break;
            case R.id.left_menu_reprint_daily_report:
                if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_SHOP_CLOSE)) {


                    Toast.makeText(activity, "没有权限", Toast.LENGTH_LONG).show();
                    return;
                }

                final NormalDialog pikeDayDialog = new NormalDialog(mContext);
                final View pickDayDialog = LayoutInflater.from(mContext).inflate(R.layout.layout_business_date_pick_day, null);
                final List<String> dates = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                for (int i = 0; i < 30; i++) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    dates.add(DateHelper.dateFormater.format(calendar.getTime()));
                }

                final WheelView printTime = (WheelView) pickDayDialog.findViewById(R.id.year_month_day);
                ArrayWheelAdapter<String> weekAdapter = new ArrayWheelAdapter<>(activity, dates);
                printTime.setViewAdapter(weekAdapter);
                pikeDayDialog.title("重打日结详单");
                pikeDayDialog.widthScale((float) 0.5);
                pikeDayDialog.heightScale((float) 0.3);
                pikeDayDialog.content(pickDayDialog);
                pikeDayDialog.show();
                pikeDayDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        pikeDayDialog.superDismiss();
                        try {
                            SanyiScalaRequests.reprintDailyDetailRequest(DateHelper.dateFormater.parse(dates.get(printTime.getCurrentItem())), new Request.ICallBack() {


                                @Override
                                public void onFail(String error) {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onSuccess(String status) {
                                    // TODO Auto-generated method stub

                                    Toast.makeText(activity, status, Toast.LENGTH_LONG).show();


                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });


//                final NormalDialog reprintDialog = new NormalDialog(mContext);
//                final View pickDayDialog = LayoutInflater.from(mContext).inflate(R.layout.layout_business_date_pick, null);
//                final View loopView = pickDayDialog.findViewById(R.id.time_picker);
//                final WheelTime pickDayWheel = new WheelTime(loopView, WheelTime.Type.YEAR_MONTH_DAY);
//                Date date = new Date();
//                pickDayWheel.setEndTime(date);
//                setDate(pickDayWheel, date);
//                pickDayWheel.setCyclic(false);
//                reprintDialog.content(pickDayDialog);
//                reprintDialog.title("选择重打时间");
//                reprintDialog.widthScale((float) 0.5);
//                Date getDate = null;
//                try {
//                    getDate = DateHelper.dateFormater.parse(pickDayWheel.getTime());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                final Date finalGetDate = getDate;
//                reprintDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
//                    @Override
//                    public void onClickConfirm() {
//
//                    }
//                });
//                reprintDialog.show();
                break;
            case R.id.left_menu_business_daily:
                if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_SHOP_CLOSE)) {

                    Toast.makeText(activity, "没有权限", Toast.LENGTH_LONG).show();
                    return;
                }
                pickBusinessDay = null;
                SanyiScalaRequests.daySettleListRequest(new DaySettleBillListRequest.IGetClosedBillListsListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub
                        final NormalDialog dialog = new NormalDialog(mContext);
                        dialog.content(error);
                        dialog.widthScale((float) 0.5);
                        dialog.heightScale((float) 0.3);
                        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                            @Override
                            public void onClickConfirm() {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }

                    @Override
                    public void onSuccess(final DaySettleResult resp) {
                        // TODO Auto-generated method stub
                        final NormalDialog businessDialog = new NormalDialog(mContext);

                        View businessView = LayoutInflater.from(mContext).inflate(R.layout.dialog_shop_close, null);
                        TextView startTime = (TextView) businessView.findViewById(R.id.textView_business_start_time);
                        startTime.setText(DateHelper.noSecondFormater.format(resp.beginDate) + "  ");
                        endTime = (TextView) businessView.findViewById(R.id.textView_business_end_time);
                        endTime.setText(DateHelper.noSecondFormater.format(resp.endDate) + " >");
                        endTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final NormalDialog pickDialog = new NormalDialog(mContext);
                                View pickView = LayoutInflater.from(mContext).inflate(R.layout.layout_business_date_pick, null);
                                View timePickView = pickView.findViewById(R.id.time_picker);
                                wheelTime = new WheelTime(timePickView, WheelTime.Type.ALL);
                                wheelTime.setBeginTime(resp.beginDate);
                                wheelTime.setEndTime(resp.endDate);
                                setDate(wheelTime, resp.endDate);
                                wheelTime.setCyclic(false);
                                pickDialog.content(pickView);
                                pickDialog.widthScale((float) 0.5);
                                pickDialog.heightScale((float) 0.3);
                                pickDialog.show();
                                pickDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                                    @Override
                                    public void onClickConfirm() {
                                        pickDialog.superDismiss();
                                        endTime.setText(wheelTime.getTime() + " >");
                                    }
                                });
                            }
                        });
                        textViewPickDay = (TextView) businessView.findViewById(R.id.textView_pick_business_day);
                        textViewPickDay.setText(DateHelper.dateFormater.format(resp.businessDay));
                        textViewPickDay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pikeDay(resp.businessDays);
                            }
                        });
                        businessDialog.content(businessView);
                        businessDialog.widthScale((float) 0.5);
                        businessDialog.heightScale((float) 0.3);
                        businessDialog.show();
                        businessDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                            @Override
                            public void onClickConfirm() {
                                String end = endTime.getText().toString();
                                end = end.substring(0, end.length() - 2);
                                getDaySettlePreview(end, textViewPickDay.getText().toString());
                                businessDialog.superDismiss();

                            }
                        });
                    }
                });


                break;
            case R.id.left_menu_hand_over:
                if (SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
                    SanyiScalaRequests.handOverPreviewRequest(new _HandoverPreviewRequest.IHandOverPreviewListener() {


                        @Override
                        public void onFail(String error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(DisplayItems.HandoverInformationList resp) {
                            // TODO Auto-generated method stub
                            HandOverDialog handOverDialog = new HandOverDialog(mContext, resp);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "只有收银权限才能执行此操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.left_menu_open_money:
//
                if (SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER)) {
                    try {
                        cashDrawer = new CashDrawer();

                        if (null != cashDrawer && cashDrawer.getCashDrawerStatus() == CashDrawer.STATUS_CLOSED) {
                            cashDrawer.openCashDrawer();
                        }
                        SanyiScalaRequests.openDrawerRequest(new Request.ICallBack() {


                            @Override
                            public void onFail(String error) {
                                // TODO Auto-generated method stub

                                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onSuccess(String status) {
                                // TODO Auto-generated method stub

                                Toast.makeText(activity, status, Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "只有收银权限才能执行此操作", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.left_menu_member:
                Intent intent = new Intent(activity, MemberFragment.class);
                activity.startActivity(intent);
                break;
            case R.id.left_menu_print_setting:
                NormalDialog printerDialog = new NormalDialog(activity);
                View printView = LayoutInflater.from(activity).inflate(R.layout.dialog_shop_printer, null);
                printerDialog.title("打印机状态");
                printerDialog.content(printView);
                printerDialog.heightScale((float) 0.7);
                printerDialog.widthScale((float) 0.5);
                printerDialog.isHasConfirm(false);
                ShopPrinterAdapter shopPrinterAdapter = new ShopPrinterAdapter(activity);
                ListView listView_dialog_shop_printer_list = (ListView) printView.findViewById(R.id.listView_dialog_shop_printer_list);
                Button button_shop_printer_item_test = (Button) printView.findViewById(R.id.button_shop_printer_item_test);
                button_shop_printer_item_test.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        List<Long> printers = new ArrayList<Long>();
                        for (ShopPrinter printer : SanyiSDK.rest.operationData.shopPrinters) {
                            printers.add(printer.id);
                        }
                        SanyiScalaRequests.testPrinterRequest(printers, new _TestPrintRequest.ITestPrintListener() {
                            @Override
                            public void onSuccess(TestPrintResult result) {

                            }

                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                });
                listView_dialog_shop_printer_list.setAdapter(shopPrinterAdapter);
                printerDialog.show();
                break;
            case R.id.left_menu_print_sale_detail:
                if (!SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_REPORT)) {
                    Toast.makeText(mContext, "只有报表查看权限才能执行此操作", Toast.LENGTH_LONG).show();
                    return;
                }
                SanyiScalaRequests.printSalesStatisticsRequest(new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {
                        Toast.makeText(activity, status, Toast.LENGTH_LONG).show();
                    }


                    @Override
                    public void onFail(String error) {
                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.left_menu_local_setting:
                if (true) {
                    Intent intentSetting = new Intent(activity, SettingActivity.class);
                    activity.startActivity(intentSetting);
                    return;
                }

                final NormalDialog normalDialog = new NormalDialog(mContext);
                View settingView = LayoutInflater.from(mContext).inflate(R.layout.layout_local_setting, null);
                normalDialog.content(settingView);
                normalDialog.widthScale((float) 0.5);
                normalDialog.heightScale((float) 0.6);
                normalDialog.title("本机参数");
                break;
            case R.id.left_menu_abouts:
                Intent intentAbout = new Intent(activity, AboutActivity.class);
                activity.startActivity(intentAbout);

//                final NormalDialog aboutDialog = new NormalDialog(mContext);
//                View aboutView = LayoutInflater.from(mContext).inflate(R.layout.layout_left_menu_about, null);
//                aboutDialog.content(aboutView);
//                TextView version = (TextView) aboutView.findViewById(R.id.textView_about_version);
//                version.setText(Restaurant.getVersionName(mContext) + " - Build No." + String.valueOf(Restaurant.getVersionCode(mContext) + " " + SanyiSDK.rest.config.mode));
//                TextView shopName = (TextView) aboutView.findViewById(R.id.textView_about_shop_name);
//                shopName.setText(SanyiSDK.rest.operationData.shop.name);
//                aboutDialog.widthScale((float) 0.5);
//                aboutDialog.title("关于");
//                aboutDialog.show();
//                aboutDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
//                    @Override
//                    public void onClickConfirm() {
//                        aboutDialog.dismiss();
//                    }
//                });
                break;
            case R.id.left_menu_today_bills:
                if (!SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_CASHIER)) {
                    Toast.makeText(mContext, "只有收银权限才能执行此操作", Toast.LENGTH_SHORT).show();
                    return;
                }
//                activity.displayView(MainScreenActivity.BILL_FRAGMENT, null);
                Intent billIntent = new Intent(mContext, ReportActivity.class);
                mContext.startActivity(billIntent);
                break;
        }
    }

    public void pikeDay(final List<Date> date) {
        final NormalDialog pikeDayDialog = new NormalDialog(mContext);
        final View pickDayDialog = LayoutInflater.from(mContext).inflate(R.layout.layout_business_date_pick_day, null);
        final List<String> dates = new ArrayList<>();
        for (Date dat : date) {
            if (!dates.contains(dat)) {
                dates.add(DateHelper.dateFormater.format(dat));
            }
        }
        wl_ymd = (WheelView) pickDayDialog.findViewById(R.id.year_month_day);
        ArrayWheelAdapter<String> weekAdapter = new ArrayWheelAdapter<>(activity, dates);
        wl_ymd.setViewAdapter(weekAdapter);
        pikeDayDialog.title("日结");
        pikeDayDialog.widthScale((float) 0.5);
        pikeDayDialog.heightScale((float) 0.3);
        pikeDayDialog.content(pickDayDialog);
        pikeDayDialog.show();
        pikeDayDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                pikeDayDialog.superDismiss();
                pickBusinessDay = dates.get(wl_ymd.getCurrentItem());
                textViewPickDay.setText(pickBusinessDay);
            }
        });


    }

    public void setDate(WheelTime wheelTime, Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null)
            calendar.setTimeInMillis(System.currentTimeMillis());
        else
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);
    }


    public void getDaySettlePreview(final String endTime, final String businessDay) {
        SanyiScalaRequests.daySettlePreviewRequest(endTime + ":00", businessDay, new DaySettlePreviewRequest.IDaySettlePreviewListener() {

            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }

            public void onSuccess(final DisplayItems.HandoverInformationList resp) {
                // TODO Auto-generated method stub
                View iView = LayoutInflater.from(mContext).inflate(R.layout.handover_preview, null, false);
                ExpandableListView detailView = (ExpandableListView) iView.findViewById(R.id.listViewHandoverInfo);
                initData(resp);
                HandoverAdapter adapter = new HandoverAdapter(groupList, childList, mContext);
                detailView.setAdapter(adapter);
                detailView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                        return true;
                    }
                });

                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    detailView.expandGroup(i);
                }

                final NormalDialog dialog = new NormalDialog(mContext);
                dialog.content(iView);
                dialog.title("日结预览");
                dialog.widthScale((float) 0.5);
                dialog.heightScale((float) 0.7);
                dialog.show();
                dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        isRemind(resp, endTime, businessDay);
                        dialog.dismiss();
                    }
                });

            }

        });
    }


    public void isRemind(DisplayItems.HandoverInformationList resp, final String endTime, final String businessDay) {
        if (resp.reminder != null) {
//            final NormalDialog dialog = new NormalDialog(mContext);
//            dialog.content(resp.reminder);
//            dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
//                @Override
//                public void onClickConfirm() {
//                    dialog.dismiss();
//                    shopClosedNow(endTime, businessDay);
//                }
//            });
//            dialog.show();

            final DaySetNoticeDialog dialog=new DaySetNoticeDialog(mContext, resp.reminder, new DaySetNoticeDialog.IPaySuccessListener() {
                @Override
                public void onComfirm() {
                    shopClosedNow(endTime, businessDay);
                }
            });
            dialog.show();
        } else {
            shopClosedNow(endTime, businessDay);
        }

    }

    private void shopClosedNow(String endTime, String businessDay) {
        SanyiScalaRequests.daySettleRequest(endTime + ":00", businessDay, new Request.ICallBack() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String status) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<String> groupList;
    private List<List<DisplayItems.DisplayAmountItem>> childList;

    private void initData(DisplayItems.HandoverInformationList handoverInformationList) {
        groupList = new ArrayList<String>();
        childList = new ArrayList<List<DisplayItems.DisplayAmountItem>>();
        groupList.add("实收");
        groupList.add("会员");
        groupList.add("虚收");
        childList.add(handoverInformationList.handoverInfos);
        childList.add(handoverInformationList.memberInfos);
        childList.add(handoverInformationList.waiveInfos);
    }

}
