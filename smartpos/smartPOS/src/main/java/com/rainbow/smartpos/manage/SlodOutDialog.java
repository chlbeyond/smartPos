package com.rainbow.smartpos.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnChooseSlodOutListener;
import com.rainbow.smartpos.util.Listener.OnSureListener;

public class SlodOutDialog implements OnClickListener{
	FragmentActivity activity;
	AlertDialog dialog;
	OnChooseSlodOutListener chooseSlodOutListener;
	boolean isSlodout = false;
	public SlodOutDialog(FragmentActivity activity, boolean isSlodOut, OnChooseSlodOutListener chooseSlodOutListener) {
		this.activity = activity;
		this.isSlodout = isSlodOut;
		this.chooseSlodOutListener = chooseSlodOutListener;
	}
	public void show() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.please_choose_slodout_way));
		View view = LayoutInflater.from(activity).inflate(R.layout.slodout_dialog, null);
		LinearLayout slodout_soon = (LinearLayout) view.findViewById(R.id.slodout_soon);
		LinearLayout auto_slodout = (LinearLayout) view.findViewById(R.id.auto_slodout);
		LinearLayout slodout_longtime = (LinearLayout) view.findViewById(R.id.slodout_longtime);
		slodout_soon.setOnClickListener(this);
		auto_slodout.setOnClickListener(this);
		slodout_longtime.setOnClickListener(this);
		
		TextView slodout_soon_text = (TextView) view.findViewById(R.id.slodout_soon_text);
		TextView auto_slodout_text = (TextView) view.findViewById(R.id.auto_slodout_text);
		ImageView slodout_soon_image = (ImageView) view.findViewById(R.id.slodout_soon_image);
		ImageView auto_slodout_image = (ImageView) view.findViewById(R.id.auto_slodout_image);
		if (!isSlodout) {
			slodout_soon_text.setText(activity.getString(R.string.slodout_soon));
			auto_slodout_text.setText(activity.getString(R.string.auto_slodout));
		}else{
			slodout_soon_image.setImageResource(R.drawable.resume_normal);
			auto_slodout_image.setImageResource(R.drawable.change_count);
			slodout_soon_text.setText(activity.getString(R.string.resume_normal));
			auto_slodout_text.setText(activity.getString(R.string.change_count));
		}
		builder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				chooseSlodOutListener.cancel();
			}
		});
		builder.setView(view);
		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.slodout_soon:
			chooseSlodOutListener.onSlodOutSoon();
			break;
		case R.id.auto_slodout:
			chooseSlodOutListener.onSlodOutWithCount();
			break;
		case R.id.slodout_longtime:
			chooseSlodOutListener.onSlodOutLongTime();
			break;

		default:
			break;
		}
		dialog.dismiss();
	}
}
