package com.rainbow.smartpos.util;

import com.sanyipos.smartpos.model.RemarkBeanList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/9/19.
 */
public class RemarkFilter {

    public static List<String> filterRemark(String remark) {

        List<String> remarkList = new ArrayList<>();
        while (true) {
            int index = remark.indexOf(" ");
            if (index == -1) {
                remarkList.add(remark);
                break;
            }
            String newRemark = remark.substring(0, index).trim();
            if (!newRemark.isEmpty())
                remarkList.add(newRemark);
            remark = remark.substring(index + 1, remark.length());
        }
        return remarkList;
//        for (String string : remarkList) {
//            for (int i = 0; i < lists.size(); i++) {
//                if (lists.get(i).equals(string)) {
//                    lists.get(i).count++;
//                } else {
//                    lists.add(new RemarkBean(string));
//                }
//            }
//        }
//
//        return lists;
    }

    public static boolean remarkIsExit(List<RemarkBeanList.RemarkBean> lists, String string) {
        if (lists != null && lists.size() > 0)
            for (RemarkBeanList.RemarkBean remarkBean : lists) {
                if (remarkBean.getRemark().equals(string)) {
                    return true;
                }
            }
        return false;
    }
}
