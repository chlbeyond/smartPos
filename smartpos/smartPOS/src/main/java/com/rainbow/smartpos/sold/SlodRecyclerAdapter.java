package com.rainbow.smartpos.sold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.model.rest.ProductRest;

import java.util.List;


/**
 * Created by ss on 2016/1/21.
 */
public class SlodRecyclerAdapter extends BaseAdapter {
    public Context mContext;
    public SoldFragment.Type type;

    public void setmProducts(List<ProductRest> mProducts) {
        this.mProducts = mProducts;
    }

    public List<ProductRest> mProducts;

    public SlodRecyclerAdapter(Context context, SoldFragment.Type type) {
        super();
        this.mContext = context;
        this.type = type;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    //    public void onBindViewHolder(SlodProduct slodProduct, int i) {
//        if (i < mProducts.size()) {
//            ProductRest product = mProducts.get(i);
//            slodProduct.mName.setText(product.getName());
//        } else {
//            slodProduct.mName.setVisibility(View.GONE);
//            slodProduct.mDelete.setVisibility(View.GONE);
//            slodProduct.mState.setVisibility(View.GONE);
//            slodProduct.mAdd.setVisibility(View.VISIBLE);
//        }
//    }

//    public SlodProduct onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_slod_adapter_item, viewGroup, false);
//        if (i < mProducts.size()) {
//            view.setBackground(mContext.getResources().getDrawable(R.drawable.has_choose_promotion_selector));
//        } else {
//            view.setBackground(mContext.getResources().getDrawable(R.drawable.dash_coners_bg));
//        }
//
//        return new SlodProduct(view);
//    }


    @Override
    public int getCount() {
        return mProducts.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return mProducts.get(i);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_slod_adapter_item, viewGroup, false);
        } else {
            view = convertView;
        }
        TextView mName = (TextView) view.findViewById(R.id.textView_slod_adapter_item_name);
        TextView mState = (TextView) view.findViewById(R.id.textView_slod_adapter_item_state);
        TextView mDelete = (TextView) view.findViewById(R.id.textView_slod_adapter_item_deletes);
        ImageView mAdd = (ImageView) view.findViewById(R.id.imageView_slod_adapter_item_add);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SanyiScalaRequests.cancelSoldoutDishRequest(mProducts.get(position).id, new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {

                        Toast.makeText(mContext,status,Toast.LENGTH_LONG).show();
                    }



                    @Override
                    public void onFail(String error) {

                        Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        if (position < mProducts.size()) {
            mName.setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.VISIBLE);
            mState.setVisibility(View.VISIBLE);
            ProductRest product = mProducts.get(position);
            mName.setText(product.getName());
            if (product.isMultiUnitProduct) {
                mName.setText(product.getName() + "(" + product.getUnitName() + ")");
            }
            switch (type) {
                case Stop:
                    mState.setText("停售");
                    break;
                case Sold:
                    mState.setText(Integer.toString(product.soldoutCount.intValue()));
                    if (product.productType.isIsWeight()) {
                        mState.setText(product.soldoutCount.toString());
                    }
                    break;
            }
            view.setBackground(mContext.getResources().getDrawable(R.drawable.has_choose_promotion_selector));
        } else {
            mName.setVisibility(View.GONE);
            mDelete.setVisibility(View.GONE);
            mState.setVisibility(View.GONE);
            mAdd.setVisibility(View.VISIBLE);
            view.setBackground(mContext.getResources().getDrawable(R.drawable.dash_coners_bg));
        }

        return view;
    }
}
