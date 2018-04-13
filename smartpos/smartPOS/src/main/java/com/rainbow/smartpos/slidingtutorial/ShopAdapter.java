package com.rainbow.smartpos.slidingtutorial;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.sanyipos.android.sdk.androidUtil.RegisteDataUtils;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.api.RegisteData;
import com.sanyipos.sdk.api.SanyiCloudRequests;
import com.sanyipos.sdk.api.services.cloud._BindPosToShopRequest;
import com.sanyipos.sdk.model.cloud.ShopList;

import java.util.List;

/**
 * Created by ss on 2016/3/19.
 */
public class ShopAdapter extends BaseAdapter {
    public Context mContext;
    public List<ShopList.Shop> mShops;
    public SlidingTutorial activity;

    public ShopAdapter(Context context, List<ShopList.Shop> shops) {
        super();
        this.mContext = context;
        this.mShops = shops;
        activity = (SlidingTutorial) context;
    }

    @Override
    public int getCount() {
        return mShops.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (view == null)
            vi = LayoutInflater.from(mContext).inflate(R.layout.sliding_shop_recycler, null);
        TextView textView = (TextView) vi.findViewById(R.id.textView_sliding_shop_recycler_shop_name);
        textView.setText(mShops.get(i).name);
        vi.setOnClickListener(listener);
        vi.setTag(i);
        return vi;
    }

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ShopList.Shop shop = mShops.get((int) view.getTag());
            NormalDialog normalDialog = new NormalDialog(mContext);
            View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_input_pos_name, null);
            final EditText editText = (EditText) v.findViewById(R.id.editText_dialog_input_pos_name);
            normalDialog.content(v);
            normalDialog.show();
            normalDialog.widthScale((float) 0.5);
            normalDialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                @Override
                public void onClickConfirm() {
                    if(editText.getText().toString().length()<1){
                        Toast.makeText(mContext,"POS机名称不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    activity.progressHUD.setDetailsLabel("正在连接主机，请稍后");
                    activity.progressHUD.show();
                    SanyiCloudRequests.bindPosToShopRequest(shop.shop, editText.getText().toString(), new _BindPosToShopRequest.IBindPosToShopListener() {
                        @Override
                        public void onSuccess(String domain, RegisteData registeData) {
                            SharePreferenceUtil.saveStringPreference(mContext, SmartPosPrivateKey.SP_RD_ANGET_ADDRESS, domain);
                            RegisteDataUtils.savePosRegisteData(mContext, registeData);
                            Intent intent = new Intent(mContext,MainScreenActivity.class);
                            mContext.startActivity(intent);
                        }


                        @Override
                        public void onFail(String error) {

                        }
                    });
                }
            });


        }
    };
}
