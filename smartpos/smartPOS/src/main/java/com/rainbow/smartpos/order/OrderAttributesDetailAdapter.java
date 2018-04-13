package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 2018/1/30.
 */

public class OrderAttributesDetailAdapter extends BaseAdapter {

    private Context context;
    private List<String> list = new ArrayList<>();
    private List<Integer> selects = new ArrayList<>();


    public OrderAttributesDetailAdapter(Context context, List<String> list) {
        this.context = context;
        this.list.clear();
        this.list.addAll(list);
    }

    public OrderAttributesDetailAdapter(Context context, List<String> list, List<Integer> selects) {
        this.context = context;
        this.list.clear();
        this.list.addAll(list);
        this.selects.clear();
        this.selects.addAll(selects);
    }

    public String getSelectString() {
        if (selects != null && selects.size() > 0)
            return list.get(selects.get(0));
        else
            return "";
    }

    public void setSelects(List<String> showlist) {
        selects.clear();
        for (int i = 0; i < list.size(); i++
                ) {
            if (showlist.contains(list.get(i))) {
                selects.add(i);
            }
        }
        notifyDataSetChanged();
    }

    public void setSelectPosition(List<Integer> selectPosition) {
        selects.clear();
        selects.addAll(selectPosition);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_list_orderattdetail, null);
            holder = new ViewHolder();
            holder.setHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if (list.get(position) != null)
            if (list.get(position).length() > 6)
                holder.orderAttName.setText(list.get(position).substring(0, 5) + "...");
            else
                holder.orderAttName.setText(list.get(position));
        holder.orderAttName.setBackground(context.getResources().getDrawable(R.drawable.order_op_dialog_gird_item_bg_single));
        if (selects != null)
            for (Integer i : selects) {
                if (i == position) {
                    holder.orderAttName.setBackground(context.getResources().getDrawable(R.drawable.order_op_dialog_grid_item_bg_multiple));
                }
            }

        return convertView;
    }


    class ViewHolder {
        TextView orderAttName;


        public void setHolder(View v) {
            orderAttName = (TextView) v.findViewById(R.id.tv_item_orderattdetail_name);
        }
    }

    public void refresh(List<Integer> list2) {
        this.selects.clear();
        this.selects.addAll(list2);
        notifyDataSetChanged();
    }

}
