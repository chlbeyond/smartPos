package com.rainbow.smartpos.place.presenter;

import android.view.View;

import com.sanyipos.sdk.model.OrderDetail;

import java.util.List;

/**
 * Created by ss on 2016/1/11.
 */
public interface PlaceDetailPresenter {

    void loadDetails(List<Long> tableIds);

    void clickDish(OrderDetail orderDetail, int position, View view);

    void clickBatchButton(int buttonId);

    void clickConfirm(int buttonId);

    void changePeople();

    void printBill();

    void cleanPrintState();

    void operationLog();

    void switchTable();

    boolean isCanCheck();

}
