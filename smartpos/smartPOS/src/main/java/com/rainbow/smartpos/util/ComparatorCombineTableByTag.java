package com.rainbow.smartpos.util;

import com.sanyipos.sdk.model.SeatEntity;

import java.util.Comparator;

public class ComparatorCombineTableByTag implements Comparator {

	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub

		SeatEntity table0 = (SeatEntity) arg0;
		SeatEntity table1 = (SeatEntity) arg1;
		if (table0.order == null || table1.order == null || table0.order.tag.isEmpty() || table1.order.tag.isEmpty()) {
			return 0;
		}
		String tag0 = table0.order.tag;
		String tag1 = table1.order.tag;
		if (tag0.equals(tag1)) {
			if (table0.tableOrder > table1.tableOrder)
				return 1;
			else
				return -1;
		} else {
			return getCombineTag(tag0)> getCombineTag(tag1) ? 1 : -1;
		}
	}

	public int getCombineTag(String str) {
		str = str.trim();
		return str.hashCode();
	}

}
