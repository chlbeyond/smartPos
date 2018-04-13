package com.sanyipos.smartpos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/9/20.
 */

public class RemarkBeanList {

    public List<RemarkBean> remarkBeans = new ArrayList<>();

    public void addRemark(String remark) {
        RemarkBean remarkBean = new RemarkBean();
        remarkBean.setRemark(remark);
        remarkBeans.add(remarkBean);
    }

    public void changeRemark(String remark) {
        for (RemarkBean remarkBean : remarkBeans) {
            if (remarkBean.getRemark().equals(remark)) {
                remarkBean.setCount(remarkBean.getCount() + 1);
            }
        }
    }

    public class RemarkBean {

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String remark = "";

        public int count = 1;
    }

}
