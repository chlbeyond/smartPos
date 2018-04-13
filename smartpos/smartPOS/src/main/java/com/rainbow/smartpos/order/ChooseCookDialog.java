package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.ChooseCountPopWindow;
import com.rainbow.smartpos.util.Listener.OnChooseCookListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.GoodsSet.SetItems.SetItemDetails;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.utils.OrderUtil;

public class ChooseCookDialog {
    public static final int PRIVATE_COOK_METHOD = 0;
    public static final int ALL_COOK_METHOD = 1;
    public static final int INGREDIENT = 2;

    private MyDialog dialog;
    private OrderDetail orderDetail;
    TextView menu_sure;
    ImageButton menu_cancel;
    TextView op_dialog_back;
    TextView name_text;


    Context activity;
    private GridView can_choose_gridview;
    private GridView private_cookmethod_gridview;
    private GridView all_method_gridview;

    private ScrollView private_cook_layout;
    private View menuView;
    private LayoutInflater inflater;
    public CookMethodAdapter mCookAdapter;
    public CookMethodAdapter mAllCookAdapter;
    private int flag;
    private OnChooseCookListener listener;

    public ChooseCookDialog(Context activity, OrderDetail orderDetail, int flag, OnChooseCookListener listener) {
        this.activity = activity;
        this.orderDetail = orderDetail;
        this.flag = flag;
        this.listener = listener;
    }

    public void show() {
        inflater = LayoutInflater.from(activity);
        menuView = inflater.inflate(R.layout.order_op_cook_layout, null, false);
        dialog = new MyDialog(activity, MainScreenActivity.getScreenWidth() * 0.4, MainScreenActivity.getScreenHeight() * 0.9, menuView, R.style.OpDialogTheme);

        initMenuView(menuView);
        menu_sure.setOnClickListener(onClickListener);
        menu_cancel.setOnClickListener(onClickListener);
        op_dialog_back.setOnClickListener(onClickListener);

        can_choose_gridview.setAdapter(mCookAdapter);
        can_choose_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // TODO Auto-generated method stub
                switch (flag) {
                    case OrderOptionDialog.COOK_METHOD:
                        orderDetail.addPublicCookMethod(OrderUtil.createOrderDetail(mCookAdapter.getItem(position), orderDetail.getQuantity()));
                        dialog.dismiss();
                        listener.sure();
                        break;
                    case OrderOptionDialog.INGREDIENT:
                        ChooseCountPopWindow chooseCountPopWindow = new ChooseCountPopWindow(view, activity, 1, position % can_choose_gridview.getNumColumns(), can_choose_gridview.getVerticalSpacing(),
                                new ChooseCountPopWindow.OnSureListener() {

                                    @Override
                                    public void onSureClick(int value) {
                                        OrderDetail order = OrderUtil.createOrderDetail(mCookAdapter.getItem(position), orderDetail.getQuantity() - orderDetail.getVoid_quantity());
                                        order.setUnitCount(value);
                                        order.setQuantity(value*(orderDetail.getQuantity() - orderDetail.getVoid_quantity()));
                                        orderDetail.addIngredient(order);
                                        dialog.dismiss();
                                        listener.sure();
                                        // TODO Auto-generated method stub
                                    }
                                });
                        chooseCountPopWindow.show();
                        break;
                }

            }
        });


        mAllCookAdapter = new CookMethodAdapter(activity, orderDetail, ALL_COOK_METHOD);
        all_method_gridview.setAdapter(mAllCookAdapter);
        all_method_gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                orderDetail.addPublicCookMethod(OrderUtil.createOrderDetail(mAllCookAdapter.getItem(i), orderDetail.getQuantity()));
                dialog.dismiss();
                listener.sure();
                // TODO Auto-generated method stub

            }
        });

        dialog.show();
    }

    private void initMenuView(View view) {
        can_choose_gridview = (GridView) view.findViewById(R.id.can_choose_gridview);
        private_cookmethod_gridview = (GridView) view.findViewById(R.id.private_cookmethod_gridview);
        all_method_gridview = (GridView) view.findViewById(R.id.all_method_gridview);
        name_text = (TextView) view.findViewById(R.id.name_text);
        menu_sure = (TextView) view.findViewById(R.id.menu_sure_btn);
        menu_cancel = (ImageButton) view.findViewById(R.id.menu_iv_close_dialog);
        op_dialog_back = (TextView) view.findViewById(R.id.op_dialog_back);
        private_cook_layout = (ScrollView) view.findViewById(R.id.private_cook_layout);

        TextView can_choose_hint_cook = (TextView) view.findViewById(R.id.can_choose_hint_cook);
        TextView can_choose_hint_ing = (TextView) view.findViewById(R.id.can_choose_hint_ing);
        switch (flag) {
            case OrderOptionDialog.COOK_METHOD:
                name_text.setText(activity.getString(R.string.op_menu_cookmethod));
                if (orderDetail.getParent() == null) {
                    ProductRest productRest = SanyiSDK.rest.getProductById(orderDetail.getProductId());
                    if (null != productRest && productRest.getSelfCooks().size() > 0) {
                        can_choose_gridview.setVisibility(View.GONE);
                        private_cook_layout.setVisibility(View.VISIBLE);
                    } else {
                        mCookAdapter = new CookMethodAdapter(activity, orderDetail, ChooseCookDialog.ALL_COOK_METHOD);
                        can_choose_gridview.setVisibility(View.VISIBLE);
                        private_cook_layout.setVisibility(View.GONE);
                    }
                } else {
                    SetItemDetails details = SanyiSDK.rest.getSetItemByGoodsId(orderDetail.getGoodsId());
                    if (null != details && null != details.getSelfCooks() && details.getSelfCooks().size() > 0) {
                        can_choose_gridview.setVisibility(View.GONE);
                        private_cook_layout.setVisibility(View.VISIBLE);
                    } else {
                        mCookAdapter = new CookMethodAdapter(activity, orderDetail, ChooseCookDialog.ALL_COOK_METHOD);
                        can_choose_gridview.setVisibility(View.VISIBLE);
                        private_cook_layout.setVisibility(View.GONE);
                    }
                }

                can_choose_hint_cook.setVisibility(View.VISIBLE);
                can_choose_hint_ing.setVisibility(View.GONE);
                break;
            case OrderOptionDialog.INGREDIENT:
                mCookAdapter = new CookMethodAdapter(activity, orderDetail, ChooseCookDialog.INGREDIENT);
                name_text.setText(activity.getString(R.string.op_menu_ingredient));
                can_choose_gridview.setVisibility(View.VISIBLE);
                private_cook_layout.setVisibility(View.GONE);
                can_choose_hint_cook.setVisibility(View.GONE);
                can_choose_hint_ing.setVisibility(View.VISIBLE);
                break;

            default:

                break;
        }
    }


    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.menu_sure_btn:
                    onSureClick();
                    listener.sure();
                    dialog.dismiss();
                    break;
                case R.id.menu_iv_close_dialog:
                    dialog.dismiss();
                    listener.cancel();
                    break;
                case R.id.op_dialog_back:
                    dialog.dismiss();
                    listener.cancel();
                    break;
                default:
                    break;
            }
        }
    };


    public void onSureClick() {
        switch (flag) {
            case OrderOptionDialog.COOK_METHOD:
                break;
            case OrderOptionDialog.INGREDIENT:
                break;
            default:
                break;
        }
    }
}
