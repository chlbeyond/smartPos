package com.rainbow.smartpos.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.login.BaseDialog.BaseDialogInterface;
import com.rainbow.smartpos.login.SearchLocalHost.SearchFinish;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.ConnectHostRequest;
import com.sanyipos.sdk.utils.SendLogUtil;
import com.rainbow.smartpos.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectHostFragment extends Fragment {
    private TextView inputConnectHost;
    private EditText editTextConnectHost;
    private TextView autoSearchHost;
    public List<String> hosts = new ArrayList<String>();
    public List<String> availableHosts = new ArrayList<String>();
    int i = 0;
    Button button_ConnectHost;
    public Timer timer;
    public MainScreenActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connecthost, null);
        activity = (MainScreenActivity) getActivity();
        inputConnectHost = (TextView) view.findViewById(R.id.inputConnectHost);
        editTextConnectHost = (EditText) view.findViewById(R.id.editText_ConnectHost);
        autoSearchHost = (TextView) view.findViewById(R.id.autoSearchHost);
        editTextConnectHost.setSelection(editTextConnectHost.getText().toString().length());
        TextView app_info = (TextView) view.findViewById(R.id.app_info);
        app_info.setText("版本号" + Restaurant.getVersionName(activity) + "-" + Restaurant.getVersionCode(activity));
        button_ConnectHost = (Button) view.findViewById(R.id.button_ConnectHost);
        if (SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, "") != "") {
            editTextConnectHost.setText(SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, ""));
        }
        autoSearchHost.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                if (timer != null)
//                    timer.cancel();
//                i = 0;
//                hosts.clear();
//                availableHosts.clear();
//                startDetechAgent();
            }
        });
        editTextConnectHost.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    button_ConnectHost.performClick();
                    return true;
                }
                return false;
            }
        });
        editTextConnectHost.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    button_ConnectHost.performClick();
                    closeInputMethod();
                }
                return false;
            }
        });
        button_ConnectHost.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                inputConnectHost.setText("正在连接主机");
                final String host  = editTextConnectHost.getText().toString().trim();
                SanyiSDK.getSDK().setHost(host);
                SanyiScalaRequests.connectHostRequest(new ConnectHostRequest.UploadFileListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub
                        inputConnectHost.setText(error);
                        SanyiSDK.registerData.setDeviceRegistered(false);
                    }

                    @Override
                    public void uploadFile() {
                        // TODO Auto-generated method stub
                        SanyiSDK.registerData.setDeviceRegistered(false);
                        SendLogUtil.sendFileToAgent(Restaurant.CRASHLOGPATH);
                        SendLogUtil.sendFileToAgent(Restaurant.operationLogTxtPath);
                    }

                    @Override
                    public void onSuccess(String resp) {
                        // TODO Auto-generated method stub
                        Window window = activity.getWindow();
                        View view = window.peekDecorView();
                        if (view != null) {
                            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        SharePreferenceUtil.saveStringPreference(activity, SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, host);
                        activity.loginFragment.showFragment();
                    }
                });
            }
        });
        //autoLinkHost();
        return view;
    }

    public void startDetechAgent() {
        new SearchLocalHost(new SearchFinish() {

            @Override
            public void searchFinish(List<String> h) {
                // TODO Auto-generated method stub
                hosts = h;
                detechAgent();
            }
        });
    }

    public void closeInputMethod() {
        Window window = activity.getWindow();
        View view = window.peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void detechAgent() {
        if (i < hosts.size()) {
            if (i == 0) {
                availableHosts.add("手动输入");
            }
            SanyiSDK.getSDK().setHost(hosts.get(i++));
            SanyiScalaRequests.connectHostRequest(new ConnectHostRequest.UploadFileListener() {



                @Override
                public void onFail(String error) {
                    // TODO Auto-generated method stub
                    detechAgent();
                }

                @Override
                public void onSuccess(String status) {
                    // TODO Auto-generated method stub
                    availableHosts.add(hosts.get(i - 1));
                    detechAgent();
                }

                @Override
                public void uploadFile() {
                    // TODO Auto-generated method stub

                }
            });
        } else {
            if (availableHosts.size() < 1) {
                new BaseDialog(activity, "提示", "未检测到主机地址，请手动输入", false, getResources().getString(R.string.sure)).showDialog(new BaseDialogInterface() {

                    @Override
                    public void onPositive(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("请选择您的主机");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, availableHosts);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (which == 0) {
                            dialog.dismiss();
                        } else {
                            editTextConnectHost.setText(availableHosts.get(which));
                            SanyiSDK.getSDK().setHost(availableHosts.get(which));
                            SharePreferenceUtil.saveStringPreference(activity, SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, availableHosts.get(which));
                            activity.loginFragment.showFragment();
                        }
                    }
                });
                builder.setCancelable(false);
                builder.show();

            }
        }

    }


    public void autoLinkHost() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                SanyiScalaRequests.connectHostRequest(new ConnectHostRequest.UploadFileListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void uploadFile() {
                        // TODO Auto-generated method stub
                        SendLogUtil.sendFileToAgent(Restaurant.CRASHLOGPATH);
                    }

                    @Override
                    public void onSuccess(String resp) {
                        // TODO Auto-generated method stub
                        Window window = activity.getWindow();
                        View view = window.peekDecorView();
                        if (view != null) {
                            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        activity.loginFragment.showFragment();
                    }
                });

            }
        }, 0, 5000);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
