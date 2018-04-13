package com.rainbow.smartpos.util;

import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.IDType;
import com.sanyipos.sdk.model.rest.MemberTypes;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.check.MemberInfo;

import java.util.List;

public class Listener {
    public static interface OnSureListener {
        public void onSuccess();

        public void onFailed();
    }

    /**
     * 沽清选择
     *
     * @author zz
     */
    public static interface OnChooseSlodOutListener {
        public void onSlodOutSoon();

        public void onSlodOutLongTime();

        public void onSlodOutWithCount();

        public void cancel();
    }

    /**
     * 修改沽清数量
     *
     * @author zz
     */
    public static interface OnChangeSlodOutCountListener {
        public void sure(Double count);

        public void cancel();
    }

    /**
     * 选择查到的会员
     */
    public static interface OnChooseMemberListener {
        public void sure(MemberInfo memberInfo);

        public void cancel();
    }

    /**
     * 并台点菜选择
     *
     * @author zz
     */
    public static interface OnChooseOrderTypeListener {
        public void onOrderSelf();

        public void onOrderWithAll();

        public void cancel();
    }

    public static interface OnOpenTableListener {
        public void sure(int peopleCount);

        public void cancel();
    }

    public static interface OnFastModeNumberListener{
        public void onFastNumber(String number);
        public void cancel();
    }

    public static interface OnChooseCookListener {
        public void sure();

        public void cancel();

        public void exit();
    }

    public static interface OnOrderOpBtnClickListener {
        public void sure();

        public void sure(OrderDetail orderDetail);

        public void cancel();
    }

    public static interface OnSetReturnDishCountListener {
        public void sure(int count);

        public void cancel();
    }

    public static interface OnAuthListener {
        public void onAuthSuccess(StaffRest staff);

        public void onCancel();
    }

    public static interface OnChooseReasonListener {
        public void onSure(long selectId, String editReason);

        public void onCancel();
    }

    public static interface OnPlaceDishOptionListener {
        public void onIsFreeDish(boolean isFree, StaffRest superUser);

        public void onChangePrice(StaffRest superUser);

        public void onWeightDish();

        public void onReturnDish();
    }

    public static interface OnChangePriceListener {
        public void onSure(Double count);

        public void onCancel();
    }

    public static interface OnChoosePromotionListener {
        public void onSure();

        public void onCancel();
    }

    public static interface OnVerifyMemberListener {
        public void onQuerySuccess(List<MemberInfo> memberInfos);

        public void onQueryCanceled();
    }

    public static interface OnChooseVoucherListener {
        public void onSure();

        public void onCancel();
    }

    public static interface OnChooseStaffListener {
        public void onSure(StaffRest staff);

        public void onCancel();
    }

    public static interface OnChooseSexListener {
        public void onSure(String sex);

        public void onCancel();
    }

    public static interface OnChooseMemberTypeListener {
        public void onSure(MemberTypes MemberType);

        public void onCancel();
    }

    public static interface OnChooseIdTypeListener {
        public void onSure(IDType idType);

        public void onCancel();
    }
}
