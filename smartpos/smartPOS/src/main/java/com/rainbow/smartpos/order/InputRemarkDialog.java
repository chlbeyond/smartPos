package com.rainbow.smartpos.order;

import com.rainbow.smartpos.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class InputRemarkDialog {
	private Context context;
	private String text;
	private AlertDialog dialog;
	private OnInputCompleteInterface onInputCompleteInterface;
	public static interface OnInputCompleteInterface{
		public void onSure(String remark);
	}
	public InputRemarkDialog(Context context , String text , OnInputCompleteInterface onInputCompleteInterface){
		this.context = context;
		this.text = text;
		this.onInputCompleteInterface = onInputCompleteInterface;
	}
	
	public void show(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(text);
		View view = LayoutInflater.from(context).inflate(R.layout.input_remark_dialog, null);
		final EditText edit = (EditText) view.findViewById(R.id.input_remark);
		edit.requestFocus();
		builder.setPositiveButton(context.getString(R.string.sure1), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String input_remark = edit.getText().toString();
				if (!input_remark.isEmpty()) {
					onInputCompleteInterface.onSure(input_remark);
				}
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(context.getString(R.string.cancel1), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.setView(view);
		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
}
