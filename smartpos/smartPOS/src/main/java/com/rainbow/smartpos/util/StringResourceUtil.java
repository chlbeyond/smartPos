package com.rainbow.smartpos.util;

import android.content.Context;

import com.sanyipos.sdk.model.Payment;

public class StringResourceUtil {
	public static String getPaymentNameByPaymentType(Context ctx, int paymentType) {
		switch (paymentType) {
		case Payment.PAYMENT_ALIPAY:
			return ctx.getString(com.rainbow.smartpos.R.string.pay_alipay_name);
		case Payment.PAYMENT_CASH:
			return ctx.getString(com.rainbow.smartpos.R.string.pay_cash_name);
		case Payment.PAYMENT_BANK_CARD:
			return ctx.getString(com.rainbow.smartpos.R.string.pay_card_name);
		case Payment.PAYMENT_WECHAT:
			return ctx.getString(com.rainbow.smartpos.R.string.pay_tenpay_name);
		case Payment.PAYMENT_DEBIT:
			return ctx.getString(com.rainbow.smartpos.R.string.pay_debit_name);
		case Payment.PAYMENT_WAIVE:
			return ctx.getString(com.rainbow.smartpos.R.string.pay_waive_name);
		case Payment.PAYMENT_VOUCHER:
			return ctx.getString(com.rainbow.smartpos.R.string.pay_voucher_name);
		case Payment.PAYMENT_STORE_VALUE:
			return ctx.getString(com.rainbow.smartpos.R.string.pay_storevalue_name);

		default:
			return "";
		}

	}
}
