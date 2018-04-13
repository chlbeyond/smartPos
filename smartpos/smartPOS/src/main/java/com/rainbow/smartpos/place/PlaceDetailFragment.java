package com.rainbow.smartpos.place;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.SmartPosBundle;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.place.DishRecycleAdapter.OnRecyclerViewItemClickListener;
import com.rainbow.smartpos.place.presenter.PlaceDetailPresenterImpl;
import com.rainbow.smartpos.place.view.PlaceDetailView;
import com.rainbow.smartpos.tablestatus.TableAdapter;
import com.rainbow.smartpos.util.SwitchTableDialog;
import com.rainbow.smartpos.util.SwitchTableDialog.ISwitchTableListener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.bean.TableOrderInfo;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.sanyipos.sdk.utils.OrderUtil;
import com.sanyipos.smartpos.model.OrderParams;

import java.util.List;

public class PlaceDetailFragment extends Fragment implements OnClickListener, PlaceDetailView {
    public static final String TAG = "PlaceDetailFragment";
    public int mCurrentMode = -1;
    private View view;
    private TextView textViewTableName;
    private TextView textViewPersonCount;
    private TextView updatePersonCount;
    private TextView updateBillRemark;
    private TextView textViewRemark;
    private TextView textViewBillState;
    private TextView clearBillState;
    private TextView textViewBillAmount;
    private TextView clearBillStateHint;
    private TextView textViewPlaceFragmentBillAmount;
    private LinearLayout mCookDishButton;
    private LinearLayout mRemindDishButton;
    private LinearLayout mReturnDishButton;
    private LinearLayout mChangeDishButton;
    private LinearLayout mOperationLogButton;
    private LinearLayout mFreeDishButton;
    private TextView mReprintBill;
    private LinearLayout switch_table;

    private ImageView choose;

    private Button continue_order;
    private Button check;
    private Button sure_btn;
    private LinearLayout place_right_layout;
    private LinearLayout sure_button_layout;
    private RelativeLayout continue_order_layout;

    public List<Long> tableIds;
    public boolean isForMergeTable;
    public SeatEntity currentTable;
    public static MainScreenActivity activity;

    public ListView mRecyclerView;
    public DishRecycleAdapter mDishRecycleAdapter;
    private PlaceDetailPresenterImpl mPlacePresenter;
    public Bundle bundle;

    public ImageView imageViewSawtooth;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.place_detail_fragment_layout, container, false);
        activity = (MainScreenActivity) getActivity();
        mPlacePresenter = new PlaceDetailPresenterImpl(getActivity(), this);
        initView();
        setListener();
        initTable();
        return view;
    }

    public void initTable() {
        // TODO Auto-generated method stub
        if (bundle != null) {
            OrderParams table = (OrderParams) bundle.getSerializable(SmartPosBundle.TABLE_ID);

            tableIds = table.getTableIds();
            isForMergeTable = bundle.getBoolean(SmartPosBundle.IS_MERGE, false);
            initData();
        } else {
            activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
        }
    }

    public void initData() {
        currentTable = SanyiSDK.rest.operationData.getSeat(tableIds.get(0));

        if (null != currentTable) {
            textViewTableName.setText(currentTable.tableName);
            if ((currentTable.state & TableAdapter.PREPRINT) == TableAdapter.PREPRINT) {
                billPrintedStateText();

            } else {

                clearBillStateText();
            }
        }
        openTableRequest();
    }


    @SuppressLint("WrongViewCast")
    public void initView() {
        textViewTableName = (TextView) view.findViewById(R.id.textViewTableName);
        textViewPersonCount = (TextView) view.findViewById(R.id.textViewPersonCount);
        textViewBillState = (TextView) view.findViewById(R.id.textViewBillState);
        textViewBillAmount = (TextView) view.findViewById(R.id.textViewBillAmount);
        updatePersonCount = (TextView) view.findViewById(R.id.updatePersonCount);
        updateBillRemark = (TextView) view.findViewById(R.id.updateBillRemark);
        textViewRemark = (TextView) view.findViewById(R.id.textViewBillRemark);
        clearBillState = (TextView) view.findViewById(R.id.clearBillState);
        clearBillStateHint = (TextView) view.findViewById(R.id.clearBillStateHint);
        mCookDishButton = (LinearLayout) view.findViewById(R.id.call_up);
        mRemindDishButton = (LinearLayout) view.findViewById(R.id.pull_food);
        mReturnDishButton = (LinearLayout) view.findViewById(R.id.return_dish);
        mChangeDishButton = (LinearLayout) view.findViewById(R.id.change_dish);
        mFreeDishButton = (LinearLayout) view.findViewById(R.id.free_dish);
        choose = (ImageView) view.findViewById(R.id.choose);
        textViewPlaceFragmentBillAmount = (TextView) view.findViewById(R.id.textViewPlaceFragmentBillAmount);
        sure_btn = (Button) view.findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);
        sure_button_layout = (LinearLayout) view.findViewById(R.id.sure_button_layout);
        choose.setVisibility(View.GONE);

        continue_order = (Button) view.findViewById(R.id.continue_order);
        check = (Button) view.findViewById(R.id.check);
        mReprintBill = (TextView) view.findViewById(R.id.reprintTableBill);
        mReprintBill.setOnClickListener(this);
        mOperationLogButton = (LinearLayout) view.findViewById(R.id.order_op_log);
        switch_table = (LinearLayout) view.findViewById(R.id.switch_table);

        continue_order_layout = (RelativeLayout) view.findViewById(R.id.continue_order_layout);
        continue_order_layout.setVisibility(View.VISIBLE);
        place_right_layout = (LinearLayout) view.findViewById(R.id.place_right_layout);
        mRecyclerView = (ListView) view.findViewById(R.id.dishRecyclerView);
        //创建布局管理器
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        place_right_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int width = place_right_layout.getWidth();
//                int height = place_right_layout.getHeight();
//                KLog.d(TAG, width);
//                mRecyclerView.setLayoutParams(new RecyclerView.LayoutParams(width,height));
//            }
//        });
//        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycle_view_space);
//        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration(spacingInPixels);
//        mRecyclerView.addItemDecoration(spaceItemDecoration);
        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPlacePresenter.clickDish(mDishRecycleAdapter.getSetDish(position), position, view);
            }
        });
        imageViewSawtooth = (ImageView) view.findViewById(R.id.imageView_order_sawtooth);
        imageViewSawtooth.scrollTo(0, -3);
//        place_left_layout = (LinearLayout) view.findViewById(R.id.place_left_layout);
//        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

//		view.findViewById(R.id.linearLayoutPlace).post(new Runnable() {
//			@Override
//			public void run() {
//				HighLight high = new HighLight(activity).anchor(view.findViewById(R.id.linearLayoutPlace)).addHighLight(R.id.linearLayout_people_count, R.layout.apk_update, new HighLight.OnPosCallback() {
//					@Override
//					public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
//						marginInfo.leftMargin = rectF.right - rectF.width() / 2;
//						marginInfo.topMargin = rectF.bottom;
//					}
//				});
//				high.show();
//			}
//		});

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initTable();
            cancelBatchMode();
        } else {
            bundle = null;
            activity.unLockTable(tableIds, false);
        }
    }

    public void setListener() {
        updatePersonCount.setOnClickListener(this);
        updateBillRemark.setOnClickListener(this);
        clearBillState.setOnClickListener(this);
        continue_order.setOnClickListener(this);
        check.setOnClickListener(this);
        mReprintBill.setOnClickListener(this);
        mOperationLogButton.setOnClickListener(this);
        mCookDishButton.setOnClickListener(this);
        mRemindDishButton.setOnClickListener(this);
        mReturnDishButton.setOnClickListener(this);
        mChangeDishButton.setOnClickListener(this);
        mFreeDishButton.setOnClickListener(this);
        choose.setOnClickListener(this);
        switch_table.setOnClickListener(this);
    }


    /**
     * 进台
     */
    private void openTableRequest() {
        initOrderList();
        List<OpenTableDetail> resp = (List<OpenTableDetail>) bundle.get(SmartPosBundle.ORDERDETAILS);
        if (resp == null) {
            mPlacePresenter.loadDetails(tableIds);
            return;
        }
        mPlacePresenter.setDealerOrders(resp.get(0).ods, tableIds);
        updateBillInfo(resp.get(0).info);
        updateDetails(resp.get(0).ods);
    }

    private void updateBillInfo(TableOrderInfo info) {
        // TODO Auto-generated method stub
        if (info != null) {
            textViewPersonCount.setText("人数:" + info.getPersonCount() + "人");
            if (info.getRemark() != null) {
                textViewRemark.setText(info.getRemark());
            } else {
                textViewRemark.setText("");
            }
            textViewBillAmount.setText(OrderUtil.dishPriceFormatter.format(info.getAmount()));
            textViewTableName.setText(info.getTableName());
            if (isForMergeTable) {
                TextPaint tp = textViewTableName.getPaint();
                tp.setFakeBoldText(true);
                String combineTableName = SanyiSDK.rest.getCombineTableNameByTag(info.getTag());
                String str = String.format("%s(<font color='#FF0000'>%s</font>)", combineTableName, info.getTag());
                textViewTableName.setText(Html.fromHtml(str));
            }
        }
    }

    private void initOrderList() {
        mDishRecycleAdapter = new DishRecycleAdapter(activity,mPlacePresenter);
        mRecyclerView.setAdapter(mDishRecycleAdapter);
        mDishRecycleAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(OrderDetail orderDetail, int position, View view) {
                mPlacePresenter.clickDish(orderDetail, position, view);
            }


        });
    }

    @Override
    public void showOrderFragment() {
        activity.displayView(MainScreenActivity.ORDER_FRAGMENT, bundle);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.updatePersonCount:
                mPlacePresenter.changePeople();
                break;
            case R.id.updateBillRemark:
                final NormalDialog remarkDialog = new NormalDialog(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_table_remark, null);
                final EditText remarkEditText = (EditText) view.findViewById(R.id.editText_dialog_remark);
                remarkEditText.setText(textViewRemark.getText().toString());
                remarkEditText.setSelection(textViewRemark.getText().toString().length());
                remarkDialog.content(view);
                remarkDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
//                        RealmHelper realmHelper = new RealmHelper(getContext());
//                        String remark = realmHelper.queryTableRemark(tableIds.get(0));
//                        if (remark == null) {
//                            realmHelper.insertTableRemark(tableIds.get(0), remarkEditText.getText().toString());
//                        } else {
//                            realmHelper.changeTableRemark(tableIds.get(0), remarkEditText.getText().toString());
//                        }
                        SanyiScalaRequests.addOrderRemarkRequest(tableIds.get(0), SanyiSDK.currentUser.id, remarkEditText.getText().toString(), new Request.ICallBack() {
                            @Override
                            public void onSuccess(String status) {
                                Toast.makeText(activity,status,Toast.LENGTH_LONG).show();
                                textViewRemark.setText("" + remarkEditText.getText().toString());
                                remarkDialog.dismiss();
                            }


                            @Override
                            public void onFail(String error) {

                            }
                        });

                    }
                });
                remarkDialog.widthScale((float) 0.5);
                remarkDialog.show();
                break;
            case R.id.clearBillState:
                mPlacePresenter.cleanPrintState();
                break;
            case R.id.continue_order:
                continueOrder();
                break;
            case R.id.check:
                check();
                break;
            case R.id.reprintTableBill:
                mPlacePresenter.printBill();
                break;
            case R.id.order_op_log:
                mPlacePresenter.operationLog();
                break;
            case R.id.call_up:
            case R.id.pull_food:
            case R.id.return_dish:
            case R.id.change_dish:
            case R.id.free_dish:
                mPlacePresenter.clickBatchButton(mCurrentMode = v.getId());
                setButtonSelected();
                break;
            case R.id.choose:
                break;
            case R.id.switch_table:
                mPlacePresenter.switchTable();
                break;
            case R.id.sure_btn:
                mPlacePresenter.clickConfirm(mCurrentMode);
                break;

            default:
                break;
        }
    }

    public void setButtonSelected() {
        mCookDishButton.setSelected(false);
        mRemindDishButton.setSelected(false);
        mReturnDishButton.setSelected(false);
        mChangeDishButton.setSelected(false);
        mFreeDishButton.setSelected(false);
        if (mCurrentMode != -1) showSureBrnLayout();
        switch (mCurrentMode) {
            case R.id.call_up:
                mCookDishButton.setSelected(true);
                break;
            case R.id.pull_food:
                mRemindDishButton.setSelected(true);
                break;
            case R.id.return_dish:
                mReturnDishButton.setSelected(true);
                break;
            case R.id.change_dish:
                mChangeDishButton.setSelected(true);
                break;
            case R.id.free_dish:
                mFreeDishButton.setSelected(true);
                break;
        }
    }

    @Override
    public void updateSelectedDetails(List<OrderDetail> details) {
        mDishRecycleAdapter.setSelectedDetails(details);
    }

    @Override
    public void clearSelectedDetails() {
        mDishRecycleAdapter.clearSelectedDetails();
    }

    @Override
    public void notifyDataSetChanged() {
        mDishRecycleAdapter.notifyDataSetChanged();
    }

    public void showSureBrnLayout() {
        sure_button_layout.setVisibility(View.VISIBLE);
        continue_order_layout.setVisibility(View.GONE);
        textViewPlaceFragmentBillAmount.setText(getString(R.string.please_choose_order));
        textViewBillAmount.setVisibility(View.GONE);

    }

    public void hideSureBrnLayout() {
        sure_button_layout.setVisibility(View.GONE);
        continue_order_layout.setVisibility(View.VISIBLE);
        textViewPlaceFragmentBillAmount.setText(getString(R.string.bill_amount));
        textViewBillAmount.setVisibility(View.VISIBLE);
    }


    private void switchTable() {
        // TODO Auto-generated method stub
        if (null != currentTable) {
            new SwitchTableDialog(activity, SwitchTableDialog.ORDER, currentTable, new ISwitchTableListener() {
                @Override
                public void batchOperation(long[] tableIds) {

                }

                @Override
                public void openTable(SeatEntity table, int personCount) {
                    // TODO Auto-generated method stub
                    if (table.state == TableAdapter.AVAILABLE) {
                        long[] tableIds = {table.seat};
//						activity.onSwitchTable(tableIds, personCount, TableOperation.OPEN_AND_ORDER, true);
                    } else {
                        long[] tableIds = {table.seat};
//						activity.onSwitchTable(tableIds, table.personCount, TableOperation.OPEN_AND_ORDER, false);
                    }
                }

                @Override
                public void cancel() {
                    // TODO Auto-generated method stub
                }
            }).show();
        }
    }

    public void clearBillStateText() {
        textViewBillState.setText("账单状态:" + activity.getString(R.string.without_preprint));
        clearBillState.setVisibility(View.INVISIBLE);
        clearBillStateHint.setVisibility(View.INVISIBLE);
    }

    public void billPrintedStateText() {
        textViewBillState.setText("账单状态:" + activity.getString(R.string.has_preprint));
        clearBillState.setVisibility(View.VISIBLE);
        clearBillStateHint.setVisibility(View.VISIBLE);
    }

    @Override
    public void cancelBatchMode() {
        hideSureBrnLayout();
        mCurrentMode = -1;
        setButtonSelected();
    }


    /**
     * 继续点菜
     */
    public void continueOrder() {
        if (currentTable != null ) {
            bundle.putInt("peopleNumber", currentTable.personCount);
            bundle.putBoolean("isNewBill", false);
            activity.displayView(MainScreenActivity.ORDER_FRAGMENT, bundle);
        } else {
            activity.displayView(MainScreenActivity.TABLE_FRAGMENT, null);
        }
    }

    /**
     * 结账
     */
    public void check() {
        if (mPlacePresenter.isCanCheck())
            activity.displayView(MainScreenActivity.CHECK_FRAGMENT, bundle);
        else {
            Toast.makeText(activity,"无可结账菜品",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        activity.unLockTable(tableIds, false);
        mCurrentMode = -1;
        super.onDestroyView();
    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void updateTableInfo(TableOrderInfo tableInfo) {
        updateBillInfo(tableInfo);
    }

    @Override
    public int currentMode() {
        return mCurrentMode;
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(activity,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateItem(int position, View view) {
        mDishRecycleAdapter.refreshSingleDish(position, view);
    }

    @Override
    public void updateDetails(List<OrderDetail> details) {
        mDishRecycleAdapter.setOrderDetails(details, mCurrentMode == -1);
    }

    @Override
    public SeatEntity getCurrentTable() {
        return currentTable;
    }

    @Override
    public void setPeople(int count) {
        textViewPersonCount.setText("人数:" + count + "人");
    }
}
