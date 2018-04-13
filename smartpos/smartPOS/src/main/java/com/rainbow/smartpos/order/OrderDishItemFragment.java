package com.rainbow.smartpos.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableRow;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.rest.GoodsGroup;
import com.sanyipos.sdk.model.rest.SubGroupsRest;
import com.sanyipos.sdk.model.rest.Units;

import java.lang.reflect.Field;

public class OrderDishItemFragment extends Fragment implements OnClickListener {
    static final String LOG_TAG = "OrderDishItemFragment";
    //    int number;
    public OrderDishAdapter adapter;
    GridView gridViewOrderDish;
    //    AutoRecyclerView recyclerViewOrderDish;
    TableRow tableRowSubCatetory;
    OrderFragment orderFragment;
    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
    public Button subCats[] = null;
    GoodsGroup subGroupsRest;
    Bundle bundle;
    Context context;

    public interface FlingListener {
        void onLeft();

        void onRight();
    }


    private FlingListener gestureListener;

    public void setFlingListener(FlingListener l) {
        gestureListener = l;
    }

    private GestureDetector mGestureDetector;

    public interface OnClickDish {
        void onClickDish(Units unit, View view);
    }


    public void setClickDishListener(OnClickDish clickDishListener) {
        this.clickDishListener = clickDishListener;
    }

    private OnClickDish clickDishListener;


    public void setOrderFragment(OrderFragment fragmet) {
        orderFragment = fragmet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        number = getArguments() != null ? getArguments().getInt("number") : 1;
//        subGroupsRest = SanyiSDK.rest.goodsGroup.get(number);
        params.leftMargin = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_dish_item, container, false);
        tableRowSubCatetory = (TableRow) view.findViewById(R.id.tableRowSubCategory);
        tableRowSubCatetory.setFocusable(false);
        tableRowSubCatetory.setFocusableInTouchMode(false);
        adapter = new OrderDishAdapter(view.getContext());
        adapter.setOrderFragment(orderFragment);
        gridViewOrderDish = (GridView) view.findViewById(R.id.gridViewOrderDish);
        gridViewOrderDish.setAdapter(adapter);
        gridViewOrderDish.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickDishListener.onClickDish(adapter.getItem(position), view);
            }
        });
        context=getActivity();
        populateSubCategory();

        mGestureDetector = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        try {
                            if (gestureListener != null) {
                                if (e1.getX() - e2.getX() < -89) {
                                    gestureListener.onLeft();
                                } else if (e1.getX() - e2.getX() > 89) {
                                    gestureListener.onRight();
                                }
                                return true;
                            }
                        } catch (Exception e) {
                        }
                        return false;
                    }
                });

        gridViewOrderDish.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.tableRowSubCategory: {
                Button subCatButton = (Button) v;
                clickSubCats((Integer) subCatButton.getTag());

                break;
            }
        }
    }

    public void setDishTypePos(int pos) {
        subGroupsRest = SanyiSDK.rest.goodsGroup.get(pos);
        populateSubCategory();
    }

    public void clickSubCats(int position) {
        if (subGroupsRest == null && subGroupsRest.subgroups == null) {
            Toast.makeText(getActivity(), "餐厅数据不全，请重新下载", Toast.LENGTH_LONG).show();
            return;
        }
        if (adapter != null) {
            adapter.setDish(subGroupsRest.getSubgroups().get(position).getUnits());
            for (int i = 0; i < subCats.length; i++) {
                subCats[i].setSelected(false);

            }
            subCats[position].setSelected(true);
        }
    }

    public void refreshDishState() {
        if (subCats != null) {
            for (int i = 0; i < subCats.length; ++i)
                if (subCats[i].isSelected()) {
                    clickSubCats(i);
                    break;
                }
        }
    }

    public void populateSubCategory() {

        if (tableRowSubCatetory == null || subGroupsRest == null) return;
        if (tableRowSubCatetory.getChildCount() > 0) {
            tableRowSubCatetory.removeAllViews();
        }
        subCats = new Button[subGroupsRest.subgroups.size()];
        for (int j = 0; j < subGroupsRest.subgroups.size(); j++) {

            SubGroupsRest subCategory;
            String subCatName;

            subCategory = subGroupsRest.subgroups.get(j);
            if (subCategory.getId() == -1) {

                subCatName = "全部";
            } else {

                subCatName = subCategory.name;
            }
            Button btn1 = new Button(context);

            subCats[j] = btn1;
            btn1.setText(subCatName);
            btn1.setTextSize(context.getResources().getDimensionPixelOffset(R.dimen.table_status_bottom_text));
            btn1.setBackgroundResource(R.drawable.subcat_trip_button_selector);
            btn1.setTextColor(Color.WHITE);
            btn1.setPadding(20, 10, 20, 10);
            params.setMargins(5, 10, 5, 10);
            btn1.setLayoutParams(params);

            btn1.setOnClickListener(this);
            btn1.setId(R.id.tableRowSubCategory);
            btn1.setGravity(Gravity.CENTER);
            btn1.setTag(j);

            tableRowSubCatetory.addView(btn1);
        }
        clickSubCats(0);
    }

//    public void refreshDish() {
//        subGroupsRest = SanyiSDK.rest.getGoodsGroup().get(number);
//        if (adapter != null)
//            adapter.notifyDataSetChanged();
//    }

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

}
