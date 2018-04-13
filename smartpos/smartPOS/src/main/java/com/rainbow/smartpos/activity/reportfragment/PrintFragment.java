package com.rainbow.smartpos.activity.reportfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rainbow.common.view.wheelview.WheelView;
import com.rainbow.common.view.wheelview.adapter.ArrayWheelAdapter;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.DateHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ss on 2016/9/12.
 */
public class PrintFragment extends Fragment implements View.OnClickListener {


    public LinearLayout button_fragment_manager_reprint_daily_detail;
    public Context mContext;
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_print, container, false);
        button_fragment_manager_reprint_daily_detail = (LinearLayout) rootView.findViewById(R.id.button_fragment_manager_reprint_daily_detail);
        button_fragment_manager_reprint_daily_detail.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.button_fragment_manager_reprint_daily_detail:
                if (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_SHOP_CLOSE)) {

                    Toast.makeText(mContext,"没有权限",Toast.LENGTH_LONG).show();
                    return;
                }

                final NormalDialog pikeDayDialog = new NormalDialog(mContext);
                final View pickDayDialog = LayoutInflater.from(mContext).inflate(R.layout.layout_business_date_pick_day, null);
                final List<String> dates = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                calendar.add(Calendar.DAY_OF_MONTH,0);
                dates.add(DateHelper.dateFormater.format(calendar.getTime()));
                for (int i = 0; i < 30; i++) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    dates.add(DateHelper.dateFormater.format(calendar.getTime()));
                }

                final WheelView printTime = (WheelView) pickDayDialog.findViewById(R.id.year_month_day);
                ArrayWheelAdapter<String> weekAdapter = new ArrayWheelAdapter<>(mContext, dates);
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
                                    Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();


                                }

                                @Override
                                public void onSuccess(String status) {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(mContext,status,Toast.LENGTH_LONG).show();

                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;

        }
    }
}
