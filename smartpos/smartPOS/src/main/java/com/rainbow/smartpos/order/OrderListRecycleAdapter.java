package com.rainbow.smartpos.order;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/4/6.
 */
public class OrderListRecycleAdapter extends RecyclerView.Adapter<OrderListRecycleAdapter.OrderListHolder> {

    public List<OrderDetail> mOrderList = new ArrayList<>();
    public Context mContext;
    public int mCurrentSelection;

    public OrderListRecycleAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public OrderDetail getCurrentSelectOrder() {
        return mOrderList.get(mCurrentSelection);
    }

    public List<OrderDetail> getmOrderList() {
        return mOrderList;
    }

    public void setmOrderList(List<OrderDetail> mOrderList) {
        this.mOrderList = mOrderList;
    }

    public void setDishHold(boolean hold){
        for(OrderDetail order : mOrderList){
            order.setHold(hold);
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    @Override
    public OrderListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        KLog.d("OrderListRecycleAdapter"," = = = = = = = onCreateViewHolder start" +System.currentTimeMillis());
        OrderListHolder holder = new OrderListHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_order_list_recycle, parent, false));

        KLog.d("OrderListRecycleAdapter"," = = = = = = = onCreateViewHolder finish" +System.currentTimeMillis());
        return holder;
    }

    @Override
    public void onBindViewHolder(OrderListHolder holder, int position) {
        OrderDetail order = mOrderList.get(position);
//        holder.mTextViewRowNo.setText(Integer.toString(position + 1));
        holder.mTextViewOrderName.setText(order.getName());
//        holder.mTextViewQuantity.setText(Integer.toString(order.getQuantity()));
        KLog.d("OrderListRecycleAdapter"," = = = = = = = onBindViewHolder" +System.currentTimeMillis());
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    class OrderListHolder extends RecyclerView.ViewHolder {
//        TextView mTextViewRowNo;
        TextView mTextViewOrderName;
//        LinearLayout mLinearLayoutRemarkLayout;
//        TextView mTextViewOriginalPrice;
//        TextView mTextViewCurrentPrice;
//        TextView mTextViewQuantity;
//        ImageView mImageViewArrows;

        public OrderListHolder(View itemView) {
            super(itemView);
//            mTextViewRowNo = (TextView) itemView.findViewById(R.id.textViewRowNo);
            mTextViewOrderName = (TextView) itemView.findViewById(R.id.textViewDishName);
//            mLinearLayoutRemarkLayout = (LinearLayout) itemView.findViewById(R.id.remarkLayout);
//            mTextViewOriginalPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
//            mTextViewCurrentPrice = (TextView) itemView.findViewById(R.id.textViewRealPrice);
//            mTextViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
//            mImageViewArrows = (ImageView) itemView.findViewById(R.id.imageView_arrows);
        }

    }
}
