package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.rainbow.common.view.HorizontalListView;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.GoodsSet;
import com.sanyipos.sdk.model.rest.GoodsSet.SetItems;
import com.sanyipos.sdk.model.rest.GoodsSet.SetItems.SetItemDetails;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectDishSetAdapter extends BaseExpandableListAdapter {
    private Context context;
    private OrderDetail orderDetail;

    private GoodsSet goodsSet;
    private long productId;

    private Map<Integer, Integer> map = new HashMap<>();//每个道菜选中的子菜
    private Map<Integer, Integer> map2 = new HashMap<>();//每个道菜选中的子菜的初始状态

    public SelectDishSetAdapter(OrderFragment fragment, long productId, OrderDetail orderDetail) {
        this.context = fragment.getActivity();
        this.orderDetail = orderDetail;
        this.productId = productId;
        initGoodsSet(productId);
    }

    public void initGoodsSet(long productId) {
        for (GoodsSet set : SanyiSDK.rest.goodsSets) {
            if (set.getProduct().id == productId) {
                this.goodsSet = set;
            }
        }
        for (int i = 0; i < goodsSet.getItems().size(); i++) {
            for (int j = 0; j < goodsSet.getItems().get(i).getDetails().size(); j++) {
                if (goodsSet.getItems().get(i).getDetails().get(j).isDefault()) {
                    map.put(i, j);
                    map2.put(i, j);
                }
            }
        }
        if (this.orderDetail != null && this.orderDetail.getSetOrderDetailList() != null && this.orderDetail.getSetOrderDetailList().size() > 0) {
            for (int i = 0; i < goodsSet.getItems().size(); i++) {
                SetItems goodsSet1 = goodsSet.getItems().get(i);
                for (int j = 0; j < goodsSet1.getDetails().size(); j++) {
                    if (orderDetail.getSetOrderDetailList().get(i) != null) {
                        goodsSet1.getDetails().get(j).setIsDefault(false);
                    }
                    if (orderDetail.getSetOrderDetailList().get(i).getGoodsId() == goodsSet1.getDetails().get(j).getGoods()) {
                        goodsSet1.getDetails().get(j).setIsDefault(true);
                        map.put(i, j);
                        map2.put(i, j);
                    }
                }
            }
        }

    }

    @Override
    public SetItemDetails getChild(int groupPosition, int childPosition) {
        return this.goodsSet.getItems().get(groupPosition).getDetails().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.set_dish_item_view, null);
        }
        HorizontalListView listViewSetItemDish = (HorizontalListView) convertView.findViewById(R.id.listViewSetItemDish);
        List<SetItemDetails> setItemDetails = goodsSet.getItems().get(groupPosition).getDetails();
//        final SetItemDishAdapter setItemDishAdapter = new SetItemDishAdapter(context, setItemDetails);
        final SetItemDishAdapter setItemDishAdapter = new SetItemDishAdapter(context, setItemDetails, map, groupPosition);
        listViewSetItemDish.setAdapter(setItemDishAdapter);
        listViewSetItemDish.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
//                onChildClick(groupPosition, position);
                map.put(groupPosition, position);
                setItemDishAdapter.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public void onChildClick(int groupPosition, int childPosition) {
        for (SetItemDetails detail : goodsSet.getItems().get(groupPosition).getDetails()) {
            if (detail.getGoods() == getChild(groupPosition, childPosition).getGoods()) {
                if (orderDetail != null) {
                    OrderUtil.replaceGoodsItemDetail(orderDetail.setOrders.get(groupPosition), detail);
                }
                detail.setIsDefault(true);
            } else {
                detail.setIsDefault(false);
            }
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.goodsSet.getItems().get(groupPosition).getDetails() != null) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public SetItems getGroup(int groupPosition) {
        return goodsSet.getItems().get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        if (goodsSet != null)
            return goodsSet.getItems().size();
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = goodsSet.getItems().get(groupPosition).getName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.select_dishset_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.textViewGroupName);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        // TODO Auto-generated method stub
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        // TODO Auto-generated method stub
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public OrderDetail getOrderDetail() {
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i) != map2.get(i))//如果不相等,则说明该道菜有进行过修改,相等则说明保持原来的子菜不变
                onChildClick(i, map.get(i));
        }
        ProductRest dish = SanyiSDK.rest.getProductById(productId);
        OrderDetail orderDetail = OrderUtil.createOrderDetail(dish, 1);
        return orderDetail;
    }

}
