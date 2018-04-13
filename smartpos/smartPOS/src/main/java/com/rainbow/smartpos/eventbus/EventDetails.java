package com.rainbow.smartpos.eventbus;

import com.sanyipos.sdk.model.OrderDetail;

import java.util.List;

/**
 * Created by ss on 2016/5/12.
 */
public class EventDetails {

    public List<OrderDetail> orderDetails;

    public EventDetails(List<OrderDetail> orders) {
        this.orderDetails = orders;
    }
}
