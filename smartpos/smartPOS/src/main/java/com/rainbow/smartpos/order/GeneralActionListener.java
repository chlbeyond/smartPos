package com.rainbow.smartpos.order;

import android.os.Bundle;

import com.sanyipos.sdk.api.bean.TableOrderInfo;

import java.util.List;

public interface GeneralActionListener {

	public void onClickQuit();

	public void onClickHome();

	public void onTableOperation(long[] tableId, int numOfPeoplePerTable, TableOperation oper, boolean newbill, List<TableOrderInfo> infos);

	void onSwitchTable(long[] tableId, int numOfPeople, TableOperation oper, boolean newbill);
	void onTableCheck(TableOperation oper, Bundle bundle);
}
