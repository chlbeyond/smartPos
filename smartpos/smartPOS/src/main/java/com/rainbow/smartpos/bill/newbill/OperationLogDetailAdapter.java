package com.rainbow.smartpos.bill.newbill;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.DetailOpGroupList;
import com.sanyipos.sdk.utils.DateHelper;

import java.util.List;

/**
 * Created by ss on 2016/1/14.
 */
public class OperationLogDetailAdapter extends BaseAdapter {
    public List<DetailOpGroupList.DetailOpChildList> mLogDetails;
    public Context mContext;

    public OperationLogDetailAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public void setmLogDetails(List<DetailOpGroupList.DetailOpChildList> logs) {
        this.mLogDetails = logs;
    }

    @Override
    public int getCount() {
        if (mLogDetails != null)
            return mLogDetails.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.bill_fragment_operation_item, null);
        }
        DetailOpGroupList.DetailOpChildList log = mLogDetails.get(i);
        TextView mTextViewIndex = (TextView) view.findViewById(R.id.textView_bill_fragment_operation_index);
        mTextViewIndex.setBackgroundColor(Color.parseColor(getRowNoColor(i)));
        mTextViewIndex.setText(Integer.toString(i + 1));
        TextView mTextViewTime = (TextView) view.findViewById(R.id.textView_bill_fragment_operation_time);
        TextView mTextViewMsg = (TextView) view.findViewById(R.id.textView_bill_fragment_operation_msg);
        mTextViewTime.setText(DateHelper.hmFormater.format(log.detailLogs.get(0).createon));
        mTextViewMsg.setText(log.detailLogs.get(0).authStaffName + " " + log.detailLogs.get(0).message);
        return view;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public String[] colors = {"#33B45A", "#2951A8", "#B5A6CF", "#BD1C84", "#5970C0"};

    private String getRowNoColor(int position) {
        return colors[position % colors.length];
    }
}
