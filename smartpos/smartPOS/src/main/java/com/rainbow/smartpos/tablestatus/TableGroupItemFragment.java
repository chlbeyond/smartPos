package com.rainbow.smartpos.tablestatus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableRow;
import android.widget.Toast;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.rainbow.smartpos.order.GeneralActionListener;
import com.rainbow.smartpos.util.Listener;
import com.rainbow.smartpos.util.Listener.OnChooseOrderTypeListener;
import com.rainbow.smartpos.util.NoRepeatClickUtils;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.inters.Request.ICallBack;
import com.sanyipos.sdk.api.services.scala.OpenTablesRequest;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.scala.changeTable.ChangeTableAction;
import com.sanyipos.sdk.model.scala.openTable.OpenTableDetail;

import java.util.ArrayList;
import java.util.List;

public class TableGroupItemFragment extends Fragment implements OnClickListener {
    static final String LOG_TAG = "TableLocationItemFragment";
    private final int NORMAL_MODE = 0;

    private final int SPLIT_TABLE_MODE = 1;
    private final int UNSPLIT_TABLE_MODE = 2;
    private final int CHANGE_TABLE_MODE = 4;
    private final int MERGE_TABLE_MODE = 8;

    public static final int MENU_MERGE_TABLE = 1;
    public static final int MENU_CHANGE_TABLE = 2;
    public static final int MENU_CHECK = 3;
    public static final int MENU_UNMERGE_TABLE = 4;
    public static final int MENU_RETURN_TABLE = 5;
    public static final int MENU_SPLIT_TABLE = 6;
    public static final int MENU_MERGE_TABLE_ORDER = 7;
    public static final int MENU_MERGE_TABLE_CHECK = 8;
    public static final int MENU_UNLOCK_TABLE = 9;
    public static final int MENU_UNSPLIT_TABLE = 10;
    public static final int MENU_CHECK_SELF = 11;
    public static final int MENU_CHECK_ALL = 12;

    int number;
    public TableAdapter adapter;
    GridView mGridView;
    private boolean isMultiSelect = false;
    TableRow tableRowOkCancel;
    long originTableId = -1;
    Button buttonMultiSelectOK;
    Button buttonMultiSelectCancel;

    public TableGroupItemFragment prevFragment;
    public TableGroupItemFragment nextFragment;

    int currentActionMode = NORMAL_MODE;
    public MainScreenActivity activity;

    public void endMergeTable() {
        this.isMultiSelect = false;
        currentActionMode = NORMAL_MODE;
        tableRowOkCancel.setVisibility(View.INVISIBLE);
        adapter.endMultiSelectTable();
        adapter.resetSelectedTable();

    }

    public void startChangeTable() {
        // this.isMultiSelect = true;
        currentActionMode = CHANGE_TABLE_MODE;
        tableRowOkCancel.setVisibility(View.VISIBLE);
        adapter.setOriginTableId(originTableId);
        // adapter.notifyDataSetChanged();
    }

    public void endChangeTable() {
        // this.isMultiSelect = false;
        currentActionMode = NORMAL_MODE;
        tableRowOkCancel.setVisibility(View.INVISIBLE);
        adapter.setTargetTableId(-1);
        adapter.resetSelectedTable();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number = getArguments() != null ? getArguments().getInt("number") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_group_item, container, false);
        // container.addView(view);

        tableRowOkCancel = (TableRow) view.findViewById(R.id.tableRowOkCancel);
        buttonMultiSelectOK = (Button) view.findViewById(R.id.buttonMultiSelectOK);
        buttonMultiSelectOK.setOnClickListener(this);
        buttonMultiSelectCancel = (Button) view.findViewById(R.id.buttonMultiSelectCancel);
        buttonMultiSelectCancel.setOnClickListener(this);

        if (isMultiSelect) {
            tableRowOkCancel.setVisibility(View.VISIBLE);
        }

        activity = (MainScreenActivity) getActivity();
        adapter = new TableAdapter(activity, number);
        if (prevFragment != null && prevFragment.adapter != null) {
            adapter.prevAdapter = prevFragment.adapter;
            prevFragment.adapter.nextAdapter = adapter;
        }
        if (nextFragment != null && nextFragment.adapter != null) {
            nextFragment.adapter.prevAdapter = adapter;
            adapter.nextAdapter = nextFragment.adapter;
        }
        mGridView = (GridView) view.findViewById(R.id.gridViewTables);
        mGridView.setAdapter(adapter);
        mGridView.setFocusable(false);

        // adapter.setTableGroup(number);
        // adapter.setMultiSelect(isMultiSelect);
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                NormalDialog normalDialog = new NormalDialog(activity);
                View dialogView = LayoutInflater.from(activity).inflate(R.layout.layout_grid_item_long_click, null);
                GridView gridView = (GridView) dialogView.findViewById(R.id.gridView_table_fragment_long_click);
                gridView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });
                normalDialog.content(dialogView);
                return false;
            }
        });
        registerForContextMenu(mGridView);

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {

                if (NoRepeatClickUtils.isFastClick()) {
                    return;
                }
                // reset store bill
//                adapter.setSelectedTable(position);
                // TODO: should use id to search a table

                final SeatEntity table = adapter.getItem(position);
                if (table == null) return;
                if (table.lock && SanyiSDK.getDeviceId() != table.lockedDevice) {
                    Toast.makeText(getActivity().getApplicationContext(), "桌子已经上锁，请稍后再试！", Toast.LENGTH_LONG).show();
                    return;
                }

                if (currentActionMode == MERGE_TABLE_MODE) {
                    // CheckableTableLayout view = (CheckableTableLayout) arg1;
                    // CheckBox checkBox = (CheckBox) view
                    // .findViewById(R.id.checkBoxTableSelected);
                    // checkBox.setChecked(!checkBox.isChecked());
                    return;
                }

                if (currentActionMode == CHANGE_TABLE_MODE) {
                    if (table.seat != originTableId) {
                        adapter.setTargetTableId(table.seat);
                        adapter.notifyDataSetChanged();
                        return;
                    } else {
                        Toast.makeText(getActivity(), "不能将台转给自己", Toast.LENGTH_LONG).show();
                        return;
                    }

                }

                // if it is already opened, we don't need count
                if (table.state != TableAdapter.AVAILABLE) {
                    final long[] ids = {table.seat};
                    if ((table.state & TableAdapter.MERGE) == TableAdapter.MERGE) {
                        final List<Long> tables = SanyiSDK.rest.getCombineTableIdsById(table.seat);
                        if (tables != null && tables.size() > 1) {
                            new CombineTableOpenDialog(getActivity(), new OnChooseOrderTypeListener() {

                                @Override
                                public void onOrderWithAll() {
                                    // TODO Auto-generated method stub
                                    activity.displayView(MainScreenActivity.PLACE_FRAGMENT, activity.smartPosBundle.getBundle(table.order.personCount, tables, false, true));
                                }

                                @Override
                                public void onOrderSelf() {
                                    // TODO Auto-generated method stub
                                    activity.displayView(MainScreenActivity.PLACE_FRAGMENT, activity.smartPosBundle.getBundle(table.order.personCount, ids, false, false));
                                }

                                @Override
                                public void cancel() {
                                    // TODO Auto-generated method stubrderUtil
                                }
                            }).show();
                        } else {
                            activity.displayView(MainScreenActivity.PLACE_FRAGMENT, activity.smartPosBundle.getBundle(table.order.personCount, ids, false, false));
                        }
                    } else {
                        List<Long> tables = new ArrayList<Long>();
                        for (int i = 0; i < ids.length; i++) {
                            tables.add(ids[i]);
                        }
                        SanyiScalaRequests.openTableRequest(tables, new OpenTablesRequest.OnOpenTableListener() {
                            @Override
                            public void onSuccess(List<OpenTableDetail> resp) {
                                if (resp.get(0).ods.size() == 0) {
                                    activity.displayView(MainScreenActivity.ORDER_FRAGMENT, activity.smartPosBundle.getBundle(table.order.personCount, ids, false, false, resp));
                                } else {
                                    activity.displayView(MainScreenActivity.PLACE_FRAGMENT, activity.smartPosBundle.getBundle(table.order.personCount, ids, false, false, resp));
                                }
                            }


                            @Override
                            public void onFail(String error) {
                                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();


                            }
                        });
                    }
                    return;
                }
                new OpenTableDialog(activity, getString(R.string.open_table_dialog_hint), String.valueOf(table.personCount), OpenTableDialog.OPEN_TABLE, new Listener.OnOpenTableListener() {

                    @Override
                    public void sure(int peopleCount) {
                        long[] tableIds = {table.seat};
                        activity.displayView(MainScreenActivity.ORDER_FRAGMENT, activity.smartPosBundle.getBundle(peopleCount, tableIds, true, false));
                    }

                    @Override
                    public void cancel() {
                        // TODO Auto-generated method stub
                        adapter.setSelectedTable(position);
                    }
                }).show();

            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.gridViewTables) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            super.onCreateContextMenu(menu, v, menuInfo);
            SeatEntity objTable = adapter.getItem(info.position);
            if (objTable == null) return;
            long state = objTable.state;
            originTableId = objTable.seat;
            if (objTable.lock) {
                menu.add(Menu.NONE, MENU_UNLOCK_TABLE, 5, getResources().getString(R.string.unlock_table));
                return;
            }
            switch ((int) state) {
                case TableAdapter.SERVING:
                case TableAdapter.SERVING | TableAdapter.PREPRINT:
                case TableAdapter.SERVING | TableAdapter.RESERVE:
                case TableAdapter.SERVING | TableAdapter.PREPRINT | TableAdapter.RESERVE:
                    menu.add(Menu.NONE, MENU_SPLIT_TABLE, 5, getResources().getString(R.string.split_table));
                    menu.add(Menu.NONE, MENU_CHANGE_TABLE, 5, getResources().getString(R.string.change_table));
                    if (objTable.order.amount <= 0) {
                        menu.add(Menu.NONE, MENU_RETURN_TABLE, 5, getResources().getString(R.string.return_table));
                    } else {
                        menu.add(Menu.NONE, MENU_CHECK, 5, getResources().getString(R.string.check));
                    }
                    break;
                case TableAdapter.SERVING | TableAdapter.MERGE:
                case TableAdapter.SERVING | TableAdapter.MERGE | TableAdapter.PREPRINT:
                case TableAdapter.SERVING | TableAdapter.MERGE | TableAdapter.RESERVE:
                case TableAdapter.SERVING | TableAdapter.MERGE | TableAdapter.PREPRINT | TableAdapter.RESERVE:
                    menu.add(Menu.NONE, MENU_SPLIT_TABLE, 5, getResources().getString(R.string.split_table));
                    menu.add(Menu.NONE, MENU_CHANGE_TABLE, 5, getResources().getString(R.string.change_table));
                    if (objTable.order.amount <= 0) {
                        menu.add(Menu.NONE, MENU_RETURN_TABLE, 5, getResources().getString(R.string.return_table));
                    }
                    menu.add(Menu.NONE, MENU_CHECK_ALL, 5, getResources().getString(R.string.check_all));
                    break;
                case TableAdapter.AVAILABLE:
                    if (objTable.sysSeat > 0) {
                        menu.add(Menu.NONE, MENU_UNSPLIT_TABLE, 5, getResources().getString(R.string.unsplit_table));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        SeatEntity objTable = adapter.getItem(info.position);
        //

        if (objTable == null) return true;
        final long[] ids = {objTable.seat};
        switch (item.getItemId()) {
            case MENU_UNLOCK_TABLE: {
                final MainScreenActivity activity = (MainScreenActivity) getActivity();
                final List<Long> tableIds = new ArrayList<>();
                tableIds.add(objTable.seat);
                final NormalDialog dialog = new NormalDialog(getContext());
                CharSequence context = Html.fromHtml("这张桌子正在被<font color='red' >" + objTable.lockedStaff + "</font>在设备<font color='red'>" + objTable.lockedDeviceName + "</font>上操作，强制解锁会导致数据错误，请确认要解锁吗？");
                dialog.content(context.toString());
                CharSequence title = Html.fromHtml("<font color='#FF7F27'>解锁确认</font>");
                dialog.title(title.toString());
                dialog.setNormalListener(new NormalDialog.INormailDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        dialog.dismiss();
                        activity.unLockTable(tableIds, true);
                    }
                });
                dialog.show();

                break;
            }
            case MENU_SPLIT_TABLE: {
                splitTable(objTable.seat);
                break;
            }
            case MENU_UNSPLIT_TABLE: {
                unSplitTable(objTable.seat);
                break;
            }
            case MENU_RETURN_TABLE: {
                cancelTable(objTable.seat);
                break;
            }
            case MENU_CHANGE_TABLE: {
                startChangeTable();
                break;
            }
            case MENU_CHECK: {
                activity.displayView(MainScreenActivity.CHECK_FRAGMENT, activity.smartPosBundle.getBundle(objTable.order.personCount, ids, false, false));
                break;
            }
            case MENU_CHECK_ALL: {

                final List<Long> tables = SanyiSDK.rest.getCombineTableIdsById(objTable.seat);
                activity.displayView(MainScreenActivity.CHECK_FRAGMENT, activity.smartPosBundle.getBundle(objTable.order.personCount, tables, false, true));
                break;
            }

        }
        return true;
    }

    public void cancelTable(long tableId) {
        SanyiScalaRequests.ChangeTableRequest(ChangeTableAction.CANCELTABLE, tableId, SanyiSDK.currentUser.id, new ICallBack() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();


            }

            @Override
            public void onSuccess(String status) {
                // TODO Auto-generated method stub

                Toast.makeText(activity, "消台成功", Toast.LENGTH_LONG).show();


            }
        });
    }

    public void splitTable(final long tableId) {
        // find next a seat to use for tableId
        SanyiScalaRequests.ChangeTableRequest(ChangeTableAction.SPLITTABLE, tableId, SanyiSDK.currentUser.id, new ICallBack() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();


            }

            @Override
            public void onSuccess(String status) {
                // TODO Auto-generated method stub
                List<Long> tableIds = new ArrayList<Long>();
                tableIds.add(tableId);
                activity.unLockTable(tableIds, false);
            }
        });
    }

    public void unSplitTable(final long tableId) {
        // find next a seat to use for tableId
        SanyiScalaRequests.ChangeTableRequest(ChangeTableAction.UNDOSPLITTABLE, tableId, SanyiSDK.currentUser.id, new ICallBack() {


            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();


            }

            @Override
            public void onSuccess(String status) {

                Toast.makeText(activity, "取消搭台成功", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void changeTable(long sourceSeatId, long targetSeatId) {
        SanyiScalaRequests.ChangeTableRequest(ChangeTableAction.TURNTABLE, sourceSeatId, SanyiSDK.currentUser.id, targetSeatId, new ICallBack() {

            @Override
            public void onFail(String error) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();


                endChangeTable();
            }

            @Override
            public void onSuccess(String status) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, "转台成功", Toast.LENGTH_LONG).show();
                endChangeTable();
            }
        });
    }

    public void refreshTable(int filter) {
        if (adapter != null) {
            adapter.setFilter(filter);
        }

    }

    @Override
    public void onClick(View v) {

        int buttonId = v.getId();
        switch (buttonId) {
            case R.id.buttonMultiSelectOK:
                if (currentActionMode == MERGE_TABLE_MODE) {
                    tableRowOkCancel.setVisibility(View.INVISIBLE);
                    GeneralActionListener activity = (GeneralActionListener) getActivity();
                    // TODO: find selected table here.

                    long[] tableIds = adapter.getSelectedTableIds();
                    // startNewBill(tableIds);
                }
                if (currentActionMode == CHANGE_TABLE_MODE) {
                    if (adapter.isTableAvaliable(adapter.getTargetTableId())) {
                        changeTable(adapter.getOriginTableId(), adapter.getTargetTableId());
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "不能转到已经开单的台.请重新选择", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.buttonMultiSelectCancel:
                if (currentActionMode == MERGE_TABLE_MODE) {
                    tableRowOkCancel.setVisibility(View.INVISIBLE);
                    endMergeTable();
                }

                if (currentActionMode == CHANGE_TABLE_MODE) {
                    endChangeTable();
                    adapter.resetSelectedTable();
                }
                break;
            default:
                break;
        }
    }

}
