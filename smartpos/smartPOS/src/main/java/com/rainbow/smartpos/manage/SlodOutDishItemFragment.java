package com.rainbow.smartpos.manage;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableRow;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.Restaurant;
import com.rainbow.smartpos.util.Listener.OnChangeSlodOutCountListener;
import com.rainbow.smartpos.util.Listener.OnChooseSlodOutListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.SoldoutDetailRequest.ISoldoutListener;
import com.sanyipos.sdk.model.rest.GoodsGroup;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.rest.SubGroupsRest;
import com.sanyipos.sdk.model.scala.Soldout;

import java.util.List;

public class SlodOutDishItemFragment extends Fragment implements OnClickListener {
	static final String LOG_TAG = "SlodOutDishItemFragment";
	public int number;
	SlodOutDishAdapter adapter;
	GridView gridViewOrderDish;
	TableRow tableRowSubCatetory;
	TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
	Button subCats[] = null;
	MainScreenActivity activity;
	Dialog contextActions = null;
	public final static int SUB_CATETORY_ALL = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (MainScreenActivity) getActivity();
		number = getArguments() != null ? getArguments().getInt("number") : 1;
		Log.d(LOG_TAG, "category index number onCreate:" + number);
		GoodsGroup obj = SanyiSDK.rest.goodsGroup.get(number);
		Restaurant.selectedSloidCategory = obj.id;
		Restaurant.selectedSloidSubcategory = -2;
		params.leftMargin = 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(LOG_TAG, "category index number onCreateView:" + number);

		View view = inflater.inflate(R.layout.fragment_slodout_dish_item, container, false);
		// container.addView(view);
		tableRowSubCatetory = (TableRow) view.findViewById(R.id.tableRowSubCategory);
		populateSubCategory();

		adapter = new SlodOutDishAdapter(getActivity(), true , number);
		gridViewOrderDish = (GridView) view.findViewById(R.id.gridViewOrderDish);

		gridViewOrderDish.setAdapter(adapter);
		gridViewOrderDish.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// create new order;
				final ProductRest product = adapter.getItem(position);
				if (!product.soldout) {
					new SlodOutDialog(getActivity(), product.soldout , new OnChooseSlodOutListener() {
						
						@Override
						public void onSlodOutWithCount() {
							// TODO Auto-generated method stub
							new ChangeSlodOutCountDialog(getActivity(), String.format("<font color='#ff0000'>%s</font>", "("+product.name+")"), 0.0, product.productType.isIsWeight() , product.unitName , new OnChangeSlodOutCountListener() {
//								
								@Override
								public void sure(Double count) {
									// TODO Auto-generated method stub
									SanyiScalaRequests.soldoutDishRequest(product.id, count, false, new ISoldoutListener(){


										@Override
										public void onFail(String error) {
											// TODO Auto-generated method stub

											Toast.makeText(activity,error,Toast.LENGTH_LONG).show();


										}

										@Override
										public void onSuccess(Soldout result) {
											// TODO Auto-generated method stub
											product.soldout = true;
											product.setSoldoutCount(result.soldout.count);
											Toast.makeText(activity,"操作成功",Toast.LENGTH_LONG).show();
											adapter.notifyDataSetChanged();
										}
										
									});
								}
								
								@Override
								public void cancel() {
									// TODO Auto-generated method stub
									
								}
							}).show();
						}
						
						@Override
						public void onSlodOutSoon() {
							// TODO Auto-generated method stub
							SanyiScalaRequests.soldoutDishRequest(product.id, 0.0, false, new ISoldoutListener(){

								@Override
								public void onFail(String error) {
									// TODO Auto-generated method stub
									Toast.makeText(activity,error,Toast.LENGTH_LONG).show();


								}

								@Override
								public void onSuccess(Soldout result) {
									// TODO Auto-generated method stub
									product.soldout = true;
									product.setSoldoutCount(0.0);
									Toast.makeText(activity,"操作成功",Toast.LENGTH_LONG).show();
									adapter.notifyDataSetChanged();
								}
								
							});
						}
						
						@Override
						public void onSlodOutLongTime() {
							// TODO Auto-generated method stub
							SanyiScalaRequests.soldoutDishRequest(product.id, 0.0, true, new ISoldoutListener(){


								@Override
								public void onFail(String error) {
									// TODO Auto-generated method stub
									Toast.makeText(activity,error,Toast.LENGTH_LONG).show();


								}

								@Override
								public void onSuccess(Soldout result) {
									// TODO Auto-generated method stub
									product.soldout = true;
									product.setSoldoutCount(0.0);
									product.setLongterm(result.soldout.longterm);
									Toast.makeText(activity,"操作成功",Toast.LENGTH_LONG).show();
									adapter.notifyDataSetChanged();
								}
								
							});
						}
						
						@Override
						public void cancel() {
							// TODO Auto-generated method stub
							
						}
					}).show();
				}else{
					Toast.makeText(activity,"菜品已沽清,无法进行操作",Toast.LENGTH_LONG).show();
				}
				
//				if (!product.isSouldOut) {
//					SanyiRequests.updateSoldStatusRequest(product.id, true, new Request.ICallBack() {
//
//						@Override
//						public void request_timeout() {
//							// TODO Auto-generated method stub
//							MainScreenActivity.toastRequestTimeOut();
//						}
//
//						@Override
//						public void request_fail() {
//							// TODO Auto-generated method stub
//							MainScreenActivity.toastRequestFail();
//						}
//
//						@Override
//						public void onFail(String error) {
//							// TODO Auto-generated method stub
//							MainScreenActivity.toastText(error);
//						}
//
//						@Override
//						public void onSuccess(String status) {
//							product.isSoldOut = true;
//							refreshDish();
//							MainScreenActivity.toastText(product.name + "沽清成功");
//						}
//					});
//
//				}else{
//					Toast.makeText(getActivity(), "菜品已经沽清", 0).show();
//				}

			}
		});
		return view;
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		switch (viewId) {
		case R.id.tableRowSubCategory: {
			Button subCatButton = (Button) v;
			int selectedSloidSubcategory = Integer.parseInt(subCatButton.getTag().toString());
			for (int i = 0; i < subCats.length; i++) {
				if (subCats[i] != null) {
					subCats[i].setSelected(false);
				}
			}
			subCatButton.setSelected(true);
			if (Restaurant.selectedSloidSubcategory != selectedSloidSubcategory) {
				Restaurant.selectedSloidSubcategory = Integer.parseInt(subCatButton.getTag().toString());
				adapter.updateDish();
				adapter.refresh();
			}
			break;
		}
		}
	}
	
	public void setClick(){
		if (null == subCats) {
			return;
		}
		for (int i = 0; i < subCats.length; i++) {
			Button subCatButton = (Button) subCats[i];
//			if (Integer.parseInt(subCatButton.getTag().toString()) == Restaurant.selectedSubcategory) {
//				subCatButton.performClick();
//			}
		}
	}

	public void populateSubCategory() {

		// clear all items in category layout
		if (tableRowSubCatetory.getChildCount() > 0) {
			tableRowSubCatetory.removeAllViews();
		}

		List<SubGroupsRest> subCategories = SanyiSDK.rest.getGoodsGroup(Restaurant.selectedSloidCategory).subgroups;

		subCats = new Button[subCategories.size()];
		for (int j = 0; j < subCategories.size(); ++j) {

			SubGroupsRest subCategory;
			String subCatName;
			String subCatId;

			subCategory = subCategories.get(j);
			subCatName = subCategory.name;
			subCatId = Long.toString(subCategory.id);
			if (subCategory.units.size() != 0) {
				if (Restaurant.selectedSloidSubcategory == -2) {
					Restaurant.selectedSloidSubcategory = subCategory.id;
				}

				Button btn1 = new Button(getActivity());
				subCats[j] = btn1;
				btn1.setText(subCatName);
				btn1.setTextSize(getResources().getDimensionPixelOffset(R.dimen.table_status_bottom_text));
				btn1.setBackgroundResource(R.drawable.subcat_button_selector);
				btn1.setPadding(20, 10, 20, 10);
				params.setMargins(5, 10, 5, 10);
				btn1.setLayoutParams(params);
				if (Integer.parseInt(subCatId) == Restaurant.selectedSloidSubcategory) {
					btn1.setSelected(true);
				} else {
					btn1.setSelected(false);

				}

				btn1.setOnClickListener(this);
				btn1.setId(R.id.tableRowSubCategory);
				btn1.setGravity(Gravity.CENTER);
				btn1.setTag(subCatId);

				tableRowSubCatetory.addView(btn1);
			}
		}
	}

	public void refreshDish() {
		if (adapter != null) {
			this.adapter.refresh();
		}
	}
}
