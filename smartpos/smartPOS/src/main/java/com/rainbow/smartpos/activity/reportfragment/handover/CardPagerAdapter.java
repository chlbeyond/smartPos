package com.rainbow.smartpos.activity.reportfragment.handover;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.manage.HandoverAdapter;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.model.DisplayItems;
import com.sanyipos.sdk.model.scala.HandoverListResult;
import com.sanyipos.sdk.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private float mBaseElevation;
    private Context mContext;
    public HandoverListResult result;

    public CardPagerAdapter(Context context, HandoverListResult result) {
        this.mContext = context;
        this.result = result;
        mViews = new ArrayList<>();

        for (int i = 0; i < result.handovers.size(); i++) {
            mViews.add(null);
        }

    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return result.handovers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.fragment_handover_card, container, false);
        container.addView(view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);

        List<String> groupList = new ArrayList<String>();
        List<List<DisplayItems.DisplayAmountItem>> childList = new ArrayList<List<DisplayItems.DisplayAmountItem>>();
        groupList.add("实收");
        groupList.add("会员");
        groupList.add("虚收");
        childList.add(result.handovers.get(position).stat.handoverInfos);
        childList.add(result.handovers.get(position).stat.memberInfos);
        childList.add(result.handovers.get(position).stat.waiveInfos);
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.listView_cardView);
        listView.setAdapter(new HandoverAdapter(groupList, childList, mContext));
        for (int i = 0; i < groupList.size(); i++) {

            listView.expandGroup(i);
        }
        TextView textViewName = (TextView) view.findViewById(R.id.textView_cardView_staff);
        textViewName.setText(result.handovers.get(position).staffName);

        TextView textViewHandTime = (TextView) view.findViewById(R.id.textView_cardView_handoverTime);
        textViewHandTime.setText(DateHelper.systemDateFormater.format(result.handovers.get(position).createon));

        Button buttonPrint = (Button) view.findViewById(R.id.button_cardView_handover_print);
        buttonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SanyiScalaRequests.printHandoverRequest(result.handovers.get(position).handover, new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {
                        Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();
                    }


                    @Override
                    public void onFail(String error) {
                        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

}
