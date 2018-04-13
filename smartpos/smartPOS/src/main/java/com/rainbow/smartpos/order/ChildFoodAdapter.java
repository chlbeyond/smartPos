package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.MyGridView;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.ChooseCountPopWindow;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.AppendProductGoodsSpec;
import com.sanyipos.sdk.model.rest.AppendProductGroup;
import com.sanyipos.sdk.model.rest.AppendProductSpec;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.Rest;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by administrator on 2018/3/5.
 */

public class ChildFoodAdapter extends BaseAdapter {

    private Context mContext;
    private OrderDetail orderDetail;
    private AppendProductSpec appendProductGoodsSpec;
    private long mixednum = 0;
    private int selectnum = 0;
    private List<List<TempAppend>> allSelectFood = new ArrayList<>();


    private List<Integer> selectNums = new ArrayList<>();//所有item分别选择的菜品数量


    public ChildFoodAdapter(Context mContext, long mixednum, OrderDetail orderDetail) {
        this.mContext = mContext;
        this.orderDetail = orderDetail;
        this.mixednum = mixednum;
        init();
    }

    private void init() {
        if (mixednum > 0)
            appendProductGoodsSpec = SanyiSDK.rest.getAppendProductSpecById(mixednum);
        else if (orderDetail.getProductId() > 0)
            appendProductGoodsSpec = SanyiSDK.rest.getAppendProductSpecByProductId(orderDetail.getProductId());
        if (allSelectFood.size() > 0)
            allSelectFood.clear();
        if (appendProductGoodsSpec != null) {
            for (int i = 0; i < appendProductGoodsSpec.items.size(); i++) {
                selectNums.add(0);
                List<TempAppend> tempAppends = new ArrayList<>();
                for (int j = 0; j < appendProductGoodsSpec.items.get(i).details.size(); j++) {
                    TempAppend temp = new TempAppend();
                    temp.setNum(0);
                    temp.setAppendProductGoodsSpec(appendProductGoodsSpec.items.get(i).details.get(j));
                    tempAppends.add(temp);
                }
                allSelectFood.add(tempAppends);
            }
        }
        if (orderDetail.getChildrenOrderDetail() != null && orderDetail.getChildrenOrderDetail().size() > 0) {
            for (OrderDetail order : orderDetail.getChildrenOrderDetail()
                    ) {
                for (int i = 0; i < allSelectFood.size(); i++) {

                    List<TempAppend> list = allSelectFood.get(i);
                    for (int j = 0; j < list.size(); j++) {
                        int num = 0;
                        TempAppend temp = list.get(j);
                        if (temp.getAppendProductGoodsSpec().goods == order.getGoodsId()&&appendProductGoodsSpec.items.get(i).name.equals(order.getData("groupname"))) {
                            temp.setNum(order.getQuantity());
                            num += order.getQuantity();
                        }
                        selectNums.set(i, selectNums.get(i) + num);
                    }

                }
            }

        }
    }


    @Override
    public int getCount() {
        return appendProductGoodsSpec.items.size();
    }

    @Override
    public Object getItem(int position) {
        return appendProductGoodsSpec.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.child_food_lv, null);
            holder = new ViewHolder();
            holder.setView(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final AppendProductGroup appendProductGroup = appendProductGoodsSpec.items.get(position);
        List<AppendProductGoodsSpec> foodslist = new ArrayList<>();
        foodslist.addAll(appendProductGroup.details);

        final List<TempAppend> selects = allSelectFood.get(position);

        holder.childFoodNameTv.setText(appendProductGroup.name);
        if (appendProductGroup.min == appendProductGroup.max)
            holder.childFoodNumTv.setText("必选: " + appendProductGroup.min);
        else
            holder.childFoodNumTv.setText("必选: " + appendProductGroup.min + " ~ " + appendProductGroup.max);
        final ChildFoodGvAdapter adapter = new ChildFoodGvAdapter(mContext, foodslist, selects);
        holder.childFoodGv.setAdapter(adapter);
        adapter.setChildFoodGvListener(new ChildFoodGvAdapter.ChildFoodGvListener() {
            @Override
            public void add(int position2) {
                int addnum = 0;//如果当前该菜的数量是,则点击添加按钮,添加该菜品的min份,如果不为0,则点击添加一份
                if (selects.get(position2).getNum() == 0) {
                    addnum = selects.get(position2).getAppendProductGoodsSpec().min;
                } else
                    addnum = 1;

                if (appendProductGroup.max >= (selectNums.get(position) + addnum)) {
//                    int firstnum = -1;//点开选择数量dialog后,初始显示的是本菜品已经选择过的数量
//                    if (selects.get(position2).getNum() > 0) {
//                        firstnum = selects.get(position2).getNum();
//                    } else
//                        firstnum = 1;

                    if (selects.get(position2).getAppendProductGoodsSpec().goods == adapter.getItem(position2).goods)
                        if (selects.get(position2).getAppendProductGoodsSpec().max >= selects.get(position2).getNum() + addnum )
                            if (selects.get(position2).getNum() == 0)
                                selects.get(position2).setNum(selects.get(position2).getNum() + selects.get(position2).getAppendProductGoodsSpec().min);
                            else
                                selects.get(position2).setNum(selects.get(position2).getNum() + 1);
                        else
                            Toast.makeText(mContext, "已达该菜品可选数量上限", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    selectnum = 0;
                    for (int i = 0; i < selects.size(); i++) {
                        selectnum += selects.get(i).getNum();
                    }
                    selectNums.set(position, selectnum);

//                    ChooseCountPopWindow chooseCountPopWindow = new ChooseCountPopWindow(view, mContext, firstnum, selects.get(position2).getAppendProductGoodsSpec().max, appendProductGroup.max - selectNums.get(position) + selects.get(position2).getNum(), true, position % holder.childFoodGv.getNumColumns(), holder.childFoodGv.getVerticalSpacing(),
//                            new ChooseCountPopWindow.OnSureListener() {
//
//                                @Override
//                                public void onSureClick(int value) {
//                                    // TODO Auto-generated method stub
//                                    for (int i = 0; i < selects.size(); i++) {
//                                        if (selects.get(i).getAppendProductGoodsSpec().goods == adapter.getItem(position2).goods)
//                                            selects.get(i).setNum(value);
//                                    }
//                                    adapter.notifyDataSetChanged();
//                                    selectnum = 0;
//                                    for (int i = 0; i < selects.size(); i++) {
//                                        selectnum += selects.get(i).getNum();
//                                    }
//                                    selectNums.set(position, selectnum);
//                                }
//                            });
//                    chooseCountPopWindow.show();
                } else {
                    Toast.makeText(mContext, "已达该分组最多可选数量", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void min(int position2) {
                if (selects.get(position2).getNum() > 0) {
                    for (int i = 0; i < selects.size(); i++) {
                        if (selects.get(i).getAppendProductGoodsSpec().goods == adapter.getItem(position2).goods)
                            if (selects.get(i).getNum() > selects.get(position2).getAppendProductGoodsSpec().min)
                                selects.get(i).setNum(selects.get(i).getNum() - 1);
                            else if (selects.get(i).getNum() == selects.get(position2).getAppendProductGoodsSpec().min)
                                selects.get(i).setNum(0);
                    }
                    adapter.notifyDataSetChanged();
                    selectnum = 0;
                    for (int i = 0; i < selects.size(); i++) {
                        selectnum += selects.get(i).getNum();
                    }
                    selectNums.set(position, selectnum);

//                    ChooseCountPopWindow chooseCountPopWindow = new ChooseCountPopWindow(view, mContext, firstnum, selects.get(position2).getAppendProductGoodsSpec().max, appendProductGroup.max - selectNums.get(position) + selects.get(position2).getNum(), true, position % holder.childFoodGv.getNumColumns(), holder.childFoodGv.getVerticalSpacing(),
//                            new ChooseCountPopWindow.OnSureListener() {
//
//                                @Override
//                                public void onSureClick(int value) {
//                                    // TODO Auto-generated method stub
//                                    for (int i = 0; i < selects.size(); i++) {
//                                        if (selects.get(i).getAppendProductGoodsSpec().goods == adapter.getItem(position2).goods)
//                                            selects.get(i).setNum(value);
//                                    }
//                                    adapter.notifyDataSetChanged();
//                                    selectnum = 0;
//                                    for (int i = 0; i < selects.size(); i++) {
//                                        selectnum += selects.get(i).getNum();
//                                    }
//                                    selectNums.set(position, selectnum);
//                                }
//                            });
//                    chooseCountPopWindow.show();
                }
            }
        });

//        holder.childFoodGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position2, long id) {
//                if (appendProductGroup.max > (selectNums.get(position) - selects.get(position2).getNum())) {
////                    int firstnum = -1;//点开选择数量dialog后,初始显示的是本菜品已经选择过的数量
////                    if (selects.get(position2).getNum() > 0) {
////                        firstnum = selects.get(position2).getNum();
////                    } else
////                        firstnum = 1;
//
//
//                    for (int i = 0; i < selects.size(); i++) {
//                        if (selects.get(i).getAppendProductGoodsSpec().goods == adapter.getItem(position2).goods)
//                            if (selects.get(position2).getAppendProductGoodsSpec().max > selects.get(i).getNum() && selects.get(i).getNum() < appendProductGroup.max - selectNums.get(position) + selects.get(position2).getNum())
//                                if (selects.get(i).getNum() == 0)
//                                    selects.get(i).setNum(selects.get(i).getNum() + selects.get(i).getAppendProductGoodsSpec().min);
//                                else
//                                    selects.get(i).setNum(selects.get(i).getNum() + 1);
//                            else if (selects.get(position2).getAppendProductGoodsSpec().max > selects.get(i).getNum() && selects.get(i).getNum() >= appendProductGroup.max - selectNums.get(position) + selects.get(position2).getNum()) {
//                                Toast.makeText(mContext, "已达该菜品可选数量上限", Toast.LENGTH_SHORT).show();
//                            } else if (selects.get(position2).getAppendProductGoodsSpec().max <= selects.get(i).getNum())
//                                Toast.makeText(mContext, "已达该类配菜可选数量上限", Toast.LENGTH_SHORT).show();
//                    }
//                    adapter.notifyDataSetChanged();
//                    selectnum = 0;
//                    for (int i = 0; i < selects.size(); i++) {
//                        selectnum += selects.get(i).getNum();
//                    }
//                    selectNums.set(position, selectnum);
//
////                    ChooseCountPopWindow chooseCountPopWindow = new ChooseCountPopWindow(view, mContext, firstnum, selects.get(position2).getAppendProductGoodsSpec().max, appendProductGroup.max - selectNums.get(position) + selects.get(position2).getNum(), true, position % holder.childFoodGv.getNumColumns(), holder.childFoodGv.getVerticalSpacing(),
////                            new ChooseCountPopWindow.OnSureListener() {
////
////                                @Override
////                                public void onSureClick(int value) {
////                                    // TODO Auto-generated method stub
////                                    for (int i = 0; i < selects.size(); i++) {
////                                        if (selects.get(i).getAppendProductGoodsSpec().goods == adapter.getItem(position2).goods)
////                                            selects.get(i).setNum(value);
////                                    }
////                                    adapter.notifyDataSetChanged();
////                                    selectnum = 0;
////                                    for (int i = 0; i < selects.size(); i++) {
////                                        selectnum += selects.get(i).getNum();
////                                    }
////                                    selectNums.set(position, selectnum);
////                                }
////                            });
////                    chooseCountPopWindow.show();
//                } else {
//                    Toast.makeText(mContext, "已达该类配菜最多可选数量", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        return convertView;
    }

    public List<OrderDetail> getSelectChildFood() {
        List<OrderDetail> selectOrder = new ArrayList<>();
        for (int i = 0; i < appendProductGoodsSpec.items.size(); i++) {
            if (appendProductGoodsSpec.items.get(i).min <= selectNums.get(i) && appendProductGoodsSpec.items.get(i).max >= selectNums.get(i)) {
                List<TempAppend> list = allSelectFood.get(i);
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getNum() > 0) {
                        ProductRest rest = SanyiSDK.rest.getProductByGoodsId(list.get(j).getAppendProductGoodsSpec().goods);
                        OrderDetail od = OrderUtil.createOrderDetail(rest, list.get(j).getNum());
                        od.setData("groupname",appendProductGoodsSpec.items.get(i).name);
                        selectOrder.add(od);
                    }
                }
            }
        }

        return selectOrder;
    }

    public boolean checkAllSelected() {
        boolean allSelected = true;
        for (int i = 0; i < appendProductGoodsSpec.items.size(); i++) {
            if (selectNums.get(i) < appendProductGoodsSpec.items.get(i).min || selectNums.get(i) > appendProductGoodsSpec.items.get(i).max) {
                allSelected = false;
            }
        }
        return allSelected;
    }


    public class TempAppend {
        public AppendProductGoodsSpec getAppendProductGoodsSpec() {
            return appendProductGoodsSpec;
        }

        public void setAppendProductGoodsSpec(AppendProductGoodsSpec appendProductGoodsSpec) {
            this.appendProductGoodsSpec = appendProductGoodsSpec;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        AppendProductGoodsSpec appendProductGoodsSpec;
        int num;
    }


    class ViewHolder {
        TextView childFoodNameTv;
        TextView childFoodNumTv;
        MyGridView childFoodGv;

        public void setView(View v) {
            childFoodNameTv = (TextView) v.findViewById(R.id.tv_child_food_name);
            childFoodNumTv = (TextView) v.findViewById(R.id.tv_child_food_num);
            childFoodGv = (MyGridView) v.findViewById(R.id.gv_child_food);
        }
    }

}
