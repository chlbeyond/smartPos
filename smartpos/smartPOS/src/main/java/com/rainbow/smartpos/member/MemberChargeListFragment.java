package com.rainbow.smartpos.member;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala._GetMemberChargeListRequest;
import com.sanyipos.sdk.model.scala.MemberChargeData;

/**
 * Created by ss on 2016/10/12.
 */

public class MemberChargeListFragment extends Fragment {
    private ListView listMemberCharge;
    private MemberChargeListAdapter chargeListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_charge_list, container, false);
        listMemberCharge = (ListView) view.findViewById(R.id.listView_member_charge_list);
        SanyiScalaRequests.getMemberChargeListRequest(new _GetMemberChargeListRequest.IGetMemberChargeListListener() {
            @Override
            public void onSuccess(MemberChargeData resp) {
                if (resp.getCharges().size() > 0) {
                    chargeListAdapter = new MemberChargeListAdapter(getActivity(), resp);
                    listMemberCharge.setAdapter(chargeListAdapter);
                }
            }


            @Override
            public void onFail(String error) {

                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();

            }
        });

        listMemberCharge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
        return view;
    }
}
