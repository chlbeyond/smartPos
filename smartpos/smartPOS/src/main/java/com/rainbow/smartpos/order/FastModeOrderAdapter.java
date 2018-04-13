package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.sanyipos.sdk.utils.DateHelper;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.List;

/**
 * Created by ss on 2016/9/29.
 */

public class FastModeOrderAdapter extends BaseAdapter {

    private Context mContext;
    private List<OpenTableDetail> openTableDetails;

    public FastModeOrderAdapter(Context context, List<OpenTableDetail> openTableDetails) {
        this.mContext = context;
        this.openTableDetails = openTableDetails;
    }

    @Override
    public int getCount() {
        return openTableDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(com.rainbow.smartpos.R.layout.dialog_list_fastmode_item, null);
        } else {
            view = convertView;
        }
        TextView textViewSn = (TextView) view.findViewById(R.id.textView_fastmode_sn);
        textViewSn.setText(Long.toString(openTableDetails.get(position).info.getSn()));

        TextView textViewName = (TextView) view.findViewById(R.id.textView_fastmode_name);
        textViewName.setText(openTableDetails.get(position).info.getOrderName());

        TextView textViewAmount = (TextView) view.findViewById(R.id.textView_fastmode_amount);
        textViewAmount.setText(OrderUtil.dishPriceFormatter.format(openTableDetails.get(position).info.getAmount()));

        TextView textViewStaff = (TextView) view.findViewById(R.id.textView_fastmode_staff);
        textViewStaff.setText(openTableDetails.get(position).info.getOpenStaffName());

        TextView textViewTime = (TextView) view.findViewById(R.id.textView_fastmode_time);
        textViewTime.setText(DateHelper.hmFormater.format(openTableDetails.get(position).info.getCreateTime()));

        return view;
    }
}
