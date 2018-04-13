package com.rainbow.smartpos.activity.settingfragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.minipos.device.SerialPort;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.activity.SettingActivity;
import com.rainbow.smartpos.install.Constants;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by administrator on 2018/1/23.
 */

public class SetBalanceFragment extends Fragment implements View.OnClickListener {
    public Context activity;
    private View settingView;


    private TextView dialogSetBalanceExitTv;
    private TextView dialogSetBalanceOpenCloseTv;
    private TextView dialogSetBalanceSelPortTv;
    private TextView dialogSetBalanceBaudrateTv;
    private TextView dialogSetBalanceDataBitsTv;
    private TextView dialogSetBalanceStopBitsTv;
    private TextView dialogSetBalanceParityTv;
    private TextView dialogSetBalanceFlowControlTv;
    private CheckBox dialogSetBalanceSwitchCb;

    private RadioGroup dialogSetBalanceFormatRg;
    private RadioButton dialogSetBalanceTextRb;
    private RadioButton dialogSetBalanceHexRb;

    private TextView dialogSetBalanceClearTv;
    private EditText dialogSetBalanceEt;

    boolean mRecvText = true;


    private final Object mRecvLock = new Object();
    private final ByteArrayOutputStream mRecvStream = new ByteArrayOutputStream();


    SerialPort mSerialPort = null;
    final SerialPortDataReceiver mDataReceiver = new SerialPortDataReceiver();

    private static final int MSG_ON_RECIEVED = 1;
    private static final int MSG_ON_RECV_STOP = 2;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ON_RECIEVED:
                    updateRecvView();
                    break;
                case MSG_ON_RECV_STOP:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void postOnReceived(byte[] data) throws IOException {
        synchronized (mRecvLock) {
            mRecvStream.write(data);
        }
        mHandler.obtainMessage(MSG_ON_RECIEVED).sendToTarget();
    }

    private void postOnRecvThreadStop() {
        mHandler.obtainMessage(MSG_ON_RECV_STOP).sendToTarget();
    }


    private class SerialPortDataReceiver implements Runnable {

        volatile Thread mThread = null;
        private InputStream mInput = null;
        volatile boolean mExitRequest = false;

        public synchronized boolean isRunning() {
            return mThread != null;
        }

        public synchronized void start(InputStream is) {
            if (mThread != null)
                return;

            mInput = is;
            mExitRequest = false;

            mThread = new Thread(this);
            mThread.start();
        }

        public synchronized void stop() {
            if (mThread == null)
                return;

            mExitRequest = true;
            if (mInput != null) {
                try {
                    mInput.close();
                } catch (Throwable e) {
                }
                mInput = null;
            }


            join();
        }

        public void join() {
            while (mThread != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void run() {
            Log.d("", "receiver thread : start...");
            try {
                read();
            } finally {
                mInput = null;
                mThread = null;

                Log.d("", "receiver thread : stop...");
                postOnRecvThreadStop();
            }
        }

        private void read() {
            int size;
            final byte[] data = new byte[128];

            try {
                while (mInput != null && !mExitRequest) {
                    if (mInput.available() > 0) {
                        size = mInput.read(data);
                        Log.d("", "recv : " + size + " bytes ");
                        if (size > 0) {
                            ByteBuffer bb = ByteBuffer.allocate(size);
                            bb.put(data, 0, size);
                            //Log.d(TAG, "data : " + bytesToHexString(bb.array()));
                            postOnReceived(bb.array());
                        } else {
                            break;
                        }
                    } else {
                        Thread.sleep(50);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingView = inflater.inflate(R.layout.fragment_set_balance, container, false);
        activity = getActivity();

        dialogSetBalanceExitTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_exit);
        dialogSetBalanceSelPortTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_port);
        dialogSetBalanceBaudrateTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_baudrate);
        dialogSetBalanceDataBitsTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_databits);
        dialogSetBalanceStopBitsTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_stopbits);
        dialogSetBalanceParityTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_parity);
        dialogSetBalanceFlowControlTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_flowcontrol);
        dialogSetBalanceClearTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_clear);
        dialogSetBalanceOpenCloseTv = (TextView) settingView.findViewById(R.id.tv_dialog_set_balance_switch);
        dialogSetBalanceSwitchCb = (CheckBox) settingView.findViewById(R.id.cb_dialog_set_balance_switch);

        dialogSetBalanceFormatRg = (RadioGroup) settingView.findViewById(R.id.rg_dialog_set_balance_format);
        dialogSetBalanceTextRb = (RadioButton) settingView.findViewById(R.id.rb_dialog_set_balance_text);
        dialogSetBalanceHexRb = (RadioButton) settingView.findViewById(R.id.rb_dialog_set_balance_hex);

        dialogSetBalanceEt = (EditText) settingView.findViewById(R.id.et_dialog_set_balance);
        dialogSetBalanceEt.setEnabled(false);

        dialogSetBalanceExitTv.setOnClickListener(this);
        dialogSetBalanceSelPortTv.setOnClickListener(this);
        dialogSetBalanceBaudrateTv.setOnClickListener(this);
        dialogSetBalanceDataBitsTv.setOnClickListener(this);
        dialogSetBalanceStopBitsTv.setOnClickListener(this);
        dialogSetBalanceParityTv.setOnClickListener(this);
        dialogSetBalanceFlowControlTv.setOnClickListener(this);
        dialogSetBalanceClearTv.setOnClickListener(this);
        dialogSetBalanceOpenCloseTv.setOnClickListener(this);


        boolean open=SharePreferenceUtil.getBooleanPreference(getActivity(), SmartPosPrivateKey.ST_OPEN_BALANCE, false);
        if(open)
        {
            Constants.openBalance=true;
            dialogSetBalanceSwitchCb.setChecked(true);
        }else
        {
            Constants.openBalance=false;
            dialogSetBalanceSwitchCb.setChecked(false);
        }

        dialogSetBalanceSwitchCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Constants.openBalance = true;
                    SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_OPEN_BALANCE, isChecked);
                } else {
                    Constants.openBalance = false;
                    SharePreferenceUtil.saveBooleanPreference(getActivity(), SmartPosPrivateKey.ST_OPEN_BALANCE, isChecked);
                }
            }
        });

//        dialogSetBalanceFormatRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                onRecvTypeChanged(checkedId == R.id.rb_dialog_set_balance_text);
//            }
//        });


        return settingView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_set_balance_exit:
                if (mSerialPort != null) {
                    mSerialPort.close();
                    mSerialPort = null;
                    mDataReceiver.stop();
                }
                ((SettingActivity) activity).showBalanceSetting(false);
                break;
            case R.id.tv_dialog_set_balance_port:
                selectSerialPort();
                break;
            case R.id.tv_dialog_set_balance_baudrate:
                selectBaudrate();
                break;
            case R.id.tv_dialog_set_balance_databits:
                selectDataBits();
                break;
            case R.id.tv_dialog_set_balance_stopbits:
                selectStopBits();
                break;
            case R.id.tv_dialog_set_balance_parity:
                selectParity();
                break;
            case R.id.tv_dialog_set_balance_flowcontrol:
                selectFlowControl();
                break;
            case R.id.tv_dialog_set_balance_clear:
                clearRecv();
                break;
            case R.id.tv_dialog_set_balance_switch:
                openCloseSerialPort();
                break;
        }
    }

    void openCloseSerialPort() {
        if (mSerialPort == null) {
            if (Constants.mPort == null) {
                Toast.makeText(activity, "请先选择端口", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Constants.mBaudrate == 0) {
                Toast.makeText(activity, "请先选择波特率", Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                mSerialPort = SerialPort.open(Constants.mPort, Constants.mBaudrate,
                        Constants.mParity, Constants.mDataBits, Constants.mStopBits, Constants.mFlowControl);

                dialogSetBalanceOpenCloseTv.setText("停止测试");
                mDataReceiver.start(mSerialPort.getInputStream());
            } catch (Throwable e) {
                e.printStackTrace();
                if (mSerialPort != null) {
                    mSerialPort.close();
                    mSerialPort = null;
                }
            }
        } else {
            mSerialPort.close();
            mSerialPort = null;

            dialogSetBalanceOpenCloseTv.setText("开始测试");

            mDataReceiver.stop();
        }
    }

    public void closePort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;

            mDataReceiver.stop();
        }
    }

    final String[] strFC = {"None", "Hardware", "Xon/Xoff"};
    final int[] intFC = {SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_RTSCTS, SerialPort.FLOWCONTROL_XONXOFF};

    void selectFlowControl() {
        String def = strFC[0];
        for (int i = 0; i < intFC.length; i++)
            if (Constants.mFlowControl == intFC[i])
                def = strFC[i];
        new RadioGroupDialog(activity, "FlowControl", strFC, def) {
            @Override
            protected void onOk() {
                String sel = getSelection();
                for (int i = 0; i < intFC.length; i++)
                    if (sel.equals(strFC[i]))
                        Constants.mFlowControl = intFC[i];
                dialogSetBalanceFlowControlTv.setText("FlowControl:" + sel);
                Log.d("", "sel flowcontrol : " + sel + ", " + Constants.mFlowControl);
            }
        };
    }

    final String[] strParity = {"None", "Even", "Odd"};
    final int[] intParity = {SerialPort.PARITY_NONE, SerialPort.PARITY_EVEN, SerialPort.PARITY_ODD};

    void selectParity() {
        String def = strParity[0];
        for (int i = 0; i < intParity.length; i++)
            if (Constants.mParity == intParity[i])
                def = strParity[i];
        new RadioGroupDialog(activity, "parity", strParity, def) {
            @Override
            protected void onOk() {
                String sel = getSelection();
                for (int i = 0; i < intParity.length; i++)
                    if (sel.equals(strParity[i]))
                        Constants.mParity = intParity[i];
                dialogSetBalanceParityTv.setText("Parity:" + sel);
                Log.d("", "sel parity : " + sel + ", " + Constants.mParity);
            }
        };
    }

    final String[] strStopbits = {"1", "1.5", "2"};
    final int[] intStopbits = {SerialPort.STOPBITS_1, SerialPort.STOPBITS_1_5, SerialPort.STOPBITS_2};

    void selectStopBits() {
        String def = strStopbits[0];
        for (int i = 0; i < intStopbits.length; i++)
            if (Constants.mStopBits == intStopbits[i])
                def = strStopbits[i];
        new RadioGroupDialog(activity, "stopbits", strStopbits, def) {
            @Override
            protected void onOk() {
                String sel = getSelection();
                for (int i = 0; i < intStopbits.length; i++)
                    if (sel.equals(strStopbits[i]))
                        Constants.mStopBits = intStopbits[i];
                dialogSetBalanceStopBitsTv.setText("StopBits:" + sel);
                Log.d("", "sel stopbits : " + sel + ", " + Constants.mStopBits);
            }
        };
    }

    final String[] databits = {"5", "6", "7", "8"};

    void selectDataBits() {
        new RadioGroupDialog(activity, "databits", databits, String.valueOf(Constants.mDataBits)) {
            @Override
            protected void onOk() {
                Constants.mDataBits = Integer.valueOf(getSelection());
                dialogSetBalanceDataBitsTv.setText("DataBits:" + Constants.mDataBits);
                Log.d("", "sel databits : " + getSelection() + ", " + Constants.mDataBits);
            }
        };
    }

    final String[] baudrates = {"9600", "19200", "38400", "115200"};

    void selectBaudrate() {
        new RadioGroupDialog(activity, "baudrate", baudrates, String.valueOf(Constants.mBaudrate)) {
            @Override
            protected void onOk() {
                Constants.mBaudrate = Integer.valueOf(getSelection());
                dialogSetBalanceBaudrateTv.setText("Baudrate:" + Constants.mBaudrate);
                Log.d("", "sel baudrates : " + getSelection() + ", " + Constants.mBaudrate);
            }
        };
    }

    static String[] getSerialPorts() throws Throwable {
        return SerialPort.listDevices();
    }

    void selectSerialPort() {
        String[] ports;
        try {
            ports = getSerialPorts();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        new RadioGroupDialog(activity, "serial port", ports, Constants.mPort) {
            @Override
            protected void onOk() {
                Constants.mPort = this.getSelection();
                dialogSetBalanceSelPortTv.setText("Port:" + Constants.mPort);
                Log.d("", "sel port : " + Constants.mPort);
            }
        };
    }

//    void onRecvTypeChanged(boolean recvText) {
//        if (mRecvText == recvText)
//            return;
//        mRecvText = recvText;
//        updateRecvView();
//    }

    void updateRecvView() {
        String txt;
        if (mRecvText) {
            synchronized (mRecvLock) {
                txt = mRecvStream.toString();
            }
        } else {
            synchronized (mRecvLock) {
                txt = bytesToHexString(mRecvStream.toByteArray());
            }
        }

        dialogSetBalanceEt.setText(txt);
    }

    void clearRecv() {
        synchronized (mRecvLock) {
            mRecvStream.reset();
        }
        dialogSetBalanceEt.getText().clear();
    }

    static String bytesToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
            sb.append(String.format("%02X ", data[i]));
        return sb.toString();
    }

    static abstract class AbstractInputDialog {
        protected final View mView;

        protected abstract void onOk();

        protected abstract View createView(Context context);

        public AbstractInputDialog(Context context, String title) {
            mView = createView(context);
            (new AlertDialog.Builder(context))
                    .setTitle(title)
                    .setView(mView)
                    .setInverseBackgroundForced(true)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onOk();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    static abstract class RadioGroupDialog extends AbstractInputDialog {

        private String mSelected = null;
        //private RadioGroup mRadioGroup;
        private ScrollView mView;
        private RadioButton[] mBtns;

        public RadioGroupDialog(Context context, String title, String[] items, String def) {
            super(context, title);

            if (items == null || items.length == 0)
                return;

            mBtns = new RadioButton[items.length];
            for (int i = 0; i < items.length; i++) {
                mBtns[i] = new RadioButton(context);
                mBtns[i].setText(items[i]);
                if (items[i].equals(def)) {
                    mBtns[i].setChecked(true);
                    mSelected = def;
                }
                mBtns[i].setOnClickListener(onClick);
            }

            TableLayout tl = new TableLayout(context);
            final int num_row = 4;

            TableRow[] tr = new TableRow[num_row];
            for (int i = 0; i < num_row; i++) {
                tr[i] = new TableRow(context);
                tl.addView(tr[i]);
            }

            for (int i = 0; i < items.length; i++) {
                final int row = i % num_row;
                tr[row].addView(mBtns[i]);
            }

            if (mSelected == null) {
                mBtns[0].setChecked(true);
                mSelected = items[0];
            }

            mView.addView(tl);
        }

        @Override
        protected View createView(Context context) {
            return (mView = new ScrollView(context));
        }

        public String getSelection() {
            return mSelected;
        }

        final View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                mSelected = rb.getText().toString();
                for (int i = 0; i < mBtns.length; i++)
                    mBtns[i].setChecked(rb == mBtns[i]);
            }
        };
    }
}
