package com.rainbow.smartpos.tablestatus;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rainbow.smartpos.R;

public class DailyProgressDialog {
	public Activity activity;
	public TextView textView_daily_progress;
	public ProgressBar progressBar_daily_progress;
	public Dialog dialog;
	public Timer timer;
	public int progressTime = 100;
	public int progress = 0;
	public static final int UPDATE = 1;
	public boolean isUpdateProgress = false;
	public CustomDialogInterface inter = null;

	public interface CustomDialogInterface {
		public String DialogCancel();

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch ((Integer) msg.obj) {
			case UPDATE:
				progressBar_daily_progress.setProgress(progress / 10);
				if (progress > 800) {
					progress++;
				} else {
					progress = progress + 2;
				}
				if (progress > 1000) {
					dialog.dismiss();
					timer.cancel();
					inter.DialogCancel();
				}
				break;

			default:
				break;
			}
		}

	};

	public void dialogShow() {
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	public void dialogDismiss() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void show(Activity activity, String Message, CustomDialogInterface dialogInterface) {
		this.activity = activity;
		dialog = new Dialog(activity);
		inter = dialogInterface;
		dialog.setContentView(R.layout.daily_progress_dialog);
		dialog.setTitle("提示");
		textView_daily_progress = (TextView) dialog.findViewById(R.id.textView_daily_progress);
		textView_daily_progress.setText(Message);
		progressBar_daily_progress = (ProgressBar) dialog.findViewById(R.id.progressBar_daily_progress);
		dialog.show();
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.obj = UPDATE;
				handler.sendMessage(message);
			}
		}, 1000 * 3, progressTime);
		dialog.setCancelable(false);
	}

	public void setProgress(int progress) {
		progressBar_daily_progress.setProgress(progress);
	}
}
