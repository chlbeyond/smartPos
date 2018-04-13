package com.rainbow.smartpos.activity.reportfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.activity.reportfragment.handover.CardPagerAdapter;
import com.rainbow.smartpos.activity.reportfragment.handover.ShadowTransformer;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala._HandoverDetailRequest;
import com.sanyipos.sdk.model.scala.HandoverListResult;

/**
 * Created by ss on 2016/9/12.
 */
public class HandoverFragment extends Fragment {

    private ViewPager viewPager;

    private CardPagerAdapter mCardAdapter;


    private ShadowTransformer mCardShadowTransformer;

    private LinearLayout emptyLinearLayout;
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handover, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager_fragment_handover);
        emptyLinearLayout = (LinearLayout) view.findViewById(R.id.empty_layout);
        SanyiScalaRequests.getHandoverDetailRequest(new _HandoverDetailRequest.IGetHandoverDetail() {
            @Override
            public void onSuccess(HandoverListResult resp) {
                if (resp.handovers.size() == 0) {
                    Toast.makeText(getActivity(), "暂无交接记录", Toast.LENGTH_LONG).show();
                    emptyLinearLayout.setVisibility(View.VISIBLE);
                    return;
                }
                mCardAdapter = new CardPagerAdapter(getActivity(), resp);

                viewPager.setAdapter(mCardAdapter);

                mCardShadowTransformer = new ShadowTransformer(viewPager, mCardAdapter);
                viewPager.setPageTransformer(false, mCardShadowTransformer);
            }


            @Override
            public void onFail(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();


            }
        });

        return view;
    }

}
