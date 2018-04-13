package com.rainbow.smartpos.place;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.place.presenter.PlaceDetailPresenterImpl;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.Listener.OnPlaceDishOptionListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.addDetail.model.AddDetaiAction;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.List;

public class DishRecycleAdapter extends BaseAdapter implements OnClickListener {
    private static final String TAG = "DishRecycleAdapter";
    private static final int VIEW_TYPE_ORDER = 0;
    private static final int VIEW_TYPE_SET = 1;
    private Context context;
    public List<OrderDetail> singleDishs = new ArrayList<>();
    public List<OrderDetail> setDishs = new ArrayList<>();
    private LayoutInflater mInflater;
    public PlaceDetailFragment mPlaceDetailFragment;
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private List<OrderDetail> selectedDish = new ArrayList<>();
    private MainScreenActivity activity;
    PlaceDishAdapter placeDishAdapter;
    GridView gridView;
    private PlaceDetailPresenterImpl mPlacePresenter;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(OrderDetail orderDetail, int position, View view);
    }

    public DishRecycleAdapter(Context context, PlaceDetailPresenterImpl mPlacePresenter) {
        super();
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.activity = (MainScreenActivity) context;
        this.mPlacePresenter = mPlacePresenter;
        mPlaceDetailFragment = activity.placeFragment;
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(1, 1, 1, 1);
    }

    public void setOrderDetails(List<OrderDetail> orderDetails, boolean mode) {
        this.singleDishs = new ArrayList<>();
        this.singleDishs.addAll(orderDetails);
        this.setDishs = new ArrayList<>();
        if (mode) {
            for (int i = 0; i < orderDetails.size(); i++) {
                if (orderDetails.get(i).isSet()) {
                    setDishs.add(orderDetails.get(i));
                    singleDishs.remove(orderDetails.get(i));
                } else if (orderDetails.get(i).getChildrenOrderDetail() != null && orderDetails.get(i).getChildrenOrderDetail().size() > 0) {
                    setDishs.add(orderDetails.get(i));
                    singleDishs.remove(orderDetails.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSelectedDetails(List<OrderDetail> orderDetails) {
        this.selectedDish = orderDetails;
    }

    public void clearSelectedDetails() {
        this.selectedDish.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        if (position == 0 && singleDishs.size() > 0) {
            return VIEW_TYPE_ORDER;
        } else {
            return VIEW_TYPE_SET;
        }
    }

//    public void onBindViewHolder(ViewHolder viewHolder, int position) {
//        // TODO Auto-generated method stub
//        if (viewHolder instanceof SingleDishViewHolder) {
//            bindSingleDishHolder((SingleDishViewHolder) viewHolder);
//        } else if (viewHolder instanceof SetDishViewHolder) {
//            bindSetDishHolder((SetDishViewHolder) viewHolder, position);
//            viewHolder.itemView.setTag(position);
//        }
//    }

//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
//        // TODO Auto-generated method stub
//        View v = null;
//        ViewHolder holder = null;
//        if (viewtype == VIEW_TYPE_ORDER) {
//            v = mInflater.inflate(R.layout.single_dish_item, parent, false);
//            holder = new SingleDishViewHolder(v);
//        } else {
//            v = mInflater.inflate(R.layout.place_set_dish_item, parent, false);
//            v.setOnClickListener(this);
//            holder = new SetDishViewHolder(v);
//        }
//        return holder;
//    }

    public class SingleDishViewHolder {
        GridView singleDishGridView;

        public SingleDishViewHolder(View view) {

            // TODO Auto-generated constructor stub
            this.singleDishGridView = (GridView) view.findViewById(R.id.singleDishGridView);
        }

    }

    public class SetDishViewHolder {
        TextView textViewDishName;
        TextView textViewDishWeight;
        TextView textViewDishPrice;
        TextView textViewDishCount;
        TextView textViewDishReturnCount;
        ImageView isChooseBox;
        LinearLayout remarkLayout;
        GridView setDishGridView;

        public SetDishViewHolder(View view) {
            // TODO Auto-generated constructor stub
            this.textViewDishName = (TextView) view.findViewById(R.id.textViewDishName);
            this.textViewDishWeight = (TextView) view.findViewById(R.id.textViewDishWeight);
            this.textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
            this.textViewDishCount = (TextView) view.findViewById(R.id.textViewDishCount);
            this.textViewDishReturnCount = (TextView) view.findViewById(R.id.textViewDishReturnCount);
            this.isChooseBox = (ImageView) view.findViewById(R.id.set_choose_box);
            this.setDishGridView = (GridView) view.findViewById(R.id.setItemDishGridView);
            this.remarkLayout = (LinearLayout) view.findViewById(R.id.remarkLayout);
        }

    }

    public void bindSingleDishHolder(SingleDishViewHolder viewHolder) {
        final PlaceDishAdapter placeDishAdapter = new PlaceDishAdapter(context, singleDishs, selectedDish);
        viewHolder.singleDishGridView.setAdapter(placeDishAdapter);
        viewHolder.singleDishGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                mOnItemClickListener.onItemClick(singleDishs.get(position), position, view);
            }
        });
    }

    @Override
    public int getCount() {
        if (singleDishs.size() == 0 && setDishs.size() == 0) return 0;
        if (singleDishs.size() == 0 && setDishs.size() > 0) return setDishs.size();
        return setDishs.size() + 1;
    }

    public OrderDetail getSetDish(int position) {
        if (singleDishs.size() == 0) {

            return setDishs.get(position);
        } else {
            return setDishs.get(position - 1);
        }
    }

    public OrderDetail getItem(int position) {

        return singleDishs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void refreshSingleDish(int position, View view) {
        if (placeDishAdapter != null) {
            placeDishAdapter.setSelectedDetails(selectedDish);
            placeDishAdapter.updateView(view, position);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (singleDishs.size() > 0 && position == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.single_dish_item, null);
            gridView = (GridView) view.findViewById(R.id.singleDishGridView);
            placeDishAdapter = new PlaceDishAdapter(context, singleDishs, selectedDish);
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    mOnItemClickListener.onItemClick(singleDishs.get(position), position, view);
                }
            });
            gridView.setAdapter(placeDishAdapter);
            gridView.setTag(view);
            return view;
        }
        if (position >= 1 || (singleDishs.size() == 0 && position == 0)) {
            view = LayoutInflater.from(context).inflate(R.layout.place_set_dish_item, null);
            TextView textViewDishName = (TextView) view.findViewById(R.id.textViewDishName);
            TextView textViewDishWeight = (TextView) view.findViewById(R.id.textViewDishWeight);
            TextView textViewDishPrice = (TextView) view.findViewById(R.id.textViewDishPrice);
            TextView textViewDishCount = (TextView) view.findViewById(R.id.textViewDishCount);
            TextView textViewDishReturnCount = (TextView) view.findViewById(R.id.textViewDishReturnCount);
            ImageView isChooseBox = (ImageView) view.findViewById(R.id.set_choose_box);
            TextView textViewDishCurrPirce = (TextView) view.findViewById(R.id.currPriceTextView);
            LinearLayout remarkLayout = (LinearLayout) view.findViewById(R.id.remarkLayout);
            TextView currPrice = (TextView) view.findViewById(R.id.currPriceTextView);
            TextView holdeLayout = (TextView) view.findViewById(R.id.holdTextView);
            TextView sendLayout = (TextView) view.findViewById(R.id.sendTextView);
            TextView quanLayout = (TextView) view.findViewById(R.id.quanTextView);
            TextView changeLayout = (TextView) view.findViewById(R.id.changedTextView);
            GridView setDishGridView = (GridView) view.findViewById(R.id.setItemDishGridView);
            final OrderDetail orderDetail;
            if (singleDishs.size() > 0) {
                orderDetail = setDishs.get(position - 1);
            } else {
                orderDetail = setDishs.get(position);
            }
            textViewDishName.setText(orderDetail.getName());
            textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(orderDetail.getCurrentPrice()));
            if (orderDetail.isFree()) {
                textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(orderDetail.getOriginPrice()));
            }
            if (orderDetail.isWeight()) {
                textViewDishWeight.setVisibility(View.VISIBLE);
                textViewDishWeight.setText(orderDetail.getWeight() + orderDetail.getUnitName());
            }
//            remarkLayout.removeAllViews();
            textViewDishCount.setText("x" + orderDetail.getQuantity());
            isChooseBox.setVisibility(View.GONE);
            if (orderDetail.getQuantity() == orderDetail.getVoid_quantity()) {
                textViewDishName.setTextColor(context.getResources().getColor(R.color.Red));
                textViewDishPrice.setTextColor(context.getResources().getColor(R.color.Red));
                textViewDishCount.setTextColor(context.getResources().getColor(R.color.Red));
                textViewDishReturnCount.setVisibility(View.VISIBLE);
                textViewDishReturnCount.setText(context.getString(R.string.has_return_dish));
                textViewDishName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            } else if (orderDetail.getQuantity() > orderDetail.getVoid_quantity() && orderDetail.getVoid_quantity() > 0) {
                textViewDishReturnCount.setVisibility(View.VISIBLE);
                textViewDishReturnCount.setText("退量 " + orderDetail.getVoid_quantity());
            }
            final PlaceSetItemDishAdapter placeSetItemDishAdapter = new PlaceSetItemDishAdapter(context, orderDetail);
            setDishGridView.setAdapter(placeSetItemDishAdapter);
            setDishGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    final OrderDetail clickorderDetail = placeSetItemDishAdapter.getItem(position);
                    if (clickorderDetail.isAvailable() && clickorderDetail.getPublicCookMethod().size() > 0 || clickorderDetail.getIngredient().size() > 0)
                        new PlaceDishOptionDialog().show(context, clickorderDetail, new OnPlaceDishOptionListener() {
                            @Override
                            public void onReturnDish() {

                            }

                            @Override
                            public void onIsFreeDish(boolean isFree, StaffRest superUser) {

                            }


                            @Override
                            public void onChangePrice(StaffRest superUser) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onWeightDish() {
                                // TODO Auto-generated method stub
                            }

                        });
                    if (orderDetail.childrenOrderDetail != null && orderDetail.childrenOrderDetail.size() > 0) {
                        new PlaceDishOptionDialog().show(context, clickorderDetail, new Listener.OnPlaceDishOptionListener() {

                            @Override
                            public void onReturnDish() {
                                mPlacePresenter.returnDish(clickorderDetail);
                            }

                            @Override
                            public void onIsFreeDish(boolean b, StaffRest superUser) {
                                clickorderDetail.setAuth_staff_id(superUser.id);
                                mPlacePresenter.changeDish(new PlaceDetailPresenterImpl.ListParams(clickorderDetail).getOrderDetails(), b ? AddDetaiAction.ACTION_GIFT : AddDetaiAction.ACTION_UNDOGIFT);
                            }

                            @Override
                            public void onWeightDish() {
                                new WeightDialog().show(context, context.getString(R.string.weight_dish), String.valueOf(clickorderDetail.getWeight()), ChangePriceDialog.CHANGE_WEIGHT, new Listener.OnChangePriceListener() {
                                    @Override
                                    public void onSure(Double count) {
                                        mPlacePresenter.changeDishPriceAWeight(clickorderDetail, AddDetaiAction.ACTION_CHANGEWEIGHT, count, SanyiSDK.currentUser);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                            }

                            @Override
                            public void onChangePrice(final StaffRest superUser) {
                                new ChangePriceDialog().show(context, clickorderDetail, ChangePriceDialog.CHANGE_PRICE, new Listener.OnChangePriceListener() {
                                    @Override
                                    public void onSure(Double count) {
                                        mPlacePresenter.changeDishPriceAWeight(clickorderDetail, AddDetaiAction.ACTION_CHANGEPRICE, count, superUser);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });

                            }
                        });
                    }
                }
            });

            if (singleDishs.size() == 0 && position == 0) {
                if (orderDetail.isHold() && holdeLayout != null) {
//                addRemarkText(remarkLayout, "挂");
                    holdeLayout.setVisibility(View.VISIBLE);
                } else if(holdeLayout != null)
                    holdeLayout.setVisibility(View.GONE);
                if (orderDetail.isFree() && sendLayout != null) {
//                addRemarkText(remarkLayout, "赠");
                    sendLayout.setVisibility(View.VISIBLE);
                } else if(sendLayout != null)
                    sendLayout.setVisibility(View.GONE);
                if (orderDetail.getCouponId() > 0 && quanLayout != null) {
//                addRemarkText(remarkLayout, "劵");
                    quanLayout.setVisibility(View.VISIBLE);
                } else if(quanLayout != null)
                    quanLayout.setVisibility(View.GONE);
                if (orderDetail.isPriceChanged() && !orderDetail.isMarket() && changeLayout != null) {
//                addRemarkText(remarkLayout, "改");
                    changeLayout.setVisibility(View.VISIBLE);
                } else if(changeLayout != null)
                    changeLayout.setVisibility(View.GONE);
                if (orderDetail.isMarket() && currPrice != null) {
                    currPrice.setVisibility(View.VISIBLE);
//                addRemarkText(remarkLayout, "时");
                } else if(currPrice != null)
                    currPrice.setVisibility(View.GONE);
            }
            return view;
        }
        return view;
    }

    public void bindSetDishHolder(SetDishViewHolder viewHolder, int position) {
        OrderDetail orderDetail;
        if (singleDishs.size() > 0) {
            orderDetail = setDishs.get(position - 1);
        } else {
            orderDetail = setDishs.get(position);
        }
        viewHolder.textViewDishName.setText(orderDetail.getName());
        viewHolder.textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(orderDetail.getCurrentPrice()));
        if (orderDetail.isFree()) {
            viewHolder.textViewDishPrice.setText(OrderUtil.dishPriceFormatter.format(orderDetail.getOriginPrice()));
        }
        if (orderDetail.isWeight()) {
            viewHolder.textViewDishWeight.setVisibility(View.VISIBLE);
            viewHolder.textViewDishWeight.setText(orderDetail.getWeight() + orderDetail.getUnitName());
        }
        viewHolder.remarkLayout.removeAllViews();
        viewHolder.textViewDishCount.setText("x" + orderDetail.getQuantity());
        viewHolder.isChooseBox.setVisibility(View.GONE);
        if (orderDetail.getQuantity() == orderDetail.getVoid_quantity()) {
            viewHolder.textViewDishName.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishPrice.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishCount.setTextColor(context.getResources().getColor(R.color.Red));
            viewHolder.textViewDishReturnCount.setVisibility(View.VISIBLE);
            viewHolder.textViewDishReturnCount.setText(context.getString(R.string.has_return_dish));
            viewHolder.textViewDishName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else if (orderDetail.getQuantity() > orderDetail.getVoid_quantity() && orderDetail.getVoid_quantity() > 0) {
            viewHolder.textViewDishReturnCount.setVisibility(View.VISIBLE);
            viewHolder.textViewDishReturnCount.setText("退量 " + orderDetail.getVoid_quantity());
        }
        final PlaceSetItemDishAdapter placeSetItemDishAdapter = new PlaceSetItemDishAdapter(context, orderDetail);
        viewHolder.setDishGridView.setAdapter(placeSetItemDishAdapter);
        viewHolder.setDishGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                final OrderDetail orderDetail = placeSetItemDishAdapter.getItem(position);
                if (orderDetail.isAvailable() && orderDetail.getPublicCookMethod().size() > 0 || orderDetail.getIngredient().size() > 0)
                    new PlaceDishOptionDialog().show(context, orderDetail, new OnPlaceDishOptionListener() {
                        @Override
                        public void onReturnDish() {

                        }

                        @Override
                        public void onIsFreeDish(boolean isFree, StaffRest superUser) {

                        }


                        @Override
                        public void onChangePrice(StaffRest superUser) {
                            // TODO Auto-generated method stub
                        }


                        @Override
                        public void onWeightDish() {
                            // TODO Auto-generated method stub
                        }

                    });
            }
        });

        if (orderDetail.isHold()) {
            addRemarkText(viewHolder.remarkLayout, "挂");
        }
        if (orderDetail.isFree()) {
            addRemarkText(viewHolder.remarkLayout, "赠");
        }
        if (orderDetail.getCouponId() > 0) {
            addRemarkText(viewHolder.remarkLayout, "劵");
        }
        if (orderDetail.isPriceChanged() && !orderDetail.isMarket()) {
            addRemarkText(viewHolder.remarkLayout, "改");
        }
        if (orderDetail.isMarket()) {
            addRemarkText(viewHolder.remarkLayout, "时");
        }

    }


    public void addRemarkText(LinearLayout layout, String remarkText) {
        TextView textView = new TextView(context);
        textView.setText(remarkText);
        textView.setTextSize(context.getResources().getDimension(R.dimen.app_info_text_size));
        textView.setPadding(2, 0, 2, 0);
        textView.setTextColor(Color.parseColor("#FF0000"));
        textView.setBackgroundResource(R.drawable.red_remark_bg);
        layout.addView(textView, params);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(setDishs.get((Integer) v.getTag() - 1), (Integer) v.getTag() - 1, v);
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
