package com.rainbow.smartpos.bill;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.CheckOrderAdapter;
import com.rainbow.smartpos.check.CheckPaymentAdapter;
import com.rainbow.smartpos.order.DetailOpLogsDialog;
import com.rainbow.smartpos.order.ProgressDialogHandler;
import com.rainbow.smartpos.util.NumberPad;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.api.services.scala.GetClosedBillDetailRequest.IGetClosedBillDetailListener;
import com.sanyipos.sdk.api.services.scala.GetClosedBillListRequest.IGetClosedBillsListener;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.OrderEntity;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.ClosedBill;
import com.sanyipos.sdk.model.scala.ClosedBillListResult;
import com.sanyipos.sdk.model.scala.addDetail.model.AddDetailOrder;
import com.sanyipos.sdk.model.scala.changeBill.ChangeBillAction;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.DateHelper;
import com.rainbow.smartpos.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class BillFragment extends Fragment implements OnClickListener, ProgressDialogHandler {

	View mainView;
	Button buttonTableStatus;
	Button buttonReverseBill;
	Button buttonReprint;
	Button buttonDetailLogs;
	Button buttonBillSN;
	Button buttonTableNo;
	Button buttonTablePersonCount;
	Button buttonTableAmount;
	Button buttonTableClosedTime;

	ListView billList, billDetail;
	ListView billPayment;
	BillListAdapter billListAdpater;
	CheckOrderAdapter billDetailAdpater;
	CheckPaymentAdapter billPaymentAdapter;

	MainScreenActivity activity;
	long[] tableSeateId = new long[1];
	TextView textViewBillDetailSN;
	TextView textViewMarketType;
	TextView textViewOpenTime;
	TextView textViewOpenPerson;
	TextView textViewBillPerson;
	TextView textViewCombineTable;
	TextView textViewTotal;
	TextView textViewPaid;
	LinearLayout layoutCombineTable;
	long selectedBillId;
	long seatId;
	long selectedOrderId;
	Drawable downArrow;
	Drawable upArrow;
	ProgressDialog progressDialog;
	private List<SeatEntity> selectBillTables;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_bill, container, false);
		mainView = rootView;
		activity = (MainScreenActivity) getActivity();
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle(null);
		progressDialog.setMessage("请等候");
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setIndeterminateDrawable(getActivity().getResources().getDrawable(R.drawable.spinner));
		progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		textViewBillDetailSN = (TextView) rootView.findViewById(R.id.textViewBillDetailSN);

		textViewMarketType = (TextView) rootView.findViewById(R.id.textViewMarketType);

		textViewOpenTime = (TextView) rootView.findViewById(R.id.textViewOpenTime);

		textViewOpenPerson = (TextView) rootView.findViewById(R.id.textViewOpenPerson);

		textViewBillPerson = (TextView) rootView.findViewById(R.id.textViewBillPerson);
		
		textViewCombineTable = (TextView) rootView.findViewById(R.id.textViewCombineTable);

		textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
		textViewPaid = (TextView) rootView.findViewById(R.id.textViewPaid);

		buttonTableStatus = (Button) rootView.findViewById(R.id.buttonTableStatus);
		buttonTableStatus.setOnClickListener(this);

		buttonReverseBill = (Button) rootView.findViewById(R.id.buttonReverseBill);
		buttonReverseBill.setOnClickListener(this);
		buttonReverseBill.setEnabled(false);

		buttonReprint = (Button) rootView.findViewById(R.id.buttonReprint);
		buttonReprint.setOnClickListener(this);
		
		buttonDetailLogs = (Button) rootView.findViewById(R.id.buttonDetailLogs);
		buttonDetailLogs.setOnClickListener(this);
		
		buttonBillSN = (Button) rootView.findViewById(R.id.buttonBillSN);
		buttonBillSN.setOnClickListener(this);

		buttonTableNo = (Button) rootView.findViewById(R.id.buttonTableNo);
		buttonTableNo.setOnClickListener(this);

		buttonTablePersonCount = (Button) rootView.findViewById(R.id.buttonTablePersonCount);
		buttonTablePersonCount.setOnClickListener(this);

		buttonTableAmount = (Button) rootView.findViewById(R.id.buttonTableAmount);
		buttonTableAmount.setOnClickListener(this);

		buttonTableClosedTime = (Button) rootView.findViewById(R.id.buttonTableClosedTime);
		buttonTableClosedTime.setOnClickListener(this);
		
		layoutCombineTable = (LinearLayout) rootView.findViewById(R.id.layoutCombineTable);
		layoutCombineTable.setVisibility(View.INVISIBLE);

		Resources res = getResources();

		downArrow = res.getDrawable(R.drawable.down_arrow);
		upArrow = res.getDrawable(R.drawable.up_arrow);

		// set bill no as default sort ,
		buttonTableClosedTime.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
		buttonTableClosedTime.setSelected(true);
		buttonTableClosedTime.setTag(Boolean.TRUE);

		billDetail = (ListView) mainView.findViewById(R.id.listViewBillDetail);
		billDetailAdpater = new CheckOrderAdapter(getActivity());

		billDetail.setAdapter(billDetailAdpater);

		billPayment = (ListView) mainView.findViewById(R.id.listViewCheckPayment);
		billPaymentAdapter = new CheckPaymentAdapter(getActivity());

		billPayment.setAdapter(billPaymentAdapter);

		billList = (ListView) rootView.findViewById(R.id.listViewBill);
		billListAdpater = new BillListAdapter(inflater);

		billList.setAdapter(billListAdpater);

		billList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				billListAdpater.setSelectedPosition(position);
				billListAdpater.notifyDataSetChanged();
				itemClick(position);
			}
		});
		return rootView;
	}


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        requestClosedBill();
    }

    public void requestClosedBill() {
		// TODO Auto-generated method stub
		SanyiScalaRequests.getClosedBillsRequest(new IGetClosedBillsListener() {


			@Override
			public void onFail(String error) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ClosedBillListResult closedBillListResult) {
				// TODO Auto-generated method stub
				List<ClosedBill> closedBillList = closedBillListResult.bills;
				if (closedBillList.size() > 0) {
					buttonReverseBill.setEnabled(true);
				}
				billListAdpater.setClosedBillList(closedBillList);
				billListAdpater.notifyDataSetChanged();
				if (closedBillList.size() > 0) {
					billListAdpater.sort(buttonTableClosedTime.getId(), true);
					ClosedBill billObj = billListAdpater.getItem(0);
					buttonReprint.setEnabled(true);
					buttonDetailLogs.setEnabled(true);
					buttonReverseBill.setEnabled(true);
					seatId = billObj.orders.get(0).tableId;
					selectedBillId = billObj.id;
					selectedOrderId = billObj.orders.get(0).id;
					tableSeateId[0] = billObj.orders.get(0).tableSeatId;
					billListItemClick(billObj.id);
                    billListAdpater.setSelectedPosition(0);
				}else{
					cleanDetail();
				}
			}
		});
	}

	public void billListItemClick(long billId) {
		SanyiScalaRequests.getClosedBillDetailRequest(billId, new IGetClosedBillDetailListener() {


			@Override
			public void onFail(String error) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(CashierResult resp, List<OrderDetail> details) {
				// TODO Auto-generated method stub
				try {
					textViewBillDetailSN.setText(resp.bill.sn);
					textViewMarketType.setText(SanyiSDK.rest.getServiceHourNameByTime(resp.orders.get(0).createOn));
					textViewOpenTime.setText(DateHelper.inputParser.format(resp.orders.get(0).createOn));
					textViewOpenPerson.setText(resp.orders.get(0).staffName);
					textViewBillPerson.setText(resp.bill.staffName);
					
					if (resp.orders.size() > 1) {
						layoutCombineTable.setVisibility(View.VISIBLE);
						textViewCombineTable.setText(getCombineTableName(resp));
					}else{
						layoutCombineTable.setVisibility(View.INVISIBLE);
					}
					billDetailAdpater.reset();
					initSelectBillTables(resp);
					billDetailAdpater.setOrderDetails(details);

					billPaymentAdapter.allPayments.clear();
					billPaymentAdapter.addAllPayments(resp.payments);
					updateBillDetail();
					// }

					billDetailAdpater.notifyDataSetChanged();
					billPaymentAdapter.notifyDataSetChanged();
					textViewTotal.setText(Restaurant.currentcyFormatter.format(resp.bill.amount));
					textViewPaid.setText(Restaurant.currentcyFormatter.format(resp.bill.realValue));

				} catch (Exception e) {
					Toast.makeText(getActivity().getApplicationContext(), "无法获得订单记录", Toast.LENGTH_LONG).show();
					e.printStackTrace();
					cleanDetail();
					buttonDetailLogs.setEnabled(false);
					buttonReprint.setEnabled(false);
					//buttonReverseBill.setEnabled(false);
				}
			}
		});
	}
	/**
	 * 初始化订单详情的参数
	 * @param resp
	 */
	private void initSelectBillTables(CashierResult resp){
		selectBillTables = new ArrayList<SeatEntity>();
		for (int i = 0; i < resp.orders.size(); i++) {
			AddDetailOrder order = resp.orders.get(i);
			SeatEntity table = new SeatEntity();
			table.seat = order.tableSeatId;
			table.tableName = order.tableName;
			OrderEntity orderEntity = new OrderEntity();
			orderEntity.order = order.orderId;
			table.order = orderEntity;
			selectBillTables.add(table);
		}
	}
	
	public String getCombineTableName(CashierResult resp){
		String name = "";
		for (AddDetailOrder order : resp.orders) {
			if (!order.tableName.isEmpty()) {
				name = name + order.tableName + ",";
			}
		}
		if (!name.isEmpty()) {
			name = name.substring(0,name.length()-1);	
		}
		return name;
	}

	public void itemClick(int position) {
		ClosedBill billObj = billListAdpater.getItem(position);
		if (!(selectedBillId == billObj.id)) {
			selectedBillId = billObj.id;
			selectedOrderId = billObj.orders.get(0).id;
			tableSeateId[0] = billObj.orders.get(0).tableSeatId;
			billListItemClick(billObj.id);
		}
	}

	public void cleanDetail() {
		textViewBillDetailSN.setText("");
		textViewMarketType.setText("");
		textViewOpenTime.setText("");
		textViewOpenPerson.setText("");
		textViewBillPerson.setText("");
		textViewTotal.setText("");
		textViewPaid.setText("");
		//buttonReverseBill.setEnabled(false);
		buttonReprint.setEnabled(false);
		buttonDetailLogs.setEnabled(false);
		billDetailAdpater.reset();
		billDetailAdpater.notifyDataSetChanged();
		billPaymentAdapter.allPayments.clear();
		billPaymentAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		Button button = (Button) v;
		boolean selected = button.isSelected();
		int viewId = v.getId();

		switch (viewId) {

		case R.id.buttonBillSN:
		case R.id.buttonTableNo:
		case R.id.buttonTableAmount:
		case R.id.buttonTablePersonCount:
		case R.id.buttonTableClosedTime: {
			if (selected) {
				if (button.getTag() == Boolean.TRUE) {
					button.setTag(Boolean.FALSE);
					button.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
				} else {
					button.setTag(Boolean.TRUE);
					button.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
				}
			} else {
				unSelectButton(button);
				button.setSelected(true);
				button.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
			}
			billListAdpater.sort(viewId, button.getTag() == Boolean.TRUE);
			if (billListAdpater.getCount() > 0) {
				billListAdpater.setSelectedPosition(0);
				billListAdpater.notifyDataSetChanged();
				itemClick(0);
			}
			break;
		}

		case R.id.buttonTableStatus: {
			activity.displayView(MainScreenActivity.MANAGER_FRAGMENT, new Bundle());
			break;
		}
		case R.id.buttonReverseBill: {
			if (SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_REVERSE_BILL)) {
				reverseBill(SanyiSDK.currentUser);
			} else {
				NumberPad np = new NumberPad();
				np.setAdditionalText("输入密码或刷卡授权");
				np.show(getActivity(), "逆结单", NumberPad.HIDE_INPUT, new NumberPad.numbPadInterface() {
					public String numPadInputValue(String value) {
						StaffRest superUser = SanyiSDK.getSDK().getStaff(value);
						if (superUser != null && SanyiSDK.getPermissionByStaff(superUser, ConstantsUtil.PERMISSION_FREE_BILL)) {
							Toast.makeText(getActivity().getApplicationContext(), "授权通过", Toast.LENGTH_LONG).show();
							reverseBill(superUser);
						} else {
							Toast.makeText(getActivity().getApplicationContext(), "密码不对，授权错误", Toast.LENGTH_LONG).show();
						}
						return null;
					}

					// This is called when the user clicks the
					// 'Cancel'
					// button
					// on the Dialog
					public String numPadCanceled() {
						return null;
					}
				});
			}

			break;
		}
		case R.id.buttonDetailLogs:
			showDetailOpLogs();
			break;
		case R.id.buttonReprint:
			final Dialog dialog = new Dialog(getActivity());
			dialog.setContentView(R.layout.table_status_settlement);
			dialog.setTitle(Html.fromHtml("<font color='#16a4fa'>确定要重打账单吗？</font>"));
			Button day_settlement_ok = (Button) dialog.findViewById(R.id.day_settlement_ok);
			Button day_settlement_cancel = (Button) dialog.findViewById(R.id.day_settlement_cancel);
			day_settlement_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			day_settlement_ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					SanyiScalaRequests.ChangeBillRequest(ChangeBillAction.REPRINT, selectedBillId, SanyiSDK.currentUser.id, new ICallBack() {


						@Override
						public void onFail(String error) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(String status) {
							// TODO Auto-generated method stub

						}
					});
					dialog.dismiss();

				}
			});
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
			break;
		}

	}

	public void updateBillDetail() {

	}
	
	/**
	 * 查看菜品操作日志
	 */
	public void showDetailOpLogs() {
		// TODO Auto-generated method stub
		if (null == selectBillTables) {
			selectBillTables = new ArrayList<SeatEntity>();
		}
		if (selectBillTables.size() == 0) {
			SeatEntity table = SanyiSDK.rest.operationData.getSeat(tableSeateId[0]);
			if (null != table) {
				selectBillTables.add(table);
			}
		}
		if (selectBillTables.size() > 0) {
			new DetailOpLogsDialog(getActivity(), selectBillTables).show();
		}else{
		}
	}

	public void reverseBill(final StaffRest staff) {
		SanyiScalaRequests.ChangeBillRequest(ChangeBillAction.UNDOBILL, selectedBillId, staff.id, new ICallBack() {

			@Override
			public void onFail(String error) {
				// TODO Auto-generated method stub


			}

			@Override
			public void onSuccess(String status) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void unSelectButton(Button v) {
		int buttonId = v.getId();
		if (buttonId == R.id.buttonBillSN) {
			buttonTableNo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTablePersonCount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTableAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTableClosedTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

			buttonTableNo.setSelected(false);
			buttonTablePersonCount.setSelected(false);
			buttonTableClosedTime.setSelected(false);
			buttonTableAmount.setSelected(false);

		}

		if (buttonId == R.id.buttonTableNo) {
			buttonBillSN.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTablePersonCount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTableAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTableClosedTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

			buttonBillSN.setSelected(false);
			buttonTablePersonCount.setSelected(false);
			buttonTableClosedTime.setSelected(false);
			buttonTableAmount.setSelected(false);

		}
		if (buttonId == R.id.buttonTablePersonCount) {
			buttonTableNo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonBillSN.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTableAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTableClosedTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

			buttonTableNo.setSelected(false);
			buttonBillSN.setSelected(false);
			buttonTableClosedTime.setSelected(false);
			buttonTableAmount.setSelected(false);

		}
		if (buttonId == R.id.buttonTableAmount) {
			buttonTableNo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTablePersonCount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonBillSN.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTableClosedTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

			buttonTableNo.setSelected(false);
			buttonTablePersonCount.setSelected(false);
			buttonTableClosedTime.setSelected(false);
			buttonBillSN.setSelected(false);
		}
		if (buttonId == R.id.buttonTableClosedTime) {
			buttonTableNo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTablePersonCount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonBillSN.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			buttonTableAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

			buttonTableNo.setSelected(false);
			buttonTablePersonCount.setSelected(false);
			buttonTableAmount.setSelected(false);
			buttonBillSN.setSelected(false);
		}
	}

	@Override
	public void showProgressDialog(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeProgressDialog() {
		// TODO Auto-generated method stub

	}

}