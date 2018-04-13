package com.rainbow.smartpos.manage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnChangeSlodOutCountListener;
import com.rainbow.smartpos.util.Listener.OnChooseSlodOutListener;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.SoldoutDetailRequest.ISoldoutListener;
import com.sanyipos.sdk.model.rest.ProductRest;
import com.sanyipos.sdk.model.scala.Soldout;

public class HasSlodOutFragment extends Fragment {
	private View view;
	private GridView hasSlodOutGrid;
	private SlodOutDishAdapter adapter;
	private MainScreenActivity activity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_has_slod_out, container, false);
		activity = (MainScreenActivity) getActivity();
		initView();
		return view;
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (adapter != null) {
			adapter.refresh();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		hasSlodOutGrid = (GridView) view.findViewById(R.id.has_slod_out_grid);
		adapter = new SlodOutDishAdapter(getActivity(), false , MainScreenActivity.HAS_SLODOUT_FRAGMENT);
		hasSlodOutGrid.setAdapter(adapter);
		hasSlodOutGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// create new order;
				final ProductRest product = adapter.getItem(position);
				if (product.soldout) {
					if(product.getSoldoutCount() > 0){
						new SlodOutDialog(getActivity(), true, new OnChooseSlodOutListener() {
							
							@Override
							public void onSlodOutWithCount() {
								// TODO Auto-generated method stub
								new ChangeSlodOutCountDialog(getActivity(), String.format("<font color='#ff0000'>%s</font>", "("+product.name+")"), product.getSoldoutCount(), product.productType.isIsWeight(), product.unitName, new OnChangeSlodOutCountListener() {
									
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
								SanyiScalaRequests.cancelSoldoutDishRequest(product.id, new Request.ICallBack() {


									@Override
									public void onFail(String error) {
										// TODO Auto-generated method stub

										Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
									}
									
									@Override
									public void onSuccess(String status) {
										// TODO Auto-generated method stub
										product.soldout = false;
										product.setLongterm(false);
										product.setSoldoutCount(0.0);
										adapter.refresh();

										Toast.makeText(activity,status,Toast.LENGTH_LONG).show();
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
					}else if (product.getSoldoutCount() == 0) {
						SanyiScalaRequests.cancelSoldoutDishRequest(product.id, new Request.ICallBack() {


							@Override
							public void onFail(String error) {
								// TODO Auto-generated method stub
								Toast.makeText(activity,error,Toast.LENGTH_LONG).show();
							}
							
							@Override
							public void onSuccess(String status) {
								// TODO Auto-generated method stub
								product.soldout = false;
								product.setLongterm(false);
								product.setSoldoutCount(0.0);
								adapter.refresh();
								Toast.makeText(activity,status,Toast.LENGTH_LONG).show();
							}
						});
					}
					
				}
//				SanyiRequests.updateSoldStatusRequest(product.id, false, new Request.ICallBack() {
//
//					@Override
//					public void request_timeout() {
//						// TODO Auto-generated method stub
//						MainScreenActivity.toastRequestTimeOut();
//					}
//
//					@Override
//					public void request_fail() {
//						// TODO Auto-generated method stub
//						MainScreenActivity.toastRequestFail();
//					}
//
//					@Override
//					public void onFail(String error) {
//						// TODO Auto-generated method stub
//						MainScreenActivity.toastText(error);
//					}
//
//					@Override
//					public void onSuccess(String status) {
//						// TODO Auto-generated method stub
//						product.isSouldOut = false;
//						refreshDish();
//						MainScreenActivity.toastText(product.name + "反沽清成功");
//					}
//				});
			}
		});
		hasSlodOutGrid.setEmptyView(view.findViewById(R.id.empty_view));
	}

	public void refreshDish() {
		if (adapter != null) {
			adapter.updateDish();
			adapter.refresh();
		}
	}
}
