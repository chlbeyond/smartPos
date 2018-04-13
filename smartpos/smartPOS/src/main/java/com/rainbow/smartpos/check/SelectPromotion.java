package com.rainbow.smartpos.check;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.CashierRequest.ICashierRequestListener;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.scala.check.CashierParamResult;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.Promotion;
import com.sanyipos.sdk.model.scala.check.CashierParamResult.Promotion.MemberType;
import com.sanyipos.sdk.model.scala.check.CashierPromotion;
import com.sanyipos.sdk.model.scala.check.CashierResult;
import com.sanyipos.sdk.model.scala.check.MemberInfo;
import com.sanyipos.sdk.utils.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectPromotion {
	// flag values
	public static int NOFLAGS = 0;
	public static int HIDE_INPUT = 1;
	public static int HIDE_PROMPT = 2;
	public ListView promotion_more;
	public List<CashierParamResult.Promotion> moreParams = new ArrayList<CashierParamResult.Promotion>();
	public List<String> singleList = new ArrayList<String>();
	public LayoutInflater inflater;
	public Activity activity;
	public ListView listView_promotion;
	public LinearLayout linearLayout_single_promotion;
	public LinearLayout linearLayout_more_promotion;
	public PromotionMoreAdapter pAdapter;
	public List<Promotion> params = new ArrayList<CashierParamResult.Promotion>();
	public AlertDialog altDlg;
	public MemberInfo memberInfo;

	public MemberInfo getMemberInfo() {
		return memberInfo;
	}

	public void setMemberInfo(MemberInfo memberInfo) {
		this.memberInfo = memberInfo;
	}

	public List<Integer> moreSelects = new ArrayList<Integer>();
	public SelectPromotionInterface sInferface;
	public CheckFragment checkFragment;
	public List<Promotion> param;
	public List<CashierPromotion> cParam;

	public List<CashierPromotion> getcParam() {
		return cParam;
	}

	public void setcParam(List<CashierPromotion> cParam) {
		this.cParam = cParam;
	}

	public void setPromotion(List<Promotion> param) {
		this.param = param;
	}

	public interface SelectPromotionInterface {
		public void selectSinglePromotion(long promotionId);

		public void undoPromotion(long promotion);
	}

	public void parserParams() {
		moreParams.clear();
		moreSelects.clear();
		for (Promotion pro : param) {
			if (memberInfo != null) {
				for (MemberType m : pro.memberTypes) {
					if (m.id == memberInfo.memberType) {
						addParams(pro);
					}
				}
				if (pro.promotionType == ConstantsUtil.PROMOTION_SPECIAL) {
					addParams(pro);
				}
			} else {
				addParams(pro);

			}
		}
	}

	public void addParams(Promotion p) {
		moreParams.add(p);
		for (CashierPromotion cP : cParam) {
			if (p.id == cP.promotion) {
				moreSelects.add(moreParams.size() - 1);
			}
		}
	}

	public SelectPromotion(final Activity activity) {
		Builder inputDialog = new AlertDialog.Builder(activity);

		// Inflate the Dialog layout
		this.activity = activity;
		inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.select_promotion, null, false);
		listView_promotion = (ListView) dialogView.findViewById(R.id.listView_promotion);
		pAdapter = new PromotionMoreAdapter();
		listView_promotion.setAdapter(pAdapter);
		listView_promotion.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				addSelect(arg2);
			}
		});
		inputDialog.setView(dialogView);
		inputDialog.setTitle("促销活动");
		inputDialog.setNegativeButton(activity.getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				altDlg.dismiss();
			}
		});
		altDlg = inputDialog.create();

	}

	public void promotionShow() {
		altDlg.show();
	}

	public boolean promotionIsShow() {
		return altDlg.isShowing();
	}

	public void promotioDismiss() {
		altDlg.dismiss();
	}

	public class PromotionMoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return moreParams.size();
		}

		public void setParams(List<Promotion> ps) {
			moreParams = ps;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = LayoutInflater.from(activity);
			View view;
			if (convertView == null) {
				view = inflater.inflate(R.layout.promotion_item, null);
			} else {
				view = convertView;
			}
			final CheckBox textViewPromotion = (CheckBox) view.findViewById(R.id.textView_promotion);
			textViewPromotion.setText(moreParams.get(position).name);
			textViewPromotion.setChecked(false);
			for (Integer i : moreSelects) {
				if (position == i) {
					textViewPromotion.setChecked(true);
				}
			}
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addSelect(position);
				}
			});
			return view;
		}

	}

	public void addSelect(final int position) {
		if (moreSelects.contains(position)) {
			// moreSelects.remove((Object) position);
			for (CashierPromotion p : cParam) {
				if (p.promotion == moreParams.get(position).id) {
					SanyiScalaRequests.CashierUndoPromotionRequest(checkFragment.seats, p.id, SanyiSDK.currentUser.id, new ICashierRequestListener() {

						@Override
						public void onFail(String error) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
							// TODO Auto-generated method stu
							checkFragment.updateUI(resp, ods);
						}
					});
				}
			}
		} else {
			if (memberInfo != null || moreParams.get(position).promotionType == ConstantsUtil.PROMOTION_SPECIAL) {
				SanyiScalaRequests.CashierPromotionRequest(checkFragment.seats, moreParams.get(position).id, (memberInfo != null) ? memberInfo.id : 0, SanyiSDK.currentUser.id,
						new ICashierRequestListener() {


							@Override
							public void onFail(String error) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(CashierResult resp, List<OrderDetail> ods) {
								// TODO Auto-generated method stu
								checkFragment.updateUI(resp, ods);
							}
						});
			} else {
				altDlg.dismiss();
				//checkFragment.buttonCheckVerifyMember.performClick();
			}
		}
	}

}