package com.rainbow.smartpos.tablestatus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.SmartPosApplication;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;

import java.lang.reflect.Field;

@SuppressLint("ResourceAsColor")
public class TableStatusFragment extends Fragment {

    static final String LOG_TAG = "TableStatusFragment";

    View mainView;


    LayoutInflater inflater;

    static MainScreenActivity activity;
    static SmartPosApplication application;
    public View rootView;
    public TableViewPagerFragment tableViewPagerFragment;

    public Fragment currentFragment;
    public EditText editTextCheck;

    public void hideView(FragmentTransaction ft) {
        if (tableViewPagerFragment != null) {
            ft.hide(tableViewPagerFragment);
        }
    }

    public void disPlayView(int position, Bundle bundle) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        hideView(ft);
        switch (position) {
            case MainScreenActivity.TABLE_VIEWPAGER_FRAGMENT:
                if (tableViewPagerFragment != null) {
                    ft.show(tableViewPagerFragment);
                } else {
                    tableViewPagerFragment = new TableViewPagerFragment();
                    ft.add(R.id.table_status_fragment, tableViewPagerFragment);
                }
                currentFragment = tableViewPagerFragment;
                break;
            default:
                break;
        }
        ft.commit();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_table_status, container, false);
        mainView = rootView;
        activity = (MainScreenActivity) getActivity();
        application = (SmartPosApplication) getActivity().getApplication();

        setHasOptionsMenu(true);
        disPlayView(MainScreenActivity.TABLE_VIEWPAGER_FRAGMENT, new Bundle());
        initEditText();
        requestFocus();
        return rootView;
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

//    @Override
//    public void onResume() {
//        // TODO Auto-generated method stub
//        super.onResume();
//        activity.disPlayTableView();
//    }


    public void requestFocus() {
        if (editTextCheck != null) {
            editTextCheck.getText().clear();
            editTextCheck.setFocusable(true);
            editTextCheck.setFocusableInTouchMode(true);
            editTextCheck.requestFocus();

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden) {
            cancelFocus();
        } else {
            activity.disPlayTableView();
            requestFocus();
        }
    }

    public void cancelFocus() {
        if (editTextCheck != null) {
            editTextCheck.setFocusable(false);
        }
    }

    private void initEditText() {
        // TODO Auto-generated method stub
        editTextCheck = (EditText) rootView.findViewById(R.id.editTextCheck);
        requestFocus();
        editTextCheck.setInputType(InputType.TYPE_NULL);
        editTextCheck.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                editTextCheck.setInputType(InputType.TYPE_NULL);
                return false;
            }
        });
        editTextCheck.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });

        editTextCheck.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) | (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    String editText = editTextCheck.getText().toString();
                    checkBillSn(editText);
                    return true;
                }
                return false;
            }
        });

    }

    public void checkBillSn(String editText) {
        if (editText != null && !editText.isEmpty()) {
            for (SeatEntity seat : SanyiSDK.rest.operationData.shopTables) {
                if (seat.order != null) {
                    if (seat.order.sn.equals(editText.substring(0, editText.length() - 1))) {
                        if (seat.lock) {
                            Toast.makeText(activity, "桌子被锁", Toast.LENGTH_SHORT).show();
                            showCheckErrorDialog("桌子被锁");
                            requestFocus();
                            return;
                        }
                        long[] tableIds = {seat.seat};
                        if (null != seat.order.tag) {
//							activity.onTableOperation(tableIds, 0, TableOperation.MERGE_CHECK, false, null);
                            return;
                        }
//						activity.onTableOperation(tableIds, 0, TableOperation.CHECK, false, null);
                        return;
                    }
                }
            }
            Toast.makeText(activity, "请检查账单是否正确", Toast.LENGTH_SHORT).show();
            showCheckErrorDialog("请检查账单是否正确");
            requestFocus();
        }

    }

    public void showCheckErrorDialog(String extraInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("extraInfo");
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                dlg.dismiss();
                requestFocus();
            }
        });
        final Dialog dialog = builder.create();
        dialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mHandler.sendMessage(Message.obtain());
                dialog.dismiss();
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            requestFocus();
        }

        ;
    };

}
