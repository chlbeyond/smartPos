package com.rainbow.smartpos.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rainbow.smartpos.BaseActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.activity.reportfragment.DetailSalesReportFragment;
import com.rainbow.smartpos.activity.reportfragment.HandoverFragment;
import com.rainbow.smartpos.activity.reportfragment.PrintFragment;
import com.rainbow.smartpos.activity.reportfragment.WebViewFragment;
import com.rainbow.smartpos.bill.newbill.NewBillFragment;
import com.rainbow.smartpos.refund.RefundFragment;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.DataUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ss on 2016/8/31.
 */
public class ReportActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    @Bind(R.id.radioButton_report_today_bill)
    RadioButton radioButtonTodayBill;
    //
//    @Bind(R.id.textView_report_business)
//    TextView textViewReportBusiness;
//
    @Bind(R.id.radioButton_report_handover)
    RadioButton radioButtonReportHnadover;

    @Bind(R.id.radioButton_report_print)
    RadioButton radioButtonReportPrint;

    @Bind(R.id.radioButton_report_detail_sales)
    RadioButton radioButtonReportDetailSale;

    @Bind(R.id.radioButton_report_incoming)
    RadioButton radioButtonReportIncoming;

    @Bind(R.id.radioButton_report_goodsgroup)
    RadioButton radioButtonReportGoodsGroup;

    @Bind(R.id.radioGroup_report)
    RadioGroup radioGroupReport;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        radioGroupReport.setOnCheckedChangeListener(this);
        setCustomTitle("报表");
        if (!SanyiSDK.rest.config.isInvoiceReport) {
            radioButtonReportIncoming.setVisibility(View.GONE);
            radioButtonReportGoodsGroup.setVisibility(View.GONE);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, new NewBillFragment()).commit();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButton_report_today_bill:
                if (!SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_CASHIER)) {
                    Toast.makeText(this, "只有收银权限才能执行此操作", Toast.LENGTH_LONG).show();
                    return;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, new NewBillFragment()).commit();
                break;
            case R.id.radioButton_report_handover:
                if (!SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_CASHIER)) {
                    Toast.makeText(this, "只有收银权限才能执行此操作", Toast.LENGTH_LONG).show();
                    return;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, new HandoverFragment()).commit();
                break;
            case R.id.radioButton_report_print:
                if (!SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_REPORT)) {
                    Toast.makeText(this, "只有报表查看权限才能执行此操作", Toast.LENGTH_LONG).show();
                    return;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, new PrintFragment()).commit();
                break;
            case R.id.radioButton_report_detail_sales:
                if (!SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_REPORT)) {
                    Toast.makeText(this, "只有报表查看权限才能执行此操作", Toast.LENGTH_LONG).show();
                    return;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, new DetailSalesReportFragment()).commit();
                break;
            case R.id.radioButton_report_incoming:
                WebViewFragment incoming = new WebViewFragment();
                incoming.setUrl(DataUtil.parseIncomingReportURL());
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, incoming).commit();
                break;
            case R.id.radioButton_report_goodsgroup:
                WebViewFragment sales = new WebViewFragment();
                sales.setUrl(DataUtil.parseSalesReportURL());
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, sales).commit();
                break;
            case R.id.radioButton_qpay_refund:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, new RefundFragment()).commit();
                break;
        }
    }
}
