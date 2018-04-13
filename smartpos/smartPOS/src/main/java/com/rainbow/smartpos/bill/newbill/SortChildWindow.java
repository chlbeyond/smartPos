package com.rainbow.smartpos.bill.newbill;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.ClosedBill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/2/19.
 */
public class SortChildWindow {
    private View mView;
    private PopupWindow mPopupWindow;
    private ListView listView;
    private Context mContext;

    public void setListener(ISelectedChildSortListener listener) {
        this.listener = listener;
    }

    private ISelectedChildSortListener listener;

    public static interface ISelectedChildSortListener {
        void selectedPositon(int position);

    }


    public void show(Context context, List<ClosedBill.BillWrapper> billWrappers, View parentView, View view, final ISelectedChildSortListener listener) {
        mView = LayoutInflater.from(context).inflate(R.layout.bill_fragment_child_sort, null);
        this.listener = listener;
        this.mContext = context;
        listView = (ListView) mView.findViewById(R.id.listView_bill_sort);
        List<String> strings = new ArrayList<>();
        for (ClosedBill.BillWrapper wrapper : billWrappers) {
            strings.add(wrapper.getTypeName());
        }
        listView.setAdapter(new SortAdapter(strings));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectedPositon(position);
                mPopupWindow.dismiss();
            }
        });


        mPopupWindow = new PopupWindow(mView,
                view.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.showAsDropDown(parentView, view.getWidth(), 0);
    }

    public class SortAdapter extends BaseAdapter {

        public List<String> strings;

        public SortAdapter(List<String> strings) {
            this.strings = strings;
        }

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sort_item, null);
            TextView textView = (TextView) view.findViewById(R.id.bill_fragment_sort_item);
            textView.setText(strings.get(position));
            return view;
        }
    }

}
