package com.rainbow.smartpos.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.KeyboardUtil;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.rest.Units;
import com.sanyipos.sdk.utils.SearchAlgorithm;

import java.util.List;

public class SearchDishFragment extends Fragment {
    public EditText textView_fragment_order_dish_title;
    public GridView gridView_order_fragment_hold;
    public OrderDishAdapter orderDishAdapter;
    public OrderFragment orderFragment;
    public View v;
    public final static String TAG = "SearchDishFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    public void setOrderFragment(OrderFragment orderFragment) {
        this.orderFragment = orderFragment;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        v = inflater.inflate(R.layout.search_dish, container, false);
        textView_fragment_order_dish_title = (EditText) v.findViewById(R.id.textView_fragment_order_dish_title);
        gridView_order_fragment_hold = (GridView) v.findViewById(R.id.gridView_order_fragment_change);
        orderDishAdapter = new OrderDishAdapter(getActivity());
        gridView_order_fragment_hold.setAdapter(orderDishAdapter);
        textView_fragment_order_dish_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    List<Units> unitses = SearchAlgorithm.searchUnits(SanyiSDK.rest.searchProducts, s.toString().toUpperCase());
                    setSearchDish(unitses);
                    if (unitses.size() == 1) {
                        Units units = orderDishAdapter.getItem(0);
                        if (units != null) {
                            String code = units.getPinyinCodes();
                            int leftBrack = code.indexOf("(");
                            int rightBrack = code.indexOf(")");
                            if (leftBrack != -1 && rightBrack != -1) {
                                String subString = units.getPinyinCodes().substring(leftBrack + 1, rightBrack);
                                if (subString.equals(s.toString())) {
                                    orderFragment.onClickDish.onClickDish(orderDishAdapter.getItem(0), v);
                                    textView_fragment_order_dish_title.setText("");
                                    return;
                                }
                            }
                        }

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView_fragment_order_dish_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    Units units = orderDishAdapter.getItem(0);
                    if (units != null) {
                        orderFragment.onClickDish.onClickDish(orderDishAdapter.getItem(0), v);
                        textView_fragment_order_dish_title.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
        boolean isNum = SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_SEATCH_TYPE, false);
        new KeyboardUtil(v, getActivity(), textView_fragment_order_dish_title, isNum).showKeyboard();


        gridView_order_fragment_hold.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                orderFragment.onClickDish.onClickDish(orderDishAdapter.getItem(position), view);
                textView_fragment_order_dish_title.setText("");
            }


        });

//        List<Units> unitses = SearchAlgorithm.searchUnits(SanyiSDK.rest.searchProducts, "");
        setSearchDish(SanyiSDK.rest.searchProducts);

        return v;
    }

    public void setSearchDish(List<Units> dishs) {

        if (orderDishAdapter != null) {
            orderDishAdapter.setDish(dishs);
            orderDishAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        super.onHiddenChanged(hidden);
        if (!hidden) {
            textView_fragment_order_dish_title.setText("");
        }
    }

}
