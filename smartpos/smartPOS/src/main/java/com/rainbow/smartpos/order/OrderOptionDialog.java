package com.rainbow.smartpos.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rainbow.common.view.MyDialog;
import com.rainbow.common.view.MyListView;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.Listener.OnOrderOpBtnClickListener;
import com.rainbow.smartpos.util.RemarkFilter;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.utils.JsonUtil;
import com.sanyipos.sdk.utils.OrderUtil;
import com.sanyipos.smartpos.model.RemarkBeanList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tagview.TagCloudView;

public class OrderOptionDialog {
    public static final int COOK_METHOD = 0;
    public static final int INGREDIENT = 1;
    public static final int PRIVATE_COOK_METHOD = 2;
    private MyDialog dialog;
    private OrderDetail orderDetail;
    TextView sure;
    ImageButton cancel;


    Context activity;
    private GridView cookmethod_gridview;
    private GridView ingredient_gridview;
    private GridView privateCookMethodGridView;
    private GridView common_use_remark_gridview;

    private TextView childfood_textview;
    private ListView childfood_listview;

    private CookMethodAdapter cookMethodAdapter;
    private EditText editPrice;
    private TextView titleText;
    private LinearLayout editPriceLayout;

    private TextView textViewPrivateCook;
    private AdditiveAdapter mCookMethodAdapter;
    private AdditiveAdapter mIngredientAdapter;


    private CommonRemarkAdapter mCommonRemarkAdapter;
    private View mainView;
    private LayoutInflater inflater;
    private ChooseCookDialog mChooseCookDialog;
    private OnOrderOpBtnClickListener listener;
    public List<Integer> mSelectPrivateCooks = new ArrayList<>();
    private long mixednum = 0;
    private MyListView orderAttMlv;
    private ProductRest productRest;
    private OrderAttributesAdapter orderAttributesAdapter;
    public List<String> remarkLists = new ArrayList<>();
    public RemarkBeanList remarkBeans = new RemarkBeanList();
    private TagCloudView tagCloudView;
    private static final String ORDER_REMARK = "orderRemark";
    private EditText editRemark;


    ChildFoodAdapter adapter;


    public OrderOptionDialog(Context activity, OrderDetail orderDetail, long mixednum, OnOrderOpBtnClickListener listener) {
        this.activity = activity;
        this.orderDetail = orderDetail;
        this.listener = listener;
        this.mixednum = mixednum;
    }

    public void show() {
        inflater = LayoutInflater.from(activity);
        mainView = inflater.inflate(R.layout.order_op_dialog_layout, null, false);
        dialog = new MyDialog(activity, MainScreenActivity.getScreenWidth() * 0.4, MainScreenActivity.getScreenHeight() * 0.9, mainView, R.style.OpDialogTheme);

        initMainView(mainView);
        initMainListener();
        initPrivateCook();

        if (orderDetail.getProductId() > 0)
            productRest = SanyiSDK.rest.getProductById(orderDetail.getProductId());
        else {
            productRest = SanyiSDK.rest.getProductByGoodsId(orderDetail.getGoodsId());
        }
        if (productRest != null && productRest.attributes != null && productRest.attributes.size() > 0) {
            orderAttMlv.setVisibility(View.VISIBLE);
            orderAttributesAdapter = new OrderAttributesAdapter(productRest, activity);
            orderAttMlv.setAdapter(orderAttributesAdapter);
        } else
            orderAttMlv.setVisibility(View.GONE);
        editRemark.setText(orderDetail.getRemark() + " ");
        editRemark.setSelection(orderDetail.getRemark().length() + 1);
        mCookMethodAdapter = new AdditiveAdapter(activity, orderDetail, OrderOptionDialog.COOK_METHOD);
        cookmethod_gridview.setAdapter(mCookMethodAdapter);
        initRemarkList();

        mIngredientAdapter = new AdditiveAdapter(activity, orderDetail, OrderOptionDialog.INGREDIENT);
        ingredient_gridview.setAdapter(mIngredientAdapter);
        mCommonRemarkAdapter = new CommonRemarkAdapter(activity);
        common_use_remark_gridview.setAdapter(mCommonRemarkAdapter);

        cookMethodAdapter = new CookMethodAdapter(activity, orderDetail, ChooseCookDialog.PRIVATE_COOK_METHOD, mSelectPrivateCooks);
        privateCookMethodGridView.setAdapter(cookMethodAdapter);
        privateCookMethodGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (!mSelectPrivateCooks.contains(position)) {
                    mSelectPrivateCooks.add(position);
                } else {
                    for (int j = 0; j < mSelectPrivateCooks.size(); j++) {
                        if (position == mSelectPrivateCooks.get(j)) {
                            mSelectPrivateCooks.remove(j);
                        }
                    }
                }

                cookMethodAdapter.notifyDataSetChanged();
            }
        });
        if (orderDetail.isMixed()) {
            childfood_textview.setVisibility(View.VISIBLE);
            childfood_listview.setVisibility(View.VISIBLE);
            adapter = new ChildFoodAdapter(activity, mixednum, orderDetail);
            childfood_listview.setAdapter(adapter);
        } else {
            childfood_textview.setVisibility(View.GONE);
            childfood_listview.setVisibility(View.GONE);
        }
        dialog.show();
        if (cookMethodAdapter.getCount() <= 0) {
            textViewPrivateCook.setVisibility(View.GONE);
        }
    }

    private void initRemarkList() {
        String remark = SharePreferenceUtil.getPreference(activity, ORDER_REMARK, "");
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

        tagCloudView.setTags(remarkLists);
        tagCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                if (position != -1) {
                    editRemark.setText(editRemark.getText().toString() + " " + remarkLists.get(position) + " ");
                    editRemark.setSelection(editRemark.getText().toString().length() - 1);
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

        if (productRest!=null&&productRest.attributes != null && productRest.attributes.size() > 0 && orderDetail.getRemark() != null && orderDetail.getRemark().length() > 0) {
            //初始化多维度属性选中状态
            List<String> selectRemarks = new ArrayList<>();
            List<String> noSelectRemarks = new ArrayList<>();
            String[] remarks = orderDetail.getRemark().split(",");
            List<String> remarklist = new ArrayList<>();
            for (int j = 0; j < remarks.length; j++)
                remarklist.add(remarks[j]);

            for (String a : remarklist
                    ) {
                if (isSelectRemark(a))
                    selectRemarks.add(a);
                    else
                        noSelectRemarks.add(a);
            }
            List<OrderAttributesAdapter.AttRemark> attRemarkList = (List<OrderAttributesAdapter.AttRemark>) orderDetail.getData("attremark");
            if (attRemarkList != null && attRemarkList.size() > 0) {
                for (int i = 0; i < attRemarkList.size(); i++) {
                    attRemarkList.get(i).setChecked(false);
                }
                orderAttributesAdapter.setSelects(attRemarkList);
                String editRemarkString = "";
                for (String e : noSelectRemarks
                        ) {
                    editRemarkString = editRemarkString + " " + e;
                }
                editRemark.setText(editRemarkString + " ");
                editRemark.setSelection(editRemarkString.length() + 1);
            }

        }
    }

    private boolean isSelectRemark(String a) {
        boolean is = false;
        for (int i = 0; i < productRest.attributes.size(); i++) {
            if (productRest.attributes.get(i).value.contains(a))
                is = true;
        }
        return is;
    }

    private void initPrivateCook() {
        if (orderDetail.getPrivateCookMethod().size() > 0) {
            ProductRest product = SanyiSDK.rest.getProductById(orderDetail.getProductId());
            List<ProductRest> displayDishs = new ArrayList<ProductRest>();
            if (product != null) {
                displayDishs = product.getSelfCooks();
            } else {
                displayDishs = SanyiSDK.rest.getSetItemSelfCooks(orderDetail.getGoodsId());
            }
            for (OrderDetail detail : orderDetail.getPrivateCookMethod()) {
                for (int i = 0; i < displayDishs.size(); i++) {
                    if (detail.getProductId() == displayDishs.get(i).getId()) {
                        mSelectPrivateCooks.add(i);
                    }

                }
            }
        }
    }


    private void initMainView(View view) {
        cookmethod_gridview = (GridView) view.findViewById(R.id.cookmethod_gridview);
        ingredient_gridview = (GridView) view.findViewById(R.id.ingredient_gridview);
        common_use_remark_gridview = (GridView) view.findViewById(R.id.common_use_remark_gridview);
        privateCookMethodGridView = (GridView) view.findViewById(R.id.private_cookmethod_gridview);
        sure = (TextView) view.findViewById(R.id.sure_btn);
        cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);
        editPrice = (EditText) view.findViewById(R.id.editPrice);
        titleText = (TextView) view.findViewById(R.id.op_dialog_title);
        textViewPrivateCook = (TextView) view.findViewById(R.id.textView_private_cookmethed);
        tagCloudView = (TagCloudView) view.findViewById(R.id.tag_cloud_view);
        childfood_textview = (TextView) view.findViewById(R.id.childfood_name_textview);
        childfood_listview = (ListView) view.findViewById(R.id.childfood_listview);
        editRemark = (EditText) view.findViewById(R.id.remark_edit);
        orderAttMlv = (MyListView) view.findViewById(R.id.mlv_orderatt);

        titleText.setText(orderDetail.getName());
        editPriceLayout = (LinearLayout) view.findViewById(R.id.editPriceLayout);
        editPriceLayout.setVisibility(View.GONE);
//        if (orderDetail.isMarket()) {
//            editPrice.setHint(OrderUtil.decimalFormatter.format(orderDetail.getCurrentPrice()));
//            editPriceLayout.setVisibility(View.VISIBLE);
//        }
    }


    public boolean isShowing() {
        if (null == dialog) {
            return false;
        }
        return dialog.isShowing();
    }

    public void dismiss() {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    private void initMainListener() {
        sure.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        cookmethod_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                OrderDetail detail = mCookMethodAdapter.getItem(position);
                if (detail.getId() == -1) {
                    mChooseCookDialog = new ChooseCookDialog(activity, orderDetail, COOK_METHOD, new Listener.OnChooseCookListener() {

                        @Override
                        public void sure() {
                            // TODO Auto-generated method stub
                            refresh();
                        }

                        @Override
                        public void cancel() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void exit() {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }

                    });
                    mChooseCookDialog.show();
                } else {
                    mCookMethodAdapter.remove(position);
                }

            }
        });
        ingredient_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                OrderDetail detail = mIngredientAdapter.getItem(position);
                if (detail.getId() == -1) {
                    mChooseCookDialog = new ChooseCookDialog(activity, orderDetail, INGREDIENT, new Listener.OnChooseCookListener() {

                        @Override
                        public void sure() {
                            // TODO Auto-generated method stub
                            refresh();
                        }

                        @Override
                        public void cancel() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void exit() {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }

                    });
                    mChooseCookDialog.show();
                } else {
                    mIngredientAdapter.remove(position);
                }
            }
        });
//        common_use_remark_gridview.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // TODO Auto-generated method stub
//                if (editRemark.getText().toString().isEmpty()) {
//                    editRemark.setText(mCommonRemarkAdapter.getItem(position));
//                } else {
//                    editRemark.append(" " + mCommonRemarkAdapter.getItem(position));
//                }
//
//            }
//        });
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.sure_btn:
                    onSureClick();
                    break;
                case R.id.iv_close_dialog:
                    listener.cancel();
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public void onSureClick() {

        if (orderDetail.isMixed()) {
            if (adapter.checkAllSelected()) {
                orderDetail.childrenOrderDetail.clear();
                orderDetail.childrenOrderDetail.addAll(adapter.getSelectChildFood());
            } else {
                Toast.makeText(activity, "请按要求选择配菜", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        orderDetail.cleanPrivateCookMethod();
        if (mSelectPrivateCooks.size() > 0) {
            for (Integer i : mSelectPrivateCooks) {
                orderDetail.addPrivateCookMethod(OrderUtil.createOrderDetail(cookMethodAdapter.getItem(i), orderDetail.getQuantity()));
            }
        }
        /*if (orderDetail.isMarket()) {
            String price = editPrice.getText().toString();
            if (!price.isEmpty()) {
                try {
                    orderDetail.setOriginPrice(Double.parseDouble(price));
                    orderDetail.setCurrentPrice(Double.parseDouble(price));
                    orderDetail.setRealCurrentPrice(Double.parseDouble(price));
                    orderDetail.setPriceChanged(true);
                    listener.sure();
                    dialog.dismiss();
                } catch (Exception e) {
                    // TODO: handle exception+
                    Toast.makeText(activity, "请输入合法的价格", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "请输入菜品的价格", Toast.LENGTH_SHORT).show();
            }
        } else */
        {
            if (orderDetail.isMixed())
                listener.sure(orderDetail);
            else
                listener.sure();
            dialog.dismiss();
        }
        String remark = "";

        if (orderAttributesAdapter != null && orderAttributesAdapter.getSelects().size() > 0) {
            List<OrderAttributesAdapter.AttRemark> a = orderAttributesAdapter.getSelects();
            for (OrderAttributesAdapter.AttRemark s : a) {
                remark += s.getRemark() + ",";
            }
            orderDetail.setData("attremark", a);
        }
        String tempremark;
        if (editRemark.getText().toString().equals(" ")) {
            tempremark = "";
        } else
            tempremark = editRemark.getText().toString();
        remark += tempremark;
        List<String> strings = RemarkFilter.filterRemark(editRemark.getText().toString());
        for (String string : strings) {
            if (!RemarkFilter.remarkIsExit(remarkBeans.remarkBeans, string)) {
                remarkBeans.addRemark(string);
            } else {
                remarkBeans.changeRemark(string);
            }
        }


        orderDetail.setRemark(remark);
        SharePreferenceUtil.saveStringPreference(activity, ORDER_REMARK, new Gson().toJson(remarkBeans));
        listener.sure();

    }

    public void refresh() {
        mCookMethodAdapter.refresh(orderDetail);

        mIngredientAdapter.refresh(orderDetail);
    }
}
