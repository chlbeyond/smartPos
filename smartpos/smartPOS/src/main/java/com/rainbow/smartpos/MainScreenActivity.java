package com.rainbow.smartpos;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cc.serialport.SerialPort;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.playlog.internal.LogEvent;
import com.qrcode.decode.Constant;
import com.rainbow.common.view.HackyDrawerLayout;
import com.rainbow.smartpos.activity.reportfragment.WebViewFragment;
import com.rainbow.smartpos.bill.newbill.NewBillFragment;
import com.rainbow.smartpos.check.CheckDetailListAdapter;
import com.rainbow.smartpos.check.CheckFragment;
import com.rainbow.smartpos.check.TenPayPopWindow;
import com.rainbow.smartpos.check.VerifyMemberDialog;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.event.EventMessage;
import com.rainbow.smartpos.login.LoginActivity;
import com.rainbow.smartpos.login.SanyiPintServiceConnection;
import com.rainbow.smartpos.mainframework.LeftMenuView;
import com.rainbow.smartpos.mainframework.MainView;
import com.rainbow.smartpos.order.OrderFragment;
import com.rainbow.smartpos.place.PlaceDetailFragment;
import com.rainbow.smartpos.place.WeightDialog;
import com.rainbow.smartpos.presentation.DisplayPresentation;
import com.rainbow.smartpos.presentation.FilePathData;
import com.rainbow.smartpos.printer.AidlUtil;
import com.rainbow.smartpos.printer.BlueToothPrinter;
import com.rainbow.smartpos.printer.BluetoothUtil;
import com.rainbow.smartpos.printer.PrinterCallback;
import com.rainbow.smartpos.service.PrintService;
import com.rainbow.smartpos.slidingtutorial.PresentationFragment;
import com.rainbow.smartpos.tablemanage.CancelTableFragment;
import com.rainbow.smartpos.tablemanage.ChangeTableFragment;
import com.rainbow.smartpos.tablemanage.MergeFragment;
import com.rainbow.smartpos.tablemanage.OpenAndMergeFragment;
import com.rainbow.smartpos.tablemanage.SplitTableFragment;
import com.rainbow.smartpos.tablemanage.TableManageFragment;
import com.rainbow.smartpos.tablemanage.UnMergeTableFragment;
import com.rainbow.smartpos.tablemanage.UnSplitTableFragment;
import com.rainbow.smartpos.tablestatus.OpenTableDialog;
import com.rainbow.smartpos.tablestatus.TableAdapter;
import com.rainbow.smartpos.tablestatus.TableStatusFragment;
import com.rainbow.smartpos.util.BufferedAudioPlayer;
import com.rainbow.smartpos.util.GlideCircleTransform;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.NetworkUtil;
import com.rainbow.smartpos.util.SwitchTableDialog;
import com.rainbow.smartpos.util.SwitchTableDialog.ISwitchTableListener;
import com.sanyipos.android.sdk.androidUtil.KeyBoardUtil;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.common.Converter;
import com.sanyipos.sdk.api.inters.PushRequestListener;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.api.services.scala.AddDetailRequest;
import com.sanyipos.sdk.api.services.scala.NewBillRequest;
import com.sanyipos.sdk.api.services.scala.QueryPreOrderRequest;
import com.sanyipos.sdk.api.services.scala.SharePrinterRequest_;
import com.sanyipos.sdk.core.AgentRequests;
import com.sanyipos.sdk.core.PosAgent;
import com.sanyipos.sdk.core.PosAgentRequest;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.ShopPrinter;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.sanyipos.sdk.model.scala.preOrder.QueryPreOrderResult;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.JsonUtil;
import com.zhy.autolayout.AutoLayoutActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import filepicker.FilePickerActivity;
import kprogresshud.KProgressHUD;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;

import static com.rainbow.smartpos.login.InputPasswordFragment.printServiceConnection;


public class MainScreenActivity extends AutoLayoutActivity implements OnClickListener, MainView {
    static final String TAG = "MainScreenActivity";
    public static int CURRENT_MODE = -1;

    public final static int LOGIN_FRAGMENT = -1;
    public final static int TABLE_FRAGMENT = 0;
    public final static int ORDER_FRAGMENT = 1;
    public final static int CHECK_FRAGMENT = 2;
    public final static int BILL_FRAGMENT = 3;
    public final static int MEMBER_FRAGMENT = 5;
    public final static int MANAGER_FRAGMENT = 6;
    public final static int TABLE_VIEWPAGER_FRAGMENT = 7;
    public final static int SETTING = 8;
    public final static int SLOD_OUT = 9;
    public final static int TABLE_MANAGE_FRAGMENT = 10;
    public final static int SPLIT_TABLE_FRAGMENT = 11;
    public final static int UNSPLIT_TABLE_FRAGMENT = 12;
    public final static int CHANGE_TABLE_FRAGMENT = 13;
    public final static int CANCEL_TABLE_FRAGMENT = 14;
    public final static int MERGE_TABLE_FRAGMENT = 15;
    public final static int UNMERGE_TABLE_FRAGMENT = 16;
    public final static int OPEN_AND_MERGE_TABLE_FRAGMENT = 17;
    public final static int HAS_SLODOUT_FRAGMENT = 18;
    public final static int HAS_STOPSALE_FRAGMENT = 19;
    public final static int PLACE_FRAGMENT = 20;
    public final static int PRESENTATION_FRAGMENT = 21;
    public final static int HANDLER_UPDAPTE_PING = 55;
    public final static int HANDLER_REQUEST_COMPLETE = 66;
    public final static int TAKEOUT_FRAGMENT = 67;

    public TextView user_info_image;
    public TextView confirm_customer_order;
    public LoginActivity loginFragment;
    public OrderFragment orderFragment;
    public PlaceDetailFragment placeFragment;
    public TableStatusFragment tableStatusFragment;
    public CheckFragment checkFragment;
    public SplitTableFragment mSplitTableFragment;
    public UnSplitTableFragment mUnSplitTableFragment;
    public ChangeTableFragment mChangeTableFragment;
    public CancelTableFragment mCancelTableFragment;
    public MergeFragment mMergeTableFragment;
    public UnMergeTableFragment mUnMergeTableFragment;
    public OpenAndMergeFragment mOpenAndMergeFragment;
    public TableManageFragment mTableManageFragment;

    public WebViewFragment mTakeOutFragment;

    //    public MemberFragment memberFragment;
    public NewBillFragment billFragment;
    //    public SoldFragment slodOutFragment;
    public MainScreenActivity activity;
    public static DisplayMetrics metric;
    public Context mContext;
    public PresentationFragment presentationFragment;
    private LinearLayout menu_layout;
    private LinearLayout title_layout;
    private RadioGroup menu_group;
    private TextView user_info_text_name;
    private ImageView logo_image;

    public Timer timer;
    public HackyDrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public SmartPosBundle smartPosBundle = new SmartPosBundle();
    public SerialPort serialPort;
    /****
     * left menu
     */
    public LinearLayout mLeftMenuHandOver;
    public LinearLayout mLeftMenuBusinessDaily;
    public LinearLayout mLeftMenuMember;
    public LinearLayout mLeftMenuSlodOut;
    public LinearLayout mLeftMenuOpenMoneyBox;
    public LinearLayout mLeftMenuLogOut;
    public LinearLayout mLeftMenuLocalSetting;
    public LinearLayout mLeftMenuPrinterSetting;
    public LinearLayout mLeftMenuChangePSD;
    public LinearLayout mLeftMenuAbout;
    public LinearLayout mLeftMenuTodayBills;
    public LinearLayout mLeftMenuReprintDailyReport;
    public LinearLayout mLeftMenuPrintSaleDetail;
    public TextView mLeftMenuShopName;
    public LeftMenuView mLeftMenuView;
    public ImageView mImageMenu;
    public ImageView mImageShopLogo;
    public KProgressHUD progressHUD;
    public DisplayPresentation presentation;

    private TenPayPopWindow confirmCodePopWindow;
    private long preOrderTableId;  //预订单桌号
    private List<OrderDetail> confirmedPreOrderDishes;
    private String odrerSn; //预订单号


    private BluetoothDevice bluetoothDevice;
    private List<String> printInfo = new ArrayList<>();
    private BlueToothPrinter bluePrinter;//蓝牙打印机
    public boolean hasBindServer = false;


    public DSKernel mDSKernel = null;//商米双屏控制器

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private BufferedAudioPlayer mPlayer;
//    private int musics[] = {R.raw.autoorder_mt, R.raw.cancelorder_mt, R.raw.neworder_mt,
//                            R.raw.refundorder_mt, R.raw.remindorder_mt};

    private HashMap<String, Integer> audioMap = new HashMap() {
        {
            put("neworder.mt", R.raw.neworder_mt);
            put("autoorder.mt", R.raw.autoorder_mt);
            put("cancelorder.mt", R.raw.cancelorder_mt);
            put("refundorder.mt", R.raw.refundorder_mt);
            put("remindorder.mt", R.raw.remindorder_mt);
            put("cancelorder.el", R.raw.cancelorder_el);
            put("neworder.el", R.raw.neworder_el);
            put("autoorder.el", R.raw.autoorder_el);
            put("cancelorder.el", R.raw.cancelorder_el);
            put("refundorder.el", R.raw.refundorder_el);
            put("remindorder.el", R.raw.remindorder_el);
        }
    };

    @Override
    public void switchMenu() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_menu_hand_over:
            case R.id.left_menu_business_daily:
            case R.id.left_menu_member:
            case R.id.left_menu_slod_out:
            case R.id.left_menu_open_money:
            case R.id.left_menu_local_setting:
            case R.id.left_menu_change_psd:
            case R.id.left_menu_today_bills:
            case R.id.left_menu_log_out:
            case R.id.left_menu_reprint_daily_report:
            case R.id.left_menu_print_sale_detail:
            case R.id.left_menu_print_setting:
            case R.id.left_menu_abouts:
                if (mLeftMenuView != null) {
                    mLeftMenuView.onClick(view);
                    drawerLayout.closeDrawers();
                }
                break;

            case R.id.imageView_ic_menu:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }


    }

    public void restartToUpdate() {

        if (!loginFragment.inputPasswordFragment.isUpdateing) {
            loginFragment.inputPasswordFragment.isUpdateing=true;
//        Intent intent = new Intent(activity, LoginActivity.class);
            Intent i = getPackageManager()
                    .getLaunchIntentForPackage(getPackageName());
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (null != loginFragment && null != loginFragment.inputPasswordFragment && null != printServiceConnection) {
                try {
                    unbindService(printServiceConnection);
                } catch (IllegalArgumentException e) {

                }
                PrintService printService = printServiceConnection.printService;
                if (null != printService && isServiceWorked("com.rainbow.smartpos.service.PrintService")) {
                    printService.onStop();
                }
            }
            if (Restaurant.usbDriver != null && Restaurant.usbDriver.mUsbReceiver != null) {
                if (Restaurant.usbDriver.usbReceiverIsRegistered) {
                    getApplicationContext().unregisterReceiver(Restaurant.usbDriver.mUsbReceiver);
                    Restaurant.usbDriver.usbReceiverIsRegistered = false;
                }
            }
//        Intent i = this.getPackageManager()
//                .getLaunchIntentForPackage(this.getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
            EventBus.getDefault().unregister(this);
            loginFragment.inputPasswordFragment.isUpdateing = false;
//        startActivity(intent);
            startActivity(i);
            activity.finish();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ExitManager.getInstance().addActivity(activity);
        initProgressDialog();
        setContentView(R.layout.activity_mainscreen);
        myHandler = new MyHandler(this);

        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        mContext = this;
        menu_layout = (LinearLayout) findViewById(R.id.menu_layout);
        title_layout = (LinearLayout) findViewById(R.id.title_layout);
        menu_group = (RadioGroup) findViewById(R.id.menu_group);
        menu_group.setOnCheckedChangeListener(checkedChangeListener);

        user_info_text_name = (TextView) findViewById(R.id.user_info_text_name);
        logo_image = (ImageView) findViewById(R.id.logo_image);
        mImageMenu = (ImageView) findViewById(R.id.imageView_ic_menu);
        mImageMenu.setOnClickListener(this);
        mImageShopLogo = (ImageView) findViewById(R.id.imageView_shop_logo);
        initDrawerLayout();

        user_info_image = (TextView) findViewById(R.id.user_info_image);
        user_info_image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stubect
                logOut();
            }
        });
        confirm_customer_order = (TextView) findViewById(R.id.confirm_customer_order);
        confirm_customer_order.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_customer_order();
            }
        });
        initPresent();
        initLedScreen();
        initBoothPrint();
        displayView(LOGIN_FRAGMENT, null);
//        mMainPresenter = new MainPresenterImpl(mContext, this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        SanyiSDK.getSDK().initRequestListener(new PushRequestListener() {
            @Override
            public void startRequest(PosAgentRequest request) {
//                if (request instanceof AgentRequests.CheckAgentRequest_) return;
                if (request instanceof AgentRequests.POSPollingRequest) return;
                if (request instanceof AgentRequests.LeaveSeatRequest) return;
                if (progressHUD != null) progressHUD.show();
                EventBus.getDefault().post(new EventMessage(Restaurant.EVENT_REQUEST));

            }
        });

        SanyiSDK.getSDK().initRequestCompleteListener(new PosAgent.PosAgentRequestListener() {

            @Override
            public void requestComplete(PosAgentRequest request) {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.obj = request;
                handler.sendMessage(message);
                EventBus.getDefault().post(new EventMessage(Restaurant.EVENT_REQUEST_COMPLETE));

            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        EventBus.getDefault().register(this);

        Restaurant.isTakeoutVoiceOff = SharePreferenceUtil.getBooleanPreference(activity, SmartPosPrivateKey.ST_TAKEOUT_VOICE_OFF, false);
    }

    private void initSunmiScreen() {
        if (Build.BRAND.equals("SUNMI")){

        }
    }

    private void initBoothPrint() {
        AidlUtil.getInstance().connectPrinterService(this);
        if(!BluetoothUtil.connectBlueTooth(MainScreenActivity.this)){
            Log.d("luo","连接蓝牙打印机失败");
//            Toast.makeText(MainScreenActivity.this, "连接蓝牙打印机失败", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("luo","连接蓝牙打印机成功");
//            Toast.makeText(MainScreenActivity.this, "连接蓝牙打印机成功", Toast.LENGTH_SHORT).show();

            bluetoothDevice = BluetoothUtil.getDevice(BluetoothUtil.getBTAdapter());//获得蓝牙设备对象
            printInfo = AidlUtil.getInstance().getPrinterInfo();

            if (printInfo != null && printInfo.size() == 7) {
//                Log.e("111", printInfo.toString());
                SharePreferenceUtil.saveStringPreference(MainScreenActivity.this, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, "");//clear usb printer setting
                SharePreferenceUtil.saveStringPreference(MainScreenActivity.this, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER_NAME, "");

                SharePreferenceUtil.saveStringPreference(MainScreenActivity.this, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, printInfo.get(0));//蓝牙打印机序列号
                SharePreferenceUtil.saveStringPreference(MainScreenActivity.this, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER_NAME, printInfo.get(1));//蓝牙打印机型号
                SharePreferenceUtil.saveStringPreference(MainScreenActivity.this, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, ""); //clear COM printer
                SharePreferenceUtil.saveStringPreference(MainScreenActivity.this, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, ""); //clear COM printer
            } else {
                Log.d("luo","链接不上打印机,请退出后重试");
//                Toast.makeText(this, "链接不上打印机,请退出后重试", Toast.LENGTH_SHORT).show();
            }

            bluePrinter = new BlueToothPrinter("蓝牙打印机", bluetoothDevice, 0);

        }

        printServiceConnection = new SanyiPintServiceConnection(bluePrinter, new SanyiPintServiceConnection.PrinterServiceStartListener() {
            @Override
            public void onPrintServiceStartSuccess() {
                Log.d("luo","绑定打印服务成功");
//                Toast.makeText(context, "绑定打印服务成功", Toast.LENGTH_SHORT).show();
                if (!Constant.HAS_SHARE_BLUETOOTH_PRINT) {
                    createSharedPriner(bluePrinter);
                    Constant.HAS_SHARE_BLUETOOTH_PRINT = true;
                }
                // initRemotePrinterPreference();
            }

            @Override
            public void onPrintServiceStartFailed() {
                Log.d("luo","绑定打印服务失败");
//                Toast.makeText(NewTablesActivity.this, "绑定打印服务失败", Toast.LENGTH_SHORT).show();
            }
        });

        if (!hasBindServer) {
            Intent intent = new Intent(MainScreenActivity.this, PrintService.class);
            activity.bindService(intent, printServiceConnection, Context.BIND_AUTO_CREATE);
            hasBindServer = true;
        }

//        Restaurant.usbDriver.usbReceiverIsRegistered = true;


    }


    public void createSharedPriner(final BlueToothPrinter printer) {
        if (printer != null)
            SanyiScalaRequests.sharePrinterRequest(SanyiSDK.registerData.getPosName() + "打印机", NetworkUtil.getIPAddress(true), new SharePrinterRequest_.ISharePrintListener() {

                @Override
                public void onFail(String error) {
                    // TODO Auto-generated method stub
                    Log.d("luo","蓝牙打印机共享失败");
//                    Toast.makeText(activity, "蓝牙打印机共享失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(ShopPrinter result) {
                    // TODO Auto-generated method stub
                    Log.d("luo","打印机共享成功");
//                    Toast.makeText(activity, "打印机共享成功", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void playTakeOutMusic(String key) {
        if (key != null && mPlayer != null) {
            setTakeOutMusicOff(Restaurant.isTakeoutVoiceOff);
            mPlayer.playOnce(key.trim().toLowerCase());
        }
    }

    public void setTakeOutMusicOff(boolean isSilent) {
        if (mPlayer != null)
            mPlayer.setSilent(isSilent);
    }

    public void confirm_customer_order() {
        confirmCodePopWindow = new TenPayPopWindow(confirm_customer_order, getString(R.string.confirm_customer_order), this, TenPayPopWindow.Type.PREORDER, new TenPayPopWindow.OnSureListener() {

            @Override
            public void onSureClick(String value) {
                // TODO Auto-generated method stub
                confirmCodePopWindow.dismiss();
                if (!value.isEmpty()) {
                    checkOrder(value);
                }
            }
        });
        confirmCodePopWindow.show();
    }


    private void checkOrder(String orderNo) {
        //TODO 检查订单确认码
        confirmedPreOrderDishes = null;
        preOrderTableId = -1;
        odrerSn = "";
        SanyiScalaRequests.queryPreOrderRequest(orderNo, new QueryPreOrderRequest.IQueryPreOrderListener() {
            @Override
            public void onSuccess(final QueryPreOrderResult result) {

                preOrderTableId = result.order.seat;//查询预订单确定
                odrerSn = result.order.g_sn;
                SeatEntity st = SanyiSDK.rest.getTableById(preOrderTableId);
                if (st == null) {
                    Toast.makeText(MainScreenActivity.this, "找不到桌子", Toast.LENGTH_LONG);
                    return;
                }
                final NormalDialog dishListDlg = new NormalDialog(MainScreenActivity.this);
                final ListView dialogView = new ListView(MainScreenActivity.this);
                dialogView.setVerticalScrollBarEnabled(true);
                dialogView.setBackgroundColor(Color.WHITE);
                final CheckDetailListAdapter orderAdapter = new CheckDetailListAdapter(activity, CheckDetailListAdapter.Type.PREORDER);
                List<OrderDetail> ods = new ArrayList<>();
                for (int i = 0; i < result.details.size(); ++i)
                    ods.add(Converter.convertPreOrder2OrderDetail(result.details.get(i)));
                orderAdapter.setOrderDetails(ods);
                dialogView.setAdapter(orderAdapter);
                dialogView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final OrderDetail od = orderAdapter.getItem(position);
                        if (od.isWeight()) {//称重
                            WeightDialog dlg = new WeightDialog();
                            dlg.show(MainScreenActivity.this, getString(R.string.weight), String.valueOf(od.getWeight()),
                                    WeightDialog.CHANGE_WEIGHT, new Listener.OnChangePriceListener() {

                                        @Override
                                        public void onSure(Double count) {
                                            od.setWeight(count);
                                            orderAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancel() {
                                        }
                                    });
                        } else if (od.isMarket()) {//时价
                            WeightDialog dlg = new WeightDialog();
                            dlg.show(MainScreenActivity.this, getString(R.string.customer_price), String.valueOf(od.getCurrentPrice()),
                                    WeightDialog.CUSTOMER_PRICE, new Listener.OnChangePriceListener() {

                                        @Override
                                        public void onSure(Double count) {
                                            od.setOriginPrice(count);
                                            od.setCurrentPrice(count);
                                            od.setRealCurrentPrice(count);
                                            od.setPriceChanged(true);
                                            orderAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancel() {
                                        }
                                    });
                        } else {

                        }
                    }
                });
                dishListDlg.content(dialogView);
                dishListDlg.title("\'" + st.tableName + "\':确认预订单");
                dishListDlg.widthScale((float) 0.5);
                dishListDlg.heightScale((float) 0.8);
                dishListDlg.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        dishListDlg.dismiss();
                        confirmedPreOrderDishes = orderAdapter.getSelectedPreOrderList();
                        if (confirmedPreOrderDishes.size() > 0) {
                            SeatEntity seat = SanyiSDK.rest.getTableById(preOrderTableId);
                            if (seat.state == TableAdapter.AVAILABLE) {
                                new OpenTableDialog(MainScreenActivity.this, "\'" + seat.tableName + "\'" + ":" + getString(R.string.open_table_dialog_hint),
                                        result.order.personCount+"", OpenTableDialog.OPEN_TABLE, new Listener.OnOpenTableListener() {

                                    @Override
                                    public void sure(int peopleCount) {
                                        openTableAndConfirmOrder(peopleCount);
                                    }

                                    @Override
                                    public void cancel() {
                                    }
                                }).show();
                            } else {
                                confirmOrderDishes();
                            }
                        }
                    }
                });
                dishListDlg.show();
            }

            @Override
            public void onFail(String s) {
                Toast.makeText(MainScreenActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openTableAndConfirmOrder(int numOfPerson) {

        List<Long> tables = new ArrayList<>();
        tables.add(preOrderTableId);
        SanyiScalaRequests.addBillRequest(tables, numOfPerson, new NewBillRequest.INewBillRequestListener() {

            @Override
            public void onFail(String error) {
                //
                Toast.makeText(MainScreenActivity.this, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(List<OpenTableDetail> result) {
                //
                OpenTableDetail openTableDetail = null;
                for (OpenTableDetail otd : result) {
                    if (otd.info.getTableSeatId() == preOrderTableId) {
                        openTableDetail = otd;
                    }
                }
                if (openTableDetail != null) {
                    confirmedPreOrderDishes.addAll(openTableDetail.ods);
                }
                //SanyiApplication.writeLogs("open--table--request-end-update--");
                confirmOrderDishes();
            }
        });
    }

    private void confirmOrderDishes() {
        if (confirmedPreOrderDishes == null || confirmedPreOrderDishes.size() == 0)
            return;
        final List<Long> seats = new ArrayList<>();
        seats.add(preOrderTableId);

        SanyiScalaRequests.AddDetailsRequest(Boolean.TRUE, seats, odrerSn, confirmedPreOrderDishes, new AddDetailRequest.IAddDetailListener() {

            @Override
            public void onFail(String error) {
                //
                Toast.makeText(MainScreenActivity.this, "下单失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(List<OpenTableDetail> resp) {
                //
                SanyiScalaRequests.unLockTableRequest(seats, false, new ICallBack() {

                    @Override
                    public void onFail(String error) {
                    }

                    @Override
                    public void onSuccess(String status) {
                    }
                });
                Toast.makeText(MainScreenActivity.this, "下单成功", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logOut() {
        if (CURRENT_MODE != LOGIN_FRAGMENT) {
            displayView(LOGIN_FRAGMENT, null);
        }
    }

    public void initLedScreen() {

        if (serialPort != null) {
            serialPort.close();
        }
        try {
            String port = SharePreferenceUtil.getPreference(this, SmartPosPrivateKey.ST_LOCAL_COM_LED, "");
            String baud = SharePreferenceUtil.getPreference(this, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_LED, "");
            if (!port.isEmpty() && !baud.isEmpty()) {
                serialPort = new SerialPort(new File(port), Integer.parseInt(baud));
            }
        } catch (IOException e) {
            serialPort = null;
        }
    }


    public void initPresent() {
        if (!Build.BRAND.equals("SUNMI")) {
            DisplayManager manager = (DisplayManager) mContext.getSystemService(DISPLAY_SERVICE);
            Display[] displays = manager.getDisplays();
            if (displays.length > 1) {
                presentation = new DisplayPresentation(activity, displays[1]);
                presentation.show();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PosAgentRequest request = ((PosAgentRequest) msg.obj);

            if (progressHUD != null)
                progressHUD.dismiss();

            if (request != null) {
                if (request.getResponseCode() == ConstantsUtil.RESPONSE_SUCCESS) {
                    request.onRequestSuccess();
                } else {
                    request.onRequestException();
                }
            }


        }
    };


    public void loadShopLogo() {
        String url = AgentRequests.port_9090_url + "logo/" + SanyiSDK.getShopId();
        Glide.with(activity).load(url).centerCrop().transform(new GlideCircleTransform(activity)).into(mImageShopLogo);
    }


    public void closedDrawer() {//关闭手势滑动
        if (drawerLayout != null)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void openDrawer() { //打开手势滑动
        if (drawerLayout != null)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    private void initDrawerLayout() {
        mLeftMenuView = new LeftMenuView(mContext, this);
        drawerLayout = (HackyDrawerLayout) findViewById(R.id.mainactivity_drawerlayout);
        if (drawerLayout != null) {
            mLeftMenuHandOver = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_hand_over);
            mLeftMenuHandOver.setOnClickListener(this);
            mLeftMenuBusinessDaily = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_business_daily);
            mLeftMenuBusinessDaily.setOnClickListener(this);
            mLeftMenuMember = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_member);
            mLeftMenuMember.setOnClickListener(this);
            mLeftMenuSlodOut = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_slod_out);
            mLeftMenuSlodOut.setOnClickListener(this);
            mLeftMenuOpenMoneyBox = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_open_money);
            mLeftMenuOpenMoneyBox.setOnClickListener(this);
            mLeftMenuLocalSetting = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_local_setting);
            mLeftMenuLocalSetting.setOnClickListener(this);
            mLeftMenuPrinterSetting = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_print_setting);
            mLeftMenuPrinterSetting.setOnClickListener(this);
            mLeftMenuLogOut = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_log_out);
            mLeftMenuLogOut.setOnClickListener(this);
            mLeftMenuChangePSD = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_change_psd);
            mLeftMenuChangePSD.setOnClickListener(this);
            mLeftMenuAbout = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_abouts);
            mLeftMenuAbout.setOnClickListener(this);

            mLeftMenuTodayBills = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_today_bills);
            mLeftMenuTodayBills.setOnClickListener(this);
            mLeftMenuReprintDailyReport = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_reprint_daily_report);
            mLeftMenuReprintDailyReport.setOnClickListener(this);
            mLeftMenuPrintSaleDetail = (LinearLayout) drawerLayout.findViewById(R.id.left_menu_print_sale_detail);
            mLeftMenuPrintSaleDetail.setOnClickListener(this);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };
            actionBarDrawerToggle.syncState();
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            mLeftMenuShopName = (TextView) drawerLayout.findViewById(R.id.textView_left_menu_shop_name);

        }
    }

    public void setLeftShopName() {
        if (SanyiSDK.rest.operationData != null) {
            mLeftMenuShopName.setText(SanyiSDK.rest.operationData.shop.name);
            loadShopLogo();
        }
    }

    public void setCurrentUserName() {
        user_info_text_name.setText(SanyiSDK.currentUser.name);
    }

    private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            switch (checkedId) {
                case R.id.menu_table:
                    if (CURRENT_MODE == TABLE_FRAGMENT) {
                        return;
                    }
                    displayView(TABLE_FRAGMENT, null);
                    break;
                case R.id.menu_order:
                    if (CURRENT_MODE == ORDER_FRAGMENT || CURRENT_MODE == PLACE_FRAGMENT) {
                        return;
                    }
                    new SwitchTableDialog(activity, SwitchTableDialog.ORDER, null, new ISwitchTableListener() {
                        @Override
                        public void openTable(SeatEntity table, int personCount) {
                            long[] tableIds = {table.seat};
                            if (table.state == TableAdapter.AVAILABLE) {
                                displayView(ORDER_FRAGMENT, smartPosBundle.getBundle(personCount, tableIds, true, false));
                            } else {
                                displayView(ORDER_FRAGMENT, smartPosBundle.getBundle(table.personCount, tableIds, false, false));
                            }
                        }

                        @Override
                        public void batchOperation(long[] tableIds) {

                        }

                        @Override
                        public void cancel() {
                            initGroup();
                        }
                    }).show();
                    break;
                case R.id.menu_check:
                    if (CURRENT_MODE == CHECK_FRAGMENT) {
                        return;
                    }
                    new SwitchTableDialog(activity, SwitchTableDialog.CHECK, null, new ISwitchTableListener() {
                        @Override
                        public void openTable(SeatEntity table, int personCount) {
                            long[] tableIds = {table.seat};
                            if (table.state == TableAdapter.AVAILABLE) {
                                displayView(CHECK_FRAGMENT, smartPosBundle.getBundle(personCount, tableIds, true, false));
                            } else {
                                displayView(CHECK_FRAGMENT, smartPosBundle.getBundle(table.personCount, tableIds, false, false));
                            }
                        }

                        @Override
                        public void batchOperation(long[] tableIds) {

                        }

                        @Override
                        public void cancel() {
                            initGroup();
                        }
                    }).show();
                    break;
                case R.id.menu_member:
                    if (CURRENT_MODE == MEMBER_FRAGMENT) {
                        return;
                    }
                    if (SanyiSDK.currentUser.hasPermissonOfMember()) {
                        displayView(MainScreenActivity.MEMBER_FRAGMENT, null);

                    } else {
                        Toast.makeText(activity, "只有会员管理权限才能执行此操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case R.id.menu_table_manage:
                    displayView(MainScreenActivity.TABLE_MANAGE_FRAGMENT, null);
                    break;
//                case R.id.menu_revenue:
//                    displayView(MainScreenActivity.BILL_FRAGMENT, null);
//                    break;
                case R.id.menu_take_out:
                    displayView(MainScreenActivity.TAKEOUT_FRAGMENT, null);
                    break;
                default:
                    break;
            }
        }
    };

    public static int getScreenWidth() {
        return metric.widthPixels;
    }

    // 显示器的逻辑密度，Density Independent Pixel（如3.0）
    public static float getScreenDensity() {
        return metric.density;
    }

    public static int getScreenHeight() {
        return metric.heightPixels;
    }

    public static Restaurant.ScreenScale getScreenScale() {
        double screenScale = metric.widthPixels / metric.heightPixels;
        if (screenScale <= 1.4) {
            return Restaurant.ScreenScale.S4B3;
        }
        if (screenScale > 1.4 && screenScale <= 1.8) {
            return Restaurant.ScreenScale.S16B9;
        }
        if (screenScale > 1.8 && screenScale <= 2.3) {
            return Restaurant.ScreenScale.S21B9;
        }
        return Restaurant.ScreenScale.S21B9;
    }

    public void initProgressDialog() {
        // TODO Auto-generated method stub
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("请稍候...")
                .setCancellable(false)
                .setDimAmount((float) 0.5);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent ev) {
        return true;
    }

    public void hideView(FragmentTransaction ft) {

        if (tableStatusFragment != null) {
            tableStatusFragment.cancelFocus();
        }
        if (loginFragment != null) {
            ft.hide(loginFragment);
        }
        if (tableStatusFragment != null) {
            ft.hide(tableStatusFragment);
        }
        if (orderFragment != null) {
            ft.hide(orderFragment);
        }
        if (checkFragment != null) {
            ft.hide(checkFragment);
        }
        if (placeFragment != null) {
            ft.hide(placeFragment);
        }
        if (mSplitTableFragment != null) {
            ft.detach(mSplitTableFragment);
        }
        if (mUnSplitTableFragment != null) {
            ft.detach(mUnSplitTableFragment);
        }
        if (mChangeTableFragment != null) {
            ft.detach(mChangeTableFragment);
        }
        if (mCancelTableFragment != null) {
            ft.detach(mCancelTableFragment);
        }
        if (mMergeTableFragment != null) {
            ft.detach(mMergeTableFragment);
        }
        if (mUnMergeTableFragment != null) {
            ft.detach(mUnMergeTableFragment);
        }
        if (mOpenAndMergeFragment != null) {
            ft.detach(mOpenAndMergeFragment);
        }
        if (mTableManageFragment != null) {
            ft.detach(mTableManageFragment);
        }
//        if (memberFragment != null) {
//            ft.detach(memberFragment);
//        }
        if (billFragment != null) {
            ft.detach(billFragment);
        }
//        if (slodOutFragment != null) {
//            ft.detach(slodOutFragment);
//        }
        if (presentationFragment != null) {
            ft.detach(presentationFragment);
        }
        if (mTakeOutFragment != null) {
            ft.detach(mTakeOutFragment);
        }
    }

    public void displayView(int position, Bundle bundle) {
//        mImageMenu.setVisibility(View.GONE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideView(ft);

        switch (position) {
            case LOGIN_FRAGMENT:
                closedDrawer();
                disPlayLoginView();
                if (loginFragment != null) {
                    ft.show(loginFragment);
                } else {
                    loginFragment = new LoginActivity();
                    ft.add(R.id.frame_container, loginFragment);
                }
                CURRENT_MODE = LOGIN_FRAGMENT;
                if (loginFragment.inputPasswordFragment != null) {
                    loginFragment.inputPasswordFragment.pFragment.requestFocus();
                }
                break;
            case TABLE_FRAGMENT:
                openDrawer();

                disPlayTableView();
                if (Restaurant.isFastMode) {
                    title_layout.setVisibility(View.GONE);
                    if (!SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_CASHIER)) {
                        Toast.makeText(activity, "没有权限", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (orderFragment != null) {
                        ft.show(orderFragment);
                    } else {
                        orderFragment = new OrderFragment();
                        ft.add(R.id.frame_container, orderFragment);
                    }
                    if (bundle != null) {
                        orderFragment.setBundle(bundle);
                    }
                    menu_group.setVisibility(View.GONE);
                    CURRENT_MODE = ORDER_FRAGMENT;
                    break;
                }
                if (tableStatusFragment != null) {
                    ft.show(tableStatusFragment);
                } else {
                    tableStatusFragment = new TableStatusFragment();
                    ft.add(R.id.frame_container, tableStatusFragment);
                }
                CURRENT_MODE = TABLE_FRAGMENT;
                tableStatusFragment.requestFocus();
//                mImageMenu.setVisibility(View.VISIBLE);
                break;
            case ORDER_FRAGMENT:
                if (orderFragment != null) {
                    ft.show(orderFragment);
                } else {
                    orderFragment = new OrderFragment();
                    ft.add(R.id.frame_container, orderFragment);
                }
                if (bundle != null) {
                    orderFragment.setBundle(bundle);
                }


                //disPlayOrderView();
                CURRENT_MODE = ORDER_FRAGMENT;
                break;
            case PLACE_FRAGMENT:
                if (placeFragment != null) {
                    ft.show(placeFragment);
                } else {
                    placeFragment = new PlaceDetailFragment();
                    ft.add(R.id.frame_container, placeFragment);
                }
                if (bundle != null) {
                    placeFragment.setBundle(bundle);
                }
                //disPlayPlaceView();
                CURRENT_MODE = PLACE_FRAGMENT;
                break;
            case CHECK_FRAGMENT:
                if (checkFragment != null) {
                    ft.show(checkFragment);
                } else {
                    checkFragment = new CheckFragment();
                    ft.add(R.id.frame_container, checkFragment);
                }
                if (bundle != null) {
                    checkFragment.setBundle(bundle);
                }
                //disPlayChcekView();
                CURRENT_MODE = CHECK_FRAGMENT;
                break;
            case TABLE_MANAGE_FRAGMENT:
                if (mTableManageFragment != null) {
                    ft.attach(mTableManageFragment);
                } else {
                    mTableManageFragment = new TableManageFragment();
                    ft.add(R.id.frame_container, mTableManageFragment);
                }
                CURRENT_MODE = TABLE_MANAGE_FRAGMENT;
                break;
            case MainScreenActivity.SPLIT_TABLE_FRAGMENT:
                if (mSplitTableFragment != null) {
                    ft.attach(mSplitTableFragment);
                } else {
                    mSplitTableFragment = new SplitTableFragment();
                    ft.add(R.id.frame_container, mSplitTableFragment);
                }
                if (bundle != null) {
                    mSplitTableFragment.setBundle(bundle);
                }
                CURRENT_MODE = SPLIT_TABLE_FRAGMENT;
                break;
            case MainScreenActivity.UNSPLIT_TABLE_FRAGMENT:
                if (mUnSplitTableFragment != null) {
                    ft.attach(mUnSplitTableFragment);
                } else {
                    mUnSplitTableFragment = new UnSplitTableFragment();
                    ft.add(R.id.frame_container, mUnSplitTableFragment);
                }
                if (bundle != null) {
                    mUnSplitTableFragment.setBundle(bundle);
                }
                CURRENT_MODE = UNSPLIT_TABLE_FRAGMENT;
                break;
            case MainScreenActivity.CANCEL_TABLE_FRAGMENT:
                if (mCancelTableFragment != null) {
                    ft.attach(mCancelTableFragment);
                } else {
                    mCancelTableFragment = new CancelTableFragment();
                    ft.add(R.id.frame_container, mCancelTableFragment);
                }
                if (bundle != null) {
                    mCancelTableFragment.setBundle(bundle);
                }
                CURRENT_MODE = CANCEL_TABLE_FRAGMENT;
                break;
            case MainScreenActivity.CHANGE_TABLE_FRAGMENT:
                if (mChangeTableFragment != null) {
                    ft.attach(mChangeTableFragment);
                } else {
                    mChangeTableFragment = new ChangeTableFragment();
                    ft.add(R.id.frame_container, mChangeTableFragment);
                }
                if (bundle != null) {
                    mChangeTableFragment.setBundle(bundle);
                }
                CURRENT_MODE = CHANGE_TABLE_FRAGMENT;
                break;
            case MainScreenActivity.MERGE_TABLE_FRAGMENT:
                if (mMergeTableFragment != null) {
                    ft.attach(mMergeTableFragment);
                } else {
                    mMergeTableFragment = new MergeFragment();
                    ft.add(R.id.frame_container, mMergeTableFragment);
                }
                if (bundle != null) {
                    mMergeTableFragment.setBundle(bundle);
                }
                CURRENT_MODE = MERGE_TABLE_FRAGMENT;
                break;
            case MainScreenActivity.UNMERGE_TABLE_FRAGMENT:
                mUnMergeTableFragment = new UnMergeTableFragment();
                mUnMergeTableFragment.setArguments(bundle);
                ft.add(R.id.frame_container, mUnMergeTableFragment);
                if (bundle != null) {
                    mUnMergeTableFragment.setBundle(bundle);
                }
                CURRENT_MODE = UNMERGE_TABLE_FRAGMENT;
                break;
            case MainScreenActivity.OPEN_AND_MERGE_TABLE_FRAGMENT:
                if (mOpenAndMergeFragment != null) {
                    ft.attach(mOpenAndMergeFragment);
                } else {
                    mOpenAndMergeFragment = new OpenAndMergeFragment();
                    ft.add(R.id.frame_container, mOpenAndMergeFragment);
                }
                if (bundle != null) {
                    mOpenAndMergeFragment.setBundle(bundle);
                }
                CURRENT_MODE = OPEN_AND_MERGE_TABLE_FRAGMENT;
                break;
            case MainScreenActivity.MEMBER_FRAGMENT:
//                if (memberFragment != null) {
//                    ft.attach(memberFragment);
//                } else {
//                    memberFragment = new MemberFragment();
//                    ft.add(R.id.frame_container, memberFragment);
//                }
//                if (bundle != null) {
//                    memberFragment.setBundle(bundle);
//                }
//                CURRENT_MODE = MEMBER_FRAGMENT;
                break;
            case MainScreenActivity.BILL_FRAGMENT:
                if (billFragment != null) {
                    ft.attach(billFragment);
                } else {
                    billFragment = new NewBillFragment();
                    ft.add(R.id.frame_container, billFragment);
                }
                CURRENT_MODE = BILL_FRAGMENT;
                break;
//            case MainScreenActivity.SLOD_OUT:
//                disPlaySoldView();
//                Restaurant.resetCategory();
//                slodOutFragment = new SoldFragment();
//                ft.add(R.id.frame_container, slodOutFragment);
//                CURRENT_MODE = SLOD_OUT;
//                break;
            case MainScreenActivity.PRESENTATION_FRAGMENT:
                presentationFragment = new PresentationFragment();
                ft.add(R.id.frame_container, presentationFragment);
                CURRENT_MODE = PRESENTATION_FRAGMENT;
                break;
            case MainScreenActivity.TAKEOUT_FRAGMENT:
                if (mTakeOutFragment != null)
                    ft.attach(mTakeOutFragment);
                else {
                    mTakeOutFragment = new WebViewFragment();
                    mTakeOutFragment.setUrl(SanyiSDK.getSDK().rest.config.takeoutUrl);
                    ft.add(R.id.frame_container, mTakeOutFragment);
                }
                CURRENT_MODE = TAKEOUT_FRAGMENT;
                break;
            default:
                break;

        }
        ft.commitAllowingStateLoss();

        initGroup();
    }


    public void disPlayLoginView() {

        title_layout.setBackgroundResource(R.color.title_login_background);
        logo_image.setVisibility(View.VISIBLE);
        menu_layout.setVisibility(View.GONE);
        menu_group.setVisibility(View.GONE);


    }

    public void disPlayTableView() {
        title_layout.setBackgroundResource(R.color.title_table_background);
        if (SanyiSDK.getSDK().rest.config.takeoutUrl == null) {
            menu_group.findViewById(R.id.menu_take_out).setVisibility(View.GONE);
        } else {
            mPlayer = new BufferedAudioPlayer(this);
            Iterator iter = audioMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                int id = ((Integer) entry.getValue()).intValue();
                try {
                    mPlayer.loadMusic(key, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mPlayer.start();
        }
        menu_layout.setVisibility(View.VISIBLE);
        menu_group.setVisibility(View.VISIBLE);
        logo_image.setVisibility(View.GONE);
    }


    public void unLockTable(List<Long> tableId, final boolean force) {
        if (tableId == null || tableId.size() == 0) {
            return;
        }
        SanyiScalaRequests.unLockTableRequest(tableId, force, new ICallBack() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String status) {
                // TODO Auto-generated method stub
                if (force) {
                    Toast.makeText(activity, "强制解锁成功", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        // unregisterReceiver(screenOffReceiver);
        // timer.cancel();
        // posAgentTask.stop();
        // activity=null;
        if (null != loginFragment && null != loginFragment.inputPasswordFragment && null != printServiceConnection) {
            try {
                unbindService(printServiceConnection);
            } catch (IllegalArgumentException e) {

            }
            PrintService printService = printServiceConnection.printService;
            if (null != printService && isServiceWorked("com.rainbow.smartpos.service.PrintService")) {
                printService.onStop();
            }
        }
        if (Restaurant.usbDriver != null && Restaurant.usbDriver.mUsbReceiver != null) {
            if (Restaurant.usbDriver.usbReceiverIsRegistered) {
                getApplicationContext().unregisterReceiver(Restaurant.usbDriver.mUsbReceiver);
                Restaurant.usbDriver.usbReceiverIsRegistered = false;
            }
        }
        AidlUtil.getInstance().disconnectPrinterService(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    public boolean isServiceWorked(String packageName) {
        ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onUserEvent(EventMessage event) {
        switch (event.eventType) {
            case Restaurant.EVENT_LOGIN:
                displayView(MainScreenActivity.LOGIN_FRAGMENT, null);
                break;
        }
    }

    public OrderFragment getOrderFragment() {
        return orderFragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (CURRENT_MODE) {
            case ORDER_FRAGMENT:
                String kChar = KeyBoardUtil.getCharByKeyCode(keyCode);
                if (kChar != null) {
                    // orderFragment.orderDishFragment.setKeyBoardInput(kChar);
                }
                switch (keyCode) {
                    case KeyEvent.KEYCODE_TAB:
                        break;
                    case KeyEvent.KEYCODE_DEL:
                        // orderFragment.orderDishFragment.deleteKeyBoardInput();
                        break;
                    case KeyEvent.KEYCODE_ENTER:
                        break;
                    case KeyEvent.KEYCODE_ESCAPE:
                        // orderFragment.orderDishFragment.buttonOrderCancel.performClick();
                        break;
                    case KeyEvent.KEYCODE_F11:
//                        if (orderFragment.orderDishFragment.buttonOrderPlaceOrder.isEnabled()) {
//                            orderFragment.orderDishFragment.buttonOrderPlaceOrder.performClick();
//                        } else {
//                            ToastUtils.showShort(activity, "无未落单菜品");
//                        }
                        break;
                    case KeyEvent.KEYCODE_F10:
                        // if
                        // (orderFragment.orderDishFragment.buttonOrderPlaceDeliveryOrder.isEnabled())
                        // {
                        // orderFragment.orderDishFragment.buttonOrderPlaceDeliveryOrder.performClick();
                        // } else {
                        // MainScreenActivity.toastText("无未落单菜品");
                        // }
                        break;
                    case KeyEvent.KEYCODE_F9:
                        // if
                        // (orderFragment.orderDishFragment.buttonOrderCheck.isEnabled())
                        // {
                        // orderFragment.orderDishFragment.buttonOrderCheck.performClick();
                        // } else {
                        // MainScreenActivity.toastText("请选择菜品");
                        // }
                        break;

                    case KeyEvent.KEYCODE_F8:
                        // orderFragment.button_order_fragment_return_dish.performClick();
                        break;
                    case KeyEvent.KEYCODE_F7:
                        // orderFragment.button_order_fragment_cook_method.performClick();
                        break;
                    case KeyEvent.KEYCODE_F6:
                        // orderFragment.button_order_fragment_remindorder.performClick();
                        break;
                    case KeyEvent.KEYCODE_F5:
                        // orderFragment.button_order_fragment_discount.performClick();
                        break;
                    case KeyEvent.KEYCODE_F4:
                        // orderFragment.button_order_fragment_free.performClick();
                        break;
                    case KeyEvent.KEYCODE_F3:
                        // orderFragment.button_order_fragment_suspend.performClick();
                        break;
                    case KeyEvent.KEYCODE_NUMPAD_MULTIPLY:
                        // orderFragment.button_order_fragment_count.performClick();
                        break;
                    case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
                        // orderFragment.button_order_fragment_reduceQuantity.performClick();
                        break;
                    case KeyEvent.KEYCODE_NUMPAD_ADD:
                        // orderFragment.button_order_fragment_addQuantity.performClick();
                        break;
                    case KeyEvent.KEYCODE_NUMPAD_DOT:
                        // orderFragment.button_order_fragment_return_dish.performClick();
                        break;
                    case KeyEvent.KEYCODE_FORWARD_DEL:
                        // orderFragment.button_order_fragment_delete_dish.performClick();
                    case KeyEvent.KEYCODE_INSERT:
//                        if (orderFragment.orderListAdpater.getCurrentSelectOrder() != null) {
//                            if (orderFragment.orderListAdpater.getCurrentSelectOrder().isPlaced()) {
//                                MainScreenActivity.toastText("菜品已落单,无法添加备注");
//                            } else {
//                                // orderFragment.editFoodRemark();
//                            }
//                        } else {
//                            MainScreenActivity.toastText("请选择菜品");
//                        }
                        break;
                    default:
                        break;
                }
                break;
            case CHECK_FRAGMENT:
                // switch (keyCode) {
                // case KeyEvent.KEYCODE_F5:
                // if (checkFragment.buttonCheckPrePrint.isEnabled()) {
                // checkFragment.buttonCheckPrePrint.performClick();
                // } else {
                // MainScreenActivity.toastText("无预打印权限");
                // }
                // break;
                // case KeyEvent.KEYCODE_F6:
                // if (checkFragment.buttonCheckDone.isEnabled()) {
                // checkFragment.buttonCheckDone.performClick();
                // } else {
                // if
                // (!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_CASHIER))
                // {
                // MainScreenActivity.toastText("没有收银权限");
                // break;
                // }
                // MainScreenActivity.toastText("请先进行支付");
                // }
                // break;
                // case KeyEvent.KEYCODE_ESCAPE:
                // checkFragment.buttonCheckCancel.performClick();
                // break;
                // case KeyEvent.KEYCODE_ENTER:
                // case KeyEvent.KEYCODE_NUMPAD_ENTER:
                // if (checkFragment.buttonCheckConfirm.isEnabled()) {
                // checkFragment.buttonCheckConfirm.performClick();
                // } else {
                // MainScreenActivity.toastText("没有收银权限");
                // }
                // break;
                // case KeyEvent.KEYCODE_0:
                // case KeyEvent.KEYCODE_NUMPAD_0:
                // checkFragment.buttonCheckDigitPad0.performClick();
                // break;
                // case KeyEvent.KEYCODE_1:
                // case KeyEvent.KEYCODE_NUMPAD_1:
                // checkFragment.buttonCheckDigitPad1.performClick();
                // break;
                // case KeyEvent.KEYCODE_2:
                // case KeyEvent.KEYCODE_NUMPAD_2:
                // checkFragment.buttonCheckDigitPad2.performClick();
                // break;
                // case KeyEvent.KEYCODE_3:
                // case KeyEvent.KEYCODE_NUMPAD_3:
                // checkFragment.buttonCheckDigitPad3.performClick();
                // break;
                // case KeyEvent.KEYCODE_4:
                // case KeyEvent.KEYCODE_NUMPAD_4:
                // checkFragment.buttonCheckDigitPad4.performClick();
                // break;
                // case KeyEvent.KEYCODE_5:
                // case KeyEvent.KEYCODE_NUMPAD_5:
                // checkFragment.buttonCheckDigitPad5.performClick();
                // break;
                // case KeyEvent.KEYCODE_6:
                // case KeyEvent.KEYCODE_NUMPAD_6:
                // checkFragment.buttonCheckDigitPad6.performClick();
                // break;
                // case KeyEvent.KEYCODE_7:
                // case KeyEvent.KEYCODE_NUMPAD_7:
                // checkFragment.buttonCheckDigitPad7.performClick();
                // break;
                // case KeyEvent.KEYCODE_8:
                // case KeyEvent.KEYCODE_NUMPAD_8:
                // checkFragment.buttonCheckDigitPad8.performClick();
                // break;
                // case KeyEvent.KEYCODE_9:
                // case KeyEvent.KEYCODE_NUMPAD_9:
                // checkFragment.buttonCheckDigitPad9.performClick();
                // break;
                // case KeyEvent.KEYCODE_NUMPAD_DOT:
                // checkFragment.buttonCheckDigitPadDot.performClick();
                // break;
                // case KeyEvent.KEYCODE_DEL:
                // checkFragment.buttonCheckDigitPadDelete.performClick();
                // break;
                //
                // default:
                // break;
                // }
                break;
        }
        // cleanKeyStringBuilder();
        // Log.i("TAG", " keyCode " + keyCode + " " + event.getNumber());
        // mKeyBoard.getTableKeyInput(keyCode);
        // if (isExit == false) {
        // isExit = true;
        // tExit = new Timer();
        // tExit.schedule(new TimerTask() {
        // @Override
        // public void run() {
        // isExit = false;
        // }
        // }, 5000);
        //
        // } else {
        // mKeyBoard.sb.delete(0, mKeyBoard.sb.length());
        // isExit = false;
        // }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (CURRENT_MODE == TABLE_FRAGMENT) {
                initGroup();
            }
            if (CURRENT_MODE == LOGIN_FRAGMENT) {
                exit();
                return true;
            }
            return false;
        }
        return true;

    }

    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次返回键，退出应用程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);

        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.rainbow.smartpos/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.rainbow.smartpos/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information
        client.disconnect();
    }

    class ScreenOffReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDSKernel != null) {
            mDSKernel.checkConnection();
        } else {
            initSunmiDSKernel();
            initSunmiScreen();
        }
    }

    private void initSunmiDSKernel() {
        mDSKernel = DSKernel.newInstance();
        mDSKernel.init(this, mIConnectionCallback);
        mDSKernel.addReceiveCallback(mIReceiveCallback);
        mDSKernel.addReceiveCallback(mIReceiveCallback2);
        mDSKernel.removeReceiveCallback(mIReceiveCallback);
        mDSKernel.removeReceiveCallback(mIReceiveCallback2);
    }

    private MyHandler myHandler;
    private IConnectionCallback mIConnectionCallback = new IConnectionCallback() {
        @Override
        public void onDisConnect() {
            Message message = new Message();
            message.what = 1;
            message.obj = "与远程服务连接中断";
            myHandler.sendMessage(message);
        }

        @Override
        public void onConnected(ConnState state) {
            Message message = new Message();
            message.what = 1;
            switch (state) {
                case AIDL_CONN:
                    message.obj = "与远程服务绑定成功";
                    break;
                case VICE_SERVICE_CONN:
                    message.obj = "与副屏服务通讯正常";
                    break;
                case VICE_APP_CONN:
                    message.obj = "与副屏app通讯正常";
                    break;
                default:
                    break;
            }
            myHandler.sendMessage(message);
        }
    };


    private static class MyHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() != null && !mActivity.get().isFinishing()) {
                switch (msg.what) {
                    case 1://消息提示用途
                        Log.e("luo",msg.obj + "");
                        break;
                    default:
                        break;
                }
            }
        }

    }

    private IReceiveCallback mIReceiveCallback = new IReceiveCallback() {
        @Override
        public void onReceiveData(DSData data) {

        }

        @Override
        public void onReceiveFile(DSFile file) {

        }

        @Override
        public void onReceiveFiles(DSFiles files) {

        }

        @Override
        public void onReceiveCMD(DSData cmd) {

        }
    };
    private IReceiveCallback mIReceiveCallback2 = new IReceiveCallback() {
        @Override
        public void onReceiveData(DSData data) {

        }

        @Override
        public void onReceiveFile(DSFile file) {

        }

        @Override
        public void onReceiveFiles(DSFiles files) {

        }

        @Override
        public void onReceiveCMD(DSData cmd) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();//如果存在activity跳转，商米副屏需要做清理操作
        mDSKernel.onDestroy();
        mDSKernel = null;
    }

    public void exit() {
        ExitManager.getInstance().exit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case DisplayPresentation.EX_FILE_PICKER_RESULT:
//                    ExFilePickerParcelObject object = (ExFilePickerParcelObject) data.getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName());
//                    if (object.count > 0) {
//                        String uri = object.path + object.names.get(0);
//                        presentation.mVideoPlayer.setSource(Uri.fromFile(new File(uri)));
//                        presentation.mVideoPlayer.start();
//                    }
                    if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                        // For JellyBean and above

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clip = data.getClipData();

                            if (clip != null) {
                                if (clip.getItemCount() > 0) {
                                    FilePathData filePathData = new FilePathData();
                                    for (int i = 0; i < clip.getItemCount(); i++) {
                                        Uri uri = clip.getItemAt(i).getUri();
                                        filePathData.filePath.add(uri.getPath());
//                                    // Do something with the URI
//                                    File file = new File(uri.getPath());
//
//                                    filePath = filePath + uri.getPath();
////                                    for (File cfile : file.listFiles()) {
////                                        String type = MimeTypeUtil.getMimeType(cfile);
////                                        if (type != null) {
////                                            if (type.contains("image")) {
////                                                presentation.mPicFiles.add(cfile);
////                                            }
////                                            if (type.contains("video")) {
////                                                presentation.mVideofiles.add(cfile);
////                                            }
////                                        }
////                                    }
//
                                    }
                                    SharePreferenceUtil.saveStringPreference(activity, SmartPosPrivateKey.ST_LOCAL_PRESENTATION, JsonUtil.toJson(filePathData));
                                    if (presentation != null) presentation.initFragment();
                                }
                                // For Ice Cream Sandwich
                            }
                        }
                    }
                    break;
                case Restaurant.SCAN_CODE_CHECK_WECHAT:
                    if (null != checkFragment.tenPayPopWindow && checkFragment.tenPayPopWindow.isShow()) {
                        checkFragment.tenPayPopWindow.onSureListener.onSureClick(data.getExtras().getString("serial_number"));
                    }
                    break;
                case Restaurant.SCAN_CODE_CHECK_VOUCHER:
                    if (null != checkFragment.voucherService && checkFragment.voucherService.isShow()) {
                        checkFragment.voucherService.useSnVoucher(data.getExtras().getString("serial_number"));
                    }
                    break;
                case Restaurant.SCAN_CODE_CHECK_MEMBER:
                    if (verfyMemberListener != null) {
                        verfyMemberListener.memberNumber(data.getExtras().getString("serial_number"));
                    }
                    break;
                case Restaurant.SCAN_CODE_CHECK_PREORDER:
                    if (confirmCodePopWindow != null)
                        confirmCodePopWindow.dismiss();
                    if (data != null && data.getExtras() != null) {
                        checkOrder(data.getExtras().getString("serial_number"));
                    }
                    break;
            }

        }
    }

    public VerifyMemberDialog.VerifyMemberListener verfyMemberListener;

    public void setScanListener(VerifyMemberDialog.VerifyMemberListener listener) {
        verfyMemberListener = listener;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        if (CURRENT_MODE == LOGIN_FRAGMENT) {
            exit();
        }
        if (CURRENT_MODE == TABLE_FRAGMENT) {
            initGroup();
        }
    }

    public void initGroup() {
        switch (CURRENT_MODE) {
            case TABLE_FRAGMENT:
                ((RadioButton) findViewById(R.id.menu_table)).setChecked(true);
                break;
            case PLACE_FRAGMENT:
            case ORDER_FRAGMENT:
                ((RadioButton) findViewById(R.id.menu_order)).setChecked(true);
                break;
            case CHECK_FRAGMENT:
                ((RadioButton) findViewById(R.id.menu_check)).setChecked(true);
                break;
            case TABLE_MANAGE_FRAGMENT:
                ((RadioButton) findViewById(R.id.menu_table_manage)).setChecked(true);
                break;
            case MEMBER_FRAGMENT:
                ((RadioButton) findViewById(R.id.menu_member)).setChecked(true);
                break;
//            case MANAGER_FRAGMENT:
//                ((RadioButton) findViewById(R.id.menu_revenue)).setChecked(true);
//                break;
            default:
                break;
        }
    }

}

