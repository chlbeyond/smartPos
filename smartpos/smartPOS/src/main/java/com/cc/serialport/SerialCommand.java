package com.cc.serialport;

/**
 * Created by ss on 2016/11/10.
 */

public class SerialCommand {

    public final static int CHARS_DARK = 0; //所有灯不亮

    public final static int CHARS_UNIT_PRICE = 1;//单价

    public final static int CHARS_TOTAL_PRICE = 2;//总计

    public final static int CHARS_COLLECTION_PRICE = 3;//收款

    public final static int CHARS_CHANGE_PRICE = 4;//找零

    public static byte[] setCharsCommand(int chars) {
        byte[] charsByte = new byte[0];
        switch (chars) {
            case CHARS_DARK:
                charsByte = new byte[]{(byte) 27, (byte) 115, (byte) 48};
                break;
            case CHARS_UNIT_PRICE:
                charsByte = new byte[]{(byte) 27, (byte) 115, (byte) 49};
                break;
            case CHARS_TOTAL_PRICE:
                charsByte = new byte[]{(byte) 27, (byte) 115, (byte) 50};
                break;
            case CHARS_COLLECTION_PRICE:
                charsByte = new byte[]{(byte) 27, (byte) 115, (byte) 51};
                break;
            case CHARS_CHANGE_PRICE:
                charsByte = new byte[]{(byte) 27, (byte) 115, (byte) 52};
                break;
            default:
                charsByte = new byte[]{(byte) 27, (byte) 115, (byte) 48};
        }
        return charsByte;
    }

    public static byte[] clearScreen() {
        byte[] charsByte = new byte[]{(byte) 12};
        return charsByte;
    }

    public static byte[] initCommand() {
        byte[] charsByte = new byte[]{(byte) 27, (byte) 64};
        return charsByte;
    }

    public static byte[] showNums(String nums) {
        int i;
        byte[] showHead = new byte[]{(byte) 27, (byte) 81, (byte) 65};
        byte[] showContent = nums.getBytes();
        byte[] writeByte = new byte[(showContent.length + 4)];
        for (i = 0; i < showHead.length; i++) {
            writeByte[i] = showHead[i];
        }
        for (i = 0; i < showContent.length; i++) {
            writeByte[i + 3] = showContent[i];
        }
        writeByte[writeByte.length - 1] = (byte) 13;
        return writeByte;
    }
}
