package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.Units;

public class ChooseUnitDialog {
    public static interface IChooseUnitListener {
        public void sure(ProductRest productRest);
    }

    private Context activity;
    private Units mUnit;
    private IChooseUnitListener listener;
    private OrderSizeListAdapter adapter;
    public PopupWindow popupWindow;
    private View contentView;

    // private Dialog Dialog;
    public ChooseUnitDialog(Context context, Units unit, View contentView, IChooseUnitListener listener) {
        // TODO Auto-generated constructor stub
        activity = context;
        mUnit = unit;
        this.contentView = contentView;
        this.listener = listener;
    }

    public void show() {
        View view = LayoutInflater.from(activity).inflate(R.layout.choose_unit_dialog, null);
        GridView mFoodUnitGridView = (GridView) view.findViewById(R.id.operationGridView);
        adapter = new OrderSizeListAdapter(activity, mUnit.products);
        mFoodUnitGridView.setAdapter(adapter);
        int width = (int) (300 *MainScreenActivity.getScreenDensity());
        popupWindow = new PopupWindow(view, width, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        //popupWindow.setBackgroundDrawable(mAct.getResources().getDrawable(R.drawable.choose_unit_bg));
        popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
        //popupWindow.getBackground().setAlpha(190);
        int[] location = new int[2];
        contentView.getLocationOnScreen(location);
        if ((location[0] + width) < MainScreenActivity.getScreenWidth()) {
            popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.voucher_count_bg_left));
            popupWindow.getBackground().setAlpha(190);
            popupWindow.showAtLocation(contentView, Gravity.NO_GRAVITY, location[0], location[1] + contentView.getHeight());
        } else {
            popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.voucher_count_bg_right));
            popupWindow.getBackground().setAlpha(190);
            popupWindow.showAtLocation(contentView, Gravity.NO_GRAVITY, location[0] + contentView.getWidth() - width, location[1] + contentView.getHeight());
        }
        mFoodUnitGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                ProductRest food = adapter.getItem(pos);
                if (!(!food.soldout || (food.soldoutCount > 0 && !food.longterm))) {
                    if (food.longterm) {
                        Toast.makeText(activity,"菜品已经停售",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity,"菜品已经售罄",Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                listener.sure(adapter.getSelect(pos));
                popupWindow.dismiss();
            }
        });
//        DisplayUtil.backgroundAlpha(activity,0.5f);
//        popupWindow.setOnDismissListener(new OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                // TODO Auto-generated method stub
//                DisplayUtil.backgroundAlpha(activity,1f);
//            }
//        });
    }
}
