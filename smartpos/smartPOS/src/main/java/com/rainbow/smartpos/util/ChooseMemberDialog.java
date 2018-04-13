package com.rainbow.smartpos.util;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener.OnChooseMemberListener;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.utils.OrderUtil;

public class ChooseMemberDialog {
	FragmentActivity activity;
	OnChooseMemberListener chooseMemberListener;
	AlertDialog dialog;
	List<MemberInfo> members;
	private List<String> groupList;
	private int currentPosition = 0;
	MemberInfoAdapter adapter;
	public ChooseMemberDialog(FragmentActivity activity, List<MemberInfo> members, OnChooseMemberListener chooseMemberListener){
		this.activity = activity;
		this.members = members;
		this.chooseMemberListener = chooseMemberListener;
	}
	
	public void show(){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("请选择你要查询的会员");
		View view = LayoutInflater.from(activity).inflate(R.layout.handover_preview, null);
		ExpandableListView memberLv = (ExpandableListView) view.findViewById(R.id.listViewHandoverInfo);
		memberLv.setGroupIndicator(null);  
		memberLv.setFocusable(false);
		initData();
		adapter = new MemberInfoAdapter(groupList, members, activity);
		memberLv.setAdapter(adapter);
		for(int i = 0; i < adapter.getGroupCount(); i++){          
			 memberLv.expandGroup(i);     
		} 
		memberLv.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				adapter.setSelect(groupPosition);
				adapter.notifyDataSetChanged();
				return false;
			}
		});
		builder.setPositiveButton("选定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				chooseMemberListener.sure(adapter.getSelectMember());
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				chooseMemberListener.cancel();
				dialog.dismiss();
			}
		});
		builder.setView(view);
		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();
	}
	private void initData(){
		groupList = new ArrayList<String>();
		for (int i = 0; i < members.size(); i++) {
			groupList.add(String.valueOf(i+1));
		}
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
