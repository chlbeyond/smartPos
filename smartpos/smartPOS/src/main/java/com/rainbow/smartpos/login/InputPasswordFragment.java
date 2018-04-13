package com.rainbow.smartpos.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
//import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.serialport.SerialPort;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.dialog.PaySuccessDialog;
import com.rainbow.smartpos.event.EventMessage;
import com.rainbow.smartpos.install.Constants;
import com.rainbow.smartpos.login.BaseDialog.BaseDialogInterface;
import com.rainbow.smartpos.mainframework.PrintUtil;
import com.rainbow.smartpos.printer.ComPrinter;
import com.rainbow.smartpos.printer.UsbPrinter;
import com.rainbow.smartpos.service.PrintService;
import com.rainbow.smartpos.tablemanage.CancelTableFragment;
import com.rainbow.smartpos.tablemanage.ChangeTableFragment;
import com.rainbow.smartpos.tablemanage.MergeTableFragment;
import com.rainbow.smartpos.tablemanage.OpenAndMergeFragment;
import com.rainbow.smartpos.tablemanage.SplitTableFragment;
import com.rainbow.smartpos.tablemanage.UnMergeTableFragment;
import com.rainbow.smartpos.tablemanage.UnSplitTableFragment;
import com.rainbow.smartpos.tablestatus.TableGroupItemFragment;
import com.rainbow.smartpos.tablestatus.TableViewPagerFragment;
import com.rainbow.smartpos.util.NetworkUtil;
import com.rainbow.smartpos.util.ShaBcUtil;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.android.sdk.checkServices.AppChecker;
import com.sanyipos.android.sdk.checkServices.DownloadApkDialog;
import com.sanyipos.android.sdk.checkServices.DownloadShaDialog;
import com.sanyipos.sdk.api.ClientConfig;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.IPollingListener;
import com.sanyipos.sdk.api.services.scala.DownloadRestDataRequest.IRestDataListener;
import com.sanyipos.sdk.api.services.scala.SharePrinterRequest_;
import com.sanyipos.sdk.core.AgentRequests;
import com.sanyipos.sdk.model.OperationData.Notification;
import com.sanyipos.sdk.model.ShopPrinter;
import com.sanyipos.sdk.model.rest.KdsOrders;
import com.sanyipos.sdk.model.rest.Rest;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.utils.ConstantsUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class InputPasswordFragment extends Fragment {

    public static SanyiPintServiceConnection printServiceConnection;
    public PasswordFragment pFragment = new PasswordFragment();
    public ShopClosedFragment mShopClosedFragment = new ShopClosedFragment();
    public ShopMFragment mFragment = new ShopMFragment();
    public SanyiSDK sanyiSDK = SanyiSDK.getSDK();
    private static final int UPGRADE = 0;// 用于区分正在下载
    private static final int DOWN = 1;// 用于区分正在下载
    private static final int DOWN_FINISH = 2;// 用于区分下载完成
    private static final int POSINSTALL = 3;// 用于区分下载完成
    private static final int START_PRINT_FAIL = 4;
    private static final int UPDATE_FAIL = 5;
    private static final int PRINTER_FAILED = 6;
    private static final int DISCONNECTED = 7;
    private String fileSavePath;// 下载新apk的厨房地点
    private static Context context;
    private TextView textView_dialog_reminder;
    // PrintService printService;
    UsbPrinter printer;
    String localUsbPrinterName;
    String localComPrinterName;
    public int systemAutoTime;
    public Dialog dialog;
    public Dialog alertDialog;
    private LayoutInflater mInflater;
    private Intent intentService;
    public MainScreenActivity activity;
    public boolean isUpdateing = false;

    public void downloadnRestaurant() {
        SanyiScalaRequests.downloadRestRequest(new IRestDataListener() {

            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                restaurantDownloadFailedDialog(error);
            }

            @Override
            public void onSuccess(Rest resp) {
                // TODO Auto-generated method stub
                sanyiSDK.updateOperationData(resp.operationData);
                if (resp.operationData.shop.cloud_connection != null) {
                    Restaurant.currentCloudConnectStatus = resp.operationData.shop.cloud_connection;
                }
                Restaurant.CURRENT_SHOP_STATE = (resp.operationData.shop.closed != null) ? resp.operationData.shop.closed : ConstantsUtil.SHOP_OPENED;
                initShopState();
                activity.setLeftShopName();
                checkSystemAutoTime();
                installPollingListener();
                SanyiSDK.getSDK().startPolling();
            }
        });

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch ((Integer) msg.obj) {
                case PRINTER_FAILED:
                    Toast.makeText(context, "找不到打印机，请检查打印机连接是否正常", Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_FAIL:
                    String error = msg.getData().getString("error");
                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                case POSINSTALL:
                    isUpdateing = false;
                    downloadnRestaurant();
                    break;
                case DISCONNECTED:
                    showDisConnectedDialog();
                case UPGRADE:
                    break;
                case DOWN:
                    break;
                case DOWN_FINISH:
                    installAPK(fileSavePath);
                    break;
                case START_PRINT_FAIL:
                    Toast.makeText(context, "打印服务启动失败，请重新确认打印机安装正常或重新配置打印机", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public void restaurantDownloadFailedDialog(String message) {
        new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage(message).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Message message = new Message();
                message.obj = POSINSTALL;
                handler.sendMessage(message);
                dialog.dismiss();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainScreenActivity) getActivity();
        context = getContext();
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mInflater = LayoutInflater.from(getActivity());
        TextView app_info = (TextView) rootView.findViewById(R.id.app_info);
        app_info.setText("版本号" + Restaurant.getVersionName(activity) + "-" + Restaurant.getVersionCode(activity));
        localComPrinterName = SharePreferenceUtil.getPreference(activity, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, "");

        initPrinter();
        initComPrinter();
        initProgressDialog();
        initReminberDialog();
        return rootView;
    }


    private void initComPrinter() {

        try {
            String baudRatePrint = SharePreferenceUtil.getPreference(activity, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "9600");
            int baudRate = Integer.parseInt(baudRatePrint);
            if (localComPrinterName.length() > 0) {
                SerialPort port;
                port = new SerialPort(new File((String) localComPrinterName), baudRate);
                ComPrinter printer = new ComPrinter(port);
                printServiceConnection = new SanyiPintServiceConnection(printer, new SanyiPintServiceConnection.PrinterServiceStartListener() {
                    @Override
                    public void onPrintServiceStartSuccess() {
                        // createSharedPriner();
                    }

                    @Override
                    public void onPrintServiceStartFailed() {
                        Message message = new Message();
                        message.obj = InputPasswordFragment.START_PRINT_FAIL;
                        handler.sendMessage(message);
                    }
                });
                intentService = new Intent(activity, PrintService.class);
                activity.bindService(intentService, printServiceConnection, Context.BIND_AUTO_CREATE);
            }
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void bindPrintService() {
        activity.bindService(intentService, printServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindPrintServeive() {
        if (printServiceConnection != null) {
            try {
                if (printServiceConnection.binder.isBinderAlive()) {
                    activity.unbindService(printServiceConnection);
                    if (printServiceConnection.printService != null)
                        printServiceConnection.printService.onStop();
                    final BaseDialog baseDialog = new BaseDialog(context, "提示", "USB打印服务已经停止，请检查打印机连接状态", false, getResources().getString(R.string.sure));
                    baseDialog.showDialog(new BaseDialogInterface() {

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
                }
            } catch (IllegalArgumentException e) {
                final BaseDialog baseDialog = new BaseDialog(context, "提示", "USB打印服务已经停止，请检查打印机连接状态", false, getResources().getString(R.string.sure));
                baseDialog.showDialog(new BaseDialogInterface() {

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
            }

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
    }

    public void createSharedPriner() {
        SanyiScalaRequests.sharePrinterRequest(sanyiSDK.registerData.getPosName(), NetworkUtil.getIPAddress(true), new SharePrinterRequest_.ISharePrintListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(ShopPrinter print) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void initReminberDialog() {
        // TODO Auto-generated method stub
        String flag = SharePreferenceUtil.getPreference(getContext(), SmartPosPrivateKey.ST_TIME_INTERNET, "");
        if (!flag.isEmpty() && flag.equalsIgnoreCase("false"))
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = mInflater.inflate(R.layout.dialog_reminder, null);
        builder.setTitle("提示");

        TextView textView_dialog_reminder = (TextView) view.findViewById(R.id.textView_dialog_reminder);
        textView_dialog_reminder.setText("检测到您当前没有打开自动同步服务器时间,为了避免餐厅数据出错,建议设置自动同步服务器时间");
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.shutup, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharePreferenceUtil.saveStringPreference(getContext(), SmartPosPrivateKey.ST_TIME_INTERNET, "false");
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(true);

    }

    private void checkSystemAutoTime() {
        // TODO Auto-generated method stub
        try {
            systemAutoTime = android.provider.Settings.Global.getInt(activity.getContentResolver(), android.provider.Settings.System.AUTO_TIME);
        } catch (SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (systemAutoTime != ConstantsUtil.SYSTEM_AUTO_TIME) {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        } else {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }

    public void initShopState() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MainScreenActivity.CURRENT_MODE != MainScreenActivity.LOGIN_FRAGMENT) {
                    activity.displayView(MainScreenActivity.LOGIN_FRAGMENT, null);
                }
                if (null != mShopClosedFragment) {
                    if (Restaurant.CURRENT_SHOP_STATE == ConstantsUtil.SHOP_CLOSED) {
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_password, mShopClosedFragment).commitAllowingStateLoss();
                    }
                }
                if (null != mFragment) {
                    if (Restaurant.CURRENT_SHOP_STATE == ConstantsUtil.SHOP_IN_MAINTAINECE) {
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_password, mFragment).commitAllowingStateLoss();
                    }
                }
                if (null != pFragment) {
                    if (Restaurant.CURRENT_SHOP_STATE == ConstantsUtil.SHOP_OPENED || Restaurant.CURRENT_SHOP_STATE == ConstantsUtil.SHOP_CLOSING) {
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_password, pFragment).commitAllowingStateLoss();
                    }
                }
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @Override
    public void onDestroy() {
        if (mUsbStartReceiver != null) {
            activity.unregisterReceiver(mUsbStartReceiver);
        }
        super.onDestroy();
    }


    public void initProgressDialog() {
        if (!isUpdateing) {
            isUpdateing = true;
            AppChecker.getInstance().checkApkUpdate(activity, AgentRequests.port_9090_base_url + "version/" + ClientConfig.CLIENT_SMARTPOS_FOR_ANDROID,
                    Integer.toString(SanyiSDK.registerData.getDeviceId()), SanyiSDK.registerData.getSalt(), Integer.toString(SanyiSDK.registerData.getShopId()),
                    new com.sanyipos.android.sdk.checkServices.CheckListener() {

                        @Override
                        public void noUpdate() {
                            Message message = new Message();
                            message.obj = POSINSTALL;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void downloadFail() {
                            isUpdateing = false;
                            Message message = new Message();
                            message.obj = UPDATE_FAIL;
                            Bundle bundle = new Bundle();
                            bundle.putString("error", "下载更新包失败");
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }

                        @Override
                        public void downloadCompleted(String apkPath) {
                            fileSavePath = apkPath;
                            Message message2 = new Message();
                            message2.obj = DOWN_FINISH;
                            handler.sendMessage(message2);
                        }

                        @Override
                        public void downloadCancel() {
                            Message message = new Message();
                            message.obj = POSINSTALL;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void checkFail(String msg) {
                            isUpdateing = false;
                            Message message = new Message();
                            message.obj = UPDATE_FAIL;
                            Bundle bundle = new Bundle();
                            bundle.putString("error", "当前已经是最新版本");
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    });

        }
    }

    public void initPrinter() {

        localUsbPrinterName = SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, "");
        if (localUsbPrinterName.length() > 0) {
//            PrintUtil.getLocalPrint(getContext());
            PrintUtil.getLocalPrint(getActivity().getApplicationContext());
            printer = Restaurant.usbDriver.findPrinter(localUsbPrinterName);
//            PrintUtil.shartPrint(getContext(), printer);
            PrintUtil.shartPrint(getActivity().getApplicationContext(), printer);
        }
    }

    private BroadcastReceiver mUsbStartReceiver;

    /**
     * 安装apk文件
     */
    public void installAPK(String path) {
        File apkfile = new File(path);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
//        System.out.println("filepath=" + apkfile.toString() + "  " + apkfile.getPath());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
        // android.os.Process.killProcess(android.os.Process.myPid());//
        activity.finish();

    }

    @Override
    public void onResume() {
        super.onResume();
        checkSystemAutoTime();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void showDisConnectedDialog() {
        if (!Constants.isSystemUpdating) {
            if (alertDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示");
                View view = mInflater.inflate(R.layout.dialog_alert, null);
                textView_dialog_reminder = (TextView) view.findViewById(R.id.textView_dialog_reminder);
                textView_dialog_reminder.setText("检测到与主机断开连接,请检测网络");
                textView_dialog_reminder.setCompoundDrawables(getResources().getDrawable(R.drawable.warning), null, null, null);
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated
                        // method stub
                    }
                });
                builder.setView(view);
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
            }
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    private int netWorkBreakCount = 0;
    private int netWorkWellCount = 0;

    private boolean isHostNetworkWell = true;

    public void installPollingListener() {
        sanyiSDK.installPollingListener(MainScreenActivity.class.getName(), new IPollingListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
//                if (isHostNetworkWell) {
//                    netWorkBreakCount++;
//                    if (netWorkBreakCount >= 5) {
//                        netWorkWellCount = 0;
//                        isHostNetworkWell = false;
                if (Thread.currentThread().getName().equals("main")) {
                    showDisConnectedDialog();
                } else {
                    Message message = new Message();
                    message.obj = DISCONNECTED;
                    handler.sendMessage(message);
                }
//                    }
//                }

            }

            @Override
            public void net_normal() {
                // TODO Auto-generated method stub
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        netWorkBreakCount = 0;
                        isHostNetworkWell = true;
                        if (alertDialog != null) {
                            if (alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                        }
                    }
                });

            }

            @Override
            public void QueueDataUpdate() {

            }

            @Override
            public void kdsOrderUpdate(KdsOrders kdsOrders) {

            }

            @Override
            public void VersionUpdate(final Notification notify) {
                // TODO Auto-generated method stub
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final Dialog dialog = new Dialog(activity);
//                        dialog.setContentView(R.layout.dialog_reminder);
//                        dialog.setCancelable(false);
//                        dialog.setTitle("提示");
//                        final TextView textView_dialog_reminder = (TextView) dialog.findViewById(R.id.textView_dialog_reminder);
//                        textView_dialog_reminder.setText("检测到新版本，现在下载更新");
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("name", " 食通宝");
//                        map.put("version", "1");
//                        map.put("currentVersionName", Restaurant.getVersionName(activity));
//                        map.put("currentVersionCode", Restaurant.getVersionCode(activity) + "");
//                        map.put("url", notify.extraInfo);
//
//                        new DownloadApkDialog(activity, map, Integer.toString(SanyiSDK.registerData.getDeviceId()), new DownloadApkDialog.IDownloadListener() {
//
//                            @Override
//                            public void onFail() {
//                                dialog.dismiss();
//                            }
//
//                            @Override
//                            public void onCompleted(final String apkPath) {
//                                dialog.dismiss();
//                                textView_dialog_reminder.setText("正在下载新版本校验文件，请稍候");
//                                Map<String, String> Shamap = new HashMap<String, String>();
//                                Shamap.put("name", " 食通宝");
//                                Shamap.put("version", "1");
//                                Shamap.put("currentVersionName", Restaurant.getVersionName(activity));
//                                Shamap.put("currentVersionCode", Restaurant.getVersionCode(activity) + "");
//                                Shamap.put("url", notify.extraInfo + ".sha");
//                                new DownloadShaDialog(activity, Shamap, Integer.toString(SanyiSDK.registerData.getDeviceId()), new DownloadShaDialog.IDownloadListener() {
//                                    @Override
//                                    public void onCancel() {
//
//                                    }
//
//                                    @Override
//                                    public void onCompleted(String sha1Path) {
//                                        if (ShaBcUtil.bcPackAgeSHA1(apkPath, sha1Path)) {
//                                            installAPK(apkPath);
//                                        } else {
//                                            Toast.makeText(activity, "SHA校验失败，请重新下载", Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFail() {
//
//                                    }
//                                }).show();
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                dialog.dismiss();
//                            }
//                        }).show();
//
//                        dialog.show();
//                    }
//                });
                if (!isUpdateing) {
                    isUpdateing = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("提示");
                    builder.setMessage("检测到新版本,请点击确定升级");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isUpdateing = false;
                            activity.restartToUpdate();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }


            }

            @Override
            public void SoldOutUpdate(Notification notify) {
            }

            @Override
            public void ShopOpen(Notification notify) {
                // TODO Auto-generated method stub
                Restaurant.CURRENT_SHOP_STATE = ConstantsUtil.SHOP_OPENED;
                initShopState();
            }

            @Override
            public void ShopMaintain(Notification notify) {
                // TODO Auto-generated method stub
                Restaurant.CURRENT_SHOP_STATE = ConstantsUtil.SHOP_IN_MAINTAINECE;
                initShopState();
            }

            @Override
            public void ShopClosed(Notification notify) {
                // TODO Auto-generated method stub
                Restaurant.CURRENT_SHOP_STATE = ConstantsUtil.SHOP_CLOSED;
                initShopState();
            }

            @Override
            public void RestaurantDataUpdate() {
                // TODO Auto-generated method stub
                SanyiScalaRequests.downloadRestRequest(new IRestDataListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSuccess(Rest resp) {
                        // TODO Auto-generated method stub
                        Toast.makeText(activity, "餐厅数据已经更新", Toast.LENGTH_LONG).show();
                        Restaurant.orderIsRefresh = false;
                        if (activity.tableStatusFragment != null) {
                            if (activity.tableStatusFragment.isResumed()) {
                                activity.tableStatusFragment.tableViewPagerFragment.mPagerAdapter.refresh();
                                activity.tableStatusFragment.tableViewPagerFragment.indicatorAdapter.refresh();
                            }
                        }
                        if (activity.orderFragment != null) {
                            if (activity.orderFragment.orderDishFragment != null) {
                                activity.orderFragment.orderDishFragment.orderDishViewFragment = null;
                                if (activity.orderFragment.orderDishFragment.isResumed()) {
                                    activity.orderFragment.orderDishFragment.disPlayOrderView();
                                    Restaurant.orderIsRefresh = true;
                                }
                            }
                        }
                        if (activity.mTakeOutFragment != null) {
                            activity.mTakeOutFragment.reloadWebPage();
                        }
                    }
                });

            }

            @Override
            public void RestaurantDataUpdateComplete() {
            }

            @Override
            public void PasswordChanged(final Notification notify) {
                // TODO Auto-generated method stub
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(notify.extraInfo);
                            for (StaffRest staff : SanyiSDK.rest.staffs) {
                                if (staff.id == Long.parseLong(jsonObject.getString("staff"))) {
                                    staff.password = jsonObject.getString("password");
                                    if (SanyiSDK.currentUser == staff) {
                                        Toast.makeText(context, "密码修改成功,请重新登录", Toast.LENGTH_LONG).show();
                                        if (MainScreenActivity.CURRENT_MODE != MainScreenActivity.LOGIN_FRAGMENT) {
                                            activity.displayView(MainScreenActivity.LOGIN_FRAGMENT, null);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "员工资料更新失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            @Override
            public void WeChatPaySuccessed(final Notification notify) {
                Log.e("```", "WeChatPaySuccessed" + notify.extraInfo);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activity.orderFragment != null) {
                            if (activity.orderFragment.cashierResult != null) {
                                if (Long.toString(activity.orderFragment.cashierResult.bill.id).equals(notify.extraInfo) && activity.CURRENT_MODE == MainScreenActivity.ORDER_FRAGMENT) {
                                    PaySuccessDialog dialog = new PaySuccessDialog(activity,"餐桌" + activity.orderFragment.cashierResult.orders.get(0).tableName + " 支付成功。", new PaySuccessDialog.IPaySuccessListener() {
                                        @Override
                                        public void onClose() {

                                        }

                                        @Override
                                        public void onComfirm() {
                                            activity.orderFragment.clearData();
                                            activity.orderFragment.closeDrawerLayout();
                                            if (activity.orderFragment.scanDialog != null) {
                                                if (activity.orderFragment.scanDialog.isShowing()) {
                                                    activity.orderFragment.scanDialog.dismiss();
                                                }
                                            }
                                            if (activity.presentation != null) {
                                                activity.presentation.hideDialog();
                                            }
                                        }
                                    });
                                    dialog.show();
//                                    final NormalDialog normalDialog = new NormalDialog(activity);
//                                    normalDialog.content("餐桌" + activity.orderFragment.cashierResult.orders.get(0).tableName + " 支付成功。");
//                                    normalDialog.widthScale((float) 0.5);
//                                    normalDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
//                                        @Override
//                                        public void onClickConfirm() {
//                                            normalDialog.dismiss();
//                                            activity.orderFragment.clearData();
//                                            activity.orderFragment.closeDrawerLayout();
//                                            if (activity.orderFragment.scanDialog != null) {
//                                                if (activity.orderFragment.scanDialog.isShowing()) {
//                                                    activity.orderFragment.scanDialog.dismiss();
//                                                }
//                                            }
//                                            if (activity.presentation != null) {
//                                                activity.presentation.hideDialog();
//                                            }
//                                        }
//                                    });
//                                    normalDialog.show();
                                }
                            }
                        }
                        if (activity.checkFragment != null) {
                            if (activity.checkFragment.cashierResult != null) {
                                if (Long.toString(activity.checkFragment.cashierResult.bill.id).equals(notify.extraInfo) && activity.CURRENT_MODE == MainScreenActivity.CHECK_FRAGMENT) {
//                                    final NormalDialog normalDialog = new NormalDialog(activity);
//                                    normalDialog.content("餐桌" + activity.checkFragment.cashierResult.orders.get(0).tableName + " 支付成功。");
//                                    normalDialog.widthScale((float) 0.5);
//                                    normalDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
//                                        @Override
//                                        public void onClickConfirm() {
//                                            normalDialog.dismiss();
//                                            activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
//                                        }
//                                    });
//                                    normalDialog.show();
                                    PaySuccessDialog dialog = new PaySuccessDialog(activity,"餐桌" + activity.checkFragment.cashierResult.orders.get(0).tableName + " 支付成功。", new PaySuccessDialog.IPaySuccessListener() {
                                        @Override
                                        public void onClose() {

                                        }

                                        @Override
                                        public void onComfirm() {
                                            activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                                        }
                                    });
                                    dialog.show();

                                    if (activity.checkFragment.scanDialog != null) {
                                        if (activity.checkFragment.scanDialog.isShowing()) {
                                            activity.checkFragment.scanDialog.dismiss();
                                        }
                                    }
                                    if (activity.presentation != null) {
                                        activity.presentation.hideDialog();
                                    }
                                }

                            }
                        }
                    }
                });


            }

            @Override
            public void OperationDataUpdate() {
                // TODO Auto-generated method stub
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (alertDialog != null) {
                            if (alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                        }
                        if (activity.tableStatusFragment != null)
                            if (activity.tableStatusFragment.isResumed()) {
                                TableViewPagerFragment tableViewPagerFragment = activity.tableStatusFragment.tableViewPagerFragment;
                                if (tableViewPagerFragment.isResumed()) {
                                    tableViewPagerFragment.mPagerAdapter.refresh();
                                    tableViewPagerFragment.indicatorAdapter.refresh();
                                    TableGroupItemFragment tableGroupItemFragment = (TableGroupItemFragment) tableViewPagerFragment.mPagerAdapter.getItem(tableViewPagerFragment.mViewPager.getCurrentItem());
                                    if (tableGroupItemFragment != null)
                                        tableGroupItemFragment.adapter.refresh();
                                    if (activity.tableStatusFragment.tableViewPagerFragment.shopPrinterAdapter != null) {
                                        activity.tableStatusFragment.tableViewPagerFragment.shopPrinterAdapter.notifyDataSetChanged();
                                    }
                                    if (SanyiSDK.getPrinterStatus()) {
                                        activity.tableStatusFragment.tableViewPagerFragment.setPrinterImageOK();
                                    } else {
                                        activity.tableStatusFragment.tableViewPagerFragment.setPrinterImageError();
                                    }
                                    if (SanyiSDK.rest.operationData.shop.cloud_connection != Restaurant.currentCloudConnectStatus) {
                                        Restaurant.currentCloudConnectStatus = SanyiSDK.rest.operationData.shop.cloud_connection;
                                        activity.tableStatusFragment.tableViewPagerFragment.setCloudConnectImage();
                                    }
                                }
                                if (activity.tableStatusFragment.currentFragment instanceof CancelTableFragment) {
                                    ((CancelTableFragment) (activity.tableStatusFragment.currentFragment)).refresh();
                                } else if (activity.tableStatusFragment.currentFragment instanceof ChangeTableFragment) {
//                            ((ChangeTableFragment) (activity.tableStatusFragment.currentFragment)).refresh();
                                } else if (activity.tableStatusFragment.currentFragment instanceof SplitTableFragment) {
                                    ((SplitTableFragment) (activity.tableStatusFragment.currentFragment)).refresh();
                                } else if (activity.tableStatusFragment.currentFragment instanceof UnSplitTableFragment) {
                                    ((UnSplitTableFragment) (activity.tableStatusFragment.currentFragment)).refresh();
                                } else if (activity.tableStatusFragment.currentFragment instanceof MergeTableFragment) {
                                    ((MergeTableFragment) (activity.tableStatusFragment.currentFragment)).refresh();
                                } else if (activity.tableStatusFragment.currentFragment instanceof OpenAndMergeFragment) {
                                    ((OpenAndMergeFragment) (activity.tableStatusFragment.currentFragment)).refresh();
                                } else if (activity.tableStatusFragment.currentFragment instanceof UnMergeTableFragment) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("position", ((UnMergeTableFragment) (activity.tableStatusFragment.currentFragment)).getLvSelectIndex());
                                    activity.tableStatusFragment.disPlayView(MainScreenActivity.UNMERGE_TABLE_FRAGMENT, bundle);
                                }


                            }
                    }
                });

            }

            @Override
            public void ForceUnlockTable(final Notification nofity) {
                // TODO Auto-generated method stub
                if (Restaurant.isFastMode) return;
                if (activity.CURRENT_MODE == MainScreenActivity.LOGIN_FRAGMENT || activity.CURRENT_MODE == MainScreenActivity.TABLE_FRAGMENT)
                    return;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("extra", nofity.extraInfo);
                        intent.setAction(Restaurant.UNLOCK_TABLE_ACTION);
                        activity.sendBroadcast(intent);
                        activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
                    }
                });

            }

            @Override
            public void ClearSoldOutUpdate(Notification notify) {
                // TODO Auto-generated method stub

            }

            @Override
            public void UpdateSoldoutDish() {
                // TODO Auto-generated method stub
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activity.orderFragment != null && activity.orderFragment.orderDishFragment != null && activity.orderFragment.isResumed()) {
                            activity.orderFragment.orderDishFragment.refreshDish();
                        }
                        EventBus.getDefault().post(new EventMessage(Restaurant.EVENT_SOLD));
                    }
                });

            }

            @Override
            public void OtherNotification(Notification notify) {
                switch (notify.notificationType) {
                    case 13://外卖
                        activity.playTakeOutMusic(notify.extraInfo);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
