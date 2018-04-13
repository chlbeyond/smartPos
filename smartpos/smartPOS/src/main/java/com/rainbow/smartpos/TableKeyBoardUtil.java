package com.rainbow.smartpos;

public class TableKeyBoardUtil {
    public final static int OTHERMODE = 0;
    public final static int TABLEMODE = 1;
    public final static int ORDERMODE = 2;
    public final static int CHECKMODE = 3;
    public boolean isInput = true;
    public StringBuilder sb = new StringBuilder();
    public TableKeyBoardListener listener;

    public static interface TableKeyBoardListener {
        void keyInput(String billSn);
    }

    public void setTableKeyBoardUtil(TableKeyBoardListener tableKeyBoardListener) {
        this.listener = tableKeyBoardListener;
    }

    public void getTableKeyInput(int key) {
        sb.append(key - 7);
        //Log.i("sb", sb.toString());
        switch (MainScreenActivity.CURRENT_MODE) {
            case MainScreenActivity.TABLE_FRAGMENT:
                if (sb.length() == 13) {
                    listener.keyInput(sb.toString());
                    sb.delete(0, sb.length());
                }
                break;
            case MainScreenActivity.LOGIN_FRAGMENT:
                if (sb.length() == 10) {
                    listener.keyInput(sb.toString());
                    sb.delete(0, sb.length());
                }
                break;
            default:
                break;
        }

    }
}
