package com.rainbow.smartpos.manage;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnChangeSlodOutCountListener;

public class ChangeSlodOutCountDialog{
	FragmentActivity activity;
	AlertDialog dialog;
	OnChangeSlodOutCountListener onChangeSlodOutCountListener;
	double slodCount = 0;
	boolean isWeight = false;
	String unitName = "";
	String productName = "";
	public ChangeSlodOutCountDialog(FragmentActivity activity, String productName,double slodCount, boolean isWeight ,String unitName, OnChangeSlodOutCountListener onChangeSlodOutCountListener2) {
		this.activity = activity;
		this.slodCount = slodCount;
		this.isWeight = isWeight;
		this.unitName = unitName;
		this.productName = productName;
		this.onChangeSlodOutCountListener = onChangeSlodOutCountListener2;
	}

	public void show() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(Html.fromHtml(activity.getString(R.string.please_change_slodout_count)+productName));
		View view = LayoutInflater.from(activity).inflate(R.layout.change_slodout_count, null);
		final EditText count = (EditText) view.findViewById(R.id.count);
		if (!isWeight) {
			count.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		TextView unitNameText = (TextView) view.findViewById(R.id.unitName);
		unitNameText.setText(unitName);
		final TextView sure = (TextView) view.findViewById(R.id.sure);
		final TextView cancel = (TextView) view.findViewById(R.id.cancel);
		count.requestFocus();
		count.setSelection(count.getText().toString().length());
		sure.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				onChangeSlodOutCountListener.sure(Double.parseDouble((count.getText().toString().isEmpty()?"0.0":count.getText().toString())));
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				onChangeSlodOutCountListener.cancel();
			}
		});
		count.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
						sure.performClick();
						return true;
					}
					if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
						cancel.performClick();
						return true;
					}
				}
				return false;
			}
		});
		builder.setView(view);
		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();
	}
}
