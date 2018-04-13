package com.rainbow.smartpos.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.serialport.SerialPortFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.serialport.SerialPort;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.login.InputPasswordFragment;
import com.rainbow.smartpos.login.SanyiPintServiceConnection;
import com.rainbow.smartpos.printer.BufferedEscPrinter;
import com.rainbow.smartpos.printer.ComPrinter;
import com.rainbow.smartpos.printer.EscUtil;
import com.rainbow.smartpos.printer.LocalPrinter;
import com.rainbow.smartpos.printer.SanyiUSBDriver;
import com.rainbow.smartpos.printer.SanyiUSBDriver.UsbPermissionListener;
import com.rainbow.smartpos.printer.UsbPrinter;
import com.rainbow.smartpos.service.PrintService;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PrinterSettingFragment1 {
    private Context mContext;
    private LinearLayout local_usb_printer_layout;
    private LinearLayout local_gorge_printer_layout;
    private LinearLayout setting_local_baud_layout;

    private TextView local_usb_printer;
    private TextView local_gorge_printer;
    private TextView setting_local_baud;

    private SerialPortFinder mSerialPortFinder = new SerialPortFinder();

    private List<UsbPrinter> UsbPrinters;
    private List<String> UsbPrinterNames;
    private List<String> ComPrinterPath;
    private List<String> ComPrinterDescription;

    private List<String> bauds;

    private PrinterAdapter mPrinterAdapter;
    private Dialog choosePrinterDialog;
    private MainScreenActivity activity;

    public PrinterSettingFragment1(Context mContext) {
        this.mContext = mContext;
    }


    private void initView() {


        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        Restaurant.usbDriver = new SanyiUSBDriver(usbManager, mContext, PendingIntent.getBroadcast(mContext, 0, new Intent(Restaurant.ACTION_USB_PERMISSION), 0),
                new UsbPermissionListener() {
                    @Override
                    public void onPermissionAssign() {
                        initUSBPrinter();
                    }

                    @Override
                    public void onPermissionReject() {

                    }
                });
        IntentFilter filter = new IntentFilter(Restaurant.ACTION_USB_PERMISSION);
        mContext.registerReceiver(Restaurant.usbDriver.mUsbReceiver, filter);
        initUSBPrinter();
        initGorgePrinter();

    }

    private void initUSBPrinter() {
        // TODO Auto-generated method stub
        UsbPrinters = Restaurant.usbDriver.getAllPrinters();
        UsbPrinterNames = new ArrayList<String>();
        for (int i = 0; i < UsbPrinters.size(); i++) {
            UsbPrinterNames.add(UsbPrinters.get(i).getId());
        }
        local_usb_printer.setText(SharePreferenceUtil.getPreference(activity, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, ""));

    }

    private void initGorgePrinter() {
        ComPrinterPath = new ArrayList<String>();
        ComPrinterDescription = new ArrayList<String>();
        bauds = new ArrayList<String>();

//        String[] printerDescriptions = mSerialPortFinder.getAllDevices();
//        String[] printerPath = mSerialPortFinder.getAllDevicesPath();
//        for (int i = 0; i < printerPath.length; i++) {
//            ComPrinterPath.add(printerPath[i]);
////            ComPrinterDescription.add(printerDescriptions[i]);
//            if (i < baud_array.length) {
//                bauds.add(baud_array[i]);
//            }
//        }
        local_gorge_printer.setText(SharePreferenceUtil.getPreference(activity, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, ""));
        setting_local_baud.setText(SharePreferenceUtil.getPreference(activity, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, ""));
    }


    public void showChoosePrinterDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View v = LayoutInflater.from(mContext).inflate(R.layout.printer_setting_dialog_view, null);
        ListView lv = (ListView) v.findViewById(R.id.listViewPrinter);
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                mPrinterAdapter.addSelected(position);
                mPrinterAdapter.notifyDataSetChanged();
                choosePrinterDialog.dismiss();
                itemOnClick(i, position);
            }
        });
        switch (i) {
            case 0:
                builder.setTitle(mContext.getResources().getString(R.string.setting_local_usb_printer));
//                mPrinterAdapter = new PrinterAdapter(UsbPrinterNames, mContext, R.layout.printer_setting_dialog_view_item, i);
                String usingUsbPrinterName = local_usb_printer.getText().toString();
                if (!usingUsbPrinterName.isEmpty()) {
                    for (int j = 0; j < UsbPrinterNames.size(); j++) {
                        if (usingUsbPrinterName.equals(UsbPrinterNames.get(j))) {
                            mPrinterAdapter.addSelected(j);
                        }
                    }
                }
                break;
            case 1:
                builder.setTitle(mContext.getResources().getString(R.string.setting_local_gorge_printer));
//                mPrinterAdapter = new PrinterAdapter(ComPrinterDescription, mContext, R.layout.printer_setting_dialog_view_item, i);
                String usingComPrinterPath = local_gorge_printer.getText().toString();
                if (!usingComPrinterPath.isEmpty()) {
                    for (int j = 0; j < ComPrinterPath.size(); j++) {
                        if (usingComPrinterPath.equals(ComPrinterPath.get(j))) {
                            mPrinterAdapter.addSelected(j);
                        }
                    }
                }
                break;
            case 2:
                builder.setTitle(mContext.getResources().getString(R.string.setting_local_baud));
//                mPrinterAdapter = new PrinterAdapter(bauds, mContext, R.layout.printer_setting_dialog_view_item, i);
                String buadStr = setting_local_baud.getText().toString();
                if (!buadStr.isEmpty()) {
                    for (int j = 0; j < bauds.size(); j++) {
                        if (buadStr.equals(bauds.get(j))) {
                            mPrinterAdapter.addSelected(j);
                        }
                    }
                }
                break;

            default:
                break;
        }
        lv.setAdapter(mPrinterAdapter);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                choosePrinterDialog.dismiss();
            }
        });
        builder.setView(v);
        choosePrinterDialog = builder.create();
        choosePrinterDialog.show();

    }

    public void itemOnClick(int i, final int position) {
        switch (i) {
            case 0:
                local_usb_printer.setText(UsbPrinterNames.get(position));
                final UsbPrinter usbPrinter = Restaurant.usbDriver.findPrinter(UsbPrinterNames.get(position));
                if (usbPrinter != null) {
                    SharePreferenceUtil.saveStringPreference(activity, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, local_usb_printer.getText().toString());
                    Restaurant.usbDriver.setUsingPrinter(usbPrinter);
                    byte[] content = getPrinterTestContent();
                    usbPrinter.print(content, content.length);
                    local_gorge_printer.setText("");
                    showUSBPrintTestDialog(usbPrinter, UsbPrinterNames.get(position));
                }
                break;
            case 1:
                SerialPort port;
                try {
                    if (!setting_local_baud.getText().toString().isEmpty()) {
                        int baud = Integer.parseInt(setting_local_baud.getText().toString());
                        port = new SerialPort(new File(ComPrinterPath.get(position)), baud);
                    } else {
                        port = new SerialPort(new File(ComPrinterPath.get(position)), Integer.parseInt(bauds.get(0)));
                    }
                    ComPrinter printer = new ComPrinter(port);
                    byte[] content = getPrinterTestContent();
                    printer.print(content, content.length);
                    local_gorge_printer.setText(ComPrinterPath.get(position));
                    local_usb_printer.setText("");
                    showPrintTestDialog(printer, ComPrinterPath.get(position));
                    SharePreferenceUtil.saveStringPreference(activity, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, local_gorge_printer.getText().toString());
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(mContext, "打开本地串口失败", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(mContext, "打开本地串口失败", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                setting_local_baud.setText(bauds.get(position));
                SharePreferenceUtil.saveStringPreference(activity, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, bauds.get(position));
                break;
            default:
                break;
        }
    }

    private void showPrintTestDialog(final LocalPrinter printer, final String value) {
        InputPasswordFragment.printServiceConnection = new SanyiPintServiceConnection(printer, new SanyiPintServiceConnection.PrinterServiceStartListener() {
            @Override
            public void onPrintServiceStartSuccess() {
                // initRemotePrinterPreference();
            }

            @Override
            public void onPrintServiceStartFailed() {
            }
        });
        Intent intent = new Intent(activity, PrintService.class);
        activity.bindService(intent, activity.loginFragment.inputPasswordFragment.printServiceConnection, Context.BIND_AUTO_CREATE);
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setMessage("打印机设置成功");
        builder.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void showUSBPrintTestDialog(final UsbPrinter printer, final String value) {
        Dialog dialog = null;
        activity.loginFragment.inputPasswordFragment.printServiceConnection = new SanyiPintServiceConnection(printer, new SanyiPintServiceConnection.PrinterServiceStartListener() {
            @Override
            public void onPrintServiceStartSuccess() {
                // initRemotePrinterPreference();
            }

            @Override
            public void onPrintServiceStartFailed() {

            }
        });
        Restaurant.usbDriver.setUsingPrinter(printer);
        Intent intent = new Intent(activity, PrintService.class);
        activity.bindService(intent, activity.loginFragment.inputPasswordFragment.printServiceConnection, Context.BIND_AUTO_CREATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("打印机设置成功");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                local_usb_printer.setText(value);
                SharePreferenceUtil.saveStringPreference(activity, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, value);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                local_gorge_printer.setText("");
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private byte[] getPrinterTestContent() {
        BufferedEscPrinter buf = new BufferedEscPrinter(Charset.forName("GBK"));
        EscUtil escUtil = new EscUtil();
        escUtil.escInit(buf);
        escUtil.printString("打印机连接成功！", buf);
        escUtil.feed(5, buf);
        escUtil.cut(buf);
        byte[] content = buf.getBytes();
        return content;
    }


}
//
//    @Override
//    public void onDestroy() {
//        activity.unregisterReceiver(Restaurant.usbDriver.mUsbReceiver);
//        super.onDestroy();
//    }
