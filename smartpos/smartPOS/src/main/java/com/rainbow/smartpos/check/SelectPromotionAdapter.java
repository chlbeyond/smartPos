package com.rainbow.smartpos.check;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rainbow.smartpos.R;

public class SelectPromotionAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> listDataHeader; // header titles
	// child data in format of header title, child title
	private SparseArray<List<String>> listDataChild;
	public boolean[] selectStatus;
	// public List<JSONObject> promotions;
	public List<Boolean> promotionSelections = new ArrayList<Boolean>();
	CheckFragment fragment;

	public SelectPromotionAdapter(CheckFragment fragment) {
		this.context = fragment.getActivity();
//		this.listDataHeader = fragment.promotion.getPromotionNames();
//		this.listDataChild = fragment.promotion.getPromotionChild();
//		this.promotionSelections = fragment.promotion.getPromotionSelections();
	}

	@Override
	public String getChild(int groupPosition, int childPosititon) {
		return this.listDataChild.get(groupPosition).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.select_promotion_item, null);
		}

		TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

		txtListChild.setText(childText);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (this.listDataChild.get(groupPosition) != null) {
			return this.listDataChild.get(groupPosition).size();
		} else {
			return 0;
		}
	}

	@Override
	public String getGroup(int groupPosition) {
		return this.listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.select_promotion_group, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.textViewPromotionGroupName);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		Boolean selected = promotionSelections.get(groupPosition);
		CheckBox checkBoxlListHeader = (CheckBox) convertView.findViewById(R.id.checkBoxPromotionGroupName);
		checkBoxlListHeader.setChecked(selected.booleanValue());

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
