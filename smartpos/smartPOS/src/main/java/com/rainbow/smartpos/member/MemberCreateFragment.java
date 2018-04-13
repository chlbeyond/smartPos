package com.rainbow.smartpos.member;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.Listener;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.MemberCreateRequest.IMemberCreateListener;
import com.sanyipos.sdk.model.rest.IDType;
import com.sanyipos.sdk.model.rest.MemberTypes;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.member.MemberCreateParam;
import com.sanyipos.sdk.model.scala.member.MemberCreateParam.CreateMemberCard;
import com.sanyipos.sdk.model.scala.member.MemberCreateResult;
import com.rainbow.smartpos.Restaurant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberCreateFragment extends Fragment implements OnClickListener, OnTouchListener {
    public EditText editText_member_create_fragment_number; // 会员卡号
    public EditText editText_member_create_fragment_rfid; // 会员卡RID
    public EditText editText_member_create_fragment_name;// 会员名称
    public EditText editText_member_create_fragment_paper_number;// 证件号码
    public TextView editText_member_create_fragment_birthday; // 出生年月
    public TextView editText_member_create_fragment_mobilephone;// 移动电话

    // spinner
    public TextView editText_member_create_fragment_category; // 会员类型
    public TextView editText_member_create_fragment_sale;// 营销员
    public TextView editText_member_create_fragment_sex;// 会员性别
    public TextView editText_member_create_fragment_paper_type; // 证件类型

    public EditText editText_member_create_fragment_pay_pwd;

    private Button button_member_create_save_and_create;
    private Button button_member_create_cancel;

    private LinearLayout card_info_layout;
    private LinearLayout user_info_layout;

    private PopupWindow mPopupWindow;
    private NumberPopWindow mNumberPopWindow;

    private List<String> sales;
    private List<String> sexs;
    private List<String> paperTypes;
    private List<String> categorys;

    private List<IDType> idTypes;
    private List<MemberTypes> memberTypes;
    private MemberFragment activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_member_create, container, false);
        activity = (MemberFragment) getActivity();
        initView(rootView);
        return rootView;
    }

    private void initData() {
        // TODO Auto-generated method stub
        idTypes = SanyiSDK.rest.idTypes;
        memberTypes = SanyiSDK.rest.memberTypes;

        sales = new ArrayList<String>();
        sexs = new ArrayList<String>();
        paperTypes = new ArrayList<String>();
        categorys = new ArrayList<String>();
        if (null != memberTypes && memberTypes.size() > 0) {
            for (int i = 0; i < memberTypes.size(); i++) {
                categorys.add(memberTypes.get(i).name);
            }
        } else {
            Toast.makeText(getActivity(), "会员类型为空,不允许创建会员", Toast.LENGTH_SHORT).show();
        }
        sexs.add("男");
        sexs.add("女");

        if (null != idTypes) {
            for (int i = 0; i < idTypes.size(); i++) {
                paperTypes.add(idTypes.get(i).name);
            }
        }

        for (StaffRest staff : SanyiSDK.rest.staffs) {
            sales.add(staff.name);
        }

        if (categorys.size() > 0) {
            editText_member_create_fragment_category.setText(categorys.get(0));
        }
        if (paperTypes.size() > 0) {
            editText_member_create_fragment_paper_type.setText(paperTypes.get(0));
        }
        if (sales.size() > 0) {
            editText_member_create_fragment_sale.setText(sales.get(0));
        }
        if (sexs.size() > 0) {
            editText_member_create_fragment_sex.setText(sexs.get(0));
        }

    }

    public void showCardInfoLayout() {
        card_info_layout.setVisibility(View.VISIBLE);
        user_info_layout.setVisibility(View.GONE);
        button_member_create_save_and_create.setText(getString(R.string.next_step));
        button_member_create_cancel.setText(getString(R.string.cancel_save));
    }

    public void showUserInfoLayout() {
        card_info_layout.setVisibility(View.GONE);
        user_info_layout.setVisibility(View.VISIBLE);
        button_member_create_save_and_create.setText(getString(R.string.save_and_create));
        button_member_create_cancel.setText(getString(R.string.last_step));
    }

    public void initView(View view) {
        editText_member_create_fragment_paper_type = (TextView) view.findViewById(R.id.editText_member_create_fragment_ID_type);

        editText_member_create_fragment_rfid = (EditText) view.findViewById(R.id.editText_member_create_fragment_RID);
        editText_member_create_fragment_rfid.setInputType(InputType.TYPE_NULL);

        editText_member_create_fragment_number = (EditText) view.findViewById(R.id.editText_member_create_fragment_number);
        editText_member_create_fragment_number.setInputType(InputType.TYPE_CLASS_NUMBER);

        editText_member_create_fragment_category = (TextView) view.findViewById(R.id.editText_member_create_fragment_category);
        editText_member_create_fragment_sale = (TextView) view.findViewById(R.id.editText_member_create_fragment_sale);
        editText_member_create_fragment_name = (EditText) view.findViewById(R.id.editText_member_create_fragment_name);
        editText_member_create_fragment_sex = (TextView) view.findViewById(R.id.editText_member_create_fragment_sex);
        editText_member_create_fragment_paper_number = (EditText) view.findViewById(R.id.editText_member_create_fragment_ID_number);
        editText_member_create_fragment_paper_number.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText_member_create_fragment_birthday = (TextView) view.findViewById(R.id.editText_member_create_fragment_birthday);
        editText_member_create_fragment_mobilephone = (TextView) view.findViewById(R.id.editText_member_create_fragment_mobilephone);
        editText_member_create_fragment_mobilephone.setInputType(InputType.TYPE_NULL);

        editText_member_create_fragment_pay_pwd = (EditText) view.findViewById(R.id.editText_member_create_fragment_pay_pwd);

        button_member_create_save_and_create = (Button) view.findViewById(R.id.button_member_create_save_and_create);
        button_member_create_cancel = (Button) view.findViewById(R.id.button_member_create_cancel);

        card_info_layout = (LinearLayout) view.findViewById(R.id.card_info_layout);
        user_info_layout = (LinearLayout) view.findViewById(R.id.user_info_layout);

        button_member_create_save_and_create.setOnClickListener(this);
        button_member_create_cancel.setOnClickListener(this);

        editText_member_create_fragment_category.setOnClickListener(this);
        editText_member_create_fragment_paper_type.setOnClickListener(this);
        editText_member_create_fragment_sex.setOnClickListener(this);
        editText_member_create_fragment_sale.setOnClickListener(this);
        editText_member_create_fragment_birthday.setOnClickListener(this);

        editText_member_create_fragment_mobilephone.setOnClickListener(this);
        editText_member_create_fragment_pay_pwd.setOnClickListener(this);

        editText_member_create_fragment_number.setOnTouchListener(this);
        editText_member_create_fragment_name.setOnTouchListener(this);
        editText_member_create_fragment_paper_number.setOnTouchListener(this);
        editText_member_create_fragment_rfid.setOnTouchListener(this);


        initData();
        if (Restaurant.isInputSoftMode) {
            hideInputMethod();
        } else {
            showInputMethod();
        }
        showCardInfoLayout();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    public void hideInputMethod() {
        editText_member_create_fragment_number.setInputType(InputType.TYPE_NULL);
        editText_member_create_fragment_name.setInputType(InputType.TYPE_NULL);
        editText_member_create_fragment_paper_number.setInputType(InputType.TYPE_NULL);
    }

    public void showInputMethod() {
        editText_member_create_fragment_number.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText_member_create_fragment_name.setInputType(InputType.TYPE_CLASS_TEXT);
        editText_member_create_fragment_paper_number.setInputType(InputType.TYPE_CLASS_NUMBER);

    }

    public void clearEditFocus() {
        editText_member_create_fragment_number.clearFocus();
        editText_member_create_fragment_number.setFocusable(false);

        editText_member_create_fragment_name.clearFocus();
        editText_member_create_fragment_name.setFocusable(false);

        editText_member_create_fragment_paper_number.clearFocus();
        editText_member_create_fragment_paper_number.setFocusable(false);

        editText_member_create_fragment_rfid.clearFocus();
        editText_member_create_fragment_rfid.setFocusable(false);
    }

    private void setListener() {
        editText_member_create_fragment_rfid.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mNumberPopWindow != null && mNumberPopWindow.isShow()) {
            mNumberPopWindow.dismiss();
        }
        MemberPopupWindow memberPopuWindow = new MemberPopupWindow();
        switch (v.getId()) {
            case R.id.button_member_create_save_and_create:
                if (card_info_layout.getVisibility() == View.VISIBLE) {
                    nextStep();
                } else {
                    createSure();
                }
                break;
            case R.id.button_member_create_cancel:
                if (card_info_layout.getVisibility() == View.VISIBLE) {
                    clearInput();
                } else {
                    showCardInfoLayout();
                }
                break;
            case R.id.editText_member_create_fragment_category:
                if (categorys == null || categorys.size() == 0) {
                    return;
                }
                ChooseRechargeStaffDialog chooseRechargeStaffDialog = new ChooseRechargeStaffDialog(getActivity(), ChooseRechargeStaffDialog.CHOOSE_MEMBER_TYPE, memberTypes, new Listener.OnChooseMemberTypeListener() {

                    @Override
                    public void onSure(MemberTypes memberType) {
                        // TODO Auto-generated method stub
                        editText_member_create_fragment_category.setText(memberType.name);
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub

                    }
                });
                chooseRechargeStaffDialog.show();
                break;
            case R.id.editText_member_create_fragment_sale:
                if (sales == null || sales.size() == 0) {
                    return;
                }
                ChooseRechargeStaffDialog chooseMemberTypeDialog = new ChooseRechargeStaffDialog(getActivity(), ChooseRechargeStaffDialog.CHOOSE_STAFF, SanyiSDK.rest.staffs, new Listener.OnChooseStaffListener() {

                    @Override
                    public void onSure(StaffRest staff) {
                        // TODO Auto-generated method stub
                        editText_member_create_fragment_sale.setText(staff.name);
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub

                    }
                });
                chooseMemberTypeDialog.show();
                break;
            case R.id.editText_member_create_fragment_sex:
                if (sexs == null || sexs.size() == 0) {
                    return;
                }
                ChooseRechargeStaffDialog chooseSexDialog = new ChooseRechargeStaffDialog(getActivity(), ChooseRechargeStaffDialog.CHOOSE_SEX, sexs, new Listener.OnChooseSexListener() {

                    @Override
                    public void onSure(String s) {
                        // TODO Auto-generated method stub
                        editText_member_create_fragment_sex.setText(s);
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub

                    }
                });
                chooseSexDialog.show();
                break;
            case R.id.editText_member_create_fragment_ID_type:
                if (paperTypes == null || paperTypes.size() == 0) {
                    return;
                }
                ChooseRechargeStaffDialog chooseIdTypeDialog = new ChooseRechargeStaffDialog(getActivity(), ChooseRechargeStaffDialog.CHOOSE_ID_TYPE, idTypes, new Listener.OnChooseIdTypeListener() {

                    @Override
                    public void onSure(IDType idType) {
                        // TODO Auto-generated method stub
                        editText_member_create_fragment_paper_type.setText(idType.getName());
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub

                    }
                });
                chooseIdTypeDialog.show();
                break;
            case R.id.editText_member_create_fragment_birthday:
                DatePopupWindow datePopupWindow = new DatePopupWindow();
                mPopupWindow = datePopupWindow.show(editText_member_create_fragment_birthday, getActivity(), new DatePopupWindow.OnSureListener() {

                    @Override
                    public void onSureClick(String s) {
                        // TODO Auto-generated method stub
                        editText_member_create_fragment_birthday.setText(s);
                    }
                });
                break;
            case R.id.editText_member_create_fragment_mobilephone:
                mNumberPopWindow = new NumberPopWindow(v, getActivity(), editText_member_create_fragment_mobilephone.getText().toString(), new NumberPopWindow.OnSureListener() {

                    @Override
                    public void onSureClick(String value) {
                        // TODO Auto-generated method stub
                        editText_member_create_fragment_mobilephone.setText(value);
                    }

                    @Override
                    public void onNumBtnClick(String value) {
                        // TODO Auto-generated method stub
                        editText_member_create_fragment_mobilephone.setText(value);
                    }
                });
                mNumberPopWindow.show();
                break;
            case R.id.editText_member_create_fragment_pay_pwd:
                editText_member_create_fragment_pay_pwd.requestFocus();
                editText_member_create_fragment_pay_pwd.setFocusable(true);
//                mNumberPopWindow = new NumberPopWindow(v, getActivity(), editText_member_create_fragment_pay_pwd.getText().toString(), new NumberPopWindow.OnSureListener() {
//
//                    @Override
//                    public void onSureClick(String value) {
//                        // TODO Auto-generated method stub
//                        editText_member_create_fragment_pay_pwd.setText(value);
//                    }
//
//                    @Override
//                    public void onNumBtnClick(String value) {
//                        // TODO Auto-generated method stub
//                        editText_member_create_fragment_pay_pwd.setText(value);
//                    }
//                });
//                mNumberPopWindow.show();
                break;
            case R.id.editText_member_create_fragment_number:
                editText_member_create_fragment_number.requestFocus();
                editText_member_create_fragment_number.setFocusable(true);
                break;
            case R.id.editText_member_create_fragment_name:
                editText_member_create_fragment_name.requestFocus();
                editText_member_create_fragment_name.setFocusable(true);
                break;
            case R.id.editText_member_create_fragment_RID:
                editText_member_create_fragment_rfid.requestFocus();
                editText_member_create_fragment_rfid.setFocusable(true);
                break;
            case R.id.editText_member_create_fragment_ID_number:
                editText_member_create_fragment_paper_number.requestFocus();
                editText_member_create_fragment_paper_number.setFocusable(true);
                break;
        }
        closeInputMethod();
    }

    private void nextStep() {
        String cardno = editText_member_create_fragment_number.getText().toString();
        String rfid = editText_member_create_fragment_rfid.getText().toString();
        String category = editText_member_create_fragment_category.getText().toString();
        String sale = editText_member_create_fragment_sale.getText().toString();
        String name = editText_member_create_fragment_name.getText().toString();
        String mobile = editText_member_create_fragment_mobilephone.getText().toString();
        String pay_pwd = editText_member_create_fragment_pay_pwd.getText().toString();

        if (cardno.isEmpty()) {
            Toast.makeText(activity, "会员卡号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (mobile.isEmpty()) {

            Toast.makeText(activity, "手机号码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (name.isEmpty()) {

            Toast.makeText(activity, "会员姓名不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (category.isEmpty()) {

            Toast.makeText(activity, "会员类型为空,不允许创建会员", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isMobileNO(mobile)) {

            Toast.makeText(activity, "手机号码格式错误", Toast.LENGTH_LONG).show();
            return;
        }

        showUserInfoLayout();
    }

    private void createSure() {
        String cardno = editText_member_create_fragment_number.getText().toString();
        String rfid = editText_member_create_fragment_rfid.getText().toString();
        String category = editText_member_create_fragment_category.getText().toString();
        String sale = editText_member_create_fragment_sale.getText().toString();
        String name = editText_member_create_fragment_name.getText().toString();
        String sex = editText_member_create_fragment_sex.getText().toString();
        String paperType = editText_member_create_fragment_paper_type.getText().toString();
        String paperNumber = editText_member_create_fragment_paper_number.getText().toString();
        String birthday = editText_member_create_fragment_birthday.getText().toString();
        String mobile = editText_member_create_fragment_mobilephone.getText().toString();
        String pay_pwd = editText_member_create_fragment_pay_pwd.getText().toString();

        MemberCreateParam memberCreateParam = new MemberCreateParam();
        memberCreateParam.setMember_type_id(getMemberTypeId(category));
        memberCreateParam.setSale_staff(getSaleStaffId(sale));
        memberCreateParam.setCreator(SanyiSDK.currentUser.id);
        memberCreateParam.setBirthday(birthday.isEmpty() ? null : birthday);
        memberCreateParam.setMobile(mobile.isEmpty() ? null : mobile);
        memberCreateParam.setName(name.isEmpty() ? null : name);
        memberCreateParam.setEmail(null);
        memberCreateParam.setSex(sex.isEmpty() ? null : sex);
        memberCreateParam.setId_type(getPaperTypeId(paperType));
        memberCreateParam.setId_no(paperNumber.isEmpty() ? null : paperNumber);
        memberCreateParam.setPassword(pay_pwd.isEmpty() ? null : pay_pwd);
        CreateMemberCard card = new CreateMemberCard();
        card.setCard_no(cardno.isEmpty() ? null : cardno);
        card.setRfid(rfid.isEmpty() ? null : rfid);
        memberCreateParam.setCard(card);
        SanyiScalaRequests.memberCreateRequest(memberCreateParam, new IMemberCreateListener() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onSuccess(MemberCreateResult resp) {
                clearInput();
                showCardInfoLayout();
                Toast.makeText(activity, "会员创建成功", Toast.LENGTH_LONG).show();

            }
        });

    }

    // 判断手机格式是否正确
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^[1]\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 获取营销人员id
     *
     * @param sale
     * @return
     */
    private long getSaleStaffId(String sale) {
        // TODO Auto-generated method stub
        for (int i = 0; i < SanyiSDK.rest.staffs.size(); i++) {
            StaffRest staff = SanyiSDK.rest.staffs.get(i);
            if (staff.name.equals(sale)) {
                return staff.id;
            }
        }
        return 0;
    }

    /**
     * 获取会员类型id
     */
    private long getMemberTypeId(String category) {
        // TODO Auto-generated method stub
        for (int i = 0; i < categorys.size(); i++) {
            String memberName = categorys.get(i);
            if (memberName.equals(category)) {
                return SanyiSDK.rest.memberTypes.get(i).id;
            }
        }
        return 0;
    }

    /**
     * 获取证件类型id
     */
    private long getPaperTypeId(String paperType) {
        // TODO Auto-generated method stub
        for (int i = 0; i < paperTypes.size(); i++) {
            String memberName = paperTypes.get(i);
            if (memberName.equals(paperType)) {
                return idTypes.get(i).id;
            }
        }
        return 0;
    }

    /**
     * 清空输入数据
     */
    private void clearInput() {
        editText_member_create_fragment_number.getText().clear();
        editText_member_create_fragment_rfid.getText().clear();
        editText_member_create_fragment_name.getText().clear();
        editText_member_create_fragment_paper_number.getText().clear();
        editText_member_create_fragment_birthday.setText("");
        editText_member_create_fragment_mobilephone.setText("");
        editText_member_create_fragment_pay_pwd.setText("");
        if (categorys != null && categorys.size() > 0) {
            editText_member_create_fragment_category.setText(categorys.get(0));
        }
        if (paperTypes != null && paperTypes.size() > 0) {
            editText_member_create_fragment_paper_type.setText(paperTypes.get(0));
        }
        if (sales != null && sales.size() > 0) {
            editText_member_create_fragment_sale.setText(sales.get(0));
        }
        if (sexs != null && sexs.size() > 0) {
            editText_member_create_fragment_sex.setText(sexs.get(0));
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden) {
            clearInput();
        } else {
            if (Restaurant.isInputSoftMode) {
                hideInputMethod();
            } else {
                showInputMethod();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.editText_member_create_fragment_number:
                editText_member_create_fragment_number.requestFocus();
                editText_member_create_fragment_number.setFocusable(true);
                break;
            case R.id.editText_member_create_fragment_name:
                editText_member_create_fragment_name.requestFocus();
                editText_member_create_fragment_name.setFocusable(true);
                break;
            case R.id.editText_member_create_fragment_RID:
                editText_member_create_fragment_rfid.requestFocus();
                editText_member_create_fragment_rfid.setFocusable(true);
                break;
            case R.id.editText_member_create_fragment_ID_number:
                editText_member_create_fragment_paper_number.requestFocus();
                editText_member_create_fragment_paper_number.setFocusable(true);
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 关闭软键盘
     */
    public void closeInputMethod(EditText edit) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    public void closeInputMethod() {
        if (getActivity().getCurrentFocus() != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
