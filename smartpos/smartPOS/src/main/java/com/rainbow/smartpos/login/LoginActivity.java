package com.rainbow.smartpos.login;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rainbow.smartpos.ExitManager;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.printer.ComPrinter;
import com.rainbow.smartpos.printer.LocalPrinter;
import com.rainbow.smartpos.printer.UsbPrinter;
import com.sanyipos.android.sdk.androidUtil.RegisteDataUtils;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.ConnectHostRequest;
import com.sanyipos.sdk.core.AgentRequests;
import com.sanyipos.sdk.utils.SendLogUtil;
import com.rainbow.smartpos.Restaurant;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */

public class LoginActivity extends Fragment {
    public static final String GET_REGISTE_DATA_ACTION = "android.intent.action.Registe_Receiver";
    public static Toast toast;
    public MainScreenActivity activity;
    public static InputPasswordFragment inputPasswordFragment;
    public DeviceRegistrationFragment deviceRegistrationFragment;
    public ConnectHostFragment connectHostFragment;
    public static Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.
                activity_login, container, false);
        activity = (MainScreenActivity) getActivity();
        mContext = getActivity();
//		TextView textView_build_number = (TextView) view.findViewById(R.id.textView_build_number);
//		textView_build_number.setText("版本号" + Restaurant.getVersionName(activity) + "-" + Restaurant.getVersionCode(activity));
        SanyiSDK.registerData = RegisteDataUtils.getPosRegisteData(activity);
        inputPasswordFragment = new InputPasswordFragment();
        deviceRegistrationFragment = new DeviceRegistrationFragment();
        connectHostFragment = new ConnectHostFragment();
        detectAgent();
        setTimeZone();
        showConnectHostFragment();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (null == inputPasswordFragment) {
                return;
            }
            if (inputPasswordFragment.pFragment != null && inputPasswordFragment.pFragment.editRFID != null) {
                inputPasswordFragment.pFragment.editRFID.requestFocus();
            }

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setTimeZone() {
        // TODO Auto-generated method stub
        AlarmManager mAlarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
//        mAlarmManager.setTimeZone("GMT+08:00");
        mAlarmManager.setTimeZone("Asia/Shanghai");
    }

    public void showFragment() {
        if (SanyiSDK.registerData.isDeviceRegistered()) {
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_login, inputPasswordFragment).commitAllowingStateLoss();
        } else {
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_login, deviceRegistrationFragment).commitAllowingStateLoss();

        }
    }

    public void showConnectHostFragment() {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_login, connectHostFragment).commit();
    }

    public void detectAgent() {
        if (AgentRequests.port_9090_base_url != "") {
            SanyiScalaRequests.connectHostRequest(new ConnectHostRequest.UploadFileListener() {

                @Override
                public void onFail(String error) {
                    // TODO Auto-generated method stub
                    SanyiSDK.registerData.setDeviceRegistered(false);
                }

                @Override
                public void uploadFile() {
                    // TODO Auto-generated method stub
                    SendLogUtil.sendFileToAgent(Restaurant.CRASHLOGPATH);
                }

                @Override
                public void onSuccess(String resp) {
                    // TODO Auto-generated method stub
                    showFragment();

                }
            });
        }
    }

    public static class UnLockStaticReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final NormalDialog dialog = new NormalDialog(mContext);
            dialog.title("强制解锁提示");
            dialog.content(intent.getExtras().getString("extra"));
            dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                @Override
                public void onClickConfirm() {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    }

    // public static class RegisteReceiver extends BroadcastReceiver {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // // TODO Auto-generated method stub
    // Intent iIntent = new Intent();
    // iIntent.putExtra(RegisteDataKeyForSmartOrder.SP_RD_ACCESSCODE,
    // SanyiSDK.registerData.getAccessCode());
    // iIntent.putExtra(RegisteDataKeyForSmartOrder.SP_RD_DEVICEID,
    // SanyiSDK.registerData.getDeviceId());
    // iIntent.putExtra(RegisteDataKeyForSmartOrder.SP_RD_POSNAME,
    // SanyiSDK.registerData.getPosName());
    // iIntent.putExtra(RegisteDataKeyForSmartOrder.SP_RD_SALT,
    // SanyiSDK.registerData.getSalt());
    // iIntent.putExtra(RegisteDataKeyForSmartOrder.SP_RD_SHOPID,
    // SanyiSDK.registerData.getShopId());
    // iIntent.setAction(Restaurant.BACK_REGISTE_ACTION);
    // context.sendBroadcast(iIntent);
    // Uri packageURI = Uri.parse("package:com.go2smartphone.smartpos");
    // Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
    // uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // context.startActivity(uninstallIntent);
    // }
    // }
    //

    public void sendRegisteReceiver() {
        Intent intent = new Intent();
        intent.setAction(GET_REGISTE_DATA_ACTION);
        getActivity().sendBroadcast(intent);
    }


    public void exit() {
        ExitManager.getInstance().exit();
    }

    public static class UsbDisconnected extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            UsbInterface anInterface = null;
            if(device != null && (anInterface = device.getInterface(0)) != null) {
                if(anInterface.getInterfaceClass()== UsbConstants.USB_CLASS_PRINTER || device.getDeviceClass() == UsbConstants.USB_CLASS_PRINTER) {
                    if(null != inputPasswordFragment && InputPasswordFragment.printServiceConnection != null) {
                        LocalPrinter printer = InputPasswordFragment.printServiceConnection.getPrinter();
                        if(printer != null && printer instanceof UsbPrinter)
                            inputPasswordFragment.unBindPrintServeive();
                    }
                }
            }
//            if (null != inputPasswordFragment) {
//                inputPasswordFragment.unBindPrintServeive();
//            }
        }
    }

    public static class UsbConnected extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
//            if (null != inputPasswordFragment) {
//                inputPasswordFragment.initPrinter();
//            }
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            UsbInterface anInterface = null;
            if(device != null && (anInterface = device.getInterface(0)) != null) {
                if(anInterface.getInterfaceClass()== UsbConstants.USB_CLASS_PRINTER || device.getDeviceClass() == UsbConstants.USB_CLASS_PRINTER) {
                    if(null != inputPasswordFragment && InputPasswordFragment.printServiceConnection != null) {
                        LocalPrinter printer = InputPasswordFragment.printServiceConnection.getPrinter();
                        if(printer == null) //No printer in use
                            inputPasswordFragment.initPrinter();
                    }
                }
            }
        }
    }


}
