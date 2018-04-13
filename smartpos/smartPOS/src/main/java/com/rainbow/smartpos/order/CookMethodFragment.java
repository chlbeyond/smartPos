package com.rainbow.smartpos.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableRow;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CookMethodFragment extends Fragment implements OnClickListener {
    public TextView textView_order_fragment_cookmethod;
    public TextView TextView_order_fragment_dish_private_cookmethed;
    public TextView TextView_order_fragment_dish_cookmethed;
    public TableRow tableRow_middle;
    public Button buttonContinueOrder;
    public Button buttonOrderItemOptionConfirm;
    public PrivateCookMethod privateCookMethod;
    public PublicCookMethod publicCookMethod;
    public OrderDetail order;
    public List<ProductRest> productCooks = new ArrayList<ProductRest>();

    public List<OrderDetail> tmpCooks = new ArrayList<OrderDetail>();

    public void setCurrentOrder(OrderDetail order) {
        this.order = order;
    }
    public MainScreenActivity activity;
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.TextView_order_fragment_dish_cookmethed:
                getChildFragmentManager().beginTransaction().replace(R.id.order_fragment_cookmethed, publicCookMethod).commit();
                TextView_order_fragment_dish_private_cookmethed.setTextColor(Color.parseColor("#525355"));
                TextView_order_fragment_dish_cookmethed.setTextColor(getResources().getColor(R.color.primarycolor));
                break;
            case R.id.TextView_order_fragment_dish_private_cookmethed:
                getChildFragmentManager().beginTransaction().replace(R.id.order_fragment_cookmethed, privateCookMethod).commit();
                TextView_order_fragment_dish_private_cookmethed.setTextColor(getResources().getColor(R.color.primarycolor));
                TextView_order_fragment_dish_cookmethed.setTextColor(Color.parseColor("#525355"));
                break;

            default:
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_order_dish_cookmethod, container, false);
        activity = (MainScreenActivity) getActivity();
        textView_order_fragment_cookmethod = (TextView) v.findViewById(R.id.textView_order_fragment_cookmethod);
        textView_order_fragment_cookmethod.setText(order.getName());
        TextView_order_fragment_dish_private_cookmethed = (TextView) v.findViewById(R.id.TextView_order_fragment_dish_private_cookmethed);
        TextView_order_fragment_dish_private_cookmethed.setOnClickListener(this);
        TextView_order_fragment_dish_cookmethed = (TextView) v.findViewById(R.id.TextView_order_fragment_dish_cookmethed);
        TextView_order_fragment_dish_cookmethed.setOnClickListener(this);
        tableRow_middle = (TableRow) v.findViewById(R.id.tableRow_middle);
        privateCookMethod = new PrivateCookMethod();
        publicCookMethod = new PublicCookMethod();
        buttonContinueOrder = (Button) v.findViewById(R.id.buttonContinueOrder);
        buttonContinueOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                activity.orderFragment.orderListAdpater.notifyDataSetChanged();
//                activity.orderFragment.removeOrderitemOptionFragment();
            }
        });
        buttonOrderItemOptionConfirm = (Button) v.findViewById(R.id.buttonOrderItemOptionConfirm);
        buttonOrderItemOptionConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                order.getPublicCookMethod().clear();
                for (OrderDetail o : tmpCooks) {
                    order.addPublicCookMethod(o);
                }
//                activity.orderFragment.orderListAdpater.notifyDataSetChanged();
//                activity.orderFragment.removeOrderitemOptionFragment();
            }

        });

        return v;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        boolean isHasSelfCook = false;

        if (order.getParent() == null) {
            if (SanyiSDK.rest.getProductById(order.getProductId()).getSelfCooks().size() > 0) {
                isHasSelfCook = true;
                productCooks = SanyiSDK.rest.getProductById(order.getProductId()).getSelfCooks();
            }
        } else {
            if (SanyiSDK.rest.getSetItemByGoodsId(order.getGoodsId()).getSubProducts().size() > 0) {
                isHasSelfCook = true;
                productCooks = SanyiSDK.rest.getSetItemByGoodsId(order.getGoodsId()).getSelfCooks();
            }
        }
        if (isHasSelfCook) {
            getChildFragmentManager().beginTransaction().replace(R.id.order_fragment_cookmethed, privateCookMethod).commit();
            TextView_order_fragment_dish_private_cookmethed.setVisibility(View.VISIBLE);
            TextView_order_fragment_dish_private_cookmethed.setTextColor(getResources().getColor(R.color.primarycolor));
            TextView_order_fragment_dish_cookmethed.setTextColor(Color.parseColor("#525355"));
            tableRow_middle.setVisibility(View.VISIBLE);
        } else {
            getChildFragmentManager().beginTransaction().replace(R.id.order_fragment_cookmethed, publicCookMethod).commit();
            TextView_order_fragment_dish_private_cookmethed.setVisibility(View.GONE);
            TextView_order_fragment_dish_cookmethed.setTextColor(getResources().getColor(R.color.primarycolor));
            tableRow_middle.setVisibility(View.GONE);
        }

        for (OrderDetail o : order.getPublicCookMethod()) {
            tmpCooks.add(o);
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        tmpCooks.clear();
    }

    public boolean isHasCook(Long productId) {
        for (OrderDetail orderDetail : tmpCooks) {
            if (orderDetail.getProductId() == productId) {
                return true;
            }
        }
        return false;
    }

    public void removeCook(Long productId) {
        Iterator<OrderDetail> ite = tmpCooks.iterator();
        while (ite.hasNext()) {
            OrderDetail o = ite.next();
            if (o.getProductId() == productId) {
                ite.remove();
            }
        }
    }

    public class PublicCookMethod extends Fragment {
        private GridView gridView_order_fragment_cookmethod;
        public CookMethodAdapter cookMethodAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            View v = inflater.inflate(R.layout.order_fragment_cookmethed_fragment, container, false);
            gridView_order_fragment_cookmethod = (GridView) v.findViewById(R.id.gridView_order_fragment_cookmethod);
            cookMethodAdapter = new CookMethodAdapter(getActivity());
            gridView_order_fragment_cookmethod.setAdapter(cookMethodAdapter);
            gridView_order_fragment_cookmethod.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    OrderDetail o = new OrderDetail();
                    ProductRest p = SanyiSDK.rest.publicCooks.get(position);
                    o.setProductId(p.id);
                    o.setGoodsId(p.goods);
                    o.setName(p.name);
                    o.setOriginPrice(p.price);
                    o.setQuantity(order.getQuantity());
                    o.setVoid_quantity(order.getVoid_quantity());
                    o.setCurrentPrice(p.price);
                    if (isHasCook(p.id)) {
                        removeCook(p.id);
                    } else {
                        tmpCooks.add(o);
                    }
                    cookMethodAdapter.notifyDataSetChanged();
                }
            });
            return v;
        }
    }

    public class PrivateCookMethod extends Fragment {
        private GridView gridView_order_fragment_cookmethod;
        private PrivateCookMethodAdapter privateCookMethodAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            View v = inflater.inflate(R.layout.order_fragment_cookmethed_fragment, container, false);
            gridView_order_fragment_cookmethod = (GridView) v.findViewById(R.id.gridView_order_fragment_cookmethod);
            privateCookMethodAdapter = new PrivateCookMethodAdapter(getActivity());
            gridView_order_fragment_cookmethod.setAdapter(privateCookMethodAdapter);
            gridView_order_fragment_cookmethod.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    OrderDetail o = new OrderDetail();
                    ProductRest p = productCooks.get(position);
                    o.setProductId(p.id);
                    o.setGoodsId(p.goods);
                    o.setName(p.name);
                    o.setOriginPrice(p.price);
                    o.setQuantity(order.getQuantity());
                    o.setVoid_quantity(order.getVoid_quantity());
                    o.setCurrentPrice(p.price);
                    if (isHasCook(p.id)) {
                        removeCook(p.id);
                    } else {
                        tmpCooks.add(o);
                    }
                    privateCookMethodAdapter.notifyDataSetChanged();
                }
            });
            return v;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public class PrivateCookMethodAdapter extends BaseAdapter {
        public Context context;
        public LayoutInflater inflater;

        public PrivateCookMethodAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return productCooks.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = inflater.inflate(R.layout.fragment_order_dish_detail, parent, false);
            view.setBackground(getResources().getDrawable(R.drawable.orderdish_background_ser));
            TextView textViewDishDetailName = (TextView) view.findViewById(R.id.textViewDishDetailName);
            textViewDishDetailName.setText(productCooks.get(position).name);
            TextView textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
            textViewDishPrice.setText(Double.toString(productCooks.get(position).price));
//            TextView textViewDishOrdered = (TextView) view.findViewById(R.id.textViewDishOrdered);
//            for (OrderDetail s : tmpCooks) {
//                if (s.getProductId() == productCooks.get(position).id) {
//                    textViewDishOrdered.setText("√");
//                    textViewDishOrdered.setVisibility(View.VISIBLE);
//                }
//            }
            return view;
        }
    }

    public class CookMethodAdapter extends BaseAdapter {
        public Context context;
        public LayoutInflater inflater;
        public List<ProductRest> publicCooks;

        public CookMethodAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            initList();
        }

        private void initList() {
            // TODO Auto-generated method stub
            publicCooks = SanyiSDK.rest.publicCooks;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return publicCooks.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = inflater.inflate(R.layout.fragment_order_dish_detail, parent, false);
            view.setBackground(getResources().getDrawable(R.drawable.orderdish_background_ser));
            TextView textViewDishDetailName = (TextView) view.findViewById(R.id.textViewDishDetailName);
            textViewDishDetailName.setText(publicCooks.get(position).name);
            TextView textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
            textViewDishPrice.setText(Double.toString(publicCooks.get(position).price));
//            TextView textViewDishOrdered = (TextView) view.findViewById(R.id.textViewDishOrdered);
//            for (OrderDetail s : tmpCooks) {
//                if (s.getProductId() == publicCooks.get(position).id) {
//                    textViewDishOrdered.setText("√");
//                    textViewDishOrdered.setVisibility(View.VISIBLE);
//                }
//            }
            return view;
        }

    }

}