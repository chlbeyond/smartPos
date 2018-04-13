package com.rainbow.smartpos.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OtherFragment extends Fragment implements OnClickListener, OnItemClickListener {
    long selectedReturnDishID;
    public List<OtherMenu> menuList = new ArrayList<OtherMenu>();
    public GridView gridView_order_dish_other;
    public Button buttonContinueOrder;
    public Button buttonOrderItemOptionConfirm;
    public OtherFragmentInterface oFragmentInterface;
    public List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
    public OrderFragmentOtherAdapter orderFragmentOtherAdapter;
    public static MainScreenActivity activity;
    public static OrderFragment orderFragment;

    public void setOrderList(List<OrderDetail> orderList) {
        this.orderDetailList = orderList;
    }

    public interface OtherFragmentInterface {
        void onClick(View v);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_order_dish_other, container, false);
        activity = (MainScreenActivity) getActivity();
//        oFragmentInterface = activity.orderFragment;
//        orderFragment = activity.orderFragment;
        gridView_order_dish_other = (GridView) v.findViewById(R.id.gridView_order_dish_other);

//		OtherMenu otherMenu0 = new OtherMenu();
//		otherMenu0.setOtherId(OrderFragment.OTHER_MENU_CHANGE_DISH);
//		otherMenu0.setOtherName(getResources().getString(R.string.change_dish));
//		otherMenu0.setImageId(R.drawable.changedish_normal);
//		otherMenu0.setNoneImageId(R.drawable.changedish_none);
//		menuList.add(otherMenu0);
//
//		OtherMenu otherMenu1 = new OtherMenu();
//		otherMenu1.setOtherId(OrderFragment.OTHER_MENU_PROCESS_DISH);
//		otherMenu1.setOtherName(getResources().getString(R.string.process_dish));
//		otherMenu1.setImageId(R.drawable.process_normal);
//		otherMenu1.setNoneImageId(R.drawable.process_none);
//		menuList.add(otherMenu1);
//
//		OtherMenu otherMenu2 = new OtherMenu();
//		otherMenu2.setOtherId(OrderFragment.OTHER_MENU_INGRIDIENT);
//		otherMenu2.setOtherName(getResources().getString(R.string.more_ingridient));
//		otherMenu2.setImageId(R.drawable.more_ingridient_normal);
//		otherMenu2.setNoneImageId(R.drawable.more_ingridient_none);
//		menuList.add(otherMenu2);
//
//		OtherMenu otherMenu3 = new OtherMenu();
//		otherMenu3.setOtherId(OrderFragment.OTHER_MENU_RETURN_ALL_DISH);
//		otherMenu3.setOtherName(getResources().getString(R.string.return_all_dish));
//		otherMenu3.setImageId(R.drawable.return_all_dish_normal);
//		otherMenu3.setNoneImageId(R.drawable.return_all_dish_none);
//		menuList.add(otherMenu3);
//
//		OtherMenu otherMenu4 = new OtherMenu();
//		otherMenu4.setOtherId(OrderFragment.OTHER_MENU_WEIGHT);
//		otherMenu4.setOtherName(getResources().getString(R.string.weight));
//		otherMenu4.setImageId(R.drawable.weight_normal);
//		otherMenu4.setNoneImageId(R.drawable.weight_none);
//		menuList.add(otherMenu4);
//
//		OtherMenu otherMenu5 = new OtherMenu();
//		otherMenu5.setOtherId(OrderFragment.OTHER_MENU_ORDER_PLACE_NOT_PRINT);
//		otherMenu5.setOtherName(getResources().getString(R.string.order_fragment_order_place_not_print));
//		otherMenu5.setImageId(R.drawable.order_place_not_print_normal);
//		otherMenu5.setNoneImageId(R.drawable.order_place_not_print_none);
//		menuList.add(otherMenu5);
//
//		OtherMenu otherMenu6 = new OtherMenu();
//		otherMenu6.setOtherId(OrderFragment.OTHER_MENU_CANCEL_FREE);
//		otherMenu6.setOtherName(getResources().getString(R.string.order_fragment_cancel_free));
//		otherMenu6.setImageId(R.drawable.cancel_free_normal);
//		otherMenu6.setNoneImageId(R.drawable.cancel_free_none);
//		menuList.add(otherMenu6);
//
//		OtherMenu otherMenu7 = new OtherMenu();
//		otherMenu7.setOtherId(OrderFragment.OTHER_MENU_CHANGE_PEOPLE);
//		otherMenu7.setImageId(R.drawable.person_count_normal);
//		otherMenu7.setOtherName(getResources().getString(R.string.change_people));
//		otherMenu7.setNoneImageId(R.drawable.person_count_none);
//		menuList.add(otherMenu7);
//
//		OtherMenu otherMenu8 = new OtherMenu();
//		otherMenu8.setOtherId(OrderFragment.OTHER_MENU_COUPON);
//		otherMenu8.setImageId(R.drawable.coupon_normal);
//		otherMenu8.setOtherName(getResources().getString(R.string.voucher));
//		otherMenu8.setNoneImageId(R.drawable.coupon_none);
//		menuList.add(otherMenu8);
//
//		// OtherMenu otherMenu9 = new OtherMenu();
//		// otherMenu9.setOtherId(OrderFragment.OTHER_MENU_PRE_ORDER);
//		// otherMenu9.setImageId(R.drawable.pre_order_normal);
//		// otherMenu9.setOtherName(getResources().getString(R.string.pre_order));
//		// menuList.add(otherMenu9);
//		OtherMenu otherMenu10 = new OtherMenu();
//		otherMenu10.setOtherId(OrderFragment.MENU_REPRINT_ORDER);
//		otherMenu10.setImageId(R.drawable.reprint_order_normal);
//		otherMenu10.setOtherName(getResources().getString(R.string.reprint_order));
//		otherMenu10.setNoneImageId(R.drawable.reprint_order_none);
//		menuList.add(otherMenu10);
//
//		OtherMenu otherMenu11 = new OtherMenu();
//		otherMenu11.setOtherId(OrderFragment.MENU_CANCEL_PREPRINT_STATUS);
//		otherMenu11.setImageId(R.drawable.cancel_preprint_normal);
//		otherMenu11.setOtherName(getResources().getString(R.string.cancel_preprint_state));
//		otherMenu11.setNoneImageId(R.drawable.cancel_preprint_none);
//		menuList.add(otherMenu11);
//		
//		OtherMenu otherMenu12 = new OtherMenu();
//		otherMenu12.setOtherId(OrderFragment.MENU_FOOD_REMARK_EDIT);
//		otherMenu12.setImageId(R.drawable.food_remark_normal);
//		otherMenu12.setOtherName(getResources().getString(R.string.edit_food_remark));
//		otherMenu12.setNoneImageId(R.drawable.food_remark_none);
//		otherMenu12.setKeyHint("INS");
//		menuList.add(otherMenu12);
//		
//		OtherMenu otherMenu13 = new OtherMenu();
//		otherMenu13.setOtherId(OrderFragment.MENU_DETAIL_OP);
//		otherMenu13.setImageId(R.drawable.detail_op_normal);
//		otherMenu13.setOtherName(getResources().getString(R.string.detail_op));
//		otherMenu13.setNoneImageId(R.drawable.detail_op_none);
//		menuList.add(otherMenu13);

        orderFragmentOtherAdapter = new OrderFragmentOtherAdapter();
        gridView_order_dish_other.setAdapter(orderFragmentOtherAdapter);
        gridView_order_dish_other.setOnItemClickListener(this);
        buttonContinueOrder = (Button) v.findViewById(R.id.buttonContinueOrder);
        buttonContinueOrder.setOnClickListener(this);

        return v;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        menuList.clear();
    }

    public class OtherMenu {
        public int otherId;
        public String otherName;
        public String keyHint = "";
        public int imageId = 0;
        public int noneImageId = 0;

        public int getImageId() {
            return imageId;
        }

        public int getNoneImageId() {
            return noneImageId;
        }

        public void setImageId(int id) {
            imageId = id;
        }

        public void setNoneImageId(int noId) {
            noneImageId = noId;
        }

        public int getOtherId() {
            return otherId;
        }

        public void setOtherId(int otherId) {
            this.otherId = otherId;
        }

        public String getOtherName() {
            return otherName;
        }

        public void setOtherName(String otherName) {
            this.otherName = otherName;
        }

        public String getKeyHint() {
            return keyHint;
        }

        public void setKeyHint(String keyHint) {
            this.keyHint = keyHint;
        }


    }

    public class OrderFragmentOtherAdapter extends BaseAdapter {

        public LayoutInflater inflater;

        public OrderFragmentOtherAdapter() {
            this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return menuList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        // TextColor #eae8e9
        // background #7f8c9d
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = inflater.inflate(R.layout.fragment_order_other_gridview_item, parent, false);
//			TextView textView_fragment_order_other_gridView = (TextView) view.findViewById(R.id.textView_fragment_order_other_gridView);
//			textView_fragment_order_other_gridView.setText(menuList.get(position).otherName);
//			TextView keyboard_shortcuts_hint = (TextView) view.findViewById(R.id.keyboard_shortcuts_hint);
//			keyboard_shortcuts_hint.setVisibility(View.GONE);
//			if (!menuList.get(position).keyHint.isEmpty()) {
//				keyboard_shortcuts_hint.setText(menuList.get(position).keyHint);
//				keyboard_shortcuts_hint.setVisibility(View.VISIBLE);
//			}
//			ImageView imageView_fragment_order_other_gridView = (ImageView) view.findViewById(R.id.imageView_fragment_order_other_gridView);
//			if (menuList.get(position).imageId != 0) {
//				imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).noneImageId);
//			}
//			view.setEnabled(false);
//			for (int i = 0; i < orderFragment.orderListAdpater.orderDetailList.size(); i++) {
//				if (orderFragment.orderListAdpater.orderDetailList.get(i).isPlaced()) {
//					if (menuList.get(position).otherId == orderFragment.OTHER_MENU_CHANGE_DISH) {
//						imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//						view.setEnabled(true);
//						view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//						break;
//					}
//					if (menuList.get(position).otherId == orderFragment.MENU_DETAIL_OP) {
//						imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//						view.setEnabled(true);
//						view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//						break;
//					}
//				}
//			}
//			if (orderFragment.orderListAdpater.getSelectedIndex() != -1) {
//				if (orderFragment.orderListAdpater.getCurrentSelectOrder().isPlaced()) {
//					// if (menuList.get(position).otherId ==
//					// orderFragment.OTHER_MENU_CHANGE_DISH) {
//					// imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//					// view.setEnabled(true);
//					// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//					// }
//
//				} else {
//					if (menuList.get(position).otherId == orderFragment.OTHER_MENU_INGRIDIENT) {
//						if (!orderFragment.orderListAdpater.getCurrentSelectOrder().isSet()) {
//							imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//							view.setEnabled(true);
//							view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//						}
//					}
//					if (menuList.get(position).otherId == orderFragment.MENU_FOOD_REMARK_EDIT) {
//						imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//						view.setEnabled(true);
//						view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//					}
//
//				}
//				if (orderFragment.orderListAdpater.getCurrentSelectOrder().isWeight()) {
//					if (orderFragment.isForMergeTable) {
//						if (menuList.get(position).otherId == orderFragment.OTHER_MENU_WEIGHT) {
//							imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).noneImageId);
//							view.setEnabled(false);
//							view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//						}
//					}else{
//						if (menuList.get(position).otherId == orderFragment.OTHER_MENU_WEIGHT) {
//							imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//							view.setEnabled(true);
//							view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//						}
//					}
//					
//				}
//				if (orderFragment.orderListAdpater.getCurrentSelectOrder().isFree() && orderFragment.orderListAdpater.getCurrentSelectOrder().getParent() == null) {
//					if (menuList.get(position).otherId == orderFragment.OTHER_MENU_CANCEL_FREE) {
//						imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//						view.setEnabled(true);
//						view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//					}
//				}
//				
//			}
//			if (orderFragment.orderDishFragment.isEnableCheckButton()) {
//				if (menuList.get(position).otherId == orderFragment.OTHER_MENU_RETURN_ALL_DISH) {
//					imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//					view.setEnabled(true);
//					view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//				}
//			}
//			if (orderFragment.orderDishFragment.isEnableOrderPlaceButton()) {
//				if (menuList.get(position).otherId == orderFragment.OTHER_MENU_ORDER_PLACE_NOT_PRINT) {
//					imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//					view.setEnabled(true);
//					view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//				}
//			}
//			if (menuList.get(position).otherId == orderFragment.OTHER_MENU_PROCESS_DISH) {
//				imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//				view.setEnabled(true);
//				view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//			}
//			if (menuList.get(position).otherId == orderFragment.OTHER_MENU_CHANGE_PEOPLE) {
//				imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//				view.setEnabled(true);
//				view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//			}
//			if (menuList.get(position).otherId == orderFragment.OTHER_MENU_COUPON) {
//				imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//				view.setEnabled(true);
//				view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//			}
//			if (menuList.get(position).otherId == orderFragment.OTHER_MENU_PRE_ORDER) {
//				imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//				view.setEnabled(true);
//				view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//			}
//			if (menuList.get(position).otherId == orderFragment.MENU_REPRINT_ORDER) {
//				imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//				view.setEnabled(true);
//				view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//			}
//			if (menuList.get(position).otherId == orderFragment.MENU_CANCEL_PREPRINT_STATUS) {
//				imageView_fragment_order_other_gridView.setBackgroundResource(menuList.get(position).imageId);
//				view.setEnabled(true);
//				view.setBackgroundDrawable(getResources().getDrawable(R.drawable.orderfragment_other_button));
//			}
            return view;
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        orderFragment.removeOrderitemOptionFragment();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if (!view.isEnabled()) {
            return;
        }
        switch (menuList.get(position).getOtherId()) {
//		case OrderFragment.OTHER_MENU_RETURN_ALL_DISH:
//			if (SanyiSDK.currentUser.hasPermissionOf(Restaurant.PERMISSION_RETURN_DISH)) {
//				returnDish(SanyiSDK.currentUser);
//			} else {
//				NumberPad np = new NumberPad();
//				np.setAdditionalText("输入密码或刷卡授权");
//				np.show(getActivity(), "退菜", NumberPad.HIDE_INPUT, new NumberPad.numbPadInterface() {
//					public String numPadInputValue(String value) {
//						StaffRest superUser = SanyiSDK.getSDK().getStaffByAccessCode(value, false);
//						if (superUser != null && superUser.hasPermissionOf(Restaurant.PERMISSION_FREE_DISH)) {
//							returnDish(superUser);
//						} else {
//							Toast.makeText(getActivity().getApplicationContext(), "密码不对或没有权限，授权错误", Toast.LENGTH_LONG).show();
//						}
//						return null;
//					}
//
//					public String numPadCanceled() {
//						return null;
//					}
//				});
//			}
//			break;
//		case OrderFragment.OTHER_MENU_PROCESS_DISH:
//			view.setId(OrderFragment.OTHER_MENU_PROCESS_DISH);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.OTHER_MENU_CHANGE_DISH:
//			view.setId(OrderFragment.OTHER_MENU_CHANGE_DISH);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.OTHER_MENU_INGRIDIENT:
//			view.setId(OrderFragment.OTHER_MENU_INGRIDIENT);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.OTHER_MENU_WEIGHT:
//			view.setId(OrderFragment.OTHER_MENU_WEIGHT);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.OTHER_MENU_CHANGE_PEOPLE:
//			view.setId(OrderFragment.OTHER_MENU_CHANGE_PEOPLE);
//			oFragmentInterface.onClick(view);
//			break;
////		case OrderFragment.OTHER_MENU_ORDER_PLACE_NOT_PRINT:
////			view.setId(R.id.buttonOrderPlaceOrderNoPrint);
////			oFragmentInterface.onClick(view);
////			break;
//		case OrderFragment.OTHER_MENU_CANCEL_FREE:
//			view.setId(OrderFragment.OTHER_MENU_CANCEL_FREE);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.OTHER_MENU_COUPON:
//			view.setId(OrderFragment.OTHER_MENU_COUPON);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.OTHER_MENU_PRE_ORDER:
//			view.setId(OrderFragment.OTHER_MENU_PRE_ORDER);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.MENU_REPRINT_ORDER:
//			view.setId(OrderFragment.MENU_REPRINT_ORDER);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.MENU_CANCEL_PREPRINT_STATUS:
//			view.setId(OrderFragment.MENU_CANCEL_PREPRINT_STATUS);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.MENU_FOOD_REMARK_EDIT:
//			view.setId(OrderFragment.MENU_FOOD_REMARK_EDIT);
//			oFragmentInterface.onClick(view);
//			break;
//		case OrderFragment.MENU_DETAIL_OP:
//			view.setId(OrderFragment.MENU_DETAIL_OP);
//			oFragmentInterface.onClick(view);
//			break;
            default:
                break;
        }
    }
}
