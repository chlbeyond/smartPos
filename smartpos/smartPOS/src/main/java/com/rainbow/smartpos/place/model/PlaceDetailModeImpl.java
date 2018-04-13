package com.rainbow.smartpos.place.model;

import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.OpenTablesRequest;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.socks.library.KLog;

import java.util.List;

/**
 * Created by ss on 2016/1/11.
 */
public class PlaceDetailModeImpl implements PlaceDetailModel {
    private static final String TAG = "PlaceDetailModeImpl";

    public interface onLoadDetailsListener {
        void onSuccess(List<OpenTableDetail> orderDetails);

        void onFailure(String msg);

    }

    @Override
    public void loadDetals(List<Long> tableIds, final onLoadDetailsListener listener) {
        KLog.d(TAG, tableIds + " ");
        SanyiScalaRequests.openTableRequest(tableIds, new OpenTablesRequest.OnOpenTableListener() {
            @Override
            public void onSuccess(List<OpenTableDetail> resp) {
                listener.onSuccess(resp);
            }


            @Override
            public void onFail(String error) {
                listener.onFailure(error);
            }
        });
    }
}
