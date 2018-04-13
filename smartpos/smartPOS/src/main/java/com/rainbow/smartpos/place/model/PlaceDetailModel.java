package com.rainbow.smartpos.place.model;

import java.util.List;

/**
 * Created by ss on 2016/1/11.
 */
public interface PlaceDetailModel {

    void loadDetals(List<Long> tableIds,PlaceDetailModeImpl.onLoadDetailsListener listener);
}
