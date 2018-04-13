package com.rainbow.smartpos.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rainbow.smartpos.R;

public class IngridientCountDialog {
	public static interface ChangeIngridientCountListener {
		public void onSuccess(int count);
	}

	private ChangeIngridientCountListener listener;
	private int oldCount;
	private FragmentActivity context;

	public IngridientCountDialog(FragmentActivity activity, int count, ChangeIngridientCountListener listener) {
		this.context = activity;
		this.oldCount = count;
		this.listener = listener;
	}

	public void show() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = LayoutInflater.from(context).inflate(R.layout.order_operation_option, null);

		TextView textViewOrderItemDishName = (TextView) view.findViewById(R.id.textViewOrderItemDishName);
		final EditText editTextNumOfPeopleServed = (EditText) view.findViewById(R.id.editTextNumOfPeopleServed);
		Button buttonOpenTableMinusSign = (Button) view.findViewById(R.id.buttonOpenTableMinusSign);
		Button buttonOpenTablePlusSign = (Button) view.findViewById(R.id.buttonOpenTablePlusSign);
		Button sure = (Button) view.findViewById(R.id.buttonOpenTable);
		Button cancel = (Button) view.findViewById(R.id.buttonCancelOpenTable);
		sure.setVisibility(View.GONE);
		cancel.setVisibility(View.GONE);

		editTextNumOfPeopleServed.setFocusable(false);
		editTextNumOfPeopleServed.setEnabled(false);
		editTextNumOfPeopleServed.setText(String.valueOf(oldCount));
		builder.setTitle(Html.fromHtml("<font color='#16a4fa'>加料数量</font>"));
		textViewOrderItemDishName.setText("数量");
		buttonOpenTableMinusSign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String temp = editTextNumOfPeopleServed.getText().toString();
				int value = Integer.parseInt(temp);
				if (value > 0) {
					value--;
					editTextNumOfPeopleServed.setText(Integer.toString(value));
				}
			}
		});
		buttonOpenTablePlusSign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String temp = editTextNumOfPeopleServed.getText().toString();
				int value = Integer.parseInt(temp);
				value++;
				editTextNumOfPeopleServed.setText(Integer.toString(value));
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String temp = editTextNumOfPeopleServed.getText().toString();
				int count = Integer.parseInt(temp);
				listener.onSuccess(count);
				dialog.dismiss();
			}
		});
		builder.setView(view);
		Dialog dialog = builder.create();

		dialog.show();
		WindowManager m = context.getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高

		LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.5);
		// p.height = (int) (d.getHeight() * 0.25);
		dialog.getWindow().setAttributes(p);
	}
}
