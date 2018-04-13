package com.rainbow.smartpos.db.dbbean;

import java.util.Date;

/**
 * Created by ss on 2016/9/14.
 */
public class TableRemarkBean   {

    public long tableId;

    public String remark;

    public Date createOn;

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }
}
