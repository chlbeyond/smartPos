package com.rainbow.smartpos.mainframework;

import android.content.Context;

/**
 * Created by ss on 2016/1/19.
 */
public class MainPresenterImpl implements MainPresenter {
    private Context mContext;
    private MainView mMainView;

    public  MainPresenterImpl(Context context, MainView view) {
        this.mContext = context;
        this.mMainView = view;
    }

    @Override
    public void ChangePsd() {

    }
}
