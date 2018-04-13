package com.rainbow.smartpos.check.presenter;

import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.model.scala.check.CashierPromotion;

/**
 * Created by ss on 2016/4/29.
 */
public interface CheckPresenter {

    void beginCashierRequest();

    void handleRequest(int action);

    void addCustomPaymentRequest(Double value, long paymentModeId);

    void addPay(Double value, Double change, CashierPayment cashierPaymentMode);

    void cancelMember();

    void verifyMember();

    void addMemberPayment(Double value, String psd);

    void addTenPayPayment(Double unpaid, String authCode);

    void removePayment(String paymentName, Double value, final int paymentType, final long paymentId);

    void useDiscountRequest(int action, String discountName, final long staffId, final long id);

    void undoPromotionRequest(CashierPromotion promotion);

    void unDoPromotion(final CashierPromotion promotion);

    void vificationWeChatPayment(Double value, String authCode);

    void addPointPayment(Double value, String psd);
}
