package com.rainbow.smartpos.place.presenter;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.SmartPosBundle;
import com.rainbow.smartpos.order.DetailOpLogsDialog;
import com.rainbow.smartpos.place.ChangePriceDialog;
import com.rainbow.smartpos.place.PlaceDishOptionDialog;
import com.rainbow.smartpos.place.ReturnDishReasonDialog;
import com.rainbow.smartpos.place.SelectServingTable;
import com.rainbow.smartpos.place.WeightDialog;
import com.rainbow.smartpos.place.model.PlaceDetailModeImpl;
import com.rainbow.smartpos.place.view.PlaceDetailView;
import com.rainbow.smartpos.tablestatus.OpenTableDialog;
import com.rainbow.smartpos.util.AuthDialog;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.SwitchTableDialog;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.bean.TableOrderInfo;
import com.sanyipos.sdk.api.inters.IBatchHandleDetailListener;
import com.sanyipos.sdk.api.inters.IHandleDetailListener;
import com.sanyipos.sdk.api.inters.Request;
import com.sanyipos.sdk.api.services.scala.ReturnDetailsRequest;
import com.sanyipos.sdk.model.OrderDetail;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.StaffRest;
import com.sanyipos.sdk.model.scala.addDetail.model.AddDetaiAction;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;
import com.sanyipos.sdk.utils.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/1/11.
 */
public class PlaceDetailPresenterImpl implements PlaceDetailPresenter, PlaceDetailModeImpl.onLoadDetailsListener {

    private static final String TAG = "PlacePresenterImpl";

    private Context context;
    private PlaceDetailView mPlaceView;
    private PlaceDetailModeImpl mPlaceModeImpl;
    private List<OrderDetail> dealerOrders;
    private List<OrderDetail> mChooseOrders;
    private List<OrderDetail> mSelectedOrders;
    private List<Long> tableIds;
    private TableOrderInfo orderInfo;
    private MainScreenActivity activity;

    public PlaceDetailPresenterImpl(Context context, PlaceDetailView view) {
        this.context = context;
        this.activity = (MainScreenActivity) context;
        this.mPlaceView = view;
        this.mPlaceModeImpl = new PlaceDetailModeImpl();
    }

    @Override
    public void onSuccess(List<OpenTableDetail> tableDetail) {
        dealerOrders = tableDetail.get(0).ods;
        if (dealerOrders.size() == 0) mPlaceView.showOrderFragment();
        mPlaceView.updateDetails(dealerOrders);
        mPlaceView.updateTableInfo(tableDetail.get(0).info);
    }

    public void setDealerOrders(List<OrderDetail> orders, List<Long> tableIds) {
        this.dealerOrders = orders;
        this.tableIds = tableIds;
    }

    @Override
    public void onFailure(String msg) {
        mPlaceView.showMessage(msg);
    }

    @Override
    public void loadDetails(List<Long> tableIds) {
        this.tableIds = tableIds;
        mPlaceModeImpl.loadDetals(tableIds, this);
    }

    public static class ListParams {
        List<OrderDetail> orderDetails = new ArrayList<>();

        public ListParams(OrderDetail orderDetail) {
            orderDetails.add(orderDetail);
        }

        public List<OrderDetail> getOrderDetails() {
            return orderDetails;
        }
    }

    @Override
    public void clickDish(final OrderDetail orderDetail, int position, View view) {
        switch (mPlaceView.currentMode()) {
            case -1:
                if (orderDetail.isAvailable())
                    new PlaceDishOptionDialog().show(context, orderDetail, new Listener.OnPlaceDishOptionListener() {

                        @Override
                        public void onReturnDish() {
                            returnDish(orderDetail);
                        }

                        @Override
                        public void onIsFreeDish(boolean b, StaffRest superUser) {
                            orderDetail.setAuth_staff_id(superUser.id);
                            changeDish(new ListParams(orderDetail).getOrderDetails(), b ? AddDetaiAction.ACTION_GIFT : AddDetaiAction.ACTION_UNDOGIFT);
                        }

                        @Override
                        public void onWeightDish() {
                            new WeightDialog().show(context, context.getString(R.string.weight_dish), String.valueOf(orderDetail.getWeight()), ChangePriceDialog.CHANGE_WEIGHT, new Listener.OnChangePriceListener() {
                                @Override
                                public void onSure(Double count) {
                                    changeDishPriceAWeight(orderDetail, AddDetaiAction.ACTION_CHANGEWEIGHT, count, SanyiSDK.currentUser);
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }

                        @Override
                        public void onChangePrice(final StaffRest superUser) {
                            new ChangePriceDialog().show(context, orderDetail, ChangePriceDialog.CHANGE_PRICE, new Listener.OnChangePriceListener() {
                                @Override
                                public void onSure(Double count) {
                                    changeDishPriceAWeight(orderDetail, AddDetaiAction.ACTION_CHANGEPRICE, count, superUser);
                                }

                                @Override
                                public void onCancel() {

                                }
                            });

                        }
                    });
                break;
            case R.id.return_dish:
            case R.id.change_dish:
            case R.id.call_up:
            case R.id.pull_food:
            case R.id.free_dish:
                if (!mSelectedOrders.contains(orderDetail)) {
                    mSelectedOrders.add(orderDetail);
                } else {
                    mSelectedOrders.remove(orderDetail);
                }
                mPlaceView.updateSelectedDetails(mSelectedOrders);
                mPlaceView.updateItem(position, view);


        }
    }

    @Override
    public boolean isCanCheck() {
        if (dealerOrders.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void clickBatchButton(int buttonId) {
        mChooseOrders = new ArrayList<>();
        mSelectedOrders = new ArrayList<>();
        switch (buttonId) {
            case R.id.call_up:
                for (int i = 0; i < dealerOrders.size(); i++) {
                    if (dealerOrders.get(i).isHold()) {
                        mChooseOrders.add(dealerOrders.get(i));
                    }
                }
                break;
            case R.id.pull_food:
            case R.id.return_dish:
            case R.id.change_dish:
            case R.id.free_dish:
                for (int i = 0; i < dealerOrders.size(); i++) {
                    if (dealerOrders.get(i).isAvailable()) {
                        mChooseOrders.add(dealerOrders.get(i));
                    }
                }
                if (buttonId == R.id.pull_food) {
                    for (int i = 0; i < dealerOrders.size(); i++) {
                        if (dealerOrders.get(i).isSet() && dealerOrders.get(i).isAvailable()) {
                            for (OrderDetail orderDetail : dealerOrders.get(i).setOrders)
                                mChooseOrders.add(orderDetail);
                        }
                    }
                }
                break;
        }
        mPlaceView.updateDetails(mChooseOrders);
        mPlaceView.clearSelectedDetails();
    }

    @Override
    public void clickConfirm(int buttonId) {
        if (mSelectedOrders.size() == 0) {
            mPlaceView.cancelBatchMode();
            mPlaceView.updateDetails(dealerOrders);
            return;
        }
        switch (buttonId) {
            case R.id.call_up:
                batchHoldARemindDish(AddDetaiAction.ACTION_COOK);
                break;
            case R.id.pull_food:
                batchHoldARemindDish(AddDetaiAction.ACTION_REMIND);
                break;
            case R.id.return_dish:
                batchReturnDish();
                break;
            case R.id.change_dish:
                batchChangeTableDish();
                break;
            case R.id.free_dish:
                batchFreeDish();
                break;
        }
    }

    private void batchFreeDish() {
        new AuthDialog().show(context, ConstantsUtil.PERMISSION_FREE_DISH, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
            @Override
            public void onAuthSuccess(StaffRest staff) {
                changeDish(mSelectedOrders, AddDetaiAction.ACTION_GIFT);
            }

            @Override
            public void onCancel() {
                operationFinsh(null);

            }
        });
    }

    private void batchChangeTableDish() {
        new AuthDialog().show(context, ConstantsUtil.PERMISSION_TRANSFER_DISH, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
            @Override
            public void onAuthSuccess(StaffRest staff) {
                new SelectServingTable(context, mPlaceView.getCurrentTable(), new SelectServingTable.SelectServingTableInterface() {
                    @Override
                    public void OnOkButtonPressed(long tableSeatId) {
                        SanyiScalaRequests.turnDishRequest(mSelectedOrders, tableSeatId, new IBatchHandleDetailListener() {
                            @Override
                            public void onSuccess(TableOrderInfo info, List<OrderDetail> ods) {
                                for (OrderDetail selectOrderDetail : mSelectedOrders) {
                                    removeSelect(selectOrderDetail);
                                }
                                orderInfo = info;
                                operationFinsh(context.getString(R.string.operation_success));
                            }

                            @Override
                            public void onFail(String error) {
                                operationFinsh(context.getResources().getString(R.string.operation_failure) + " " + error);
                            }
                        });
                    }

                    @Override
                    public void OnCancelButtonPressed() {
                        operationFinsh(null);
                    }
                }).show();
            }

            @Override
            public void onCancel() {
                operationFinsh(null);

            }
        });

    }

    private void removeSelect(OrderDetail orderDetail) {
        for (OrderDetail detail : dealerOrders) {
            if (orderDetail == detail) {
                dealerOrders.remove(detail);
                return;
            }
        }
    }

    public void returnDish(final OrderDetail orderDetail) {
        new AuthDialog().show(context, ConstantsUtil.PERMISSION_RETURN_DISH, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
            @Override
            public void onAuthSuccess(StaffRest staff) {
                orderDetail.setAuth_staff_id(staff.id);
                if (orderDetail.getQuantity() - orderDetail.getVoid_quantity() > 1) {

                    OpenTableDialog returnDishDialog = new OpenTableDialog(context, "输入退菜数量", "1", orderDetail.getQuantity() - orderDetail.getVoid_quantity(), OpenTableDialog.RETURN_DISH_COUNT, new Listener.OnSetReturnDishCountListener() {
                        @Override
                        public void sure(final int returncount) {
                            new ReturnDishReasonDialog().show(context, new Listener.OnChooseReasonListener() {
                                @Override
                                public void onSure(long selectId, String editReason) {
                                    returnDish(orderDetail, returncount, selectId, editReason);
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                    returnDishDialog.show();
                } else {
                    new ReturnDishReasonDialog().show(context, new Listener.OnChooseReasonListener() {
                        @Override
                        public void onSure(long selectId, String editReason) {
                            returnDish(orderDetail, 1, selectId, editReason);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void returnDish(final OrderDetail order, final int returncount, long selectId, String editReason) {
        SanyiScalaRequests.returnDishRequest(order, returncount, selectId, editReason, new IHandleDetailListener() {
            @Override
            public void onSuccess(TableOrderInfo info, OrderDetail orderDetail) {
                for (int i = 0; i < dealerOrders.size(); i++) {
                    if (order == dealerOrders.get(i)) {
                        dealerOrders.get(i).setVoid_quantity(orderDetail.getVoid_quantity());
                        if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0&&orderDetail.childrenOrderDetail!=null) {
                            for (OrderDetail cod : dealerOrders.get(i).childrenOrderDetail
                                    ) {
                                for (OrderDetail ood:orderDetail.getChildrenOrderDetail()
                                     ) {
                                    if(cod.getGoodsId()==ood.getGoodsId())
                                        cod.setVoid_quantity(ood.getVoid_quantity());
                                }

                            }
                        }
                        if(dealerOrders.get(i).isSet()&&dealerOrders.get(i).setOrders!=null&&dealerOrders.get(i).setOrders.size()>0)
                        {
                            for (OrderDetail setDetail:dealerOrders.get(i).setOrders
                                 ) {
                                setDetail.setVoid_quantity(orderDetail.getVoid_quantity()*setDetail.getUnitCount());
                            }
                        }
                    } else if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0) {
                        for (OrderDetail od : dealerOrders.get(i).childrenOrderDetail) {
                            if (od == order && orderDetail.childrenOrderDetail != null && orderDetail.childrenOrderDetail.size() > 0)
                                for (OrderDetail od2 : orderDetail.childrenOrderDetail) {
                                    if (od.getGoodsId() == od2.getGoodsId())
                                        od.setVoid_quantity(od2.getVoid_quantity());
                                }

                        }
                    }
                }
                orderInfo = info;
                operationFinsh("操作成功");
            }

            @Override
            public void onFail(String error) {
                operationFinsh(error);
            }
        });
    }

    private void batchReturnDish() {
        new AuthDialog().show(context, ConstantsUtil.PERMISSION_RETURN_DISH, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
            @Override
            public void onAuthSuccess(final StaffRest staff) {
                // TODO Auto-generated method stub
                for (int i = 0; i < mSelectedOrders.size(); i++) {
                    mSelectedOrders.get(i).setAuth_staff_id(staff.id);
                }

                new ReturnDishReasonDialog().show(context, new Listener.OnChooseReasonListener() {
                    @Override
                    public void onSure(long selectId, String editReason) {
                        SanyiScalaRequests.returnDishsRequest(mSelectedOrders, selectId, editReason, new ReturnDetailsRequest.IHandleDetailListener() {
                            @Override
                            public void onSuccess(TableOrderInfo info, List<OrderDetail> orderDetails) {

                                for (int j = 0; j < mSelectedOrders.size(); j++) {
                                    for (int i = 0; i < dealerOrders.size(); i++) {
                                        if (mSelectedOrders.get(j).getId() == dealerOrders.get(i).getId()) {
                                            dealerOrders.get(i).setVoid_quantity(orderDetails.get(j).getVoid_quantity());
                                            if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0) {
                                                for (OrderDetail cod : dealerOrders.get(i).childrenOrderDetail
                                                        ) {
                                                    for (OrderDetail ood:orderDetails.get(j).getChildrenOrderDetail()
                                                            ) {
                                                        if(cod.getGoodsId()==ood.getGoodsId())
                                                            cod.setVoid_quantity(ood.getVoid_quantity());
                                                    }

                                                }
                                            }
                                        }else if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0) {
                                            for (OrderDetail od : dealerOrders.get(i).childrenOrderDetail) {
                                                if (od == mSelectedOrders.get(j) && orderDetails.get(j).childrenOrderDetail != null && orderDetails.get(j).childrenOrderDetail.size() > 0)
                                                    for (OrderDetail od2 : orderDetails.get(j).childrenOrderDetail) {
                                                        if (od.getGoodsId() == od2.getGoodsId())
                                                            od.setVoid_quantity(od2.getVoid_quantity());
                                                    }

                                            }
                                        }
                                    }
                                }
                                orderInfo = info;
                                operationFinsh(context.getString(R.string.operation_success));
                            }


                            @Override
                            public void onFail(String error) {
                                operationFinsh(context.getResources().getString(R.string.operation_failure) + " " + error);
                            }
                        });

                    }

                    @Override
                    public void onCancel() {
                        operationFinsh(null);
                    }
                });
            }

            @Override
            public void onCancel() {
                operationFinsh(null);

            }


        });
    }

    public void batchHoldARemindDish(final int action) {
        SanyiScalaRequests.changeDishRequest(mSelectedOrders, action, new IBatchHandleDetailListener() {
            @Override
            public void onSuccess(TableOrderInfo info, List<OrderDetail> ods) {
                if (action == AddDetaiAction.ACTION_COOK) {
                    for (int i = 0; i < dealerOrders.size(); i++) {
                        for (int j = 0; j < ods.size(); j++) {
                            if (dealerOrders.get(i) == mSelectedOrders.get(j)) {
                                dealerOrders.get(i).setHold(false);
                            }
                        }
                    }
                }
                orderInfo = info;
                operationFinsh(context.getResources().getString(R.string.operation_success));

            }


            @Override
            public void onFail(String error) {
                operationFinsh(context.getResources().getString(R.string.operation_failure) + " " + error);
            }
        });
    }

    @Override
    public void printBill() {
        if (SanyiSDK.getCurrentStaffPermissionById(ConstantsUtil.PERMISSION_ORDER)) {
            SanyiScalaRequests.reprintOrderRequest(tableIds.get(0), SanyiSDK.getStaffId(), new Request.ICallBack() {
                @Override
                public void onSuccess(String status) {
                    mPlaceView.showMessage(context.getString(R.string.operation_success));
                }

                @Override
                public void onFail(String error) {

                }
            });

        } else {
            mPlaceView.showMessage("权限不足");
        }
    }

    @Override
    public void changePeople() {
        new OpenTableDialog(context, context.getResources().getString(R.string.update_person_count), String.valueOf(mPlaceView.getCurrentTable().order.personCount), OpenTableDialog.UPDATE_PERSONCOUNT, new Listener.OnOpenTableListener() {

            @Override
            public void sure(final int peopleCount) {
                // TODO Auto-generated method stub
                SanyiScalaRequests.changePersonCountRequest(tableIds.get(0), SanyiSDK.currentUser.id, peopleCount, new Request.ICallBack() {


                    @Override
                    public void onFail(String error) {
                        // TODO Auto-generated method stub


                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void onSuccess(String status) {
                        // TODO Auto-generated method stub
                        mPlaceView.setPeople(peopleCount);
                        mPlaceView.getCurrentTable().personCount = peopleCount;
                        mPlaceView.showMessage(context.getResources().getString(R.string.operation_success));
                    }
                });
            }

            @Override
            public void cancel() {
                // TODO Auto-generated method stub

            }
        }).show();
    }

    @Override
    public void operationLog() {
        List<SeatEntity> seats = new ArrayList<>();
        for (Long tableid : tableIds) {
            seats.add(SanyiSDK.rest.operationData.getSeat(tableid));
        }
        new DetailOpLogsDialog(context, seats).show();
    }

    public void operationFinsh(String msg) {
        mPlaceView.cancelBatchMode();
        if (mSelectedOrders != null)
            mSelectedOrders.clear();
        mPlaceView.updateDetails(dealerOrders);
        mPlaceView.updateTableInfo(orderInfo);
        if (msg != null)
            mPlaceView.showMessage(msg);
    }

    public void changeDishPriceAWeight(final OrderDetail order, final int action, double price, StaffRest staff) {
        order.setAuth_staff_id(staff.getId());
        SanyiScalaRequests.changeDishPriceAWeightReqeust(order, action, price, new IHandleDetailListener() {

            @Override
            public void onSuccess(TableOrderInfo info, OrderDetail orderDetail) {
                orderInfo = info;
                for (int i = 0; i < dealerOrders.size(); i++) {
                    if (order == dealerOrders.get(i)) {
                        if (action == AddDetaiAction.ACTION_CHANGEPRICE) {
                            dealerOrders.get(i).setPriceChanged(true);
                            dealerOrders.get(i).setCurrentPrice(orderDetail.getCurrentPrice());
                        }
                        if (action == AddDetaiAction.ACTION_CHANGEWEIGHT) {
                            dealerOrders.get(i).setIsWeight(true);
                            dealerOrders.get(i).setWeight(orderDetail.getWeight());
                        }
                    }
                    if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0) {
                        for (OrderDetail od : dealerOrders.get(i).childrenOrderDetail
                                ) {
                            if (od == order && orderDetail.childrenOrderDetail != null && orderDetail.childrenOrderDetail.size() > 0) {
                                if (action == AddDetaiAction.ACTION_CHANGEPRICE) {
                                    for (OrderDetail od2 : orderDetail.childrenOrderDetail) {
                                        if (od2.getGoodsId() == od.getGoodsId()) {
                                            od.setPriceChanged(true);
                                            od.setCurrentPrice(od2.getCurrentPrice());
                                        }
                                    }

                                }
                                if (action == AddDetaiAction.ACTION_CHANGEWEIGHT) {
                                    for (OrderDetail od2 : orderDetail.childrenOrderDetail) {
                                        if (od2.getGoodsId() == od.getGoodsId()) {
                                            od.setIsWeight(true);
                                            od.setWeight(od2.getWeight());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                operationFinsh("操作成功");
            }


            @Override
            public void onFail(String error) {
                mPlaceView.showMessage(context.getResources().getString(R.string.operation_failure) + " " + error);
            }
        });
    }

    public void changeDish(final List<OrderDetail> orderDetails, final int action) {
        SanyiScalaRequests.changeDishRequest(orderDetails, action, new IBatchHandleDetailListener() {
            @Override
            public void onSuccess(TableOrderInfo info, List<OrderDetail> ods) {
                for (int j = 0; j < orderDetails.size(); j++) {
                    for (int i = 0; i < dealerOrders.size(); i++) {
                        if (orderDetails.get(j) == dealerOrders.get(i)) {
                            switch (action) {
                                case AddDetaiAction.ACTION_GIFT:
                                    dealerOrders.get(i).setFree(true);
                                    if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0) {
                                        for (OrderDetail orderdetail : dealerOrders.get(i).childrenOrderDetail) {
                                            orderdetail.setFree(true);
                                        }
                                    }
                                    dealerOrders.get(i).setCurrentPrice(ods.get(j).getCurrentPrice());
                                    break;
                                case AddDetaiAction.ACTION_UNDOGIFT:
                                    dealerOrders.get(i).setFree(false);
                                    if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0) {
                                        for (OrderDetail orderdetail : dealerOrders.get(i).childrenOrderDetail) {
                                            orderdetail.setFree(false);
                                        }
                                    }
                                    dealerOrders.get(i).setCurrentPrice(ods.get(j).getCurrentPrice());
                                    break;
                                case AddDetaiAction.ACTION_HOLD:
                                    dealerOrders.get(i).setHold(false);
                                    if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0) {
                                        for (OrderDetail orderdetail : dealerOrders.get(i).childrenOrderDetail) {
                                            orderdetail.setHold(false);
                                        }
                                    }
                                    break;
                            }
                        }
                        //单独对组合菜的配菜进行增,退等操作后,修改配菜的状态
                        if (dealerOrders.get(i).childrenOrderDetail != null && dealerOrders.get(i).childrenOrderDetail.size() > 0 && dealerOrders.get(i).childrenOrderDetail.contains(orderDetails.get(j))) {
                            switch (action) {
                                case AddDetaiAction.ACTION_GIFT:
                                    for (OrderDetail orderdetail : dealerOrders.get(i).childrenOrderDetail) {
                                        if (orderdetail == orderDetails.get(j))
                                            orderdetail.setFree(true);

                                    }
                                    break;
                                case AddDetaiAction.ACTION_UNDOGIFT:
                                    for (OrderDetail orderdetail : dealerOrders.get(i).childrenOrderDetail) {
                                        if (orderdetail == orderDetails.get(j))
                                            orderdetail.setFree(false);
                                    }
                                    break;
                                case AddDetaiAction.ACTION_HOLD:
                                    for (OrderDetail orderdetail : dealerOrders.get(i).childrenOrderDetail) {
                                        if (orderdetail == orderDetails.get(j))
                                            orderdetail.setHold(false);
                                    }
                                    break;
                            }
                        }
                    }
                }
                orderInfo = info;
                operationFinsh(context.getResources().getString(R.string.operation_success));
            }


            @Override
            public void onFail(String error) {
                mPlaceView.showMessage(context.getResources().getString(R.string.operation_failure) + " " + error);
            }
        });
    }

    @Override
    public void cleanPrintState() {
        new AuthDialog().show(context, ConstantsUtil.PERMISSION_PRE_PRINTER, AuthDialog.Type.PERMISSION, new Listener.OnAuthListener() {
            @Override
            public void onAuthSuccess(StaffRest staff) {
                SanyiScalaRequests.cancelPreprintRequest(tableIds.get(0), staff.getId(), new Request.ICallBack() {
                    @Override
                    public void onSuccess(String status) {
                        mPlaceView.showMessage(context.getString(R.string.operation_success));
                        mPlaceView.clearBillStateText();
                    }

                    @Override
                    public void onFail(String error) {
                        mPlaceView.showMessage(context.getResources().getString(R.string.operation_failure) + " " + error);
                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void switchTable() {
        new SwitchTableDialog(context, SwitchTableDialog.ORDER, mPlaceView.getCurrentTable(), new SwitchTableDialog.ISwitchTableListener() {
            @Override
            public void batchOperation(long[] tableIds) {

            }

            @Override
            public void openTable(SeatEntity table, int personCount) {
                // TODO Auto-generated method stub
                activity.unLockTable(tableIds, false);
                List<Long> list = new ArrayList<Long>();
                list.add(table.seat);
                mPlaceView.setBundle(SmartPosBundle.getBundle(table.order.personCount, list, false, false));
                mPlaceView.initTable();
            }

            @Override
            public void cancel() {
                // TODO Auto-generated method stub
            }
        }).show();
    }
}
