package com.rainbow.smartpos.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.tablestatus.TableTagView;
import com.rainbow.smartpos.util.ComparatorCombineTableByTag;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.VirtualTable;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.DateHelper;

public class ChooseTableAdapter extends BaseAdapter {

	public static final int AVAILABLE = 1;
	public static final int SERVING = 2;
	public static final int PREPRINT = 4;
	public static final int MERGE = 8;
	public static final int SPLIT = 16;
	public static final int OVERTIME = 64;
	public static final int RESERVE = 128;

	private Context mContext;

	public List<SeatEntity> tableShown = new ArrayList<SeatEntity>();
	final LayoutInflater inflater;

	public ChooseTableAdapter(Context c) {
		mContext = c;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		tableShown.clear();
		for (SeatEntity table : SanyiSDK.rest.operationData.shopTables) {
			if (table.state != AVAILABLE) {
				tableShown.add(table);
			}
		}
	}

	public int getCount() {
		return tableShown.size();
	}

	// return table id
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		SeatEntity table = tableShown.get(position);
		// ImageView i;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.choose_table_item, parent, false);
		}
		TextView tableName = (TextView) convertView.findViewById(R.id.tableName);
		TextView tablePersonCount = (TextView) convertView.findViewById(R.id.tablePersonCount);
		ImageView imageViewLock = (ImageView) convertView.findViewById(R.id.imageViewLock);
		tableName.setText(table.tableName);
		tablePersonCount.setText(table.personCount+"人");
		if (table.lock) {
			imageViewLock.setVisibility(View.VISIBLE);
		} else {
			imageViewLock.setVisibility(View.INVISIBLE);
		}
		convertView.setBackgroundResource(R.drawable.table_background_ser);
		if ((table.state & MERGE) == MERGE) {
			if (table.order.tag != null) {
				convertView.setBackgroundResource(R.drawable.table_background_combine);
			}
		}
		if ((table.state & PREPRINT) == PREPRINT) {
			convertView.setBackgroundResource(R.drawable.table_background_pre);
		}
		return convertView;
	}

	@Override
	public SeatEntity getItem(int position) {
		// TODO Auto-generated method stub
		return tableShown.get(position);
	}
}
