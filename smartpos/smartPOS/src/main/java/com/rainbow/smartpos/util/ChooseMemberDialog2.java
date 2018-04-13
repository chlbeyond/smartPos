package com.rainbow.smartpos.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.model.scala.check.MemberInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by administrator on 2017/8/29.
 */

public class ChooseMemberDialog2 extends Dialog{
    public ChooseMemberDialog2(Context context) {
        super(context);
    }

    public ChooseMemberDialog2(FragmentActivity activity, List<MemberInfo> members, ChooseMemberListener mChooseMemberListener) {
        super(activity);
        this.mChooseMemberListener = mChooseMemberListener;
        this.members.clear();
        this.activity = activity;
        this.members.addAll(members);
    }

    public static interface ChooseMemberListener {
        public void ChooseMember(MemberInfo memberInfo);
    }

    private List<MemberInfo> members=new ArrayList<>();
    private TextView chooseMemberCancelTv;
    private TextView chooseMemberConfirmTv;
    private ExpandableListView chooseMemberLv;
    private List<String> groupList;
    private ChooseMemberListener mChooseMemberListener;
    MemberInfoAdapter adapter;
    private int currentPosition = 0;
    FragmentActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choose_member);

        Window window = getWindow();
        //window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.height = (int) MainScreenActivity.getScreenHeight()*9/10;
        params.width = (int) MainScreenActivity.getScreenWidth()*6/10;
        window.setAttributes(params);

        chooseMemberCancelTv = (TextView) findViewById(R.id.tv_dialog_choose_member_cancel);
        chooseMemberConfirmTv = (TextView) findViewById(R.id.tv_dialog_choose_member_confirm);
        chooseMemberLv = (ExpandableListView) findViewById(R.id.lv_dialog_choose_member);

        chooseMemberLv.setGroupIndicator(null);
        chooseMemberLv.setFocusable(false);
        initData();
        adapter = new MemberInfoAdapter(groupList, members, activity);
        chooseMemberLv.setAdapter(adapter);
        for(int i = 0; i < adapter.getGroupCount(); i++){
            chooseMemberLv.expandGroup(i);
        }
        chooseMemberLv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                adapter.setSelect(groupPosition);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        chooseMemberConfirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooseMemberListener.ChooseMember(adapter.getSelectMember());
                dismiss();
            }
        });
        chooseMemberCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

    }

    private void initData(){
        groupList = new ArrayList<String>();
        for (int i = 0; i < members.size(); i++) {
            groupList.add(String.valueOf(i+1));
        }
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
        setCanceledOnTouchOutside(true);
    }



    private class MemberInfoAdapter extends BaseExpandableListAdapter {

        private List<String> groupList;
        private List<MemberInfo> childList;
        private LayoutInflater inflater;
        private Context context;

        public MemberInfoAdapter(List<String> groupList, List<MemberInfo> childList, Context context) {
            super();
            this.groupList = groupList;
            this.childList = childList;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childList.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setTextColor(Color.parseColor("#ff0000"));
            tv.setText(groupList.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            MemberInfo memberInfo = childList.get(groupPosition);
            convertView = inflater.inflate(R.layout.show_memberinfo_item, null);
            TextView member_sn = (TextView) convertView.findViewById(R.id.member_sn);
            TextView member_name = (TextView) convertView.findViewById(R.id.member_name);
            TextView member_type = (TextView) convertView.findViewById(R.id.member_type);
            TextView member_moblie = (TextView) convertView.findViewById(R.id.member_moblie);
            ImageView is_public_checkbox = (ImageView) convertView.findViewById(R.id.is_public_checkbox);

            member_sn.setText("会员姓名:    " + memberInfo.name);
            member_name.setText("会员类型:    " + memberInfo.memberTypeName);
            member_type.setText("会员卡号:    " + memberInfo.card);
            member_moblie.setText("手机号码:    " + memberInfo.mobile);
            if (currentPosition == groupPosition) {
                is_public_checkbox.setVisibility(View.VISIBLE);
            }else{
                is_public_checkbox.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }
        public void setSelect(int position){
            currentPosition = position;
        }

        public MemberInfo getSelectMember(){
            return childList.get(currentPosition);
        }

    }
}
