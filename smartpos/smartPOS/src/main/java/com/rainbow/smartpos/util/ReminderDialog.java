package com.rainbow.smartpos.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.R.layout;
import com.rainbow.smartpos.util.Listener.OnSureListener;

public class ReminderDialog {
	FragmentActivity activity;
	OnSureListener sureListener; 

	public ReminderDialog(FragmentActivity activity, String Text, OnSureListener sureListener) {
		this.activity = activity;
		this.sureListener = sureListener;
		onSlodOut(Text);
	}

	private void onSlodOut(String text) {
		AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
		dlg.setTitle("提醒");
		View view = LayoutInflater.from(activity).inflate(android.R.layout.simple_list_item_1, null);
		TextView tv = (TextView) view.findViewById(android.R.id.text1);
		tv.setText(Html.fromHtml(text));
		dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sureListener.onSuccess();
				dialog.dismiss();
			}
		});
		dlg.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sureListener.onFailed();
				dialog.dismiss();
			}
		});
		dlg.setView(view);
		AlertDialog dialog = dlg.create();
		dialog.setCancelable(false);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();
	}
}
