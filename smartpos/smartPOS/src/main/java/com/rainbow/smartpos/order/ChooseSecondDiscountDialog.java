package com.rainbow.smartpos.order;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.rest.Units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by administrator on 2017/8/29.
 */

public class ChooseSecondDiscountDialog {



    public ChooseSecondDiscountDialog(Context context, List<Units> list, int maxNum, ChooseSecondDiscountListener listener) {
        this.mChooseSecondDiscountListener = listener;
        this.list.clear();
        this.maxNum=maxNum;
        this.mContext=context;
        this.list.addAll(list);
    }

    public static interface ChooseSecondDiscountListener {
        public void confirm(HashMap<Long, Integer> map);
    }

    private Context mContext;
    private MyDialog dialog;
    private TextView chooseSecondDiscountNumTv;
    private TextView chooseSecondDiscountCancelTv;
    private TextView chooseSecondDiscountConfirmTv;
    private GridView chooseSecondDiscountGv;
    private ChooseSecondDiscountListener mChooseSecondDiscountListener;
    private List<Units> list=new ArrayList<>();
    private SecondDiscountAdapter adapter;
    private int maxNum;



    public void show() {
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
//        View view = LayoutInflater.from(mContext).inflate(R.layout.choose_second_discount_dialog, null);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.choose_second_discount_dialog, null, false);
        dialog = new MyDialog(mContext, (int) (MainScreenActivity.getScreenWidth() * 0.6), (int) (MainScreenActivity.getScreenHeight() * 0.8), view, R.style.OpDialogTheme);



        chooseSecondDiscountGv = (GridView) view.findViewById(R.id.gv_choose_second_discount);
        chooseSecondDiscountNumTv = (TextView) view.findViewById(R.id.tv_choose_second_discount_num);
        chooseSecondDiscountCancelTv = (TextView) view.findViewById(R.id.tv_choose_second_discount_cancel);
        chooseSecondDiscountConfirmTv = (TextView) view.findViewById(R.id.tv_choose_second_discount_confirm);

        adapter=new SecondDiscountAdapter(mContext,list,maxNum);
        chooseSecondDiscountGv.setAdapter(adapter);

        chooseSecondDiscountNumTv.setText("可选择"+maxNum+"份优惠菜");

//        builder.setView(view);
//        final Dialog dialog = builder.create();
        chooseSecondDiscountCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        chooseSecondDiscountConfirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooseSecondDiscountListener.confirm(adapter.getHasMap());
                dialog.dismiss();
            }
        });


        dialog.show();
//        adapter.update(list,maxNum);
//        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
//        p.width = (int) (MainScreenActivity.getScreenWidth());
//         p.height = (int) (MainScreenActivity.getScreenHeight() );
//        dialog.getWindow().setAttributes(p);
    }



}
