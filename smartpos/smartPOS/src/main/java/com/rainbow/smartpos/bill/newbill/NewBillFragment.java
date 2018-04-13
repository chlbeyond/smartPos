package com.rainbow.smartpos.bill.newbill;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.check.CheckDetailListAdapter;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.place.SpaceItemDecoration;
import com.rainbow.smartpos.util.AuthDialog;
import com.rainbow.smartpos.util.Listener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.GetClosedBillDetailRequest;
import com.sanyipos.sdk.api.services.scala.GetClosedBillListRequest;
import com.sanyipos.sdk.api.services.scala.GetDetailOperationRequest;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.Payment;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.ClosedBill;
import com.sanyipos.sdk.model.scala.ClosedBillListResult;
import com.sanyipos.sdk.model.scala.DetailOpGroupList;
import com.sanyipos.sdk.model.scala.Invoice;
import com.sanyipos.sdk.model.scala.changeBill.ChangeBillAction;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.rainbow.smartpos.R.id.button_bill_fragment_invoice;

/**
 * Created by ss on 2016/1/4.
 */
public class NewBillFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "NewBillFragment";
    public ListView mRecyclerView;
    public RecyclerViewAdapter mRecyclerViewAdapter;
    public ListView mListView;
    public CheckDetailListAdapter mOrderListAdapter;
    public OperationLogDetailAdapter mOperationAdapter;
    public Context context;
    public Button mButtonBillDetail;
    public Button mButtonBillOperation;
    public Button mButtonPrint;
    public Button mButtonReverse;
    public Button mButtonInvoice;

    //    public RelativeLayout mButtonMenuBillDetail;
    public boolean isOperationLog = false;
    public TextView mTextViewSort;
    public TextView mTexTSpentCount;
    public TextView mTextViewCount;

    private Drawable downArrow;
    private Drawable upArrow;
    private ClosedBillListResult billListResult;
    private Button mButtonBillNumber;
    private Button mButtonTableNumber;
    private Button mButtonPeopleNumber;
    private Button mButtonAmount;
    private Button mButtonSaleStaff;
    private Button mButtonClosedTime;

    private List<ClosedBill> sortBills;

    private LinearLayout emptyLinearLayout;

    private void unSelectButton() {
        mButtonBillNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mButtonTableNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mButtonPeopleNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mButtonAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mButtonClosedTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mButtonSaleStaff.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mButtonBillNumber.setSelected(false);
        mButtonTableNumber.setSelected(false);
        mButtonPeopleNumber.setSelected(false);
        mButtonAmount.setSelected(false);
        mButtonClosedTime.setSelected(false);
        mButtonSaleStaff.setSelected(false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_bill, container, false);
        context = getActivity();

        downArrow = getActivity().getResources().getDrawable(R.drawable.down_arrow);
        upArrow = getActivity().getResources().getDrawable(R.drawable.up_arrow);
        emptyLinearLayout = (LinearLayout) rootView.findViewById(R.id.empty_layout);
        mRecyclerView = (ListView) rootView.findViewById(R.id.new_bill_fragment_recyclerView);
        mListView = (ListView) rootView.findViewById(R.id.listView_new_bill);
        mButtonBillDetail = (Button) rootView.findViewById(R.id.button_bill_fragment_bill_detail);
        mButtonBillDetail.setOnClickListener(this);
        mButtonBillOperation = (Button) rootView.findViewById(R.id.button_bill_fragment_bill_operation);
        mButtonBillOperation.setOnClickListener(this);
//        mButtonMenuBillDetail = (RelativeLayout) rootView.findViewById(R.id.button_bill_fragment_menu_detail);
//        mButtonMenuBillDetail.setSelected(true);
        mButtonPrint = (Button) rootView.findViewById(R.id.button_bill_fragment_print);
        mButtonPrint.setOnClickListener(this);
        mButtonReverse = (Button) rootView.findViewById(R.id.button_bill_fragment_reverse);
        mButtonReverse.setOnClickListener(this);
        mTextViewSort = (TextView) rootView.findViewById(R.id.textView_sort);
        mTextViewSort.setOnClickListener(this);
        mTexTSpentCount = (TextView) rootView.findViewById(R.id.textView_bill_fragment_spend);
        mTextViewCount = (TextView) rootView.findViewById(R.id.textView_bill_fragment_count);

        mButtonBillNumber = (Button) rootView.findViewById(R.id.textView_bill_number);
        mButtonBillNumber.setOnClickListener(this);
        mButtonTableNumber = (Button) rootView.findViewById(R.id.textView_table_number);
        mButtonTableNumber.setOnClickListener(this);
        mButtonPeopleNumber = (Button) rootView.findViewById(R.id.textView_people_number);
        mButtonPeopleNumber.setOnClickListener(this);
        mButtonAmount = (Button) rootView.findViewById(R.id.textView_amount);
        mButtonAmount.setOnClickListener(this);
        mButtonSaleStaff = (Button) rootView.findViewById(R.id.textView_salesman);
        mButtonSaleStaff.setOnClickListener(this);
        mButtonClosedTime = (Button) rootView.findViewById(R.id.textView_closed_time);
        mButtonClosedTime.setOnClickListener(this);

        mButtonInvoice = (Button) rootView.findViewById(button_bill_fragment_invoice);
        mButtonInvoice.setOnClickListener(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.new_bill_fragment_recycle_space);
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        mRecyclerViewAdapter = new RecyclerViewAdapter(context);
        SpaceItemDecoration dividerLine = new SpaceItemDecoration(SpaceItemDecoration.VERTICAL);
        dividerLine.setSize(1);
        dividerLine.setColor(Color.parseColor("#DDDDDD"));
//        mRecyclerView.addItemDecoration(dividerLine);
        mRecyclerViewAdapter.setOnItemClickListener(onClickLitener);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                billListItemClick(mRecyclerViewAdapter.getClosedBills().get(position));
                mRecyclerViewAdapter.setmSelection(position);
            }
        });
        mOrderListAdapter = new CheckDetailListAdapter(getActivity(), CheckDetailListAdapter.Type.CHECK);
        mOperationAdapter = new OperationLogDetailAdapter(getActivity());
        if (isOperationLog) {
            mListView.setAdapter(mOperationAdapter);
        } else {
            mListView.setAdapter(mOrderListAdapter);
        }
        initLeftMenu();
        initBills();
        return rootView;
    }

    public RecyclerViewAdapter.OnItemClickLitener onClickLitener = new RecyclerViewAdapter.OnItemClickLitener() {
        @Override
        public void onItemClick(View view, int position, ClosedBill bill) {

        }


    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_bill_number:
            case R.id.textView_table_number:
            case R.id.textView_people_number:
            case R.id.textView_amount:
            case R.id.textView_salesman:
            case R.id.textView_closed_time:
                unSelectButton();
                Button button = (Button) view;
                if (button.getTag() == null) {
                    button.setTag(true);
                }
                sortBillList(view.getId(), !(Boolean) button.getTag());
                break;
            case R.id.button_bill_fragment_bill_detail:
                mButtonBillDetail.setBackgroundResource(R.drawable.left_while_borde_rounded_focused);
                mButtonBillDetail.setTextColor(getResources().getColor(R.color.white));
                mButtonBillOperation.setBackgroundResource(R.drawable.right_while_borde_rounded);
                mButtonBillOperation.setTextColor(getResources().getColor(R.color.title_login_background));
                isOperationLog = false;
                mListView.setAdapter(mOrderListAdapter);
                billListItemClick(mRecyclerViewAdapter.getSelectionBill());
                break;
            case R.id.button_bill_fragment_bill_operation:
                mButtonBillDetail.setBackgroundResource(R.drawable.left_while_borde_rounded);
                mButtonBillOperation.setBackgroundResource(R.drawable.right_while_borde_rounded_focused);
                mButtonBillDetail.setTextColor(getResources().getColor(R.color.title_login_background));
                mButtonBillOperation.setTextColor(getResources().getColor(R.color.white));
                isOperationLog = true;
                mListView.setAdapter(mOperationAdapter);
                billListItemClick(mRecyclerViewAdapter.getSelectionBill());
                break;
            case button_bill_fragment_invoice:
                if (mRecyclerViewAdapter != null && mRecyclerViewAdapter.closedBills.size() > 0)
                    if (mRecyclerViewAdapter.getSelectionBill().invoice == null) {
                        final Invoice invoice = new Invoice();
                        invoice.bill_id = mRecyclerViewAdapter.getSelectionBill().id;
                        invoice.shop_id = SanyiSDK.getShopId();

                        final NormalDialog remarkDialog = new NormalDialog(context);
                        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_table_remark, null);
                        final EditText remarkEditText = (EditText) dialogView.findViewById(R.id.editText_dialog_remark);
                        remarkDialog.title("发票备注");
                        remarkDialog.content(dialogView);
                        remarkDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                            @Override
                            public void onClickConfirm() {
                                invoice.remark = remarkEditText.getText().toString();
                                invoice.value = mRecyclerViewAdapter.getSelectionBill().amount;
                                List<Invoice> lists = new ArrayList<>();
                                lists.add(invoice);
                                SanyiScalaRequests.addBillInvoiceRequest(lists, new Request.ICallBack() {
                                    @Override
                                    public void onSuccess(String status) {
                                        remarkDialog.dismiss();
                                        mRecyclerViewAdapter.getSelectionBill().invoice = invoice;
                                        mRecyclerViewAdapter.notifyDataSetChanged();
                                        billListItemClick(mRecyclerViewAdapter.getSelectionBill());
                                        Toast.makeText(context, status, Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void onFail(String error) {
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        });
                        remarkDialog.widthScale((float) 0.5);
                        remarkDialog.show();

                    } else {
                        List<Long> lists = new ArrayList<>();
                        lists.add(mRecyclerViewAdapter.getSelectionBill().id);
                        SanyiScalaRequests.abandomBillInvoiceRequest(lists, new Request.ICallBack() {
                            @Override
                            public void onSuccess(String status) {
                                mRecyclerViewAdapter.getSelectionBill().invoice = null;
                                mRecyclerViewAdapter.notifyDataSetChanged();
                                billListItemClick(mRecyclerViewAdapter.getSelectionBill());
                                Toast.makeText(context, status, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFail(String error) {
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                break;

            case R.id.button_bill_fragment_print:
                if (mRecyclerViewAdapter != null && mRecyclerViewAdapter.closedBills.size() > 0) {
                    if (mRecyclerViewAdapter.getSelectionBill() == null) {

                        Toast.makeText(context, "请选择账单", Toast.LENGTH_LONG).show();

                        return;
                    }
                    SanyiScalaRequests.ChangeBillRequest(ChangeBillAction.REPRINT, mRecyclerViewAdapter.getSelectionBill().id, SanyiSDK.currentUser.id, new Request.ICallBack() {

                        @Override
                        public void onFail(String error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onSuccess(String status) {
                            // TODO Auto-generated method stub

                            Toast.makeText(context, "操作成功", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            case R.id.button_bill_fragment_reverse:
                if (mRecyclerViewAdapter != null && mRecyclerViewAdapter.closedBills.size() > 0) {
                    if (mRecyclerViewAdapter.getSelectionBill() == null) {

                        Toast.makeText(context, "请选择账单", Toast.LENGTH_LONG).show();
                        return;
                    }

                    AuthDialog authDialog = new AuthDialog();
                    authDialog.show(getActivity(), ConstantsUtil.PERMISSION_REVERSE_BILL, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {

                        @Override
                        public void onAuthSuccess(StaffRest staff) {
                            reverseBill(staff);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
                break;
            case R.id.textView_sort:
                final BillSortPopWindow sortWindow = new BillSortPopWindow();
                sortWindow.show(context, view, billListResult, new BillSortPopWindow.CustomSortListener() {

                            @Override
                            public void paymentCash() {
                                sortBill(Payment.PAYMENT_CASH);
                            }

                            @Override
                            public void paymentBankCard() {
                                sortBill(Payment.PAYMENT_BANK_CARD);
                            }

                            @Override
                            public void paymentStoreValue() {
                                sortBill(Payment.PAYMENT_STORE_VALUE);
                            }

                            @Override
                            public void paymentAlipay() {
                                sortBill(Payment.PAYMENT_ALIPAY);
                            }

                            @Override
                            public void paymentWechat() {
                                sortBill(Payment.PAYMENT_WECHAT);
                            }

                            @Override
                            public void paymentVoucher() {
                                sortBill(Payment.PAYMENT_VOUCHER);
                            }

                            @Override
                            public void paymentDiscount(long id) {
                                sortBill(id);
                            }

                            @Override
                            public void paymentWaive() {
                                sortBill(Payment.PAYMENT_WAIVE);
                            }

                            @Override
                            public void paymentDebit() {
                                sortBill(Payment.PAYMENT_DEBIT);
                            }

                            @Override
                            public void paymentNoCent() {

                            }

                            @Override
                            public void paymentMinCharge() {

                            }

                            @Override
                            public void paymentPromotion(long id) {
                                sortBill(id);
                            }

                            @Override
                            public void paymentCustom() {
                                sortBill(Payment.PAYMENT_CUSTOM);
                            }

                            @Override
                            public void cancelPayment() {
                                if (billListResult != null && billListResult.bills.size() > 0) {
                                    mRecyclerViewAdapter.setClosedBills(billListResult.bills);
                                    setSpentMoney(billListResult.bills);
                                    mRecyclerViewAdapter.setmSelection(0);
                                    billListItemClick(billListResult.bills.get(0));
                                    mRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                );
                break;
        }

    }

    private void reverseBill(StaffRest superUser) {

        SanyiScalaRequests.ChangeBillRequest(ChangeBillAction.UNDOBILL, mRecyclerViewAdapter.getSelectionBill().id, superUser.id, new Request.ICallBack() {

            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String status) {
                // TODO Auto-generated method stub
                getActivity().finish();
                mRecyclerViewAdapter.getClosedBills().remove(mRecyclerViewAdapter.getmSelection());
                mRecyclerViewAdapter.notifyDataSetChanged();
                if (mRecyclerViewAdapter.getmSelection() < mRecyclerViewAdapter.getCount()) {
                    billListItemClick(mRecyclerViewAdapter.getClosedBills().get(mRecyclerViewAdapter.getmSelection()));
                } else {
                    mRecyclerViewAdapter.setmSelection(0);
                    if (mRecyclerViewAdapter.getClosedBills().size() > 0)
                        billListItemClick(mRecyclerViewAdapter.getClosedBills().get(0));
                }
            }
        });

    }

    private void initBills() {
        SanyiScalaRequests.getClosedBillsRequest(new GetClosedBillListRequest.IGetClosedBillsListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ClosedBillListResult closedBillListResult) {
                // TODO Auto-generated method stub
                if (!closedBillListResult.bills.isEmpty()) {
                    billListResult = closedBillListResult;
                    List<ClosedBill> closedBillList = closedBillListResult.bills;
                    mRecyclerViewAdapter.setClosedBills(closedBillList);
                    sortBillList(R.id.textView_closed_time, true);
                    setSpentMoney(closedBillListResult.bills);
                } else {
                    emptyLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void setSpentMoney(List<ClosedBill> bills) {
        double amount = 0;
        for (ClosedBill bill : bills) {
            amount = amount + bill.amount;
        }
        mTexTSpentCount.setText("消费金额 : " + OrderUtil.dishPriceFormatter.format(amount));
        mTextViewCount.setText("单数 :" + bills.size());
    }

    public void billListItemClick(ClosedBill bill) {
        if (bill != null) {
            if (bill.invoice == null) {
                mButtonInvoice.setText("发票");
            } else {
                mButtonInvoice.setText("撤销");
            }
            if (!isOperationLog) {
                SanyiScalaRequests.getClosedBillDetailRequest(bill.id, new GetClosedBillDetailRequest.IGetClosedBillDetailListener() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onSuccess(CashierResult resp, List<OrderDetail> details) {
                        // TODO Auto-generated method stub
                        try {
                            mOrderListAdapter.setOrderDetails(details);
                            mOrderListAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "无法获得订单记录", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }
                });
            } else {
                SanyiScalaRequests.getDetailOpRequest(bill.orders.get(0).id, new GetDetailOperationRequest.IGetDetailOpLogListener() {
                    @Override
                    public void onSuccess(DetailOpGroupList detailOpGroupList) {
                        mOperationAdapter.setmLogDetails(detailOpGroupList.childList);
                        mOperationAdapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onFail(String error) {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
    }

    private void initLeftMenu() {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mButtonBillDetail.setBackgroundResource(R.drawable.left_while_borde_rounded_focused);
        mButtonBillDetail.setTextColor(getResources().getColor(R.color.white));
        mButtonBillOperation.setBackgroundResource(R.drawable.right_while_borde_rounded);
        mButtonBillOperation.setTextColor(getResources().getColor(R.color.title_login_background));
        isOperationLog = false;
    }

    public void sortBillList(final int sortId, final boolean bool) {
        if (mRecyclerViewAdapter.getClosedBills() == null) return;
        Collections.sort(mRecyclerViewAdapter.getClosedBills(), new Comparator<ClosedBill>() {

            @Override
            public int compare(ClosedBill e1, ClosedBill e2) {
                int comparedValue = 0;
                switch (sortId) {
                    case R.id.textView_bill_number:
                        comparedValue = e1.sn.compareTo(e2.sn);
                        mButtonBillNumber.setTag(bool);
                        if (bool) {
                            mButtonBillNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
                        } else {
                            mButtonBillNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
                        }
                        break;
                    case R.id.textView_amount:
                        comparedValue = Double.compare(e1.amount, e2.amount);
                        mButtonAmount.setTag(bool);
                        if (bool) {
                            mButtonAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
                        } else {
                            mButtonAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
                        }
                        break;
                    case R.id.textView_people_number:
                        comparedValue = e1.orders.get(0).personCount.compareTo(e2.orders.get(0).personCount);
                        mButtonPeopleNumber.setTag(bool);
                        if (bool) {
                            mButtonPeopleNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
                        } else {
                            mButtonPeopleNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
                        }
                        break;
                    case R.id.textView_closed_time:
                        comparedValue = e1.closedTime.compareTo(e2.closedTime);
                        mButtonClosedTime.setTag(bool);
                        if (bool) {
                            mButtonClosedTime.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
                        } else {
                            mButtonClosedTime.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
                        }
                        break;
                    case R.id.textView_salesman:
                        comparedValue = e1.cashierStaffId.compareTo(e2.cashierStaffId);
                        mButtonSaleStaff.setTag(bool);
                        if (bool) {
                            mButtonSaleStaff.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
                        } else {
                            mButtonSaleStaff.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
                        }
                        break;
                    case R.id.textView_table_number:
                        comparedValue = e1.orders.get(0).tableName.compareTo(e2.orders.get(0).tableName);
                        mButtonTableNumber.setTag(bool);
                        if (bool) {
                            mButtonTableNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
                        } else {
                            mButtonTableNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
                        }
                        break;
                }
                if (comparedValue == 0) {
                    comparedValue = e1.sn.compareTo(e2.sn);
                }
                if (bool) {
                    comparedValue = comparedValue * -1;
                }
                return comparedValue;
            }
        });
        if (mRecyclerViewAdapter.getmSelection() >= mRecyclerViewAdapter.getCount()) {
            mRecyclerViewAdapter.setmSelection(0);
        }
        billListItemClick(mRecyclerViewAdapter.getSelectionBill());
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void sortBill(long sortId) {
        sortBills = new ArrayList<>();
        for (ClosedBill bill : billListResult.bills) {
            for (ClosedBill.BillPayment payment : bill.payments) {
                if (payment.getType() == sortId) {
                    sortBills.add(bill);
                }
            }
            for (ClosedBill.BillPromotion promotion : bill.promotions) {
                if (promotion.getType() == sortId) {
                    sortBills.add(bill);
                }
            }
            for (ClosedBill.BillDiscount discount : bill.discounts) {
                if (discount.getType() == sortId) {
                    sortBills.add(bill);
                }
            }

        }
        mRecyclerViewAdapter.setClosedBills(sortBills);
        mRecyclerViewAdapter.setmSelection(0);
        billListItemClick(mRecyclerViewAdapter.getClosedBills().get(0));
        setSpentMoney(sortBills);
    }

}
