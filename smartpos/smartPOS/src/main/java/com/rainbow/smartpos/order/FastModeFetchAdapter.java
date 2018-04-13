package com.rainbow.smartpos.order;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.activity.reportfragment.handover.CardAdapter;
import com.rainbow.smartpos.check.CheckDetailListAdapter;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.sanyipos.sdk.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/10/24.
 */

public class FastModeFetchAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<OpenTableDetail> orders;
    private Context mContext;
    private float mBaseElevation;

    private FastModeCardViewListener listener;

    public interface FastModeCardViewListener {
        void onCardViewClickListener(int position);
    }

    public FastModeFetchAdapter(Context context, List<OpenTableDetail> orders) {
        this.mContext = context;
        this.orders = orders;
        mViews = new ArrayList<>();

        for (int i = 0; i < orders.size(); i++) {
            mViews.add(null);
        }
    }

    public void setClickListener(FastModeCardViewListener listener) {
        this.listener = listener;
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return orders.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.dialog_fastmode_cardview, container, false);
        container.addView(view);
        TextView textViewNumber = (TextView) view.findViewById(R.id.textView_fastmode_number);
        textViewNumber.setText(orders.get(position).info.getOrderName());

        TextView textViewStaff = (TextView) view.findViewById(R.id.textView_fastmode_staff);
        textViewStaff.setText(orders.get(position).info.getOpenStaffName());

        TextView textViewTime = (TextView) view.findViewById(R.id.textView_fastmode_time);
        textViewTime.setText(DateHelper.hmFormater.format(orders.get(position).info.getCreateTime()));

        TextView textViewReminder = (TextView) view.findViewById(R.id.textView_fastmode_reminder);
        textViewReminder.setText(position + 1 + "/" + orders.size());
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        ListView listView = (ListView) view.findViewById(R.id.listView_fastmode_fetch);
        CheckDetailListAdapter adapter = new CheckDetailListAdapter(mContext, CheckDetailListAdapter.Type.ORDER);
        adapter.setOrderDetails(orders.get(position).ods);
        listView.setAdapter(adapter);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        mViews.set(position, cardView);
        return view;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }
}
