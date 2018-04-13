package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class CookMethodAdapter extends BaseAdapter {
    public List<ProductRest> displayDishs = new ArrayList<ProductRest>();
    public Context mContext;
    public LayoutInflater inflater;
    private int flag;
    private OrderDetail currentDetail;
    private ProductRest product;
    private List<Integer> selects;

    public CookMethodAdapter(Context context, OrderDetail orderDetail, int flag) {
        this.mContext = context;
        this.flag = flag;
        this.currentDetail = orderDetail;
        this.product = SanyiSDK.rest.getProductById(orderDetail.getProductId());
        initData();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CookMethodAdapter(Context context, OrderDetail orderDetail, int flag, List<Integer> selects) {
        this.mContext = context;
        this.flag = flag;
        this.currentDetail = orderDetail;
        this.product = SanyiSDK.rest.getProductById(orderDetail.getProductId());
        initData();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selects = selects;
    }

    private void initData() {
        // TODO Auto-generated method stub
        switch (flag) {
            case ChooseCookDialog.PRIVATE_COOK_METHOD:
                if (product != null) {
                    displayDishs = product.getSelfCooks();
                } else {
                    displayDishs = SanyiSDK.rest.getSetItemSelfCooks(currentDetail.getGoodsId());
                }
                break;
            case ChooseCookDialog.ALL_COOK_METHOD:
                displayDishs = SanyiSDK.rest.publicCooks;
                break;
            case ChooseCookDialog.INGREDIENT:
                displayDishs = SanyiSDK.rest.ingredients;
                break;
            default:
                break;
        }
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return displayDishs.size();
    }

    @Override
    public ProductRest getItem(int position) {
        // TODO Auto-generated method stub
        return displayDishs.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        CookMethodItemLayout l;
        if (convertView == null) {
            l = (CookMethodItemLayout) inflater.inflate(R.layout.choose_cookmethod_dialog_detail, parent, false);

        } else {
            l = (CookMethodItemLayout) convertView;
        }
        ProductRest dish = displayDishs.get(position);
        l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_gridview_item_bg_normal));
        TextView textViewDishUnitName = (TextView) l.findViewById(R.id.textViewDishUnitName);
        TextView textViewDishUnitPrice = (TextView) l.findViewById(R.id.textViewDishUnitPrice);
        textViewDishUnitName.setText(dish.name);
        textViewDishUnitPrice.setText(OrderUtil.dishPriceFormatter.format(dish.price));

        switch (flag) {
            case ChooseCookDialog.PRIVATE_COOK_METHOD:
            case ChooseCookDialog.ALL_COOK_METHOD:
                l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_gird_item_bg_single));
                break;
            case ChooseCookDialog.INGREDIENT:
                l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_gird_item_bg_single));
                break;
            default:
                break;
        }
        if (selects != null)
            for (Integer i : selects) {
                if (i == position) {
                    l.setBackground(mContext.getResources().getDrawable(R.drawable.order_op_dialog_grid_item_bg_multiple));
                }
            }

        return l;
    }


    public void refresh() {
        initData();
        notifyDataSetChanged();
    }
}
