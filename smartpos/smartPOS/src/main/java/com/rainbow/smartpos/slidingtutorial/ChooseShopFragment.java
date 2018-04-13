package com.rainbow.smartpos.slidingtutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.place.SpaceItemDecoration;
import com.sanyipos.sdk.model.cloud.ShopList;

/**
 * Created by ss on 2016/3/19.
 */
public class ChooseShopFragment extends Fragment {
    private RecyclerView mRecyclerView;
    public ShopList lists;
    public ChooseShopFragment(ShopList list){
        this.lists = list;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sliding_choose_shop, container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_sliding_choose_shop);
        SpaceItemDecoration dividerLine = new SpaceItemDecoration(SpaceItemDecoration.VERTICAL);
        dividerLine.setSize(1);
        dividerLine.setColor(Color.parseColor("#DDDDDD"));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(dividerLine);
        mRecyclerView.setAdapter(new ChooseShopAdapter(getActivity(),lists));
        return view;
    }
}
