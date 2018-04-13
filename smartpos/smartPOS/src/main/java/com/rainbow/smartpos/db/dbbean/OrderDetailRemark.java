package com.rainbow.smartpos.db.dbbean;

import java.util.Date;


/**
 * Created by ss on 2016/9/14.
 */
public class OrderDetailRemark   {

    public int numberOfUse;

    public String remark;

    public Date createOn;

    public int staffId;

    public long deviceId;

    public int getNumberOfUse() {
        return numberOfUse;
    }

    public void setNumberOfUse(int numberOfUse) {
        this.numberOfUse = numberOfUse;
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

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }
}
