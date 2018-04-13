package com.rainbow.smartpos.member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.Listener.OnChooseSexListener;
import com.rainbow.smartpos.util.Listener.OnChooseStaffListener;
import com.rainbow.smartpos.util.Listener.OnChooseMemberTypeListener;
import com.rainbow.smartpos.util.Listener.OnChooseIdTypeListener;

import com.sanyipos.sdk.model.rest.IDType;
import com.sanyipos.sdk.model.rest.MemberTypes;
import com.sanyipos.sdk.model.rest.StaffRest;

import java.util.List;

public class ChooseRechargeStaffDialog {
	public static final int CHOOSE_STAFF = 0;
	public static final int CHOOSE_SEX = 1;
	public static final int CHOOSE_MEMBER_TYPE = 2;
	public static final int CHOOSE_ID_TYPE = 3;

	private MyDialog dialog;
	private TextView sure;
	private TextView op_dialog_title;
	private ImageButton cancel;
	private Context activity;
	private List<StaffRest> staffs;
	private List<String> sexs;
	private List<MemberTypes> memberTypes;
	private List<IDType> idTypes;

	private ListView reason_lv;
	private RechargeStaffAdapter mRechargeStaffAdapter;
	private OnChooseStaffListener mOnChooseStaffListener;
	private SexAdapter mSexAdapter;
	private OnChooseSexListener mOnChooseSexListener;
	private MemberTypeAdapter mMemberTypeAdapter;
	private OnChooseMemberTypeListener mOnChooseMemberTypeListener;
	private IdTypeAdapter mIdTypeAdapter;
	private OnChooseIdTypeListener mOnChooseIdTypeListener;
	private int flag;

	/**
	 * 选择营销员dialog
	 */
	public ChooseRechargeStaffDialog(Context activity, int flag, List<StaffRest> staffs, OnChooseStaffListener listener){
		this.activity = activity;
		this.staffs = staffs;
		this.flag = flag;
		this.mOnChooseStaffListener = listener;
	}
	/**
	 * 选择性别dialog
	 */
	public ChooseRechargeStaffDialog(Context activity, int flag, List<String> sexs, OnChooseSexListener listener){
		this.activity = activity;
		this.sexs = sexs;
		this.flag = flag;
		this.mOnChooseSexListener = listener;
	}
	/**
	 * 选择会员类型dialog
	 */
	public ChooseRechargeStaffDialog(Context activity, int flag, List<MemberTypes> memberTypes, OnChooseMemberTypeListener listener){
		this.activity = activity;
		this.memberTypes = memberTypes;
		this.flag = flag;
		this.mOnChooseMemberTypeListener = listener;
	}
	/**
	 * 选择证件类型dialog
	 */
	public ChooseRechargeStaffDialog(Context activity, int flag, List<IDType> idTypes, OnChooseIdTypeListener listener){
		this.activity = activity;
		this.idTypes = idTypes;
		this.flag = flag;
		this.mOnChooseIdTypeListener = listener;
	}
	public void show() {
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.choose_staff_dialog_layout, null, false);
		dialog = new MyDialog(activity, (int) (MainScreenActivity.getScreenWidth() * 0.4), (int) (MainScreenActivity.getScreenHeight() * 0.9), view, R.style.OpDialogTheme);

		op_dialog_title = (TextView) view.findViewById(R.id.op_dialog_title);
		reason_lv = (ListView) view.findViewById(R.id.reason_lv);
		sure = (TextView) view.findViewById(R.id.sure_btn);
		cancel = (ImageButton) view.findViewById(R.id.iv_close_dialog);

		setListener();
		setTitleText();
		setLvAdapter();
		dialog.show();
	}

	public void setTitleText(){
		switch(flag){
			case CHOOSE_STAFF:
				op_dialog_title.setText(activity.getString(R.string.sale));
				break;
			case CHOOSE_SEX:
				op_dialog_title.setText(activity.getString(R.string.sex));
				break;
			case CHOOSE_MEMBER_TYPE:
				op_dialog_title.setText(activity.getString(R.string.member_type));
				break;
			case CHOOSE_ID_TYPE:
				op_dialog_title.setText(activity.getString(R.string.documents_type));
				break;
			default:
				break;
		}
	}

	public void setLvAdapter(){
		switch(flag){
			case CHOOSE_STAFF:
				mRechargeStaffAdapter = new RechargeStaffAdapter(activity, staffs);
				reason_lv.setAdapter(mRechargeStaffAdapter);
				break;
			case CHOOSE_SEX:
				mSexAdapter = new SexAdapter(activity, sexs);
				reason_lv.setAdapter(mSexAdapter);
				break;
			case CHOOSE_MEMBER_TYPE:
				mMemberTypeAdapter = new MemberTypeAdapter(activity, memberTypes);
				reason_lv.setAdapter(mMemberTypeAdapter);
				break;
			case CHOOSE_ID_TYPE:
				mIdTypeAdapter = new IdTypeAdapter(activity, idTypes);
				reason_lv.setAdapter(mIdTypeAdapter);
				break;
			default:
				break;
		}
	}

	public void setListener() {
		cancel.setOnClickListener(onClickListener);
		reason_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(flag){
					case CHOOSE_STAFF:
						mRechargeStaffAdapter.setSelect(position);
						break;
					case CHOOSE_SEX:
						mSexAdapter.setSelect(position);
						break;
					case CHOOSE_MEMBER_TYPE:
						mMemberTypeAdapter.setSelect(position);
						break;
					case CHOOSE_ID_TYPE:
						mIdTypeAdapter.setSelect(position);
						break;
					default:
						break;
				}
				buttonSureClick();
			}
		});
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.iv_close_dialog:
				buttonCancelClick();
				break;

			default:
				break;
			}
		}
	};

	private void buttonSureClick() {
		dialog.dismiss();
		switch(flag){
			case CHOOSE_STAFF:
				mOnChooseStaffListener.onSure(mRechargeStaffAdapter.getSelectStaff());
				break;
			case CHOOSE_SEX:
				mOnChooseSexListener.onSure(mSexAdapter.getSelectSex());
				break;
			case CHOOSE_MEMBER_TYPE:
				mOnChooseMemberTypeListener.onSure(mMemberTypeAdapter.getSelectMemberTypes());
				break;
			case CHOOSE_ID_TYPE:
				mOnChooseIdTypeListener.onSure(mIdTypeAdapter.getSelectIDType());
				break;
			default:
				break;
		}
	}

	private void buttonCancelClick() {
		dialog.dismiss();
		switch(flag){
			case CHOOSE_STAFF:
				mOnChooseStaffListener.onCancel();
				break;
			case CHOOSE_SEX:
				mOnChooseSexListener.onCancel();
				break;
			case CHOOSE_MEMBER_TYPE:
				mOnChooseMemberTypeListener.onCancel();
				break;
			case CHOOSE_ID_TYPE:
				mOnChooseIdTypeListener.onCancel();
				break;
			default:
				break;
		}
	}
}
