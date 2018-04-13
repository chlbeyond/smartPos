package com.rainbow.smartpos.sold;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rainbow.common.view.Order.OrderDish;
import com.rainbow.smartpos.BaseActivity;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.check.InputCountPopWindow;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.event.EventMessage;
import com.rainbow.smartpos.order.ChooseUnitDialog;
import com.rainbow.smartpos.order.OrderDishItemFragment;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.SoldoutDetailRequest;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.Units;
import com.sanyipos.sdk.model.scala.Soldout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by ss on 2016/1/21.
 */
public class SoldFragment extends BaseActivity implements View.OnClickListener {
    private String TAG = "SoldFragment";
    public GridView mSlodRecyclerView;
    public GridView mStopRecyclerView;
    private Context mContext;
    public SlodRecyclerAdapter slodOutAdapter;
    public SlodRecyclerAdapter saleStopAdapter;
    private GridLayoutManager layoutManager;
    private NormalDialog normalDialog;
    private LinearLayout mRightView;
    private DrawerLayout mDrawerLayout;
    private OrderDish orderDish;
    private float previousX = 0;
    private Button mButtonPrintSoldList;
    InputCountPopWindow inputCountPopWindow;
    public ProductRest productRest;

    public enum Type {
        Sold, Stop
    }

    private Type type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.layout_sold_fragment);

        ButterKnife.bind(this);

        setCustomTitle("沽清管理");

        setTitleColor(Color.WHITE);
        mRightView = (LinearLayout) findViewById(R.id.view_sold_fragment);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_slod);
        mButtonPrintSoldList = (Button) findViewById(R.id.button_print_sold_list);
        mButtonPrintSoldList.setOnClickListener(this);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        orderDish = new OrderDish(mContext, getSupportFragmentManager(), new OrderDishItemFragment.OnClickDish() {
            @Override
            public void onClickDish(Units unit, View view) {
                chooseSoldMode(unit, view);
            }
        });

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        View soldView = orderDish.initView();
        Double width = MainScreenActivity.getScreenWidth() * 0.75;
        params.width = width.intValue();
        soldView.setLayoutParams(params);
        mRightView.addView(soldView);
        mSlodRecyclerView = (GridView) findViewById(R.id.recyclerView_slod_out);
        mStopRecyclerView = (GridView) findViewById(R.id.recyclerView_sale_stop);
        slodOutAdapter = new SlodRecyclerAdapter(mContext, Type.Sold);
        slodOutAdapter.setmProducts(SanyiSDK.rest.getAllSoldDish());
        saleStopAdapter = new SlodRecyclerAdapter(mContext, Type.Stop);
        saleStopAdapter.setmProducts(SanyiSDK.rest.getAllStopSellDish());
        mSlodRecyclerView.setAdapter(slodOutAdapter);
        mSlodRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < slodOutAdapter.getCount() - 1) {
                    ProductRest product = (ProductRest) slodOutAdapter.getItem(i);
                    SanyiScalaRequests.cancelSoldoutDishRequest(product.id, new Request.ICallBack() {
                        @Override
                        public void onSuccess(String status) {
                            Toast.makeText(mContext,status,Toast.LENGTH_LONG).show();

                        }


                        @Override
                        public void onFail(String error) {
                            Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    type = Type.Sold;
                }
            }
        });
        mStopRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < saleStopAdapter.getCount() - 1) {
                    ProductRest product = (ProductRest) saleStopAdapter.getItem(i);
                    SanyiScalaRequests.cancelSoldoutDishRequest(product.id, new Request.ICallBack() {
                        @Override
                        public void onSuccess(String status) {
                            Toast.makeText(mContext,status,Toast.LENGTH_LONG).show();
                        }


                        @Override
                        public void onFail(String error) {
                            Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    type = Type.Stop;
                }
            }
        });
        mStopRecyclerView.setAdapter(saleStopAdapter);

    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onUserEvent(EventMessage event) {
        switch (event.eventType) {
            case Restaurant.EVENT_SOLD:
                adapterNotify();
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_print_sold_list:
                SanyiScalaRequests.printSoldoutListRequest(new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {
                        Toast.makeText(mContext,status,Toast.LENGTH_LONG).show();
                    }



                    @Override
                    public void onFail(String error) {

                        Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }

    public void adapterNotify() {
        slodOutAdapter.setmProducts(SanyiSDK.rest.getAllSoldDish());
        saleStopAdapter.setmProducts(SanyiSDK.rest.getAllStopSellDish());
        slodOutAdapter.notifyDataSetChanged();
        saleStopAdapter.notifyDataSetChanged();
        orderDish.notifyDataChanged();
    }

    public void chooseSoldMode(final Units unit, final View view) {
        if (unit.products.size() > 1) {
            ChooseUnitDialog chooseUnitDialog = new ChooseUnitDialog(this, unit, view, new ChooseUnitDialog.IChooseUnitListener() {

                @Override
                public void sure(ProductRest pro) {
                    productRest = pro;
                    addSoldDishCount(view);
                }
            });
            chooseUnitDialog.show();
        } else {
            productRest = unit.products.get(0);
            addSoldDishCount(view);
        }

    }

    public void addSoldDishCount(View view) {
        switch (type) {
            case Sold:
                inputCountPopWindow = new InputCountPopWindow(view, this, new InputCountPopWindow.OnSureListener() {
                    @Override
                    public void onSureClick(Double value) {
                        addSoldDish(productRest, value, false);
                        inputCountPopWindow.dismiss();
                    }
                });
                inputCountPopWindow.show();
                break;
            case Stop:
                addSoldDish(productRest, 0.0, true);
                break;
        }
    }

    public void addSoldDish(final ProductRest pro, Double count, boolean isLongterm) {
        SanyiScalaRequests.soldoutDishRequest(pro.id, count, isLongterm, new SoldoutDetailRequest.ISoldoutListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(Soldout result) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext,result.soldout.longterm ? "菜品停售成功" : "菜品沽清成功",Toast.LENGTH_LONG).show();
            }

        });
    }
}
