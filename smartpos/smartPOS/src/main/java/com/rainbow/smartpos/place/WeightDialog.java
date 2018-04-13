package com.rainbow.smartpos.place;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.minipos.device.SerialPort;
import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.install.Constants;
import com.rainbow.smartpos.util.Listener.OnChangePriceListener;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.utils.OrderUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class WeightDialog {
    public static final int CHANGE_PRICE = 0;
    public static final int CHANGE_WEIGHT = 1;
    public static final int CUSTOMER_PRICE = 2;//时价
    public static final int CHANGE_COUNT = 3; //设置数量

    private MyDialog dialog;
    private String value = "";

    TextView titleTv;
    TextView hintTv;
    TextView contentTv;
    TextView sure;
    ImageButton cancel;

    static Button btn1;
    static Button btn2;
    static Button btn3;
    static Button btn4;
    static Button btn5;
    static Button btn6;
    static Button btn7;
    static Button btn8;
    static Button btn9;
    static Button btn0;
    static ImageButton btnC;
    static Button btnPoint;

    Context activity;
    String title = "";
    String defaultNumber = "";
    private int flag;
    private boolean isFirstInput = true;

    private OnChangePriceListener listener;
    final SerialPortDataReceiver mDataReceiver = new SerialPortDataReceiver();

    private static final int MSG_ON_RECIEVED = 1;
    private static final int MSG_ON_RECV_STOP = 2;

    private final Object mRecvLock = new Object();
    private final ByteArrayOutputStream mRecvStream = new ByteArrayOutputStream();


    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ON_RECIEVED:
                    updateRecvView();
                    break;
                case MSG_ON_RECV_STOP:
                    //Toast.makeText(MainActivity,this, "", duration)
                    break;
            }
            super.handleMessage(msg);
        }
    };

    void updateRecvView() {
        //StringBuilder sb = new StringBuilder();
        String txt;
        synchronized (mRecvLock) {
//				for(byte[] d : mRecvData)
//					sb.append(new String(d));
            txt = mRecvStream.toString();
        }

        //mRecvView.setText(sb.toString());
        if (txt.length() == 18) {
            txt = txt.substring(7, 13);
            contentTv.setText(Double.parseDouble(txt) + "");
            value = Double.parseDouble(txt) + "";
        }
    }


    SerialPort mSerialPort = null;

    public void show(Context activity, String title, String defaultNumber, int flag, OnChangePriceListener listener) {
        this.activity = activity;
        this.title = title;
        this.defaultNumber = defaultNumber;
        this.listener = listener;
        this.flag = flag;

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.weight_dialog_layout, null, false);
        dialog = new MyDialog(activity, (int) (MainScreenActivity.getScreenWidth() * 0.4), (int) (MainScreenActivity.getScreenHeight() * 0.9), view, R.style.OpDialogTheme);

        titleTv = (TextView) view.findViewById(R.id.op_dialog_title);
        hintTv = (TextView) view.findViewById(R.id.op_dialog_hint);
        contentTv = (TextView) view.findViewById(R.id.op_dialog_content);
        sure = (TextView) view.findViewById(R.id.sure_btn);
        cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);

        initView();

        btn1 = (Button) view.findViewById(R.id.buttonNumPad1);
        btn2 = (Button) view.findViewById(R.id.buttonNumPad2);
        btn3 = (Button) view.findViewById(R.id.buttonNumPad3);
        btn4 = (Button) view.findViewById(R.id.buttonNumPad4);
        btn5 = (Button) view.findViewById(R.id.buttonNumPad5);
        btn6 = (Button) view.findViewById(R.id.buttonNumPad6);
        btn7 = (Button) view.findViewById(R.id.buttonNumPad7);
        btn8 = (Button) view.findViewById(R.id.buttonNumPad8);
        btn9 = (Button) view.findViewById(R.id.buttonNumPad9);
        btn0 = (Button) view.findViewById(R.id.buttonNumPad0);
        btnC = (ImageButton) view.findViewById(R.id.buttonNumPadC);
        btnPoint = (Button) view.findViewById(R.id.buttonNumPadPoint);
        if (flag == CHANGE_COUNT)
            btnPoint.setText("<-");

        sure.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        btnC.setOnClickListener(onClickListener);
        btnPoint.setOnClickListener(onClickListener);
        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
        btn5.setOnClickListener(onClickListener);
        btn6.setOnClickListener(onClickListener);
        btn7.setOnClickListener(onClickListener);
        btn8.setOnClickListener(onClickListener);
        btn9.setOnClickListener(onClickListener);
        btn0.setOnClickListener(onClickListener);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mSerialPort != null) {
                    mSerialPort.close();
                    mSerialPort = null;

                    mDataReceiver.stop();
                }
            }
        });

        dialog.show();
        if (flag == CHANGE_WEIGHT) {
            getBalance();
        }
    }

    /**
     * 尝试获取电子秤
     */
    private void getBalance() {
        if (SharePreferenceUtil.getBooleanPreference(activity, SmartPosPrivateKey.ST_OPEN_BALANCE, false)) {
            if (Constants.mPort != null && Constants.mBaudrate != 0) {
                try {
//				SerialPortConfiguration cfg = new SerialPortConfiguration();
//				cfg.port = mPort;
//				cfg.baudrate = mBaudrate;
//				cfg.databits = mDataBits;
//				cfg.stopbits = mStopBits;
//				cfg.parity = mParity;
//				cfg.flowControl = SerialPort.FLOWCONTROL_RTSCTS;

                    mSerialPort = SerialPort.open(Constants.mPort, Constants.mBaudrate,
                            Constants.mParity, Constants.mDataBits, Constants.mStopBits, Constants.mFlowControl);

                    mDataReceiver.start(mSerialPort.getInputStream());
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (mSerialPort != null) {
                        mSerialPort.close();
                        mSerialPort = null;
                    }
                }
            }
        }
    }

    private void initView() {
        if (title.isEmpty()) {
            titleTv.setVisibility(View.INVISIBLE);
        } else {
            titleTv.setVisibility(View.VISIBLE);
            titleTv.setText(title);
        }

        if (defaultNumber.isEmpty()) {
            hintTv.setVisibility(View.INVISIBLE);
        } else {
            hintTv.setVisibility(View.VISIBLE);
            switch (flag) {
                case CHANGE_PRICE:
                    hintTv.setText("改价前:  " + OrderUtil.dishPriceFormatter.format(Double.parseDouble(defaultNumber)));
                    break;
                case CHANGE_WEIGHT:
                    hintTv.setText("改重前:  " + defaultNumber);
                    break;
                case CUSTOMER_PRICE:
                    hintTv.setText("默认价格:	" + OrderUtil.dishPriceFormatter.format(Double.parseDouble(defaultNumber)));
                    break;
                case CHANGE_COUNT:
                    hintTv.setText("原数量:	" + defaultNumber);
                    break;
                default:
                    break;
            }
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.sure_btn:
                    buttonSureClick();
                    break;
                case R.id.iv_close_dialog:
                    buttonCancelClick();
                    break;
                case R.id.buttonNumPad0:
                    appendNumber("0");
                    break;
                case R.id.buttonNumPad1:
                    appendNumber("1");
                    break;
                case R.id.buttonNumPad2:
                    appendNumber("2");
                    break;
                case R.id.buttonNumPad3:
                    appendNumber("3");
                    break;
                case R.id.buttonNumPad4:
                    appendNumber("4");
                    break;
                case R.id.buttonNumPad5:
                    appendNumber("5");
                    break;
                case R.id.buttonNumPad6:
                    appendNumber("6");
                    break;
                case R.id.buttonNumPad7:
                    appendNumber("7");
                    break;
                case R.id.buttonNumPad8:
                    appendNumber("8");
                    break;
                case R.id.buttonNumPad9:
                    appendNumber("9");
                    break;
                case R.id.buttonNumPadC:
                    value = "";
                    contentTv.setText("");
                    break;
                case R.id.buttonNumPadPoint:
                    if (flag == CHANGE_COUNT) {
                        deleteANumber();
                    } else {
                        appendNumber(".");
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void buttonSureClick() {
        // TODO Auto-generated method stub
        if (value.isEmpty()) {
            Toast.makeText(activity, "请输入数字", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Double count = Double.parseDouble(contentTv.getText().toString());
            listener.onSure(count);
            dialog.dismiss();
        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(activity, "请输入合法数字", Toast.LENGTH_SHORT).show();
        }
    }

    private void buttonCancelClick() {
        // TODO Auto-generated method stub
        listener.onCancel();
        dialog.dismiss();

    }

    void appendNumber(String inNumb) {
        if (inNumb.equals(".")) {
            if (value.length() > 0 && !value.contains(".")) {
                value = value + inNumb;
                contentTv.setText(contentTv.getText() + inNumb);
            }
        } else {
            if (isFirstInput) {
                value = "";
                contentTv.setText("");
                isFirstInput = false;
            }
            value = value + inNumb;
            contentTv.setText(contentTv.getText() + inNumb);
        }

    }

    void deleteANumber() {
        if (value.length() > 1) {
            value = value.substring(0, value.length() - 1);
            contentTv.setText(value);
        } else {
            value = "";
            contentTv.setText("");
            isFirstInput = true;
        }
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

//			try {
//				mThread.join();
//			} catch (Throwable e) {
//			}
//			mThread = null;

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
                    /*else if(size < 0){
                        //break;
						Thread.sleep(10);
						continue;
					} else {
						Thread.sleep(10);
					}
					*/
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void postOnRecvThreadStop() {
        mHandler.obtainMessage(MSG_ON_RECV_STOP).sendToTarget();
    }

    private void postOnReceived(byte[] data) throws IOException {
        synchronized (mRecvLock) {
//			mRecvData.add(data);
//			while(countByteArrayList(mRecvData) > 1024)
//				mRecvData.remove(0);
            mRecvStream.reset();
            mRecvStream.write(data);
        }
        mHandler.obtainMessage(MSG_ON_RECIEVED).sendToTarget();
    }

}
