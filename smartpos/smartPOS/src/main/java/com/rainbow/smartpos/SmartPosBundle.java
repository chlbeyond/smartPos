package com.rainbow.smartpos;

import android.os.Bundle;

import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.sanyipos.smartpos.model.OrderParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/1/6.
 */
public class SmartPosBundle {

    public static final String PEOPLE_NUMBER = "peopleNumber";
    public static final String TABLE_ID = "tableIds";
    public static final String NEW_BILL = "isNewBill";
    public static final String IS_MERGE = "isMerge";
    public static final String ORDERDETAILS = "orderDetails";
    public static final String TAG = "tag";

    public static Bundle getBundle(int peopleNumber, long[] tableIds, boolean isNewBill, boolean isMerge, List<OpenTableDetail> resp) {
        Bundle bundle = new Bundle();
        bundle.putInt(PEOPLE_NUMBER, peopleNumber);
        bundle.putSerializable(TABLE_ID, table2List(tableIds, isMerge));
        bundle.putBoolean(NEW_BILL, isNewBill);
        bundle.putBoolean(IS_MERGE, isMerge);
        bundle.putSerializable(ORDERDETAILS, (Serializable) resp);
        return bundle;
    }

    public static Bundle getBundle(int peopleNumber, long[] tableIds, boolean isNewBill, boolean isMerge) {
        Bundle bundle = new Bundle();
        bundle.putInt(PEOPLE_NUMBER, peopleNumber);
        bundle.putSerializable(TABLE_ID, table2List(tableIds, isMerge));
        bundle.putBoolean(NEW_BILL, isNewBill);
        bundle.putBoolean(IS_MERGE, isMerge);
        return bundle;
    }

    public static Bundle getBundle(int peopleNumber, List<Long> tableIds, boolean isNewBill, boolean isMerge) {
        Bundle bundle = new Bundle();
        bundle.putInt(PEOPLE_NUMBER, peopleNumber);
        OrderParams order = new OrderParams();
        order.tableIds = tableIds;
        bundle.putSerializable(TABLE_ID, order);
        bundle.putBoolean(NEW_BILL, isNewBill);
        bundle.putBoolean(IS_MERGE, isMerge);
        return bundle;
    }

    public static Bundle getBundle(int peopleNumber, List<Long> tableIds, boolean isNewBill, boolean isMerge, String tag) {
        Bundle bundle = new Bundle();
        bundle.putInt(PEOPLE_NUMBER, peopleNumber);
        OrderParams order = new OrderParams();
        order.tableIds = tableIds;
        bundle.putSerializable(TABLE_ID, order);
        bundle.putBoolean(NEW_BILL, isNewBill);
        bundle.putBoolean(IS_MERGE, isMerge);
        bundle.putString(TAG, tag);
        return bundle;
    }

    public static OrderParams table2List(long[] tableIds, boolean isMerge) {
        List<Long> tables = new ArrayList<>();
        for (int i = 0; i < tableIds.length; i++) {
            tables.add(tableIds[i]);
        }
        OrderParams order = new OrderParams();
        order.tableIds = tables;
        return order;
    }

}
