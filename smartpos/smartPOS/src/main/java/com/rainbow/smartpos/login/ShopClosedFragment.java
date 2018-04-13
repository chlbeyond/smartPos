package com.rainbow.smartpos.login;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.activity.PendingBillDialog;
import com.rainbow.smartpos.install.Constants;
import com.sanyipos.android.sdk.checkServices.LoadingDialog;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.api.services.scala._PendingBillsRequest;
import com.sanyipos.sdk.model.PendingBills;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.rainbow.smartpos.Restaurant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopClosedFragment extends Fragment {
    Button button_inputpassword_shop_close_start;
    MainScreenActivity activity;
    LoadingDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_shop_closed_fragment, null);
        activity = (MainScreenActivity) getActivity();
        SanyiScalaRequests.pendBillsRequest(new _PendingBillsRequest.IPendingBillsListener() {
            @Override
            public void onSuccess(PendingBills bills) {
                if (bills.bills != null && bills.bills.size() > 0)
                    new PendingBillDialog(activity, bills).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }
        });
        dialog = new LoadingDialog(getActivity());

        button_inputpassword_shop_close_start = (Button) view.findViewById(R.id.button_inputpassword_shop_close_start);
        button_inputpassword_shop_close_start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                openShop();

            }
        });


        return view;
    }

    private void openShop() {
        /**
         * isSystemUpdate表示已经检测到8132错误,系统在升级,再次进入此方法,若isSystemUpdate为true,则表示已知系统在升级,此时若返回错误,则2秒后继续轮询
         * */
        SanyiScalaRequests.shopOpenRequest(new ICallBack() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                /**
                 * 检查错误码,如果为8132,则提示系统正在升级
                 * */
                if (!Constants.isSystemUpdating) {
                    Log.e("```checkUpdate", "onFail---" + error);
                    String regex = "\\[(.*)\\]";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(error);//匹配类
                    while (matcher.find()) {
                        if (matcher.group(1).equals("8132")) {
                            Constants.isSystemUpdating = true;
                        }
                    }
                    if (!Constants.isSystemUpdating) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                    } else {
                        if (!dialog.isShowing()) {
                            dialog.show();
                            dialog.setToolTipText("系统正在升级,请稍候");
                        }
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                openShop();
                            }
                        }, 2000);
                    }
                } else {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            openShop();
                        }
                    }, 2000);
                }
            }

            @Override
            public void onSuccess(String status) {
                // TODO Auto-generated method stub
                Log.e("```checkUpdate", "onSuccess---" + status);
                if (dialog.isShowing())
                    dialog.dismiss();
                Constants.isSystemUpdating=false;
                Restaurant.CURRENT_SHOP_STATE = ConstantsUtil.SHOP_OPENED;
                activity.loginFragment.inputPasswordFragment.initShopState();

                Toast.makeText(activity, status, Toast.LENGTH_LONG).show();


            }
        });
    }

}