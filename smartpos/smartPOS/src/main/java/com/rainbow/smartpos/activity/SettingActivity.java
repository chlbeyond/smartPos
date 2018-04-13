package com.rainbow.smartpos.activity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.rainbow.smartpos.BaseActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.activity.settingfragment.LocalSettingFragment;
import com.rainbow.smartpos.activity.settingfragment.PrintSettingFragment;
import com.rainbow.smartpos.activity.settingfragment.SetBalanceFragment;
import com.rainbow.smartpos.presentation.DisplayPresentation;
import com.rainbow.smartpos.presentation.FilePathData;
import com.sanyipos.android.sdk.androidUtil.SharePreferenceUtil;
import com.sanyipos.android.sdk.androidUtil.SmartPosPrivateKey;
import com.sanyipos.sdk.utils.JsonUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import filepicker.FilePickerActivity;

/**
 * Created by ss on 2016/8/30.
 */
public class SettingActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.radioButton_setting_local_setting)
    RadioButton radioButtonSettingLocalSetting;
    @Bind(R.id.radioButton_setting_print_setting)
    RadioButton radioButtonSettingPrintSetting;
    @Bind(R.id.radioGroup_setting)
    RadioGroup radioGroupSetting;


    SetBalanceFragment setBalanceFragment = new SetBalanceFragment();
    LocalSettingFragment localFragment = new LocalSettingFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);
        setCustomTitle(getResources().getString(R.string.setting));

        radioGroupSetting.setOnCheckedChangeListener(this);

        LocalSettingFragment localFragment = new LocalSettingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, localFragment).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case DisplayPresentation.EX_FILE_PICKER_RESULT:
                    if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clip = data.getClipData();

                            if (clip != null) {
                                if (clip.getItemCount() > 0) {
                                    FilePathData filePathData = new FilePathData();
                                    for (int i = 0; i < clip.getItemCount(); i++) {
                                        Uri uri = clip.getItemAt(i).getUri();
                                        filePathData.filePath.add(uri.getPath());
                                    }
                                    SharePreferenceUtil.saveStringPreference(this, SmartPosPrivateKey.ST_LOCAL_PRESENTATION, JsonUtil.toJson(filePathData));
                                    if (presentation != null)
                                        presentation.initFragment();
                                }
                            }
                        }
                    }
            }
        }
    }


    public void showBalanceSetting(boolean b) {
        if (b)
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, setBalanceFragment).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, localFragment).commit();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButton_setting_local_setting:


                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, localFragment).commit();
                break;
            case R.id.radioButton_setting_print_setting:
                setBalanceFragment.closePort();
                PrintSettingFragment printFragment = new PrintSettingFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_setting, printFragment).commit();
                break;
        }
    }
}
