package com.rainbow.smartpos.activity.reportfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala._GetDetailSalesReportRequest;
import com.sanyipos.sdk.model.scala.DetailSalesReport;


/**
 * Created by ss on 2016/11/3.
 */

public class DetailSalesReportFragment extends Fragment {

    private LinearLayout emptyLinearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_sales, container, false);
        emptyLinearLayout = (LinearLayout) rootView.findViewById(R.id.empty_layout);
        final ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.listView_fragment_detail_sales);
        SanyiScalaRequests.getDetailSalesRequest(new _GetDetailSalesReportRequest.IGetDetailSalesListener() {
            @Override
            public void onSuccess(DetailSalesReport resp) {
                if (resp != null && resp.subgroups.size() == 0) {
                    emptyLinearLayout.setVisibility(View.VISIBLE);
                    return;
                }
                listView.setAdapter(new DetailSalesListViewAdapter(getContext(), resp));
                for (int i = 0; i < resp.subgroups.size(); i++) {
                    listView.expandGroup(i);
                }
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
        rootView.findViewById(R.id.button_fragment_detail_sales).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SanyiScalaRequests.printSalesStatisticsRequest(new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {
                        Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return rootView;
    }
}
