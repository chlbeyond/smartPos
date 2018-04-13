package com.rainbow.smartpos.member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.MemberPromotion;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/10/11.
 */

public class MemberPromotionAdapter extends BaseAdapter {
    public Context mContext;
    public List<MemberPromotion> promotions = new ArrayList<>();
    public int selected = -1;

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public MemberPromotionAdapter(Context context, List<MemberPromotion> promotions) {
        this.mContext = context;
        this.promotions = promotions;
    }


    @Override
    public int getCount() {
        return promotions.size();
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
        View l;

        if (convertView == null) {
            l = LayoutInflater.from(mContext).inflate(R.layout.fragment_member_promotion_item, parent, false);
        } else {
            l = convertView;
        }
        TextView promotionName = (TextView) l.findViewById(R.id.textView_member_promotion_name);
        promotionName.setText(promotions.get(position).getPromotionName());

        TextView promotionChargeGift = (TextView) l.findViewById(R.id.textView_member_promotion_chargegift);
        promotionChargeGift.setText("充" + OrderUtil.dishPriceFormatter.format(promotions.get(position).getCharge()) + "送" + OrderUtil.dishPriceFormatter.format(promotions.get(position).getGift()) + "(" + promotions.get(position).effectAfterDays + "天生效)");

        if (selected == position) {
            l.setBackgroundResource(R.drawable.member_promotion_bg_selector);
            promotionName.setTextColor(mContext.getResources().getColor(R.color.white));
            promotionChargeGift.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        return l;
    }
}
