package com.rainbow.smartpos.activity.settingfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.serialport.SerialCommand;
import com.cc.serialport.SerialPort;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.activity.SettingActivity;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.mainframework.PrintUtil;
import com.rainbow.smartpos.mainframework.SerialPortSetting;
import com.rainbow.smartpos.mainframework.ShareComPrinter;
import com.rainbow.smartpos.presentation.DisplayPresentation;
import com.rainbow.smartpos.printer.UsbPrinter;
import com.rainbow.smartpos.setting.ChoosePrinterAdapter;
import com.rainbow.smartpos.setting.PrinterAdapter;
import com.sanyipos.android.sdk.androidUtil.RegisteDataUtils;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.ConnectHostRequest;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import filepicker.FilePickerActivity;

/**
 * Created by ss on 2016/8/30.
 */
public class LocalSettingFragment extends Fragment {
    public Context activity;
    private View settingView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingView = inflater.inflate(R.layout.layout_local_setting, container, false);
        activity = getActivity();
        final TextView textPrint = (TextView) settingView.findViewById(R.id.textView_local_setting_print);
        RelativeLayout printLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_print);

        printLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NormalDialog switchPrint = new NormalDialog(getActivity());
                View switchPrintView = LayoutInflater.from(getActivity()).inflate(R.layout.printer_setting_dialog_view, null);
                ListView switchPrintList = (ListView) switchPrintView.findViewById(R.id.listViewPrinter);
                switchPrintList.setAdapter(new ChoosePrinterAdapter(getActivity()));

                switchPrintList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        SanyiScalaRequests.setPrinterRequest(SanyiSDK.rest.operationData.shopPrinters.get(i).id, new Request.ICallBack() {
                            @Override
                            public void onSuccess(String status) {
                                Toast.makeText(activity, status, Toast.LENGTH_LONG).show();
                                textPrint.setText(SanyiSDK.rest.operationData.shopPrinters.get(i).name);
                                switchPrint.dismiss();
                            }


                            @Override
                            public void onFail(String error) {
                                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                                switchPrint.dismiss();
                            }
                        });
                    }
                });
                switchPrint.content(switchPrintView);
                switchPrint.widthScale((float) 0.5);
                switchPrint.heightScale((float) 0.6);
                switchPrint.title("选择打印机");
                switchPrint.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        switchPrint.dismiss();
                    }
                });
                switchPrint.show();
            }
        });
        String s = SanyiSDK.registerData.getAccessCode();
        for (int i = 0; i < SanyiSDK.rest.operationData.shopPosMachine.size(); i++) {
            if (SanyiSDK.rest.operationData.shopPosMachine.get(i).accessCode != null) {
                if (SanyiSDK.rest.operationData.shopPosMachine.get(i).accessCode.equals(s)) {

                    textPrint.setText(SanyiSDK.rest.operationData.shopPosMachine.get(i).printerName == null ? "无" : SanyiSDK.rest.operationData.shopPosMachine.get(i).printerName);
                }
            }
        }

        final CheckBox logoff = (CheckBox) settingView.findViewById(R.id.textView_local_setting_logoff);
        logoff.setChecked(SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_PUBLICE_DEVICE, false));
        RelativeLayout logoffLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_logoff);
        logoffLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoff.setChecked(!logoff.isChecked());
            }
        });
        logoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Restaurant.isPublicDevice = b;
                SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_PUBLICE_DEVICE, b);
            }
        });

        final CheckBox preprint = (CheckBox) settingView.findViewById(R.id.textView_local_setting_preprint);
        preprint.setChecked(SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_PRE_IS_EXIT, false));
        RelativeLayout preprintLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_preprint);
        preprintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preprint.setChecked(!preprint.isChecked());
            }
        });
        preprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Restaurant.preIsExit = b;
                SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_PRE_IS_EXIT, b);
            }
        });

        //快餐模式
        RelativeLayout fastModeLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_fastmode);
        final CheckBox fastfood = (CheckBox) settingView.findViewById(R.id.checkBox_local_setting_fast);
        fastfood.setChecked(SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_IS_SNACK_PATTERN, false));
        fastfood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {

                boolean savedFlag = SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_IS_SNACK_PATTERN, false);
                if (savedFlag != b) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_reminder, null);
                    builder.setTitle("提示");
                    TextView textView_dialog_reminder = (TextView) view.findViewById(R.id.textView_dialog_reminder);
                    textView_dialog_reminder.setText("修改快餐模式需要重启收银机");
                    builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Restaurant.isFastMode = b;
                            SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_IS_SNACK_PATTERN, b);
                            Intent i = getContext().getPackageManager()
                                    .getLaunchIntentForPackage(getContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fastfood.setChecked(!b);
                        }
                    });
                    builder.setView(view);
                    Dialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });
        fastModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fastfood.setChecked(!fastfood.isChecked());
            }
        });

        RelativeLayout fastModeInputNumber = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_fast_input_number);
        final CheckBox fastfoodInputNumber = (CheckBox) settingView.findViewById(R.id.checkBox_local_setting_fast_input_number);
        fastfoodInputNumber.setChecked(SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_IS_SNACK_INPUT_NUMBER, true));
        fastfoodInputNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Restaurant.fastModeInputNumber = b;
                SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_IS_SNACK_INPUT_NUMBER, b);
                Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();

            }
        });
        fastModeInputNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fastfoodInputNumber.setChecked(!fastfoodInputNumber.isChecked());
            }
        });

        //副屏设置
        RelativeLayout presentationLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_presentation);
        TextView textViewLocalPresentation = (TextView) settingView.findViewById(R.id.textView_local_setting_presentation);
        String localDirectory = SharePreferenceUtil.getPreference(activity, SmartPosPrivateKey.ST_LOCAL_PRESENTATION, null);
        if (localDirectory != null) {
            textViewLocalPresentation.setText(localDirectory);
        }
        presentationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                        Intent intent = new Intent(activity, ru.bartwell.exfilepicker.ExFilePickerActivity.class);
//                        intent.putExtra(ExFilePicker.SET_CHOICE_TYPE, ExFilePicker.CHOICE_TYPE_FILES);
//                        activity.startActivityForResult(intent, DisplayPresentation.EX_FILE_PICKER_RESULT);
                Intent i = new Intent(getActivity(), FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

                // Configure initial directory by specifying a String.
                // You could specify a String like "/storage/emulated/0/", but that can
                // dangerous. Always use Android's API calls to get paths to the SD-card or
                // internal memory.
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                SettingActivity mainScreenActivity = (SettingActivity) activity;
                mainScreenActivity.startActivityForResult(i, DisplayPresentation.EX_FILE_PICKER_RESULT);
            }
        });

        //软键盘设置
        RelativeLayout keyboardLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_keyboard);
        final CheckBox keyboard = (CheckBox) settingView.findViewById(R.id.checkBox_local_setting_keyboard);
        keyboard.setChecked(Restaurant.isInputSoftMode);
        keyboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Restaurant.isInputSoftMode = b;
                SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_POPUP_KEYBOARD, b);
            }
        });
        keyboardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboard.setChecked(!keyboard.isChecked());
            }
        });

        //主机地址设置
        final TextView host = (TextView) settingView.findViewById(R.id.textView_local_setting_host);
        host.setText(SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, ""));
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NormalDialog hostDialog = new NormalDialog(getActivity());
                View addView = LayoutInflater.from(getActivity()).inflate(R.layout.update_agent_address_dialog, null);
                final EditText editText = (EditText) addView.findViewById(R.id.newAddress);
                hostDialog.title("请输入新的主机地址:");
                hostDialog.content(addView);
                hostDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        SanyiSDK.getSDK().setHost(editText.getText().toString());
                        SanyiScalaRequests.connectHostRequest(new ConnectHostRequest.UploadFileListener() {

                            @Override
                            public void onSuccess(String resp) {
                                hostDialog.dismiss();
//                                SanyiSDK.getSDK().setHost(SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, editText.getText().toString()));
//                                Toast.makeText(activity, "主机修改成功，下次启动生效", Toast.LENGTH_LONG).show();
//                                SharePreferenceUtil.saveStringPreference(getActivity(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, editText.getText().toString());
//                                SanyiSDK.registerData.setDeviceRegistered(false);
//                                RegisteDataUtils.savePosRegisteData(getActivity(), SanyiSDK.registerData);
                                Toast.makeText(getActivity(), "验证成功，程序关闭，重启中...", Toast.LENGTH_SHORT).show();
                                editText.setText(editText.getText().toString());
                                SharePreferenceUtil.saveStringPreference(getActivity(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, editText.getText().toString());
                                SanyiSDK.currentUser=null;
                                SanyiSDK.registerData.setDeviceRegistered(false);
                                RegisteDataUtils.savePosRegisteData(getActivity(), SanyiSDK.registerData);
                                //重启
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        restartToUpdate();
                                    }
                                }, 1500);


                            }

                            @Override
                            public void uploadFile() {

                            }

                            @Override
                            public void onFail(String error) {
                                hostDialog.dismiss();
                                SanyiSDK.getSDK().setHost(SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, editText.getText().toString()));
                                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();


                            }
                        });
                    }
                });
                hostDialog.widthScale((float) 0.5);
                hostDialog.show();

            }
        });

        //拼音点餐设置
        final CheckBox orderMode = (CheckBox) settingView.findViewById(R.id.checkBox_local_setting_spell_mode);
        RelativeLayout relativeLayout_local_setting_default_order = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_default_order);
        relativeLayout_local_setting_default_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderMode.setChecked(!Restaurant.isSpellOrder);
            }
        });
        orderMode.setChecked(SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_SPELL_ORDER, false));
        orderMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Restaurant.isSpellOrder = isChecked;
                SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_SPELL_ORDER, isChecked);
            }
        });

        //外卖语音提示设置
        final CheckBox takeoutVoiceOff = (CheckBox) settingView.findViewById(R.id.checkBox_local_setting_takeout_voice_off);
        RelativeLayout relativeLayout_local_setting_takeout_voice = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_takeout_voice_off);
        relativeLayout_local_setting_takeout_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeoutVoiceOff.setChecked(!Restaurant.isTakeoutVoiceOff);
            }
        });
        takeoutVoiceOff.setChecked(SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_TAKEOUT_VOICE_OFF, false));
        takeoutVoiceOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Restaurant.isTakeoutVoiceOff = isChecked;
                SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_TAKEOUT_VOICE_OFF, isChecked);
            }
        });

        //串口打印机设置
        RelativeLayout shareComRelativeLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_com_share_print);
//        final TextView shareComTextView = (TextView) view.findViewById(R.id.textView_local_com_share_print);
//        String comPrinter = SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, "");
//        String comBaud = SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "");
//        if(comBaud.isEmpty())
//            comBaud = "9600";
//        if (!comPrinter.isEmpty()) {
//            shareComTextView.setText(comPrinter+":"+comBaud);
//        }
        shareComRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareComPrinter.show(getActivity(), LocalSettingFragment.this);
            }
        });

        //电子秤设置
        RelativeLayout eleBalanceRelativeLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_com_ele_balance);
        TextView eleBalanceTextView= (TextView) settingView.findViewById(R.id.textView_local_com_ele_balance);
        boolean open=SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_OPEN_BALANCE, false);
        if(open)
        {
            eleBalanceTextView.setText("已开启");
        }else
            eleBalanceTextView.setText("未开启");
        eleBalanceRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SettingActivity) activity).showBalanceSetting(true);

            }
        });

        //客显设置
        RelativeLayout ledPresentationLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_setting_ledpresentation);
        ledPortbaud = SharePreferenceUtil.getPreference(getContext(), SmartPosPrivateKey.ST_LOCAL_BAUDRATE_LED, "");
        ledPort = SharePreferenceUtil.getPreference(getContext(), SmartPosPrivateKey.ST_LOCAL_COM_LED, "");
        if (!ledPort.isEmpty() && !ledPortbaud.isEmpty()) {
            TextView ledportview = (TextView) settingView.findViewById(R.id.textView_local_setting_ledpresentation);
            ledportview.setText(ledPort + ":" + ledPortbaud);
        }
        ledPresentationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledPort = SharePreferenceUtil.getPreference(getContext(), SmartPosPrivateKey.ST_LOCAL_COM_LED, "");
                ledPortbaud = SharePreferenceUtil.getPreference(getContext(), SmartPosPrivateKey.ST_LOCAL_BAUDRATE_LED, "");
                SerialPortSetting portSetting = new SerialPortSetting(getActivity());
                portSetting.setSerialSettingListener(serialSettingListener);
                portSetting.setNormalListener(confirmListener);
                portSetting.show(ledPort, ledPortbaud);
            }
        });

        //usb打印机设置
        RelativeLayout shareRelativeLayout = (RelativeLayout) settingView.findViewById(R.id.relativeLayout_local_share_print);
//        String usbName = SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, "");
//        if (!usbName.isEmpty()) {
//            if (Restaurant.usbDriver.getAllPrinters() != null) {
//                if (Restaurant.usbDriver.findPrinter(usbName) != null) {
//                    TextView text = (TextView) view.findViewById(R.id.textView_local_share_print);
//                    text.setText(usbName);
//                }
//            }
//        }
        shareRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<UsbPrinter> printers = PrintUtil.getLocalPrint(getActivity());

                List<String> usbPrinters = new ArrayList<String>();
                if (printers.size() > 0) {

                    for (UsbPrinter printer : printers) {
                        usbPrinters.add(printer.getDescription());
                    }

                    final NormalDialog choosePrint = new NormalDialog(getActivity());
                    View chooseView = LayoutInflater.from(getActivity()).inflate(R.layout.printer_setting_dialog_view, null);
                    ListView list = (ListView) chooseView.findViewById(R.id.listViewPrinter);
                    PrinterAdapter adapter = null;
                    if (usbPrinters.size() > 0) {
                        adapter = new PrinterAdapter(usbPrinters, getActivity());

                    }

                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            choosePrint.dismiss();
                            UsbPrinter printer = printers.get(position);
                            if (printer != null) {
                                PrintUtil.shartPrint(getActivity(), printer);
                                updatePrinterSettingView();
                            }
                        }
                    });
                    choosePrint.title("选择打印机");
                    choosePrint.widthScale((float) 0.5);
                    choosePrint.content(chooseView);
                    choosePrint.setNormalListener(new NormalDialog.INormailDialogListener() {
                        @Override
                        public void onClickConfirm() {
                            SharePreferenceUtil.saveStringPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, "");//clear usb printer setting
                            SharePreferenceUtil.saveStringPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_USB_PRINTER_NAME, "");
                            updatePrinterSettingView();
                            choosePrint.dismiss();
                        }
                    });
                    choosePrint.show();
                } else {

                    Toast.makeText(activity, "找不到打印机", Toast.LENGTH_LONG).show();
                }
            }
        });

        updatePrinterSettingView();

        return settingView;
    }


    public void restartToUpdate() {
        Intent intent = new Intent(getActivity(), MainScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (null != loginFragment && null != loginFragment.inputPasswordFragment && null != loginFragment.inputPasswordFragment.printServiceConnection) {
//            try {
//                unbindService(loginFragment.inputPasswordFragment.printServiceConnection);
//            } catch (IllegalArgumentException e) {
//
//            }
//            PrintService printService = loginFragment.inputPasswordFragment.printServiceConnection.printService;
//            if (null != printService && isServiceWorked("com.rainbow.smartpos.service.PrintService")) {
//                printService.onStop();
//            }
//        }
        if (Restaurant.usbDriver != null && Restaurant.usbDriver.mUsbReceiver != null) {
            if (Restaurant.usbDriver.usbReceiverIsRegistered) {
                getActivity().getApplicationContext().unregisterReceiver(Restaurant.usbDriver.mUsbReceiver);
                Restaurant.usbDriver.usbReceiverIsRegistered = false;
            }
        }
        EventBus.getDefault().unregister(this);
        startActivity(intent);
        getActivity().finish();
    }

    public void updatePrinterSettingView() {
        TextView shareComTextView = (TextView) settingView.findViewById(R.id.textView_local_com_share_print);
        String comPrinter = SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, "");
        String comBaud = SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "");
        if (comBaud.isEmpty())
            comBaud = "9600";
        if (!comPrinter.isEmpty()) {
            shareComTextView.setText(comPrinter + ":" + comBaud);
        } else {
            shareComTextView.setText("");
        }

        String usbPrinterId = SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, "");
        String usbPrinterName = SharePreferenceUtil.getPreference(getActivity(), SmartPosPrivateKey.ST_LOCAL_USB_PRINTER_NAME, "");
        TextView usbPrinter = (TextView) settingView.findViewById(R.id.textView_local_share_print);
        usbPrinter.setText("");
//        List<UsbPrinter> printers = PrintUtil.getLocalPrint(getActivity());
        List<UsbPrinter> printers = PrintUtil.getLocalPrint(getActivity().getApplicationContext());
        if (printers != null && !printers.isEmpty()) {
            for (UsbPrinter p : printers) {
                if (p.getId().equals(usbPrinterId)) {
                    if (!usbPrinterName.isEmpty())
                        usbPrinter.setText(usbPrinterName);
                    else
                        usbPrinter.setText(usbPrinterId);
                    break;
                }
            }
        }
    }

    private void testLed(SerialPort serialPort) {
        try {
            serialPort.getOutputStream().write(SerialCommand.initCommand());
            serialPort.getOutputStream().write(SerialCommand.clearScreen());
            serialPort.getOutputStream().write(SerialCommand.showNums("000000"));
            serialPort.getOutputStream().write(SerialCommand.setCharsCommand(SerialCommand.CHARS_CHANGE_PRICE));
            serialPort.getOutputStream().flush();
        } catch (Exception e) {
        }
    }

    //led客显监听器
    private SerialPortSetting.SerialSettingListener serialSettingListener = new SerialPortSetting.SerialSettingListener() {

        @Override
        public void portChanged(String port) {
            if (port != null && !port.isEmpty()) {
                ledPort = port;
                if (!ledPortbaud.isEmpty()) {
                    try {
                        SerialPort serialPort = new SerialPort(new File(ledPort), Integer.parseInt(ledPortbaud));
                        testLed(serialPort);
                    } catch (Exception e) {
                    }
                    Toast.makeText(getContext(), "请检查数字客显显示内容", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void baudChanged(int baud) {
            if (baud > 0) {
                ledPortbaud = String.valueOf(baud);
                if (!ledPort.isEmpty()) {
                    try {
                        SerialPort serialPort = new SerialPort(new File(ledPort), baud);
                        testLed(serialPort);
                    } catch (Exception e) {
                    }
                    Toast.makeText(getContext(), "请检查数字客显显示内容", Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    //led客显监听器
    private NormalDialog.INormailDialogListener confirmListener = new NormalDialog.INormailDialogListener() {

        @Override
        public void onClickConfirm() {
            if (!ledPort.isEmpty() && !ledPortbaud.isEmpty()) {
                TextView ledportView = (TextView) settingView.findViewById(R.id.textView_local_setting_ledpresentation);
                ledportView.setText(ledPort + ":" + ledPortbaud);
                SharePreferenceUtil.saveStringPreference(getContext(), SmartPosPrivateKey.ST_LOCAL_COM_LED, ledPort);
                SharePreferenceUtil.saveStringPreference(getContext(), SmartPosPrivateKey.ST_LOCAL_BAUDRATE_LED, ledPortbaud);
            }
            Intent i = getContext().getPackageManager()
                    .getLaunchIntentForPackage(getContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    };

    private String ledPort = "";
    private String ledPortbaud = "";
}
