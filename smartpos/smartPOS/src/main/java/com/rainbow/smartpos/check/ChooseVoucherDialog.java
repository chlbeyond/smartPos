package com.rainbow.smartpos.check;

import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qrcode.activity.CaptureActivity;
import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.check.presenter.CheckPresenterImpl;
import com.rainbow.smartpos.util.Listener.OnChooseVoucherListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.CashierRequest.ICashierRequestListener;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.scala.check.CashierAction;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.model.scala.check.CashierVoucher;

import java.util.List;

public class ChooseVoucherDialog {
    public static final int PROMOTION = 0;
    public static final int DISCOUNT = 1;
    private MyDialog dialog;
    TextView sure;
    ImageButton cancel;

    MainScreenActivity activity;
    private GridView voucher_gridview;
    private EditText voucherNumberEdit;

    private View mainView;
    private LayoutInflater inflater;
    ChooseVoucherAdapter mVoucherAdapter;
    OnChooseVoucherListener listener;
    CashierParamResult cashierParam;
    CashierResult cashierResult;
    TextView keybord;
    CheckPresenterImpl checkPresenter;

    public ChooseVoucherDialog(Activity activity, CheckPresenterImpl presenter, CashierParamResult cashierParam, CashierResult cashierResult, OnChooseVoucherListener listener) {
        this.activity = (MainScreenActivity) activity;
        this.checkPresenter = presenter;
        this.cashierParam = cashierParam;
        this.cashierResult = cashierResult;
        this.listener = listener;
    }

    public void show() {
        inflater = LayoutInflater.from(activity);
        mainView = inflater.inflate(R.layout.choose_voucher_dialog_layout, null, false);
        dialog = new MyDialog(activity, MainScreenActivity.getScreenWidth() * 0.4, MainScreenActivity.getScreenHeight() * 0.9, mainView, R.style.OpDialogTheme);

        initMainView(mainView);
        initMainListener();

        mVoucherAdapter = new ChooseVoucherAdapter(activity, cashierParam, cashierResult);
        voucher_gridview.setAdapter(mVoucherAdapter);

        dialog.show();
    }

    private void initMainView(View view) {
        voucher_gridview = (GridView) view.findViewById(R.id.voucher_gridview);
        voucherNumberEdit = (EditText) view.findViewById(R.id.voucher_number_edit);
        voucherNumberEdit.setInputType(InputType.TYPE_NULL);
        voucherNumberEdit.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    if (!voucherNumberEdit.getText().toString().equals("")) {
                        useSnVoucher(voucherNumberEdit.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        keybord = (TextView) view.findViewById(R.id.keybord);
        sure = (TextView) view.findViewById(R.id.sure_btn);
        cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);
    }

    private void initMainListener() {
        sure.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        keybord.setOnClickListener(onClickListener);
        voucherNumberEdit.setOnClickListener(onClickListener);
        voucher_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // TODO Auto-generated method stub
                final CashierVoucher cashierVoucher = mVoucherAdapter.getItem(position);

                ChooseCountPopWindow chooseCountPopWindow = new ChooseCountPopWindow(view, activity, 1, position % (voucher_gridview.getNumColumns()), voucher_gridview.getVerticalSpacing(), new ChooseCountPopWindow.OnSureListener() {

                    @Override
                    public void onSureClick(int value) {
                        // TODO Auto-generated method stub
                        useNoSnVoucher(value, cashierVoucher.id);

                        mVoucherAdapter.setSelect(position, value);
                    }
                });
                chooseCountPopWindow.show();
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
                case R.id.voucher_number_edit:
                    showNumPadPopWindows();
                    break;
                case R.id.keybord:
                    activity.startActivityForResult(new Intent(activity, CaptureActivity.class), Restaurant.SCAN_CODE_CHECK_VOUCHER);
                    break;
                default:
                    break;
            }
        }
    };

    public void useNoSnVoucher(int count, long voucherId) {
        SanyiScalaRequests.CashierVoucherRequest(CashierAction.Z_USENOSNVOUCHER, checkPresenter.seats, SanyiSDK.currentUser.id, count, voucherId, new ICashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                onSureClick();
                checkPresenter.checkView.updateUI(resp, ods);
            }
        });
    }

    public void useSnVoucher(String sn) {
        SanyiScalaRequests.CashierVoucherRequest(CashierAction.Z_USEVOUCHER, checkPresenter.seats, SanyiSDK.currentUser.id, 1, Long.valueOf(sn), new ICashierRequestListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
                // TODO Auto-generated method stub
                checkPresenter.checkView.updateUI(resp, ods);
            }
        });
        voucherNumberEdit.setText("");
    }

    protected void showNumPadPopWindows() {
        // TODO Auto-generated method stub
        VoucherSnPopWindow voucherSnPopWindow = new VoucherSnPopWindow(voucherNumberEdit, activity, new VoucherSnPopWindow.OnSureListener() {

            @Override
            public void onSureClick(String value) {
                // TODO Auto-generated method stub
                if (value.length() == 0) {
                    return;
                }
                useSnVoucher(value);
            }

            @Override
            public void onBtnClick(String value) {
                // TODO Auto-generated method stub
                voucherNumberEdit.setText(value);
            }
        });
        voucherSnPopWindow.show();
    }

    public void onSureClick() {
        listener.onSure();
        dialog.dismiss();
    }

    public void refresh(CashierResult mCashierResult) {
        this.cashierResult = mCashierResult;
        if (null != mVoucherAdapter) {
            mVoucherAdapter.refresh(cashierResult);
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

    public void dismiss() {
        if (null != dialog) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
