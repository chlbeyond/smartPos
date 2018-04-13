package com.rainbow.smartpos.manage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.DisplayItems.DisplayAmountItem;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.List;

public class HandoverAdapter extends BaseExpandableListAdapter {

    private List<String> groupList;
    private List<List<DisplayAmountItem>> childList;
    private LayoutInflater inflater;
    private Context context;

    public HandoverAdapter(List<String> groupList, List<List<DisplayAmountItem>> childList, Context context) {
        super();
        this.groupList = groupList;
        this.childList = childList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_hand_over_parent_item, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.textView_hand_over_parent);
        tv.setText(groupList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        DisplayAmountItem displayAmountItem = childList.get(groupPosition).get(childPosition);
        convertView = inflater.inflate(R.layout.handover_item, null);
        TextView descView = (TextView) convertView.findViewById(R.id.textview1);
        descView.setText(displayAmountItem.description);

        TextView countView = (TextView) convertView.findViewById(R.id.textview2);
        if (displayAmountItem.count != null) {
            countView.setText(displayAmountItem.count.intValue() + "笔");
        }
        if (displayAmountItem.subdetail != null) {
            LinearLayout voucherDetalLinearLayout = (LinearLayout) convertView.findViewById(R.id.voucher_detail_LinearLayout);
            for (int i = 0; i < displayAmountItem.subdetail.size(); i++) {
                LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(R.layout.handover_item_detail, null);
                TextView textview1 = (TextView) ingredientLayout.findViewById(R.id.textview1);
                textview1.setText("-" + displayAmountItem.subdetail.get(i).description);
                TextView textview2 = (TextView) ingredientLayout.findViewById(R.id.textview2);
                TextView textview3 = (TextView) ingredientLayout.findViewById(R.id.textview3);
                textview3.setText(displayAmountItem.subdetail.get(i).amount.toString());
                textview2.setText(displayAmountItem.subdetail.get(i).count.toString() + "笔");
                voucherDetalLinearLayout.addView(ingredientLayout);
            }
        }
        TextView amountView = (TextView) convertView.findViewById(R.id.textview3);
        if (displayAmountItem.amount != null) {
            amountView.setText(OrderUtil.decimalFormatter.format(displayAmountItem.amount));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return false;
    }

}
