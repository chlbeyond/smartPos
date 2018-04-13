package com.rainbow.smartpos.place;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.tablestatus.TableAdapter;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.SeatEntity;

public class SelectServingTable {
	GridView gridView;
	ChooseTableAdapter tableAdapter;
	Context mContext;
	private Dialog dialog;
	private View view;
	private SelectServingTableInterface postrun;
	private SeatEntity currentTable;
	public interface SelectServingTableInterface {
		public void OnOkButtonPressed(long tableSeatId);
		public void OnCancelButtonPressed();
	}
	public SelectServingTable(Context mContext, SeatEntity currentTable, SelectServingTableInterface postrun) {
		super();
		this.mContext = mContext;
		this.postrun = postrun;
		this.currentTable = currentTable;
	}
	public void show() {
		view = LayoutInflater.from(mContext).inflate(R.layout.choose_table_dialog, null);
		dialog = new MyDialog(mContext,MainScreenActivity.getScreenWidth() * 0.4, view, R.style.OpDialogTheme);
		
		tableAdapter = new ChooseTableAdapter(mContext);
		gridView = (GridView) view.findViewById(R.id.tableGridView);
		gridView.setAdapter(tableAdapter);
		
		TextView dialog_title = (TextView) view.findViewById(R.id.op_dialog_title);
		if (null != currentTable) {
			dialog_title.append("(转出台:"+currentTable.tableName+")");
		}
		setListener();
		
		dialog.show();
	}
	public void setListener(){
		view.findViewById(R.id.iv_close_dialog).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				postrun.OnCancelButtonPressed();
				dialog.dismiss();
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				SeatEntity table = tableAdapter.getItem(position);
				if (null != currentTable) {
					if (currentTable.seat == table.seat) {
						Toast.makeText(mContext, "不能将菜转到本台", Toast.LENGTH_LONG).show();
						return ;
					}
				}
				postrun.OnOkButtonPressed(table.seat);
				dialog.dismiss();
				
			}
		});
	}
}