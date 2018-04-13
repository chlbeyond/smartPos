package com.rainbow.smartpos.mainframework;

import android.content.Context;
import android.serialport.SerialPortFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.setting.PrinterAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2017/1/19.
 */

public class SerialPortSetting {

    private Context context;
    private String[] baud_array;
    private SerialSettingListener listener;
    private NormalDialog.INormailDialogListener confirmListener;

    public interface SerialSettingListener {
        public void portChanged(String portName);
        public void baudChanged(int baud);
    };

    public SerialPortSetting(Context context)
    {
        this.context = context;
        baud_array = context.getResources().getStringArray(R.array.baudrates_value);
    }

    public void setSerialSettingListener(SerialSettingListener l) {
        this.listener = l;
    }

    public void setNormalListener(NormalDialog.INormailDialogListener l) {
        this.confirmListener = l;
    }

    public void  show(String portName, String baud) {

        final NormalDialog dialog = new NormalDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_choose_com_printer, null);
        dialog.content(view);
        RelativeLayout comPrinterRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout_com_printer);

        final TextView comTextView = (TextView) view.findViewById(R.id.textView_com_printer);
        if (portName != null && !portName.isEmpty()) {
            comTextView.setText(portName);
        }
        final TextView comBaudRateTextView = (TextView) view.findViewById(R.id.textView_com_baud_rate);
        if (baud != null && !baud.isEmpty()) {
            comBaudRateTextView.setText(baud);
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
                        String portName = serialPort.get(position);
                        comTextView.setText(portName);
                        if(listener != null)
                            listener.portChanged(portName);
                    }
                });
                comDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
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
                        comBaudRateTextView.setText(baudRate);
                        if(listener != null)
                            listener.baudChanged(Integer.parseInt(baudRate));
                    }
                });
                comDialog.content(comView);
                comDialog.widthScale((float) 0.5);
                comDialog.title("选择串口波特率");

                comDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
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
                dialog.dismiss();
                if(confirmListener != null)
                    confirmListener.onClickConfirm();
            }
        });
        dialog.show();
    }
}
