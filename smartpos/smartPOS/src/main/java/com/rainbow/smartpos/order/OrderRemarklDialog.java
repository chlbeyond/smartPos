package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rainbow.common.view.MyDialog;
import com.rainbow.common.view.MyListView;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.RemarkFilter;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.Units;
import com.sanyipos.sdk.utils.JsonUtil;
import com.sanyipos.smartpos.model.RemarkBeanList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import tagview.TagCloudView;

/**
 * Created by ss on 2016/9/29.
 */

public class OrderRemarklDialog {
    private static final String ORDER_REMARK = "orderRemark";
    private MyDialog dialog;
    private EditText editRemark;
    private Context mContext;
    private LayoutInflater inflater;
    private View mainView;
    private OrderDetail orderDetail;
    private TagCloudView tagCloudView;
    public List<String> remarkLists = new ArrayList<>();
    public RemarkBeanList remarkBeans = new RemarkBeanList();
    private TextView sure;
    //    private RealmHelper realmHelper;
    private Listener.OnOrderOpBtnClickListener listener;
    public static final int REMARK_NORMAL_COUNT = 5;
    public static final int REMARK_COUNT = 15;
    private MyListView orderAttMlv;
    private ProductRest productRest;
    private OrderAttributesAdapter adapter;

    public OrderRemarklDialog(Context context, OrderDetail orderDetail, Listener.OnOrderOpBtnClickListener listener) {
        this.mContext = context;
        this.orderDetail = orderDetail;
//
//        realmHelper = new RealmHelper(context);
        this.listener = listener;
    }

    public void show() {
        inflater = LayoutInflater.from(mContext);
        mainView = inflater.inflate(R.layout.dialog_remark, null, false);

        dialog = new MyDialog(mContext, MainScreenActivity.getScreenWidth() * 0.4, MainScreenActivity.getScreenHeight() * 0.9, mainView, R.style.OpDialogTheme);
        editRemark = (EditText) mainView.findViewById(R.id.remark_edit);
        tagCloudView = (TagCloudView) mainView.findViewById(R.id.tag_cloud_view);
        sure = (TextView) mainView.findViewById(R.id.sure_btn);
        orderAttMlv = (MyListView) mainView.findViewById(R.id.mlv_orderatt);
        mainView.findViewById(R.id.iv_close_dialog).setOnClickListener(onclickListener);
        sure.setOnClickListener(onclickListener);
        initRemarkList();
        tagCloudView.setTags(remarkLists);
        tagCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                if (position != -1) {
                    editRemark.setText(editRemark.getText().toString() + " "+remarkLists.get(position) + " ");
                    editRemark.setSelection(editRemark.getText().toString().length()-1);
                }
            }
        });
        tagCloudView.setOnTagLongClickListener(new TagCloudView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(int position) {
                remarkBeans.remarkBeans.remove(position);
                remarkLists.remove(position);
                tagCloudView.setTags(remarkLists);
            }
        });

        editRemark.setText(orderDetail.getRemark() + " ");
        editRemark.setSelection(orderDetail.getRemark().length() + 1);
        if (orderDetail.getProductId() > 0)
            productRest = SanyiSDK.rest.getProductById(orderDetail.getProductId());
        else
        {
           productRest=SanyiSDK.rest.getProductByGoodsId(orderDetail.getGoodsId());
        }
        if (productRest != null && productRest.attributes != null && productRest.attributes.size() > 0) {
            orderAttMlv.setVisibility(View.VISIBLE);
            adapter = new OrderAttributesAdapter(productRest, mContext);
            orderAttMlv.setAdapter(adapter);
        } else
            orderAttMlv.setVisibility(View.GONE);
        dialog.show();
    }



    private void initRemarkList() {
        String remark = SharePreferenceUtil.getPreference(mContext, ORDER_REMARK, "");
        RemarkBeanList beans = JsonUtil.fromJson(remark, RemarkBeanList.class);
        if (beans != null) remarkBeans = beans;
        if (remarkBeans == null || remarkBeans.remarkBeans.size() == 0) return;
        Collections.sort(remarkBeans.remarkBeans, new Comparator<RemarkBeanList.RemarkBean>() {
            @Override
            public int compare(RemarkBeanList.RemarkBean lhs, RemarkBeanList.RemarkBean rhs) {
                if (lhs.count > rhs.count) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        if (remarkBeans.remarkBeans.size() > 10)
            remarkBeans.remarkBeans = remarkBeans.remarkBeans.subList(0, 10);
        for (RemarkBeanList.RemarkBean remarkBean : remarkBeans.remarkBeans) {
            if (!remarkBean.remark.trim().isEmpty())
                remarkLists.add(remarkBean.getRemark());
        }
    }

    public void onSure() {
        String remark = "";

        if (adapter != null && adapter.getSelects().size() > 0) {
            List<OrderAttributesAdapter.AttRemark> a = adapter.getSelects();
            for (OrderAttributesAdapter.AttRemark s : a) {
                remark += s.getRemark() + ",";
            }
        }
        remark += editRemark.getText().toString();
        List<String> strings = RemarkFilter.filterRemark(editRemark.getText().toString());
        for (String string : strings) {
            if (!RemarkFilter.remarkIsExit(remarkBeans.remarkBeans, string)) {
                remarkBeans.addRemark(string);
            } else {
                remarkBeans.changeRemark(string);
            }
        }


        orderDetail.setRemark(remark);
        SharePreferenceUtil.saveStringPreference(mContext, ORDER_REMARK, new Gson().toJson(remarkBeans));
        listener.sure();
    }

    private View.OnClickListener onclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sure_btn:
                    onSure();
                    dialog.dismiss();
                    break;
                case R.id.iv_close_dialog:
                    dialog.dismiss();
                    break;
            }
        }
    };
}
