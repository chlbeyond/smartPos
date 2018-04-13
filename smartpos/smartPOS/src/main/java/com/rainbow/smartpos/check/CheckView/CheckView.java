package com.rainbow.smartpos.check.CheckView;

import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;
import com.sanyipos.sdk.model.scala.check.CashierResult;

import java.util.List;

/**
 * Created by ss on 2016/4/29.
 */
public interface CheckView {
    void initParam(CashierParamResult param ,CashierResult cashierResult);

    void updateUI(CashierResult cash, List<OrderDetail> ods);

    void showQRCode(String url,double unpaid);

    void initPermissionButton();

    void clearData();

    void closeDrawerLayout();
}
