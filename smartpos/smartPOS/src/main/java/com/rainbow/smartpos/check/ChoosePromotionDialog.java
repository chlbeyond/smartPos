package com.rainbow.smartpos.check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.presenter.CheckPresenterImpl;
import com.rainbow.smartpos.util.AuthDialog;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.Listener.OnChoosePromotionListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.CashierRequest.ICashierRequestListener;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.check.CashierAction;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.DiscountPlan;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.Promotion;
import com.sanyipos.sdk.model.scala.check.CashierPromotion;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.utils.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

public class ChoosePromotionDialog {
    public static final int PROMOTION = 0;
    public static final int DISCOUNT = 1;
    private MyDialog dialog;
    //	TextView sure;
    ImageButton cancel;

    Context context;
    private GridView promotion_gridview;
    private GridView discount_gridview;
    private MainScreenActivity activity;
    private View mainView;
    private TextView promotion_title;
    private TextView discount_title;
    private LayoutInflater inflater;
    OnChoosePromotionListener listener;
    CashierParamResult mCashierParam;
    CashierResult mCashierResult;
    ChoosePromotionAdapter mPromotionAdapter;
    ChooseDiscountAdapter mDiscountAdapter;
    CheckPresenterImpl checkPresenter;

    public MemberInfo memberInfo;

    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }

    public ChoosePromotionDialog(Context activity, CheckPresenterImpl presenter,OnChoosePromotionListener onChoosePromotionListener) {
        this.context = activity;
        this.activity = (MainScreenActivity) context;
        checkPresenter = presenter;
        this.listener = onChoosePromotionListener;
    }

    public void show() {
        inflater = LayoutInflater.from(activity);
        mainView = inflater.inflate(R.layout.choose_promotion_dialog_layout, null, false);
        dialog = new MyDialog(activity, MainScreenActivity.getScreenWidth() * 0.4, MainScreenActivity.getScreenHeight() * 0.9, mainView, R.style.OpDialogTheme);

        initMainView(mainView);
        initMainListener();

        mPromotionAdapter = new ChoosePromotionAdapter(activity);
        promotion_gridview.setAdapter(mPromotionAdapter);
        mDiscountAdapter = new ChooseDiscountAdapter(activity);
        discount_gridview.setAdapter(mDiscountAdapter);

        dialog.show();
    }

    private void initMainView(View view) {
        promotion_gridview = (GridView) view.findViewById(R.id.promotion_gridview);
        discount_gridview = (GridView) view.findViewById(R.id.discount_gridview);
        promotion_title= (TextView) view.findViewById(R.id.promotion_gridview_title);
        discount_title= (TextView) view.findViewById(R.id.discount_gridview_title);

//		sure = (TextView) view.findViewById(R.id.sure_btn);
        cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);
    }

    private void initMainListener() {
//		sure.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        promotion_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Promotion promotion = mPromotionAdapter.getItem(position);
                if (mPromotionAdapter.isSelect(position)) {
                    unDoPromotion(promotion);
                } else {
                    usePromotion(promotion);
                }
                dialog.dismiss();
            }
        });
        discount_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                DiscountPlan discountPlan = mDiscountAdapter.getItem(position);
                useDiscount(discountPlan);
            }
        });
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
                    listener.onCancel();
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public void onSureClick() {
        listener.onSure();
        dialog.dismiss();
    }

    public void refresh(CashierParamResult mCashierParam, CashierResult mCashierResult) {
        this.mCashierParam = mCashierParam;
        this.mCashierResult = mCashierResult;
        List<CashierParamResult.Promotion> rightPromotion = new ArrayList<Promotion>();
        if (checkPresenter.currentMemberInfo == null) {
            //未验证会员,从所有活动取所有人参与的活动
            for (CashierParamResult.Promotion promotion : mCashierParam.promotions) {
                if (promotion.forAll) {
                    rightPromotion.add(promotion);
                }
            }
        } else {
            for (CashierParamResult.Promotion promotion : mCashierParam.promotions) {
                if (promotion.forAll)
                    rightPromotion.add(promotion);
                else if (promotion.memberTypes != null && promotion.memberTypes.size() > 0) {
                    for (CashierParamResult.Promotion.MemberType type : promotion.memberTypes)
                        if (type.id == checkPresenter.currentMemberInfo.memberType)
                            rightPromotion.add(promotion);
                }
            }
        }
        if (rightPromotion.size() <= 0)
        {
            promotion_title.setVisibility(View.GONE);
        }else
            promotion_title.setVisibility(View.VISIBLE);
        if (null != mPromotionAdapter) {
            mPromotionAdapter.setPromotion(rightPromotion, mCashierResult.promotions);
        }
        if (null != mDiscountAdapter) {
            mDiscountAdapter.setDiscount(mCashierParam.discountPlans, mCashierResult.discounts);
        }
    }

    public boolean isShow() {
        if (null != dialog) {
            if (dialog.isShowing()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取消促销活动
     *
     * @param promotion
     */
    private void unDoPromotion(Promotion promotion) {
        for (CashierPromotion p : mCashierResult.promotions) {
            if (p.promotion == promotion.id) {
                checkPresenter.unDoPromotion(p);
            }
        }
    }

    /**
     * 使用促销活动
     *
     * @param promotion
     */
    private void usePromotion(Promotion promotion) {
//        if (memberInfo != null || promotion.promotionType == ConstantsUtil.PROMOTION_SPECIAL|| promotion.promotionType == 25) {
            SanyiScalaRequests.CashierPromotionRequest(checkPresenter.seats, promotion.id, (memberInfo != null) ? memberInfo.id : 0, SanyiSDK.currentUser.id, new ICashierRequestListener() {


                @Override
                public void onFail(String error) {
                    // TODO Auto-generated method stub

                    Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                    // TODO Auto-generated method stu
                    checkPresenter.checkView.updateUI(resp, ods);
                }
            });
//        } else {
//            dialog.dismiss();
////            checkPresenter.member_btn.performClick();
//        }
    }

    public void useDiscount(final DiscountPlan discountPlan) {
        AuthDialog authDialog = new AuthDialog();
        authDialog.show(activity, (int) discountPlan.id, AuthDialog.Type.DISCOUNT, new Listener.OnAuthListener() {
            @Override
            public void onAuthSuccess(StaffRest staff) {
                checkPresenter.useDiscountRequest(CashierAction.C_USEDISCOUNT, discountPlan.name,staff.id, discountPlan.id);
                dialog.dismiss();
            }

            @Override
            public void onCancel() {

            }
        });

    }
}
