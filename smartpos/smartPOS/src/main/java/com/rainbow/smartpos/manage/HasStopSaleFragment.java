package com.rainbow.smartpos.manage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnSureListener;
import com.rainbow.smartpos.util.ReminderDialog;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.model.rest.ProductRest;

public class HasStopSaleFragment extends Fragment {
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
		adapter = new SlodOutDishAdapter(getActivity(), false , MainScreenActivity.HAS_STOPSALE_FRAGMENT);
		hasSlodOutGrid.setAdapter(adapter);
		hasSlodOutGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// create new order;
				final ProductRest product = adapter.getItem(position);
				if (product.soldout) {
					if (product.isLongterm()) {
						new ReminderDialog(getActivity(), String.format("确定要将停售的菜品“ <font color='#ff0000'>%s</font> ”恢复正常销售吗?", product.name), new OnSureListener() {
							
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								SanyiScalaRequests.cancelSoldoutDishRequest(product.id, new Request.ICallBack() {


									@Override
									public void onFail(String error) {
										// TODO Auto-generated method stub
										Toast.makeText(activity,error, Toast.LENGTH_LONG).show();
									}
									
									@Override
									public void onSuccess(String status) {
										// TODO Auto-generated method stub
										product.soldout = false;
										product.setLongterm(false);
										product.setSoldoutCount(0.0);
										refreshDish();

										Toast.makeText(activity,"操作成功",Toast.LENGTH_LONG).show();
									}
								});
							}
							
							@Override
							public void onFailed() {
								// TODO Auto-generated method stub
								
							}
						});
						
					}
				}
			}
		});
		TextView empty_view = (TextView) view.findViewById(R.id.empty_view);
		empty_view.setText("无停售菜品");
		hasSlodOutGrid.setEmptyView(empty_view);
	}

	public void refreshDish() {
		if (adapter != null) {
			adapter.updateDish();
			adapter.refresh();
		}
	}
}
