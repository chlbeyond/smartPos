package com.rainbow.smartpos.refund;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.GetQPayListRequest;
import com.sanyipos.sdk.model.GetQPayListResult;
import com.sanyipos.sdk.model.scala.QPayInfo;
import com.sanyipos.sdk.utils.ConstantsUtil;

import java.util.List;

/**
 * Created by Admin on 2017/7/6.
 */

public class RefundFragment extends Fragment implements View.OnClickListener {
    @Override
    public void onClick(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(myView == null) {
            View rootView = inflater.inflate(R.layout.fragment_qpay_refund, container, false);
            refundBtn = (Button) rootView.findViewById(R.id.button_fragment_qpayrefund_refund);
            refundBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QPayInfo qpayInfo = qpayListAdapter.getSelectedQPayItem();
                    if(qpayInfo == null) {
                        Toast.makeText(getActivity(), "请选择一项记录", Toast.LENGTH_LONG).show();
                        return;
                    }
                    refundQPay(qpayInfo);
                }
            });
            rootView.findViewById(R.id.list_head).setBackgroundColor(Color.parseColor("#CDCDCD"));
            qpayListView = (ListView) rootView.findViewById(R.id.qpay_refund_list);
            qpayListAdapter = new RefundListViewAdapter(getActivity());
            qpayListView.setAdapter(qpayListAdapter);
            qpayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    qpayListAdapter.setmSelection(position);
                }
            });

            myView = rootView;
        }
        ViewGroup parent = (ViewGroup) myView.getParent();
        if (parent != null) {
            parent.removeView(myView);
        }
        updateData();
        return myView;
    }

    private void refundQPay(final QPayInfo info)
    {
        if(info != null) {
            if(info.state == 0) {//已退款
                Toast.makeText(getActivity(), "不能重复退款", Toast.LENGTH_LONG).show();
                return;
            }
            if(!SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_QPAY_REFUND)) {
                Toast.makeText(getActivity(), "没有秒付退款权限", Toast.LENGTH_LONG).show();
                return;
            }
            final NormalDialog remarkDialog = new NormalDialog(getContext());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_table_remark, null);
            final EditText remarkEditText = (EditText) view.findViewById(R.id.editText_dialog_remark);
            remarkDialog.content(view);
            remarkDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                @Override
                public void onClickConfirm() {
                    remarkDialog.dismiss();
                    SanyiScalaRequests.refundQPay(info.bill, info.transaction, remarkEditText.getText().toString().trim(), new Request.ICallBack() {

                        @Override
                        public void onFail(String error) {
                            //
                            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(String arg0) {
                            //
                            updateData();
                        }

                    });
                }
            });
            remarkDialog.widthScale((float) 0.5);
            remarkDialog.show();
        }
    }

    public void updateData() {
        //刷新秒付记录
        SanyiScalaRequests.getQPayListRequest(new GetQPayListRequest.IGetQPayListListener() {

            @Override
            public void onFail(String error) {
                //
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(GetQPayListResult resp) {
                //
                List<QPayInfo> list = null;
                if(resp != null && resp.bills != null) {
                   qpayListAdapter.setQPayList(resp.bills);
                }

            }
        });
    }

    Button refundBtn;
    ListView qpayListView;
    View myView;
    RefundListViewAdapter qpayListAdapter;
}
