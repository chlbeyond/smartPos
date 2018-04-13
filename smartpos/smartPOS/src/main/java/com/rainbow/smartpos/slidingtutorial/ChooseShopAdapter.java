package com.rainbow.smartpos.slidingtutorial;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rainbow.common.view.MyGridView;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.GlideCircleTransform;
import com.sanyipos.sdk.model.cloud.ShopList;

/**
 * Created by ss on 2016/3/19.
 */
public class ChooseShopAdapter extends RecyclerView.Adapter<ChooseShopAdapter.ChooseShopViewHolder> {
    public static String TAG = "ChooseShopAdapter";
    public Context mContext;
    public ShopList lists;

    public ChooseShopAdapter(Context context, ShopList lists) {
        super();
        this.mContext = context;
        this.lists = lists;
    }

    @Override
    public void onBindViewHolder(ChooseShopViewHolder holder, int position) {
        ShopList.Brand brand = lists.brands.get(position);
        holder.mBrandName.setText(brand.name);
        Glide.with(mContext).load(brand.logo).centerCrop().transform(new GlideCircleTransform(mContext)).into(holder.mBrandLogo);
        holder.mShopView.setAdapter(new ShopAdapter(mContext, brand.shops));
    }

    @Override
    public ChooseShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChooseShopViewHolder holder = new ChooseShopViewHolder(LayoutInflater.from(mContext).inflate(R.layout.sliding_chooseshop_recyclerview, parent, false));
        return holder;
    }

    @Override
    public int getItemCount() {
        return lists.brands.size();
    }

    class ChooseShopViewHolder extends RecyclerView.ViewHolder {
        TextView mBrandName;
        ImageView mBrandLogo;
        MyGridView mShopView;

        public ChooseShopViewHolder(View itemView) {
            super(itemView);
            mBrandName = (TextView) itemView.findViewById(R.id.textView_sliding_choose_shop_recycler_brand);
            mShopView = (MyGridView) itemView.findViewById(R.id.gridView_sliding_choose_shop_recycler_shop);
            mBrandLogo = (ImageView) itemView.findViewById(R.id.imageView_sliding_choose_shop_recycler_brand_logo);

        }
    }
}
