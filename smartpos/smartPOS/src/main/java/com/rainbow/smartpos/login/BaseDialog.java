package com.rainbow.smartpos.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rainbow.smartpos.R;

public class BaseDialog {
	private String title;
	private String tips;
	private boolean isForce;
	private TextView textView_dialog_reminder;
	private Dialog dialog;
	private Context context;
	private String negative;

	public static interface BaseDialogInterface {
		void onPositive(DialogInterface dialog);

		void onNegative(DialogInterface dialog);
	}

	public BaseDialog(Context context, String title, String tips, boolean isForce, String negative) {
		this.context = context;
		this.title = title;
		this.tips = tips;
		this.isForce = isForce;
		this.negative = negative;
	}

	public void showDialog(final BaseDialogInterface dialogInterface) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_reminder, null);
		builder.setTitle(title);
		textView_dialog_reminder = (TextView) view.findViewById(R.id.textView_dialog_reminder);
		textView_dialog_reminder.setText(tips);
		if (isForce) {
			builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialogInterface.onPositive(dialog);
				}
			});

		}
		builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialogInterface.onNegative(dialog);
			}
		});
		builder.setView(view);
		dialog = builder.create();
		dialog.show();
	}

	public void dialogDismiss() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
