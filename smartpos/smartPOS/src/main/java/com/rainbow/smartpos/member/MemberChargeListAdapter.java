package com.rainbow.smartpos.member;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala._UndoMemberChargeRequest;
import com.sanyipos.sdk.model.scala.MemberChargeData;
import com.sanyipos.sdk.model.scala.member.UndoChargeParam;
import com.sanyipos.sdk.model.scala.member.UndoChargeResult;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.OrderUtil;

/**
 * Created by ss on 2016/10/13.
 */

public class MemberChargeListAdapter extends BaseAdapter {

    private Context mContext;

    public MemberChargeData getMemberChargeData() {
        return memberChargeData;
    }

    public void setMemberChargeData(MemberChargeData memberChargeData) {
        this.memberChargeData = memberChargeData;
    }

    private MemberChargeData memberChargeData;

    public MemberChargeListAdapter(Context context, MemberChargeData data) {
        this.mContext = context;
        this.memberChargeData = data;
    }

    @Override
    public int getCount() {
        return memberChargeData.getCharges().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.member_charge_list_item, null);
        } else {
            view = convertView;
        }
        MemberChargeData.MemberCharge memberCharge = memberChargeData.getCharges().get(position);
        TextView textViewName = (TextView) view.findViewById(R.id.textView_member_charge_Name);
        textViewName.setText(memberCharge.getMember().getName());

        TextView textViewPhone = (TextView) view.findViewById(R.id.textView_member_phone);
        String phoneNumber = memberCharge.getMember().getMobile();
        if (phoneNumber.length() > 10) {
            textViewPhone.setText(phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, phoneNumber.length()));
        } else {

            textViewPhone.setText(memberCharge.getMember().getMobile());
        }

        TextView textViewNumber = (TextView) view.findViewById(R.id.textView_member_number);
        String memberNumber = memberCharge.getMember().getCard();
        if (memberNumber.length() > 3) {
            textViewNumber.setText("***" + memberNumber.substring(memberNumber.length() - 3, memberNumber.length()));
        } else {
            textViewNumber.setText(memberNumber);
        }
        TextView textViewType = (TextView) view.findViewById(R.id.textView_member_type);
        textViewType.setText(memberCharge.getMember().getMemberTypeName());

        TextView textViewMoney = (TextView) view.findViewById(R.id.textView_member_charge_money);
        textViewMoney.setText(OrderUtil.dishPriceFormatter.format(memberCharge.getCharge()) + "送" + OrderUtil.dishPriceFormatter.format(memberCharge.getGift()));

        TextView textViewState = (TextView) view.findViewById(R.id.textView_member_charge_state);
        textViewState.setText(getChargeState(memberCharge.getState()));
        if (memberCharge.getState() == MemberChargeData.CHARGE_UNCONFIRMED) {
            textViewState.setTextColor(Color.RED);
        }

        Button buttonUndo = (Button) view.findViewById(R.id.button_member_charge_undo);
        if (memberCharge.getState() == MemberChargeData.CHARGE_SUCCESS) {
            buttonUndo.setVisibility(View.VISIBLE);
            buttonUndo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SanyiSDK.currentUser.hasPermissionOf(ConstantsUtil.PERMISSION_REVERSE_BILL)) {
                        final NormalDialog remarkDialog = new NormalDialog(mContext);
                        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_table_remark, null);
                        final EditText remarkEditText = (EditText) view.findViewById(R.id.editText_dialog_remark);
                        remarkDialog.title("请输入撤销原因");
                        remarkDialog.content(view);
                        remarkDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                            @Override
                            public void onClickConfirm() {
                                remarkDialog.dismiss();
                                UndoChargeParam param = new UndoChargeParam();
                                MemberChargeData.MemberCharge data = memberChargeData.getCharges().get(position);
                                param.setSn(data.getSn());
                                param.setStaff(SanyiSDK.currentUser.getId());
                                param.setRemark(remarkEditText.getText().toString());
                                SanyiScalaRequests.undoMemberChargeRequest(param, new _UndoMemberChargeRequest.IUndoChargeListener() {
                                    @Override
                                    public void onSuccess(UndoChargeResult resp) {
                                        memberChargeData.getCharges().get(position).setState(MemberChargeData.UNDO_SUCCESS);
                                        notifyDataSetChanged();
                                    }


                                    @Override
                                    public void onFail(String error) {
                                        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                        remarkDialog.widthScale((float) 0.5);
                        remarkDialog.show();
                    } else {
                        Toast.makeText(mContext, "权限不足", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            buttonUndo.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private String getChargeState(int state) {
        switch (state) {
            case MemberChargeData.CHARGE_SUCCESS:
                return "成功";
            case MemberChargeData.CHARGE_UNCONFIRMED:
                return "待确认";
            case MemberChargeData.UNDO_SUCCESS:
                return "已撤销";
            case MemberChargeData.CHARGE_FAILED:
                return "失败";
            default:
                return "";
        }
    }
}
