package com.rainbow.smartpos.check.presenter;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cc.serialport.SerialCommand;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.check.CheckView.CheckView;
import com.rainbow.smartpos.check.VerifyMemberDialog;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.util.ChooseMemberDialog;
import com.rainbow.smartpos.util.ChooseMemberDialog2;
import com.rainbow.smartpos.util.Listener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.BeginCashierRequest;
import com.sanyipos.sdk.api.services.scala.CashierRequest;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.scala.check.BeginCashiersult;
import com.sanyipos.sdk.model.scala.check.CashierAction;
import com.sanyipos.sdk.model.scala.check.CashierPayment;
import com.sanyipos.sdk.model.scala.check.CashierPromotion;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ss on 2016/4/29.
 */
public class CheckPresenterImpl implements CheckPresenter {
    public MainScreenActivity activity;
    public List<Long> seats;
    public Bundle mBundle;
    public CheckView checkView;
    public long discountId = -1;
    public MemberInfo currentMemberInfo = null;
    public VerifyMemberDialog verifyMemberDialog;
    public Timer timer;

    public CheckPresenterImpl(MainScreenActivity activity, List<Long> seats, Bundle bundle, CheckView checkView) {
        this.activity = activity;
        this.seats = seats;
        this.mBundle = bundle;
        this.checkView = checkView;
    }

    @Override
    public void cancelMember() {
        SanyiScalaRequests.CashierHandleRequest(CashierAction.C_UNDOMEMBER, seats, SanyiSDK.currentUser.id, new CashierRequest.ICashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                checkView.initPermissionButton();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                checkView.updateUI(resp, null);
            }
        });
    }

    @Override
    public void verifyMember() {
        verifyMemberDialog = new VerifyMemberDialog();
        verifyMemberDialog.show(activity, new Listener.OnVerifyMemberListener() {

            @Override
            public void onQuerySuccess(List<MemberInfo> memberInfos) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, "查询成功", Toast.LENGTH_LONG).show();
                if (memberInfos.size() == 1) {
                    currentMemberInfo = memberInfos.get(0);
                    useMemberRequest();
                } else {
//                    new ChooseMemberDialog(activity, memberInfos, new Listener.OnChooseMemberListener() {
//
//                        @Override
//                        public void sure(MemberInfo memberInfo) {
//                            // TODO Auto-generated method stub
//                            currentMemberInfo = memberInfo;
//                            useMemberRequest();
//                        }
//
//                        @Override
//                        public void cancel() {
//                            // TODO Auto-generated method stub
//
//                        }
//                    }).show();

                    new ChooseMemberDialog2(activity, memberInfos, new ChooseMemberDialog2.ChooseMemberListener() {
                        @Override
                        public void ChooseMember(MemberInfo memberInfo) {
                            currentMemberInfo = memberInfo;
                            useMemberRequest();
                        }
                    }).show();
                }
            }

            @Override
            public void onQueryCanceled() {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void removePayment(String paymentName, Double value, final int paymentType, final long paymentId) {
        final NormalDialog dialog = new NormalDialog(activity);
        dialog.content("付款记录: " + paymentName + " " + OrderUtil.decimalFormatter.format(Double.valueOf(value)) + "将被撤销");
        dialog.widthScale((float) 0.5);
        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                dialog.dismiss();
                SanyiScalaRequests.CashierCancelRequest(seats, SanyiSDK.currentUser.id, paymentType, paymentId, new CashierRequest.ICashierRequestListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                        checkView.initPermissionButton();
                    }

                    @Override
                    public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                        // TODO Auto-generated method stub
                        checkView.updateUI(resp, ods);
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public void addMemberPayment(Double value, String psd) {
        SanyiScalaRequests.CashierUseStoreRequest(seats, SanyiSDK.currentUser.id, currentMemberInfo.id, value, psd, new CashierRequest.ICashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                checkView.initPermissionButton();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                checkView.updateUI(resp, ods);
            }
        });
    }

    @Override
    public void addTenPayPayment(Double unpaid, String authCode) {
        double value = Double.valueOf(unpaid);

        SanyiScalaRequests.CashierTenPayRequest(seats, SanyiSDK.currentUser.id, authCode, value, new CashierRequest.ICashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                checkView.initPermissionButton();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, "支付成功", Toast.LENGTH_LONG).show();
                checkView.updateUI(resp, ods);
            }
        });
    }

    public void useMemberRequest() {
        if (currentMemberInfo != null)
            SanyiScalaRequests.CashierUserMember(seats, currentMemberInfo.id, new CashierRequest.ICashierRequestListener() {


                @Override
                public void onFail(String error) {
                    // TODO Auto-generated method stub

                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    checkView.initPermissionButton();
                }

                @Override
                public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                    // TODO Auto-generated method stub
                    checkView.updateUI(resp, ods);
                }
            });
    }

    @Override
    public void handleRequest(final int action) {
        SanyiScalaRequests.CashierHandleRequest(action, seats, SanyiSDK.currentUser.id, new CashierRequest.ICashierRequestListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                        checkView.initPermissionButton();
                    }

                    @Override
                    public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                        // TODO Auto-generated method stub
                        if (action == CashierAction.C_GETPAYURL) {
                            checkView.showQRCode(ConstantsUtil.QRCODETOOLS + resp.bill.payUrl,resp.bill.unpaid);
                            return;
                        }
                        if (action == CashierAction.C_END) {
                            if (Restaurant.isFastMode) {
                                Toast.makeText(activity, "结账成功", Toast.LENGTH_LONG).show();
                                checkView.closeDrawerLayout();
                                checkView.clearData();
                                return;
                            }
                            Toast.makeText(activity, "结账成功", Toast.LENGTH_LONG).show();
                            activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                            if (activity.serialPort != null) {
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        try {
                                            activity.serialPort.getOutputStream().write(SerialCommand.initCommand());
                                            activity.serialPort.getOutputStream().write(SerialCommand.showNums("0.00"));
                                            activity.serialPort.getOutputStream().write(SerialCommand.setCharsCommand(SerialCommand.CHARS_DARK));
                                            activity.serialPort.getOutputStream().flush();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 8 * 1000);
                            }
                            return;
                        }
                        if (action == CashierAction.C_PREPRINT) {

                            Toast.makeText(activity, "预打印成功", Toast.LENGTH_LONG).show();
                            if (Restaurant.preIsExit) {
                                activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                            }
                            return;
                        }

                        checkView.updateUI(resp, ods);


                    }
                }

        );
    }

    @Override
    public void addCustomPaymentRequest(Double value, long paymentModeId) {
        SanyiScalaRequests.CashierCustomRequest(seats, SanyiSDK.currentUser.id, value, paymentModeId, new CashierRequest.ICashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                checkView.initPermissionButton();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                checkView.updateUI(resp, ods);
            }
        });
    }

    @Override
    public void addPay(Double value, Double change, CashierPayment cashierPaymentMode) {
        if (value < 0 && cashierPaymentMode.paymentType != ConstantsUtil.PAYMENT_CASH) {

            Toast.makeText(activity, "非现金收银方式不允许出现负数", Toast.LENGTH_LONG).show();
            return;
        }
        switch (cashierPaymentMode.paymentType) {
            case ConstantsUtil.PAYMENT_BANK_CARD:
                addPayment(CashierAction.Z_USECARD, SanyiSDK.currentUser.id, value, change, null);
                break;
            case ConstantsUtil.PAYMENT_CASH:
                addPayment(CashierAction.Z_USECASH, SanyiSDK.currentUser.id, value, change, null);
                break;
            case ConstantsUtil.PAYMENT_ALIPAY:
                addPayment(CashierAction.Z_USEALIPAY, SanyiSDK.currentUser.id, value, change, null);
                break;
            case ConstantsUtil.PAYMENT_WAIVE:
                addPayment(CashierAction.Z_USEWAIVE, SanyiSDK.currentUser.id, value, change, null);
                break;
            case ConstantsUtil.PAYMENT_CUSTOM:
                addCustomPaymentRequest(value, cashierPaymentMode.id);
                break;
            case ConstantsUtil.PAYMENT_POINT:
                addPayment(CashierAction.Z_USEPOINT,SanyiSDK.currentUser.id, value, change, null);
                break;


        }
    }

    @Override
    public void beginCashierRequest() {
        SanyiScalaRequests.BeginCashierHandleRequest(seats, SanyiSDK.currentUser.id, new BeginCashierRequest.IBeginCashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
            }

            @Override
            public void onSuccess(BeginCashiersult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                if (ods.size() == 0) {
                    activity.displayView(MainScreenActivity.ORDER_FRAGMENT, mBundle);
                    return;
                }
                checkView.initParam(resp.param, resp.cashierResult);
                checkView.updateUI(resp.cashierResult, ods);
//                if (resp.cashierResult.bill.payUrl != null)
//                    checkView.showQRCode(ConstantsUtil.QRCODETOOLS + resp.cashierResult.bill.payUrl);
            }
        });
    }

    @Override
    public void useDiscountRequest(final int action, String discountName, final long staffId,
                                   final long id) {
        if (action == CashierAction.C_UNDODISCOUNT) {
            final NormalDialog dialog = new NormalDialog(activity);
            dialog.content("折扣: " + discountName + " 将被撤销");
            dialog.widthScale((float) 0.5);
            dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                @Override
                public void onClickConfirm() {
                    dialog.dismiss();
                    discount(action, staffId, id);
                }
            });
            dialog.show();
        } else {
            discount(action, staffId, id);
        }
    }

    public void discount(final int action, long staffId, long id) {

        SanyiScalaRequests.CashierDiscountRequest(action, seats, staffId, id, new CashierRequest.ICashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                checkView.initPermissionButton();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                checkView.updateUI(resp, ods);
                if (action == CashierAction.C_UNDODISCOUNT) {
                    discountId = -1;
                }
            }
        });
    }

    public void unDoPromotion(final CashierPromotion promotion) {
        final NormalDialog dialog = new NormalDialog(activity);
        dialog.content("促销活动: " + promotion.promotionName + " 将被撤销");
        dialog.widthScale((float) 0.5);
        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                dialog.dismiss();
                SanyiScalaRequests.CashierUndoPromotionRequest(seats, promotion.id, SanyiSDK.currentUser.id, new CashierRequest.ICashierRequestListener() {

                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                        // TODO Auto-generated method stu
                        checkView.updateUI(resp, ods);
                    }
                });
            }
        });
        dialog.show();

    }

    @Override
    public void vificationWeChatPayment(Double value, String authCode) {
        SanyiScalaRequests.CashierHandleRequest(seats, new CashierAction(CashierAction.Z_ACKTENPAY, SanyiSDK.currentUser.id, authCode, value), new CashierRequest.ICashierRequestListener() {
            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                checkView.updateUI(resp, ods);
            }


            @Override
            public void onFail(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void addPointPayment(Double value, String psd) {
        SanyiScalaRequests.CashierUsePointRequest(seats,  psd, new CashierRequest.ICashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                checkView.initPermissionButton();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                checkView.updateUI(resp, ods);
            }
        });
    }


    @Override
    public void undoPromotionRequest(final CashierPromotion promotion) {
        final NormalDialog dialog = new NormalDialog(activity);
        dialog.content("促销活动: " + promotion.promotionName + " 将被撤销");
        dialog.widthScale((float) 0.5);
        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                dialog.dismiss();
                SanyiScalaRequests.CashierUndoPromotionRequest(seats, promotion.id, SanyiSDK.currentUser.id, new CashierRequest.ICashierRequestListener() {

                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                        checkView.initPermissionButton();
                    }

                    @Override
                    public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                        // TODO Auto-generated method stu
                        checkView.updateUI(resp, ods);
                    }
                });
            }
        });
        dialog.show();
    }

    public void addPayment(int action, long authStffId, double value, double change, String transaction) {
        SanyiScalaRequests.CashierExpenseRequest(action, seats, authStffId, value, change, transaction, new CashierRequest.ICashierRequestListener() {

            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                checkView.initPermissionButton();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                checkView.updateUI(resp, ods);
            }
        });
    }

}
