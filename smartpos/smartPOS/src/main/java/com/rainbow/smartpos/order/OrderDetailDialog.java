package com.rainbow.smartpos.order;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.place.WeightDialog;
import com.rainbow.smartpos.util.DisplayUtil;
import com.rainbow.smartpos.util.Listener;
import com.sanyipos.sdk.model.OrderDetail;

public class OrderDetailDialog implements View.OnClickListener {
    public interface OrderDetailDialogListener {
        void freedish();

        void changePrice();

        void other();

        void deleteDish();

        void ondismiss();

        void placeReturnDish();

        void placeFreeDish();

        void placeChangePrice();

        void remark();
    }

    private MainScreenActivity activity;
    private OrderDetail orderDetail;
    private OrderDetailDialogListener listener;
    public PopupWindow popupWindow;
    private View contentView;

    private Button mButtonFree;
    private Button mButtonChangePrice;
    private Button mButtonDelete;
    private Button mButtonOther;
    private Button mButtonCookMethod;
    private Button mButtonIngrident;
    private Button mButtonReturn;
    private Button mButtonRemark;
    private TextView mTextViewReduceCount;
    private TextView mTextViewCount;
    private TextView mTextViewAddCount;
    View view;
    int height;

    // private Dialog Dialog;
    public OrderDetailDialog(MainScreenActivity context, OrderDetail orderDetail, View contentView, OrderDetailDialogListener listener) {
        // TODO Auto-generated constructor stub
        activity = context;
        this.orderDetail = orderDetail;
        this.contentView = contentView;
        this.listener = listener;
    }

    public void show() {
        if (!orderDetail.isPlaced()) {
            view = LayoutInflater.from(activity).inflate(R.layout.orderdetail_dialog, null);
            mButtonFree = (Button) view.findViewById(R.id.button_order_detail_dialog_free);
            mButtonFree.setOnClickListener(this);
            mButtonDelete = (Button) view.findViewById(R.id.button_order_detail_dialog_delete);
            mButtonDelete.setOnClickListener(this);
            mButtonChangePrice = (Button) view.findViewById(R.id.button_order_detail_dialog_changeprice);
            mButtonChangePrice.setOnClickListener(this);
//        mButtonOther = (Button) view.findViewById(R.id.button_order_detail_dialog_other);
//        mButtonOther.setOnClickListener(this);

            mButtonCookMethod = (Button) view.findViewById(R.id.button_order_detail_cook_method);
            mButtonCookMethod.setOnClickListener(this);

            mButtonIngrident = (Button) view.findViewById(R.id.button_order_detail_ingrident);
            mButtonIngrident.setOnClickListener(this);

            mTextViewReduceCount = (TextView) view.findViewById(R.id.textView_order_detail_dialog_reduce);
            mTextViewReduceCount.setOnClickListener(this);
            mTextViewAddCount = (TextView) view.findViewById(R.id.textView_order_detail_dialog_add);
            mTextViewAddCount.setOnClickListener(this);
            mTextViewCount = (TextView) view.findViewById(R.id.textView_order_detail_dialog_count);
            mTextViewCount.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
            initView();
            mTextViewCount.setText(Integer.toString(orderDetail.getQuantity()));
            mTextViewCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new WeightDialog().show(activity, activity.getString(R.string.change_count),
                            String.valueOf(orderDetail.getQuantity()), WeightDialog.CHANGE_COUNT, new Listener.OnChangePriceListener() {
                                @Override
                                public void onSure(Double count) {
                                    mTextViewCount.setText(Integer.toString(count.intValue()));
                                    orderDetail.setQuantity(count.intValue());
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                }
            });
            mButtonRemark = (Button) view.findViewById(R.id.button_order_detail_remark);
            mButtonRemark.setOnClickListener(this);
            height = (int) (460 * activity.metric.density);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.orderdetail_place_dialog, null);
            mButtonChangePrice = (Button) view.findViewById(R.id.button_order_detail_dialog_changeprice);
            mButtonChangePrice.setOnClickListener(this);
            mButtonFree = (Button) view.findViewById(R.id.button_order_detail_dialog_free);
            mButtonFree.setOnClickListener(this);
            mButtonReturn = (Button) view.findViewById(R.id.button_order_detail_return);
            mButtonReturn.setOnClickListener(this);
            height = (int) (200 * activity.metric.density);
        }
        int width = (int) (300 * activity.metric.density);

        popupWindow = new PopupWindow(view, width, height, true);
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
        final int[] location = new int[2];
        contentView.getLocationOnScreen(location);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (location[1] + height / 2 < MainScreenActivity.getScreenHeight()) {
            popupWindow.showAtLocation(contentView, Gravity.NO_GRAVITY, (location[0] + contentView.getWidth()) + 20, location[1] - height / 2);
        } else {
            popupWindow.showAtLocation(contentView, Gravity.NO_GRAVITY, (location[0] + contentView.getWidth()) + 20, location[1] - height + contentView.getHeight());
        }

        DisplayUtil.backgroundAlpha(activity, 0.5f);
        popupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                DisplayUtil.backgroundAlpha(activity, 1f);
                listener.ondismiss();
            }
        });

    }

    public void initView() {
        if (orderDetail.isFree()) {
            mButtonFree.setText("取消赠送");
            mButtonChangePrice.setEnabled(false);
        } else {
            mButtonFree.setText("赠送");
        }
        if (orderDetail.getParent() != null) {
            mTextViewAddCount.setEnabled(false);
            mTextViewReduceCount.setEnabled(false);
            mButtonDelete.setEnabled(false);
            mButtonFree.setEnabled(false);
            mButtonChangePrice.setEnabled(false);
            mTextViewCount.setEnabled(false);
        }

        if (orderDetail.isWeight()) {
            mTextViewAddCount.setEnabled(false);
            mTextViewReduceCount.setEnabled(false);
            mTextViewCount.setEnabled(false);
        }
        if (orderDetail.isSet()||orderDetail.isMixed()) {
            mTextViewAddCount.setEnabled(false);
            mTextViewCount.setEnabled(false);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_order_detail_return:
                listener.placeReturnDish();
                popupWindow.dismiss();
                break;
            case R.id.button_order_detail_dialog_changeprice:
                if (orderDetail.isPlaced()) {
                    listener.placeChangePrice();
                } else {
                    listener.changePrice();
                }
                popupWindow.dismiss();
                break;
            case R.id.button_order_detail_dialog_free:
                if (orderDetail.isPlaced()) {
                    listener.placeFreeDish();
                } else {
                    listener.freedish();
                }
                popupWindow.dismiss();
                break;
            case R.id.button_order_detail_cook_method:
            case R.id.button_order_detail_ingrident:
                listener.other();
                popupWindow.dismiss();
                break;
            case R.id.textView_order_detail_dialog_add:
                int count = Integer.valueOf(mTextViewCount.getText().toString());
                mTextViewCount.setText(Integer.toString(count + 1));
                orderDetail.setQuantity(count + 1);
                break;
            case R.id.textView_order_detail_dialog_reduce:
                int redcount = Integer.valueOf(mTextViewCount.getText().toString());
                if (redcount > 1) {
                    mTextViewCount.setText(Integer.toString(redcount - 1));
                    orderDetail.setQuantity(redcount - 1);
                } else {
                    listener.deleteDish();
                    popupWindow.dismiss();
                }
                break;
            case R.id.button_order_detail_dialog_delete:
                listener.deleteDish();
                popupWindow.dismiss();
                break;
            case R.id.button_order_detail_remark:
                listener.remark();
                popupWindow.dismiss();

        }
    }
}
