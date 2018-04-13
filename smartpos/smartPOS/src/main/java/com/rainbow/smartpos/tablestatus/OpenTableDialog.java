package com.rainbow.smartpos.tablestatus;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.Listener.OnOpenTableListener;
import com.rainbow.smartpos.util.Listener.OnSetReturnDishCountListener;
import com.socks.library.KLog;

public class OpenTableDialog implements DialogInterface.OnKeyListener {
    public static final int OPEN_TABLE = 0;
    public static final int UPDATE_PERSONCOUNT = 1;
    public static final int RETURN_DISH_COUNT = 2;
    public static final int FAST_MODE = 3;
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
    static ImageButton btnBack;

    Context activity;
    OnOpenTableListener onOpenTableListener;
    OnSetReturnDishCountListener setReturnDishCountListener;
    Listener.OnFastModeNumberListener fastModeListener;
    String title = "";
    String content = "";
    private int flag;
    private int maxCount;
    private boolean isFirstInput = true;

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        KLog.d("OpenTableDialog", "onKey");

        return true;
    }

    /**
     * 开台接口,修改人数
     *
     * @param activity
     * @param title
     * @param hint
     * @param content
     * @param listener
     * @param flag
     */
    public OpenTableDialog(Context activity, String title, String content, int flag, OnOpenTableListener listener) {
        this.activity = activity;
        this.title = title;
        this.content = content;
        this.onOpenTableListener = listener;
        this.flag = flag;
    }

    public OpenTableDialog(Context activity, String title, String content, int flag, Listener.OnFastModeNumberListener listener) {
        this.activity = activity;
        this.title = title;
        this.content = content;
        this.fastModeListener = listener;
        this.flag = flag;
    }

    /**
     * 设置退菜数量
     *
     * @param activity
     * @param title
     * @param content
     * @param flag
     * @param listener
     */
    public OpenTableDialog(Context activity, String title, String content, int maxCount, int flag, OnSetReturnDishCountListener listener) {
        this.activity = activity;
        this.title = title;
        this.content = content;
        this.setReturnDishCountListener = listener;
        this.maxCount = maxCount;
        this.flag = flag;
    }

    public void show() {
        // AlertDialog.Builder builder = new AlertDialog.Builder(activity,
        // R.style.OpDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.open_table_dialog_layout, null, false);
        dialog = new MyDialog(activity, (int) (MainScreenActivity.getScreenWidth() * 0.4), (int) (MainScreenActivity.getScreenHeight() * 0.9), view, R.style.OpDialogTheme);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        titleTv = (TextView) view.findViewById(R.id.op_dialog_title);
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
        btnBack = (ImageButton) view.findViewById(R.id.buttonNumPadBack);

        sure.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        btnC.setOnClickListener(onClickListener);
        btnBack.setOnClickListener(onClickListener);
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

        dialog.show();
    }

    private void initView() {
        if (title.isEmpty()) {
            titleTv.setVisibility(View.INVISIBLE);
        } else {
            titleTv.setVisibility(View.VISIBLE);
            titleTv.setText(title);
            if (flag == RETURN_DISH_COUNT) {
                titleTv.append(Html.fromHtml(String.format("(<font color='#ff0000' ; font-size='12px'>%s</font>)", "最大可退数量" + maxCount)));
            }
        }

//        if (content.isEmpty()) {
//            contentTv.setVisibility(View.INVISIBLE);
//        } else {
//            contentTv.setVisibility(View.VISIBLE);
        contentTv.setText(content);
        value = content;
//        }
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
                case R.id.buttonNumPadBack:
                    if (value.length() > 0) {
                        value = value.substring(0, value.length() - 1);
                        contentTv.setText(value);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void buttonSureClick() {
        // TODO Auto-generated method stub
        switch (flag) {
            case FAST_MODE:
                String s = contentTv.getText().toString();
                if(s.isEmpty()){
                    s = null;
                }
                fastModeListener.onFastNumber(s);
                dialog.dismiss();
                return;
            case OPEN_TABLE:
            case UPDATE_PERSONCOUNT:

                try {

                    int count = Integer.parseInt(contentTv.getText().toString());

                    if (count == 0) {
                        Toast.makeText(activity, "开台人数不能为0", 0).show();
                        return;
                    }
                    onOpenTableListener.sure(count);
                    dialog.dismiss();
                } catch (Exception e) {
                    // TODO: handle exception
                    Toast.makeText(activity, "请输入人数", 0).show();
                }

//                dialog.dismiss();
                break;
            case RETURN_DISH_COUNT:
                try {
                    int peopleCount = Integer.parseInt(contentTv.getText().toString());
                    if (peopleCount > maxCount) {
                        Toast.makeText(activity, "您输入的数量大于可退数量,请重新输入", 0).show();
                    } else {
                        if (peopleCount == 0) {
                            Toast.makeText(activity, "退菜数量不能为0", 0).show();
                            return;
                        }
                        setReturnDishCountListener.sure(peopleCount);
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    Toast.makeText(activity, "请输入退菜数量", 0).show();
                }
                break;
            default:
                break;
        }

    }

    private void buttonCancelClick() {
        // TODO Auto-generated method stub
        switch (flag) {
            case OPEN_TABLE:
            case UPDATE_PERSONCOUNT:
                onOpenTableListener.cancel();
                dialog.dismiss();
                break;
            case RETURN_DISH_COUNT:
                setReturnDishCountListener.cancel();
                dialog.dismiss();
                break;
            default:
                dialog.dismiss();
                break;
        }

    }

    void appendNumber(String inNumb) {
        if (isFirstInput) {
            value = "";
            contentTv.setText("");
            isFirstInput = false;
        }
        value = value + inNumb;
        contentTv.setText(contentTv.getText() + inNumb);
    }


}
