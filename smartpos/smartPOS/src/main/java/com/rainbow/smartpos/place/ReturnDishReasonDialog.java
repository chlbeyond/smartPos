package com.rainbow.smartpos.place;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnChooseReasonListener;

public class ReturnDishReasonDialog {

	private MyDialog dialog;
	private TextView sure;
	private ImageButton cancel;
	private Context activity;
	private ListView reason_lv;
	private ReasonAdapter mReasonAdapter;
	private OnChooseReasonListener listener;

	public void show(Context activity, OnChooseReasonListener listener) {
		this.activity = activity;
		this.listener = listener;
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.return_dish_reason_dialog_layout, null, false);
		dialog = new MyDialog(activity, (int) (MainScreenActivity.getScreenWidth() * 0.4), (int) (MainScreenActivity.getScreenHeight() * 0.9), view, R.style.OpDialogTheme);
		TextView op_dialog_title = (TextView) view.findViewById(R.id.op_dialog_title);
		op_dialog_title.setText("退菜原因");
		reason_lv = (ListView) view.findViewById(R.id.reason_lv);
		mReasonAdapter = new ReasonAdapter(activity);

		sure = (TextView) view.findViewById(R.id.sure_btn);
		cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);
		reason_lv.setAdapter(mReasonAdapter);

		setListener();

		dialog.show();
	}

	public void setListener() {
		sure.setOnClickListener(onClickListener);
		cancel.setOnClickListener(onClickListener);
		reason_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mReasonAdapter.currentPosition == (mReasonAdapter.getCount() - 1)) {
					InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					// 得到InputMethodManager的实例
					if (imm.isActive()) {
						// 如果开启
						imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
						// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
					}
				}
				mReasonAdapter.setSelect(position);
			}
		});
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.sure_btn:
				buttonSureClick();
				break;
			case R.id.iv_close_dialog:
				buttonCancelClick();
				break;

			default:
				break;
			}
		}
	};

	private void buttonSureClick() {
		dialog.dismiss();
		listener.onSure(mReasonAdapter.getSelectId(), mReasonAdapter.getEditReason());
	}

	private void buttonCancelClick() {
		dialog.dismiss();
		listener.onCancel();
	}
}
