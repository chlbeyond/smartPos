package com.sanyipos.smartpos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ss on 2016/7/5.
 */

public  class OrderParams implements Serializable {

    public int getPeopleNumber() {
        return peopleNumber;
    }

    public void setPeopleNumber(int peopleNumber) {
        this.peopleNumber = peopleNumber;
    }

    public int peopleNumber;

    public List<Long> getTableIds() {
        return tableIds;
    }

    public void setTableIds(List<Long> tableIds) {
        this.tableIds = tableIds;
    }

    public List<Long> tableIds;

    public boolean isNewBill() {
        return isNewBill;
    }

    public void setIsNewBill(boolean isNewBill) {
        this.isNewBill = isNewBill;
    }

    public boolean isNewBill;

    public boolean isMerge() {
        return isMerge;
    }

    public void setIsMerge(boolean isMerge) {
        this.isMerge = isMerge;
    }

    public boolean isMerge;

}
