package com.rainbow.smartpos.util;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;

import java.util.List;

/**
 * Created by ss on 2015/12/29.
 */
public class KeyboardUtil {
    private Context ctx;
    private KeyboardView keyboardView;
    private Keyboard k1;// 字母键盘
    private Keyboard k2;// 数字键盘
    public boolean isupper = true;// 是否大写
    public boolean isFirst = true;
    private EditText ed;
    private boolean isNum;

    public KeyboardUtil(View view, final Context ctx, EditText edit, final boolean isnum) {
        this.ctx = ctx;
        this.ed = edit;
        this.isNum = isnum;
        k1 = new Keyboard(ctx, R.xml.qwerty);
        k2 = new Keyboard(ctx, R.xml.symbols);
        keyboardView = (KeyboardView) view.findViewById(R.id.keyboard_view);

        if (isNum) {
            keyboardView.setKeyboard(k2);
        } else {
            keyboardView.setKeyboard(k1);
        }

        keyboardView.setEnabled(true);
        keyboardView.setShifted(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);
        keyboardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isFirst) {
                    isFirst = false;
                    if (isNum) {
                        int width = 0;
                        switch (MainScreenActivity.getScreenScale()) {
                            case S4B3:
                                width = MainScreenActivity.getScreenWidth() / 3;
                                break;
                            case S16B9:
                                width = MainScreenActivity.getScreenWidth() / 4;
                                break;
                            case S21B9:
                                width = MainScreenActivity.getScreenWidth() / 5;
                                break;
                        }
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        keyboardView.setLayoutParams(layoutParams);
                    } else {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        keyboardView.setLayoutParams(layoutParams);
                    }
                }
            }
        });
        changeKey();
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
                editable.clear();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                changeKey();
                keyboardView.setKeyboard(k1);

            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 数字键盘切换
                if (isNum) {
                    isNum = false;
                    keyboardView.setKeyboard(k1);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    keyboardView.setLayoutParams(layoutParams);
                    SharePreferenceUtil.saveBooleanPreference(ctx, SmartPosPrivateKey.ST_LOCAL_SEATCH_TYPE, false);
                } else {
                    isNum = true;
                    keyboardView.setKeyboard(k2);
                    int width = 0;
                    switch (MainScreenActivity.getScreenScale()) {
                        case S4B3:
                            width = MainScreenActivity.getScreenWidth() / 3;
                            break;
                        case S16B9:
                            width = MainScreenActivity.getScreenWidth() / 4;
                            break;
                        case S21B9:
                            width = MainScreenActivity.getScreenWidth() / 5;
                            break;
                    }
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    keyboardView.setLayoutParams(layoutParams);
                    SharePreferenceUtil.saveBooleanPreference(ctx, SmartPosPrivateKey.ST_LOCAL_SEATCH_TYPE, true);
                }

            } else if (primaryCode == 57419) { // go left
                if (start > 0) {
                    ed.setSelection(start - 1);
                }
            } else if (primaryCode == 57421) { // go right
                if (start < ed.length()) {
                    ed.setSelection(start + 1);
                }
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Key> keylist = k1.getKeys();
        for (Key key : keylist) {
            if (key.label != null && isword(key.label.toString())) {
                key.label = key.label.toString().toUpperCase();
                key.codes[0] = key.codes[0] - 32;
            }
        }

    }

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

}
