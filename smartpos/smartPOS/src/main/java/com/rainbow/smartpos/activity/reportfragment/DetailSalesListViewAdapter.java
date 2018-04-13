package com.rainbow.smartpos.activity.reportfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.model.scala.DetailSalesReport;
import com.sanyipos.sdk.model.scala.SalesSubGroup;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ss on 2016/11/7.
 */

public class DetailSalesListViewAdapter extends BaseExpandableListAdapter {

    private DetailSalesReport detailSalesReport;
    private Context mContext;

    public DetailSalesListViewAdapter(Context context, DetailSalesReport report) {
        this.mContext = context;
        this.detailSalesReport = report;
    }


    @Override
    public int getGroupCount() {
        return detailSalesReport.subgroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return detailSalesReport.subgroups.get(groupPosition).goodses.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.detail_sales_listview_group, null);
        }
        final SalesSubGroup subGroup = detailSalesReport.subgroups.get(groupPosition);
        TextView textViewGroupName = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_group_name);
        textViewGroupName.setText(subGroup.subgroupName + "(" + subGroup.groupName + ")");


        TextView textViewGroupCount = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_group_count);
        textViewGroupCount.setText(Long.toString(subGroup.realCountTotal));

        TextView textViewGroupPrice = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_group_price);
        textViewGroupPrice.setText(OrderUtil.dishPriceFormatter.format(subGroup.currentTotal));

        TextView textViewGroupRow = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_group_row);
        textViewGroupRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Long> lists = new ArrayList<Long>();
                lists.add(subGroup.group);
                SanyiScalaRequests.printSalesStatisticsRequest(lists, new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {
                        Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(String error) {

                        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.detail_sales_listview_child, null);
        }

        SalesSubGroup.SalesGoodses goodses = detailSalesReport.subgroups.get(groupPosition).goodses.get(childPosition);
        TextView textViewChildName = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_name);
        textViewChildName.setText(goodses.goods.goodsName);

        TextView textViewRow = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_row);
        textViewRow.setText(Integer.toString(childPosition + 1));

        TextView textViewUnit = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_unit);
        textViewUnit.setText(goodses.goods.unitTypeName);

        TextView textViewCount = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_count);
        textViewCount.setText((goodses.realWeight != 0) ? Double.toString(goodses.realWeight) : Long.toString(goodses.realCount));

        TextView textViewPrice = (TextView) convertView.findViewById(R.id.textView_listView_detail_sales_price);
        textViewPrice.setText(OrderUtil.dishPriceFormatter.format(goodses.current));

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
