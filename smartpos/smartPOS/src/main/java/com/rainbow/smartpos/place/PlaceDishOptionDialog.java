package com.rainbow.smartpos.place;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.AuthDialog;
import com.rainbow.smartpos.util.Listener.OnAuthListener;
import com.rainbow.smartpos.util.Listener.OnPlaceDishOptionListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

public class PlaceDishOptionDialog {

    private MyDialog dialog;
    private View view;
    private ImageButton cancel;
    private Context activity;
    private OrderDetail currentOrderDetail;

    private TextView textViewDishName;
    private TextView textViewDishPrice;
    private TextView textViewDishCount;
    private ScrollView detail_scrollview;
    private LinearLayout detail_layout;
    private LinearLayout option_button_layout;

    private TextView free_dish;
    private TextView change_price;
    private TextView constant_fold;
    //private TextView discount_coupon;
    private TextView weight_dish;
    private TextView renturn_dish;

    private OnPlaceDishOptionListener listener;

    public void show(Context activity, OrderDetail orderDetail, OnPlaceDishOptionListener listener) {
        this.activity = activity;
        currentOrderDetail = orderDetail;
        this.listener = listener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        view = inflater.inflate(R.layout.place_dish_option_dialog_layout, null, false);
        dialog = new MyDialog(activity, (int) (MainScreenActivity.getScreenWidth() * 0.4), (int) (MainScreenActivity.getScreenHeight() * 0.9), view, R.style.OpDialogTheme);
        initView();
        setListener();
        initData();
        if (null != currentOrderDetail.getParent() && !isDishHasDetail()) {
            return;
        }
        dialog.show();
    }

    private void initData() {
        // TODO Auto-generated method stub
        textViewDishName.setText(currentOrderDetail.getName());
        textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(currentOrderDetail.getCurrentPrice()));
        textViewDishCount.setText("x" + (currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity()));
        if (isDishHasDetail()) {
            addDishDetail();
        } else {
            detail_scrollview.setVisibility(View.GONE);
        }
        if (null != currentOrderDetail.getParent()) {
            option_button_layout.setVisibility(View.GONE);
        }
        if (currentOrderDetail.isFree()) {
            free_dish.setText(activity.getString(R.string.cancel_free));
        }
    }

    private void addDishDetail() {
        // TODO Auto-generated method stub
        int ingredientId = 1000000;
        int cookMethodId = 4000000;
        if (currentOrderDetail.getIngredient().size() > 0) {

            for (int i = 0; i < currentOrderDetail.getIngredient().size(); i++) {
                // add ingredient if any
                OrderDetail ingredient = currentOrderDetail.getIngredient().get(i);
                LinearLayout ingredientLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.place_dish_option_row_ingredient, null);
                ingredientLayout.setId(ingredientId + i);

                // set ingredient name
                TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListDishName);
                ingredientName.setText("-加料: " + ingredient.getName() + "(" + ingredient.getUnitCount() + ")");

                // set ingredient price
                TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListPrice);
                ingredientPrice.setText(OrderUtil.dishPriceFormatter.format(ingredient.getOriginPrice()));

                if (ingredient.isFree()) {
                    ingredientPrice.setText(OrderUtil.dishPriceFormatter.format(ingredient.getOriginPrice()));
                }
                // set ingredient quantity
                TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListQuantity);
                int quantity = currentOrderDetail.getQuantity() - currentOrderDetail.getVoid_quantity();
                ingredientQuantity.setText("x" + String.valueOf(quantity * ingredient.getUnitCount()));

                detail_layout.addView(ingredientLayout);
            }
        }
        if (currentOrderDetail.getPublicCookMethod().size() > 0) {
            for (int i = 0; i < currentOrderDetail.getPublicCookMethod().size(); i++) {
                // add ingredient if any
                OrderDetail order = currentOrderDetail.getPublicCookMethod().get(i);
                if (order.getQuantity() == 0) {
                    continue;
                }
                LinearLayout ingredientLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.place_dish_option_row_ingredient, null);
                ingredientLayout.setId(cookMethodId + i);

                // set ingredient name
                TextView ingredientName = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListDishName);
                ingredientName.setText("-做法: " + order.getName());
                // set ingredient price
                TextView ingredientPrice = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListPrice);
                double price = order.getCurrentPrice();
                ingredientPrice.setText(OrderUtil.dishPriceFormatter.format(price));

                if (order.isFree()) {
                    ingredientPrice.setText(OrderUtil.dishPriceFormatter.format(order.getOriginPrice()));
                }

                // set ingredient quantity
                TextView ingredientQuantity = (TextView) ingredientLayout.findViewById(R.id.textViewOrderListQuantity);
                int cookQuantity = order.getQuantity() - order.getVoid_quantity();
                ingredientQuantity.setText("x" + String.valueOf(cookQuantity));

                detail_layout.addView(ingredientLayout);
            }
        }

        if (currentOrderDetail.getRemark() != null && currentOrderDetail.getRemark().length() != 0) {
            LinearLayout remarkLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.place_dish_option_row_ingredient, null);
            // set ingredient name
            TextView remarkName = (TextView) remarkLayout.findViewById(R.id.textViewOrderListDishName);
            remarkName.setText("-备注: " + currentOrderDetail.getRemark());
            TextView remarkPrice = (TextView) remarkLayout.findViewById(R.id.textViewOrderListPrice);
            TextView remarkQuantity = (TextView) remarkLayout.findViewById(R.id.textViewOrderListQuantity);
            remarkPrice.setVisibility(View.GONE);
            remarkQuantity.setVisibility(View.GONE);
            detail_layout.addView(remarkLayout);
        }
    }

    /**
     * 判断菜品是否有做法,加料或者备注
     *
     * @return
     */
    private boolean isDishHasDetail() {
        boolean flag = false;
        if (currentOrderDetail.getPublicCookMethod().size() > 0) {
            return true;
        }
        if (currentOrderDetail.getIngredient().size() > 0) {
            return true;
        }
        if (currentOrderDetail.getRemark() != null && currentOrderDetail.getRemark().length() != 0) {
            return true;
        }
        return flag;
    }

    public void initView() {
        textViewDishName = (TextView) view.findViewById(R.id.textViewDishName);
        textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
        textViewDishCount = (TextView) view.findViewById(R.id.textViewDishCount);
        cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);
        detail_scrollview = (ScrollView) view.findViewById(R.id.detail_scrollview);
        detail_layout = (LinearLayout) view.findViewById(R.id.detail_layout);
        option_button_layout = (LinearLayout) view.findViewById(R.id.option_button_layout);

        free_dish = (TextView) view.findViewById(R.id.free_dish);
        change_price = (TextView) view.findViewById(R.id.change_price);
        constant_fold = (TextView) view.findViewById(R.id.constant_fold);
        //discount_coupon = (TextView) view.findViewById(R.id.discount_coupon);
        weight_dish = (TextView) view.findViewById(R.id.weight_dish);
        renturn_dish = (TextView) view.findViewById(R.id.return_dish);

        if (currentOrderDetail.isFree()) {
            change_price.setEnabled(false);
        }
    }

    public void setListener() {
        cancel.setOnClickListener(onClickListener);
        free_dish.setOnClickListener(onClickListener);
        change_price.setOnClickListener(onClickListener);
        constant_fold.setOnClickListener(onClickListener);
        //discount_coupon.setOnClickListener(onClickListener);
        weight_dish.setOnClickListener(onClickListener);
        renturn_dish.setOnClickListener(onClickListener);

    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.iv_close_dialog:
                    buttonCancelClick();
                    break;
                case R.id.free_dish:
                    freeDish();
                    break;
                case R.id.change_price:
                    changePrice();
                    break;
                case R.id.constant_fold:
                    constantFold();
                    break;
                case R.id.return_dish:
                    returnDish();
                    break;

//			case R.id.discount_coupon:
//				discountCoupon();
//				break;
                case R.id.weight_dish:
                    weightDish();
                    break;

                default:
                    break;
            }
        }
    };

    private void buttonCancelClick() {
        dialog.dismiss();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    /**
     * 赠送
     */
    protected void freeDish() {
        // TODO Auto-generated method stub
        if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_FREE_DISH)) {
            if (currentOrderDetail.isFree()) {
                listener.onIsFreeDish(false, SanyiSDK.currentUser);
            } else {
                listener.onIsFreeDish(true, SanyiSDK.currentUser);
            }
        } else {
            final AuthDialog authDialog = new AuthDialog();
            authDialog.show(activity, ConstantsUtil.PERMISSION_FREE_DISH, AuthDialog.Type.PERMISSION, new OnAuthListener() {

                @Override
                public void onCancel() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAuthSuccess(StaffRest staff) {
                    if (currentOrderDetail.isFree()) {
                        listener.onIsFreeDish(false, staff);
                    } else {
                        listener.onIsFreeDish(true, staff);
                    }
                }

            });
        }
        dialog.dismiss();
    }

    protected void returnDish() {
        listener.onReturnDish();
        dialog.dismiss();
    }

    /**
     * 改价
     */
    protected void changePrice() {
        // TODO Auto-generated method stub
        if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_CHANGE_PRICE)) {
            listener.onChangePrice(SanyiSDK.currentUser);
            dialog.dismiss();
        } else {
            final AuthDialog authDialog = new AuthDialog();
            authDialog.show(activity, ConstantsUtil.PERMISSION_CHANGE_PRICE, AuthDialog.Type.PERMISSION,new OnAuthListener() {

                @Override
                public void onCancel() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAuthSuccess(StaffRest staff) {
                    listener.onChangePrice(staff);
                    dialog.dismiss();
                }

            });
        }
    }

    /**
     * 定折
     */
    protected void constantFold() {
        // TODO Auto-generated method stub

    }

    /**
     * 优惠券
     */
    protected void discountCoupon() {
        // TODO Auto-generated method stub

    }

    /**
     * 称重确认
     */
    protected void weightDish() {
        // TODO Auto-generated method stub
        if (!currentOrderDetail.isWeight()) {
            Toast.makeText(activity, "非称重菜品,无法修改重量", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        listener.onWeightDish();
        dialog.dismiss();
    }
}
