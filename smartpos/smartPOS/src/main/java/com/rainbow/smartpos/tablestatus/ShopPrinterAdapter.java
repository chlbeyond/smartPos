package com.rainbow.smartpos.tablestatus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;

import java.util.HashMap;
import java.util.Map;

public class ShopPrinterAdapter extends BaseAdapter {
	Context context;
	Map<String, Boolean> map = new HashMap<String, Boolean>();
	int currentSelectPrint = -1;

	public ShopPrinterAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return SanyiSDK.rest.operationData.shopPrinters.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return SanyiSDK.rest.operationData.shopPrinters.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.shop_printer_list_item, null);
		TextView textView_shop_printer_item_name = (TextView) convertView.findViewById(R.id.textView_shop_printer_item_name);
		TextView textView_shop_printer_item_ip = (TextView) convertView.findViewById(R.id.textView_shop_printer_item_ip);
		TextView textView_shop_printer_item_state = (TextView) convertView.findViewById(R.id.textView_shop_printer_item_state);
		textView_shop_printer_item_name.setText(SanyiSDK.rest.operationData.shopPrinters.get(position).name);
		textView_shop_printer_item_ip.setText(SanyiSDK.rest.operationData.shopPrinters.get(position).ip);
		ImageView imageView_shop_printer_test = (ImageView) convertView.findViewById(R.id.imageView_shop_printer_test);
		for (int i = 0; i < map.size(); i++) {
			if (map.containsKey(Integer.toString(position))) {
				if (map.get(Integer.toString(position))) {
					imageView_shop_printer_test.setBackgroundResource(R.drawable.right);
				} else {
					imageView_shop_printer_test.setBackgroundResource(R.drawable.wrong);
				}
			}
		}
		if (SanyiSDK.rest.operationData.shopPrinters.get(position).stateId == 1) {
			textView_shop_printer_item_state.setText("正常");
			textView_shop_printer_item_state.setTextColor(Color.parseColor("#37bf7e"));
		} else {
			textView_shop_printer_item_state.setText("异常");
			textView_shop_printer_item_state.setTextColor(Color.parseColor("#fe5c5c"));
		}
//		button_shop_printer_item_test.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				currentSelectPrint = position;
//				List<Long> printIds = new ArrayList<Long>();
//				printIds.add(SanyiSDK.rest.operationData.shopPrinters.get(position).id);
//				SanyiScalaRequests.testPrinterRequest(printIds, new _TestPrintRequest.ITestPrintListener() {
//
//					@Override
//					public void request_timeout() {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void request_fail() {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onFail(String error) {
//						// TODO Auto-generated method stub
//						map.put(Integer.toString(currentSelectPrint), false);
//						notifyDataSetChanged();
//						MainScreenActivity.activity.toastText(error);
//					}
//
//					@Override
//					public void onSuccess(String result) {
//						// TODO Auto-generated method stub
//						map.put(Integer.toString(currentSelectPrint), true);
//						notifyDataSetChanged();
//					}
//				});
//
//			}
//		});
		return convertView;
	}

}
