package com.rainbow.smartpos.tablemanage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.tablestatus.CheckableTableLayout;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.utils.DateHelper;

public class TableUnMergeAdapter extends BaseAdapter {
	
	public static final int AVAILABLE = 1;
	public static final int SERVING = 2;
	public static final int PREPRINT = 4;
	public static final int MERGE = 8;
	public static final int SPLIT = 16;
	public static final int RESERVE = 32;
	public static final int OVERTIME = 64;
	public static final int ALL = 0xffffffff;
	public static final int UN_MERGE = 100;
	
	private Context mContext;

	public LongSparseArray<SeatEntity> selectedTable = new LongSparseArray<SeatEntity>();
	public List<SeatEntity> tableShown = SanyiSDK.rest.operationData.shopTables;
	GridView.LayoutParams params = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	final LayoutInflater inflater;
	private int flag = -1;
	public List<Integer> selectPos = new ArrayList<Integer>();
	private int selectIndex = -1;
	private SeatEntity currentTable;
	/**
	 * 要并台的桌子
	 * 
	 * @param c
	 * @param flag
	 * @param tables
	 */
	public TableUnMergeAdapter(Context c, int flag, List<SeatEntity> tables, SeatEntity currentTable) {
		mContext = c;
		this.flag = flag;
		this.currentTable = currentTable;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.tableShown = tables;
	}

	@Override
	public int getCount() {
		return tableShown.size();
	}

	public List<SeatEntity> getAllData() {
		return tableShown;
	}
	/**
	 * 单选
	 * @param position
	 */
	public void setSelect(int position){
		selectIndex = position;
	}
	/**
	 * 多选
	 * @param position
	 */
	public void addSelect(int position){
		if (selectPos.contains(position)) {
			for (int i = 0; i < selectPos.size(); i++) {
				if (selectPos.get(i) == position) {
					selectPos.remove(i);
					return;
				}
			}
		}
		selectPos.add(position);
	}
	/**
	 * 获取单选
	 * @return
	 */
	public SeatEntity getSingleSelectTable(){
		for (int i = 0; i < tableShown.size(); i++) {
			if (i == selectIndex) {
				return tableShown.get(i);
			}
		}
		return null;
	}
	/**
	 * 获取多选
	 * @return
	 */
	public List<SeatEntity> getMultipleSelectTables(){
		List<SeatEntity> tables = new ArrayList<SeatEntity>();
		for (int pos : selectPos) {
			tables.add(tableShown.get(pos));
		}
		return tables;
	}

	@Override
	public void notifyDataSetChanged() {
		refreshData();
	}

	public void refreshData() {
		super.notifyDataSetChanged();
	}

	static class ViewHolder {
		public TextView style;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SeatEntity table = tableShown.get(position);
		// ImageView i;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.choose_table_item, parent, false);
		}
		TextView tableName = (TextView) convertView.findViewById(R.id.tableName);
		TextView tablePersonCount = (TextView) convertView.findViewById(R.id.tablePersonCount);
		tableName.setTextColor(Color.parseColor("#ffffff"));
		tablePersonCount.setTextColor(Color.parseColor("#ffffff"));
		ImageView imageViewLock = (ImageView) convertView.findViewById(R.id.imageViewLock);
		tableName.setText(table.tableName);
		tablePersonCount.setText(table.personCount+"人");
		if (null != currentTable && table.seat == currentTable.seat) {
			imageViewLock.setImageResource(R.drawable.current_table);
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
		for (Integer pos : selectPos) {
			if (pos == position) {
				tableName.setTextColor(Color.parseColor("#000000"));
				tablePersonCount.setTextColor(Color.parseColor("#000000"));
				convertView.setBackgroundResource(R.drawable.order_op_dialog_grid_item_bg_multiple);
			}
		}
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return tableShown.get(position);
	}
}
