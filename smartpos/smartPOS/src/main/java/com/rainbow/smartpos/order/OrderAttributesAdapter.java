package com.rainbow.smartpos.order;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.common.view.MyGridView;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.ProductAttribute;
import com.sanyipos.sdk.model.rest.ProductRest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by administrator on 2018/1/30.
 */

public class OrderAttributesAdapter extends BaseAdapter {

    private Context context;
    List<ProductAttribute> list = new ArrayList<>();
    private List<OrderAttributesDetailAdapter> adapters = new ArrayList<>();
    private List<OrderAttributesAdapter.AttRemark> firstStrings = new ArrayList<>();
    private List<List<Integer>> mSelects = new ArrayList<>();


    public OrderAttributesAdapter(ProductRest rest, Context context) {
        this.list.addAll(rest.attributes);
        this.context = context;
        adapters.clear();
        initAdapter();
    }

    private void initAdapter() {
        for (int i = 0; i < list.size(); i++) {
            OrderAttributesDetailAdapter adapter;
            adapter = new OrderAttributesDetailAdapter(context, list.get(i).value);
            adapters.add(adapter);
            List<Integer> mSelectPrivateCooks = new ArrayList<>();
            mSelects.add(mSelectPrivateCooks);
        }
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
            convertView = View.inflate(context, R.layout.item_list_orderattributes, null);
            holder = new ViewHolder();
            holder.setHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        if (this.firstStrings.size() > 0) {
            for (int i = 0; i < list.get(position).value.size(); i++) {
                for (int j = 0; j < firstStrings.size(); j++) {
                    if (firstStrings.get(j).getRemark().equals(list.get(position).value.get(i))&&firstStrings.get(j).getName().equals(list.get(position).name)&&!firstStrings.get(j).isChecked()) {
                        mSelects.get(position).clear();
                        mSelects.get(position).add(i);
                        firstStrings.get(j).setChecked(true);
                    }
                }
            }
        }

        final List<Integer> mSelectPrivateCooks = mSelects.get(position);
        holder.orderAttributesNameTv.setText(list.get(position).name);
        final OrderAttributesDetailAdapter adapter;
//        adapter = new OrderAttributesDetailAdapter(context, list.get(position).value, mSelectPrivateCooks);
        adapter = adapters.get(position);
        adapter.setSelectPosition(mSelectPrivateCooks);
//        adapters.add(adapter);
        holder.orderAttributesListGv.setAdapter(adapter);
        holder.orderAttributesListGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!mSelectPrivateCooks.contains(position)) {
                    mSelectPrivateCooks.clear();
                    mSelectPrivateCooks.add(position);
                } else {
                    mSelectPrivateCooks.clear();
                }

                adapter.refresh(mSelectPrivateCooks);
            }
        });

        return convertView;
    }

    public List<AttRemark> getSelects() {
        List<AttRemark> selectlist = new ArrayList<>();
        for (int i = 0; i < adapters.size(); i++) {
            OrderAttributesDetailAdapter a = adapters.get(i);
            if (a.getSelectString().length() > 0) {
                AttRemark attRemark = new AttRemark();
                attRemark.setName(list.get(i).name);
                attRemark.setRemark(a.getSelectString());
                attRemark.setChecked(false);
                selectlist.add(attRemark);
            }
        }
        return selectlist;
    }

    public void setSelects(List<OrderAttributesAdapter.AttRemark> selectlist) {
        firstStrings.clear();
        firstStrings.addAll(selectlist);
    }

    public class AttRemark {
        //用来记录在多维度中选择的菜品属性,先以临时类保存在本地,方便再次进入时修改(用于不同分组中有相同属性名的特殊情况),点击下单的时候再将此属性合并到remark里面去
        String name;
        String remark;
        boolean checked;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }


    class ViewHolder {
        private TextView orderAttributesNameTv;
        private MyGridView orderAttributesListGv;


        public void setHolder(View v) {
            orderAttributesListGv = (MyGridView) v.findViewById(R.id.gv_item_orderattributes);
            orderAttributesNameTv = (TextView) v.findViewById(R.id.tv_item_orderattributes_name);
        }
    }
}
