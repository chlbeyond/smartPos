package com.rainbow.smartpos.mainframework;

import android.content.Context;
import android.serialport.SerialPortFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.serialport.SerialPort;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.activity.settingfragment.LocalSettingFragment;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.printer.BufferedEscPrinter;
import com.rainbow.smartpos.printer.ComPrinter;
import com.rainbow.smartpos.printer.EscUtil;
import com.rainbow.smartpos.setting.PrinterAdapter;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/4/28.
 */
public class ShareComPrinter {

    static String[] baud_array;

    private static byte[] getPrinterTestContent() {
        BufferedEscPrinter buf = new BufferedEscPrinter(Charset.forName("GBK"));
        EscUtil escUtil = new EscUtil();
        escUtil.escInit(buf);
        escUtil.printString("打印机连接成功！", buf);
        escUtil.feed(5, buf);
        escUtil.cut(buf);
        byte[] content = buf.getBytes();
        return content;
    }

    private static boolean testComPrinter(String portname, String baudRate)
    {
        SerialPort port = null;
        ComPrinter printer = null;
        try {
//            System.out.println("open port");
            port = new SerialPort(new File(portname), Integer.parseInt(baudRate));
            printer = new ComPrinter(port);
            byte[] content = getPrinterTestContent();
//            System.out.println("begin write port");
            printer.print(content, content.length);
//            System.out.println("end write port");
//            PrintUtil.shareComPrint(context, printer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(printer != null)
                printer.release();
            else if(port != null)
                port.close();
        }
        return true;
    }

    public static void  show(final Context context, final LocalSettingFragment settingFragment) {
        baud_array = context.getResources().getStringArray(R.array.baudrates_value);
        final NormalDialog dialog = new NormalDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_choose_com_printer, null);
        dialog.content(view);
        RelativeLayout comPrinterRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout_com_printer);

        final TextView comTextView = (TextView) view.findViewById(R.id.textView_com_printer);
        String comPrinter = SharePreferenceUtil.getPreference(context, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, "");
        if (!comPrinter.isEmpty()) {
            comTextView.setText(comPrinter);
        }
        final TextView comBaudRateTextView = (TextView) view.findViewById(R.id.textView_com_baud_rate);
        String comBaudRate = SharePreferenceUtil.getPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "");
        if (!comBaudRate.isEmpty()) {
            comBaudRateTextView.setText(comBaudRate);
        }
        comPrinterRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NormalDialog comDialog = new NormalDialog(context);
                View comView = LayoutInflater.from(context).inflate(R.layout.printer_setting_dialog_view, null);
                ListView listView = (ListView) comView.findViewById(R.id.listViewPrinter);
                SerialPortFinder finder = new SerialPortFinder();
                final List<String> serialPort = finder.getAllDevicesPath();
                PrinterAdapter adapter = new PrinterAdapter(serialPort, context);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        comDialog.dismiss();
                        comTextView.setText(serialPort.get(position));
                        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, serialPort.get(position));

                        String portName = serialPort.get(position);
                        String baudRate = SharePreferenceUtil.getPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "");
                        if(baudRate == null ||  baudRate.isEmpty())
                            baudRate = baud_array[0];
                        if(!testComPrinter(portName, baudRate)) {
                            Toast.makeText(context, "打开本地串口失败", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "请检查打印机是否打印测试单", Toast.LENGTH_LONG).show();
                        }

//                        try {
//                            SerialPort port = new SerialPort(new File(serialPort.get(position)), Integer.parseInt(SharePreferenceUtil.getPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "").isEmpty() ? baud_array[0] : SharePreferenceUtil.getPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "")));
//
//
//                            ComPrinter printer = null;
//                            try {
//                                printer = new ComPrinter(port);
//                                if (printer != null) {
//                                    byte[] content = getPrinterTestContent();
//                                    printer.print(content, content.length);
//                                    PrintUtil.shareComPrint(context, printer);
//                                }
//                            } catch (NullPointerException e) {
//
//
//                                Toast.makeText(context, "打开本地串口失败", Toast.LENGTH_LONG).show();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Toast.makeText(context, "打开本地串口失败", Toast.LENGTH_LONG).show();
//                        }
                    }
                });
                comDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, "");
                        comTextView.setText("");
                        comDialog.dismiss();
                    }
                });
                comDialog.title("选择串口");
                comDialog.widthScale((float) 0.5);
                comDialog.content(comView);
                comDialog.show();
            }
        });
        RelativeLayout comBaudRateRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout_com_baud_rate);
        comBaudRateRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NormalDialog comDialog = new NormalDialog(context);
                View comView = LayoutInflater.from(context).inflate(R.layout.printer_setting_dialog_view, null);
                ListView listView = (ListView) comView.findViewById(R.id.listViewPrinter);
                final List<String> baudList = new ArrayList<String>();
                for (int i = 0; i < baud_array.length; i++) {
                    baudList.add(baud_array[i]);
                }
                PrinterAdapter adapter = new PrinterAdapter(baudList, context);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        comDialog.dismiss();
                        String baudRate = baudList.get(position);
                        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, baudRate);

                        String portName = SharePreferenceUtil.getPreference(context, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, "");
                        if(!portName.isEmpty()) {
                            if (!testComPrinter(portName, baudRate)) {
                                Toast.makeText(context, "打开本地串口失败", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "请检查打印机是否打印测试单", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "请先选择串口", Toast.LENGTH_LONG).show();
                        }
                        comBaudRateTextView.setText(baudRate);

                    }
                });
                comDialog.content(comView);
                comDialog.widthScale((float) 0.5);
                comDialog.title("选择串口波特率");

                comDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "");
                        comBaudRateTextView.setText("");
                        comDialog.dismiss();
                    }
                });
                comDialog.show();
            }
        });
        dialog.title("串口设置");
        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                dialog.dismiss();
            }
        });
        dialog.widthScale((float) 0.5);
        dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
            @Override
            public void onClickConfirm() {
                String portName = SharePreferenceUtil.getPreference(context, SmartPosPrivateKey.ST_LOCAL_COM_PRINTER, "");
                String baudRate = SharePreferenceUtil.getPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "");
                if(portName == null || portName.isEmpty()) {
                    SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, "");
                } else {
                    if(baudRate == null || baudRate.isEmpty()) {
                        baudRate = baud_array[0];
                        SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_BAUDRATE_PRINTER, baudRate);
                    }
                    try {
                        SerialPort port = new SerialPort(new File(portName), Integer.parseInt(baudRate));
                        ComPrinter printer = new ComPrinter(port);
                        PrintUtil.shareComPrint(context, printer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SharePreferenceUtil.saveStringPreference(context, SmartPosPrivateKey.ST_LOCAL_USB_PRINTER, ""); //clear usb printer setting
                }
                settingFragment.updatePrinterSettingView();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
