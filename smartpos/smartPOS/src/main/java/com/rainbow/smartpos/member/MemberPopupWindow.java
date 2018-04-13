package com.rainbow.smartpos.member;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.rainbow.smartpos.R;

import java.util.List;

public class MemberPopupWindow {
	public ListView listView_member_fragment_create_popupwindow;
	public interface MemberInterface {
		void itemclick(String s);
	}

	public PopupWindow show(final View v, Activity activity, final List<String> sList, final MemberInterface memberInterface) {
		View contentView = LayoutInflater.from(activity).inflate(R.layout.fragment_member_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView, v.getWidth(), LayoutParams.WRAP_CONTENT, true);
		listView_member_fragment_create_popupwindow = (ListView) contentView.findViewById(R.id.listView_member_fragment_create_popupwindow);
		final ArrayAdapter<String> bizDayArrayAdapter = new ArrayAdapter<String>(activity, R.layout.dailystat_spinner);
		bizDayArrayAdapter.addAll(sList);
		listView_member_fragment_create_popupwindow.setAdapter(bizDayArrayAdapter);
		listView_member_fragment_create_popupwindow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				memberInterface.itemclick(bizDayArrayAdapter.getItem(position));
			}
		});
		popupWindow.setTouchable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});

		popupWindow.showAsDropDown(v);
		return popupWindow;
	}
}
  ;