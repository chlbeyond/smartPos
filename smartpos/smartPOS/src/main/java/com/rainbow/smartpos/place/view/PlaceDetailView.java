package com.rainbow.smartpos.place.view;

import android.os.Bundle;
import android.view.View;

import com.sanyipos.sdk.api.bean.TableOrderInfo;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.SeatEntity;

import java.util.List;

/**
 * Created by ss on 2016/1/11.
 */
public interface PlaceDetailView {

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void updateItem(int position,View view);

    void updateDetails(List<OrderDetail> details);

    void updateSelectedDetails(List<OrderDetail> details);

    void updateTableInfo(TableOrderInfo tableInfo);

    void notifyDataSetChanged();

    void cancelBatchMode();

    int currentMode();

    SeatEntity getCurrentTable();

    void setPeople(int count);

    void clearBillStateText();

    void clearSelectedDetails();

    void showOrderFragment();

    void initTable();

    void setBundle(Bundle bundle);
}
