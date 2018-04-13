package com.rainbow.smartpos.bill.newbill;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.ClosedBill;
import com.sanyipos.sdk.utils.DateHelper;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/1/4.
 */
public class RecyclerViewAdapter extends BaseAdapter {
    public Context context;
    public List<ClosedBill> closedBills = new ArrayList<>();
    public OnItemClickLitener listener;

    public int mSelection = 0;

    public RecyclerViewAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setClosedBills(List<ClosedBill> bills) {
        this.closedBills = bills;
        notifyDataSetChanged();
    }

    public List<ClosedBill> getClosedBills() {
        return closedBills;
    }

    public ClosedBill getSelectionBill() {
        if (closedBills.size() > 0)
            return closedBills.get(mSelection);
        return null;
    }

    public int getmSelection() {
        return mSelection;
    }

    public void setmSelection(int mSelection) {
        this.mSelection = mSelection;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (closedBills == null) return 0;
        return closedBills.size();
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
        View view;
        if (convertView == null) {

            view = LayoutInflater.from(context).inflate(R.layout.layout_listview_bill_item, parent, false);
        } else {
            view = convertView;
        }
        ImageView imageView = null;
        TextView mBillNumber;
        TextView mTableNumber;
        TextView mPeopleNumber;
        TextView mAmount;
        TextView mSalesman;
        TextView mClosedBillTime;
        imageView = (ImageView) view.findViewById(R.id.imageView_bill_invoice);
        mBillNumber = (TextView) view.findViewById(R.id.textView_bill_number);
        mTableNumber = (TextView) view.findViewById(R.id.textView_table_number);
        mPeopleNumber = (TextView) view.findViewById(R.id.textView_people_number);
        mAmount = (TextView) view.findViewById(R.id.textView_amount);
        mSalesman = (TextView) view.findViewById(R.id.textView_salesman);
        mClosedBillTime = (TextView) view.findViewById(R.id.textView_closed_time);
        mBillNumber.setText(closedBills.get(position).sn);
        ClosedBill.BillOrder order = closedBills.get(position).orders.get(0);
        StringBuffer tableName = new StringBuffer();
        for (int i = 0; i < closedBills.get(position).orders.size(); i++) {
            tableName.append(" " + closedBills.get(position).orders.get(i).tableName);
        }
        mTableNumber.setText(tableName);

        mPeopleNumber.setText(Integer.toString(order.personCount));

        mAmount.setText(OrderUtil.decimalFormatter.format(closedBills.get(position).amount) + " " + ((closedBills.get(position).payInfo != null) ? " ( " + closedBills.get(position).payInfo + " )" : ""));

        mSalesman.setText(closedBills.get(position).cashierStaffName);

        mClosedBillTime.setText(DateHelper.hmsFormater.format(closedBills.get(position).closedTime));

        if (position == mSelection) {
            view.setBackgroundColor(Color.parseColor("#D8D8D8"));
        } else {
            view.setBackgroundColor(Color.parseColor("#E8E8E8"));
        }
        if (closedBills.get(position).invoice == null) {
            imageView.setVisibility(View.INVISIBLE);
        } else {
            imageView.setVisibility(View.VISIBLE);
        }
        return view;
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position, ClosedBill bill);
    }

    public void setOnItemClickListener(OnItemClickLitener listener) {
        this.listener = listener;
    }
//
//    @Override
//    public void onBindViewHolder(final BillListViewHolder billListViewHolder, final int position) {
//        billListViewHolder.mBillNumber.setText(closedBills.get(position).sn);
//        billListViewHolder.mBillNumber.setOnClickListener(this);
//        ClosedBill.BillOrder order = closedBills.get(position).orders.get(0);
//        StringBuffer tableName = new StringBuffer();
//        for (int i = 0; i < closedBills.get(position).orders.size(); i++) {
//            tableName.append(" " + closedBills.get(position).orders.get(i).tableName);
//        }
//        billListViewHolder.mTableNumber.setText(tableName);
//
//        billListViewHolder.mTableNumber.setOnClickListener(this);
//        billListViewHolder.mPeopleNumber.setText(Integer.toString(order.personCount));
//
//        billListViewHolder.mPeopleNumber.setOnClickListener(this);
//        billListViewHolder.mAmount.setText(OrderUtil.decimalFormatter.format(closedBills.get(position).amount) + " " + ((closedBills.get(position).payInfo != null) ? " ( " + closedBills.get(position).payInfo + " )" : ""));
//
//        billListViewHolder.mAmount.setOnClickListener(this);
//        billListViewHolder.mSalesman.setText(closedBills.get(position).cashierStaffName);
//
//        billListViewHolder.mSalesman.setOnClickListener(this);
//        billListViewHolder.mClosedBillTime.setText(DateHelper.hmsFormater.format(closedBills.get(position).closedTime));
//
//        billListViewHolder.mClosedBillTime.setOnClickListener(this);
//
//        billListViewHolder.itemView.setOnClickListener(this);
//
//        if (position == mSelection) {
//            billListViewHolder.itemView.setBackgroundColor(Color.parseColor("#D8D8D8"));
//        } else {
//            billListViewHolder.itemView.setBackgroundColor(Color.parseColor("#E8E8E8"));
//        }
//
//    }

//    @Override
//    public BillListViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_item, viewGroup, false);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null)
//                    listener.onItemClick(v, i, closedBills.get(i));
//            }
//        });
//        BillListViewHolder holder = new BillListViewHolder(view);
//        return holder;
//    }


//    @Override
//    public int getItemCount() {
//        if (null != closedBills) {
//            return closedBills.size();
//        }
//        return 0;
//    }
//
//    class BillListViewHolder extends ViewHolder {
//        TextView mBillNumber;
//        TextView mTableNumber;
//        TextView mPeopleNumber;
//        TextView mAmount;
//        TextView mSalesman;
//        TextView mClosedBillTime;
//
//        public BillListViewHolder(View itemView) {
//            super(itemView);
//            mBillNumber = (TextView) itemView.findViewById(R.id.textView_bill_number);
//            mTableNumber = (TextView) itemView.findViewById(R.id.textView_table_number);
//            mPeopleNumber = (TextView) itemView.findViewById(R.id.textView_people_number);
//            mAmount = (TextView) itemView.findViewById(R.id.textView_amount);
//            mSalesman = (TextView) itemView.findViewById(R.id.textView_salesman);
//            mClosedBillTime = (TextView) itemView.findViewById(R.id.textView_closed_time);
//
//
//        }
//    }
}


