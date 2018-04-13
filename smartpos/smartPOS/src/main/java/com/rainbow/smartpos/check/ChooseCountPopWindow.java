package com.rainbow.smartpos.check;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;

public class ChooseCountPopWindow {
    private View parentView;
    private Context activity;
    public OnSureListener onSureListener;
    private PopupWindow mPopupWindow;
    private int count;
    private int pos;
    private int height;
    TextView sure_btn;
    Button undo_voucher;
    ImageButton add_quantity;
    ImageButton reduce_quantity;
    TextView voucher_count;
    private int max = -1;
    private int singlemax = -1;
    private boolean canzero = false;


    public interface OnSureListener {
        void onSureClick(int count);
    }

    public ChooseCountPopWindow(View v, Context activity, int count, int position, int height, OnSureListener onSureListener) {
        this.parentView = v;
        this.activity = activity;
        this.count = count;
        this.pos = position;
        this.height = height;
        this.onSureListener = onSureListener;
    }

    public ChooseCountPopWindow(View v, Context activity, int count, int max, int singlemax, boolean canzero, int position, int height, OnSureListener onSureListener) {
        this.parentView = v;
        this.activity = activity;
        this.max = max;
        this.singlemax = singlemax;
        this.count = count;
        this.pos = position;
        this.height = height;
        this.onSureListener = onSureListener;
        this.canzero = canzero;
    }

    public void show() {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.voucher_count_dialog_layout, null);
        initView(contentView);
        int width = (int) activity.getResources().getDimension(R.dimen.check_fragment_voucher_count_width);
        int height = (int) activity.getResources().getDimension(R.dimen.check_fragment_voucher_count_height);
        mPopupWindow = new PopupWindow(contentView, width, height, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
        mPopupWindow.getContentView().measure(0, 0);
        int[] location = new int[2];
        parentView.getLocationInWindow(location);
        int parentLocation = location[0];
        mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.voucher_count_bg_left));
        mPopupWindow.getBackground().setAlpha(190);
        mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, parentLocation, (int) location[1] + height / 2);
//		}else{
//			mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.voucher_count_bg_right));
//			mPopupWindow.getBackground().setAlpha(190);
//			mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, parentLocation+parentView.getWidth()-width+2, (int)(location[1] + MainScreenActivity.getScreenHeight()*0.05 - activity.getResources().getDimension(R.dimen.check_fragment_voucher_count_pad_padding)));
//		}


    }

    public void dismiss() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
        }
    }

    private void initView(View view) {
        sure_btn = (TextView) view.findViewById(R.id.sure_btn);
        undo_voucher = (Button) view.findViewById(R.id.undo_voucher);
        add_quantity = (ImageButton) view.findViewById(R.id.add_quantity);
        reduce_quantity = (ImageButton) view.findViewById(R.id.reduce_quantity);
        voucher_count = (TextView) view.findViewById(R.id.voucher_count);
        sure_btn.setOnClickListener(onClickListener);
        undo_voucher.setOnClickListener(onClickListener);
        add_quantity.setOnClickListener(onClickListener);
        reduce_quantity.setOnClickListener(onClickListener);
        voucher_count.setText(String.valueOf(count));
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.sure_btn:
                    buttonSureClick();
                    break;
                case R.id.undo_voucher:

                    break;
                case R.id.add_quantity:
                    if (max == -1)
                        changeCount(true);
                    else if (Integer.parseInt(voucher_count.getText().toString()) < max && Integer.parseInt(voucher_count.getText().toString()) < singlemax) {
                        changeCount(true);
                    } else if (Integer.parseInt(voucher_count.getText().toString()) < max && Integer.parseInt(voucher_count.getText().toString()) >= singlemax) {
                        Toast.makeText(activity, "已达该菜品可选数量上限", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(voucher_count.getText().toString()) >= max)
                        Toast.makeText(activity, "已达该类配菜可选数量上限", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.reduce_quantity:
                    changeCount(false);
                    break;
                default:
                    break;
            }
        }
    };

    private void changeCount(boolean isAdd) {
        // TODO Auto-generated method stub
        try {
            int count = Integer.parseInt(voucher_count.getText().toString());
            if (isAdd) {
                count++;
                voucher_count.setText(String.valueOf(count));
            } else {
                if (canzero) {
                    if (count > 0) {
                        count--;
                        voucher_count.setText(String.valueOf(count));
                    }
//                    if (count == 0) {
//                        buttonSureClick();
//                    }
                } else {
                    if (count > 1) {
                        count--;
                        voucher_count.setText(String.valueOf(count));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buttonSureClick() {
        try {
            onSureListener.onSureClick(Integer.parseInt(voucher_count.getText().toString()));
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
