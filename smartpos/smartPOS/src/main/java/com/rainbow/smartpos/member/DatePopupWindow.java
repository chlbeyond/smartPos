package com.rainbow.smartpos.member;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.rainbow.smartpos.R;

public class DatePopupWindow {
	public DatePicker day_settlement_datePicker;
	public interface OnSureListener {
		void onSureClick(String s);
	}

	public PopupWindow show(final View v, Activity activity, final OnSureListener onSureListener) {
		View contentView = LayoutInflater.from(activity).inflate(R.layout.choose_date_dialog, null);
		final PopupWindow popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		day_settlement_datePicker = (DatePicker) contentView.findViewById(R.id.day_settlement_datePicker);
		day_settlement_datePicker.updateDate(1990, 0, 1);
		contentView.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSureListener.onSureClick(day_settlement_datePicker.getYear() + "-"+(day_settlement_datePicker.getMonth()+1) + "-"+day_settlement_datePicker.getDayOfMonth());
				popupWindow.dismiss();
			}
		});
		popupWindow.setTouchable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(v);
		return popupWindow;
	}
}
