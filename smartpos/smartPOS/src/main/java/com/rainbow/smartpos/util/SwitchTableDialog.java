package com.rainbow.smartpos.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.common.view.MyDialog;
import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.tablestatus.TableAdapter;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.TableGroup;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

public class SwitchTableDialog implements View.OnKeyListener {
    public static final int ORDER = 0;
    public static final int CHECK = 1;

    public interface ISwitchTableListener {
        void openTable(SeatEntity table, int personCount);

        void batchOperation(long[] tableIds);


        void cancel();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        KLog.d("SwitchTableDialog" , "on Key ");
        MainScreenActivity mainScreenActivity = (MainScreenActivity) mAct;
        mainScreenActivity.onBackPressed();
        return false;
    }

    private Context mAct;
    private Dialog dialog;
    private ISwitchTableListener listener;
    private SeatEntity currentTable;
    private PersonCountAdapter mPersonCountAdapter;
    private GroupTableAdapter mGroupTableAdapter;
    private View view;
    ExpandableListView expListView;
    private GridView personCountGridView;
    //	private TextView choose_table;
    private LinearLayout choose_table_layout;
    private LinearLayout choose_personcount_layout;
    public boolean isChooseOpenTable = false;
    private int flag;

    public SwitchTableDialog(Context context, int flag, SeatEntity currentTable, ISwitchTableListener listener) {
        // TODO Auto-generated constructor stub
        mAct = context;
        this.flag = flag;
        this.currentTable = currentTable;
        this.listener = listener;
    }

    public void show() {
        view = LayoutInflater.from(mAct).inflate(R.layout.switch_table_dialog, null);
        dialog = new MyDialog(mAct, MainScreenActivity.getScreenWidth() * 0.4, MainScreenActivity.getScreenHeight() * 0.9, view, R.style.OpDialogTheme);

        choose_table_layout = (LinearLayout) view.findViewById(R.id.choose_table_layout);
        choose_personcount_layout = (LinearLayout) view.findViewById(R.id.choose_personcount_layout);

        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        mGroupTableAdapter = new GroupTableAdapter(mAct);
        expListView.setAdapter(mGroupTableAdapter);
        expListView.setGroupIndicator(null);
        expListView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        // expListView.setChildIndicator(childIndicator)
        for (int i = 0; i < mGroupTableAdapter.getGroupCount(); i++) {
            expListView.expandGroup(i);
        }
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }

        });

        personCountGridView = (GridView) view.findViewById(R.id.personCountGridView);
        mPersonCountAdapter = new PersonCountAdapter(mAct);
        personCountGridView.setAdapter(mPersonCountAdapter);

//		choose_table = (TextView) view.findViewById(R.id.choose_table);
//		if (null != currentTable) {
//			choose_table.setText("选择餐桌("+"当前餐桌"+currentTable.tableName+")");
//		}else{
//			choose_table.setText("选择餐桌");
//		}
        choose_personcount_layout.setVisibility(View.GONE);

        setListener();
        dialog.show();
    }

    public void setListener() {
        view.findViewById(R.id.iv_close_dialog).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                listener.cancel();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.sure_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null != getChooseTable()) {
                    listener.openTable(getChooseTable(), mPersonCountAdapter.getSelectNumber());
                    dialog.dismiss();
                } else {
                    Toast.makeText(mAct, "请选择餐桌",Toast.LENGTH_LONG).show();
                }

            }
        });
        personCountGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (isChooseOpenTable) {
                    mPersonCountAdapter.setSelectPosition(-1);

                    Toast.makeText(mAct, "餐桌已开台,不能选择人数",Toast.LENGTH_LONG).show();
                    return;
                }
                if (null == getChooseTable()) {
                    Toast.makeText(mAct, "未选择餐桌,不能选择人数",Toast.LENGTH_LONG).show();

                    return;
                }
                mPersonCountAdapter.setSelectPosition(position);
            }
        });
    }


    public void setTableChoose(SeatEntity table) {
        mGroupTableAdapter.refresh();
    }

    public SeatEntity getChooseTable() {
        for (int i = 0; i < SanyiSDK.rest.operationData.shopTables.size(); i++) {
            SeatEntity table = SanyiSDK.rest.operationData.shopTables.get(i);
        }
        return null;
    }

    private class SwitchTableAdapter extends BaseAdapter {
        public static final int AVAILABLE = 1;
        public static final int SERVING = 2;
        public static final int PREPRINT = 4;
        public static final int MERGE = 8;
        public static final int SPLIT = 16;
        public static final int RESERVE = 32;
        public static final int OVERTIME = 64;
        public static final int ALL = 0xffffffff;

        private Context context;
        private LayoutInflater inflater;
        private List<SeatEntity> tables;
        private TableGroup tableGroup;

        public SwitchTableAdapter(Context context, TableGroup tableGroup) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.tableGroup = tableGroup;
            initData();
        }

        private void initData() {
            // TODO Auto-generated method stub
            tables = new ArrayList<SeatEntity>();
            List<SeatEntity> seats = SanyiSDK.rest.operationData.getTablesByTableGroupId(tableGroup.id);
            if (null != seats) {
                for (int i = 0; i < seats.size(); i++) {
                    SeatEntity seatEntity = seats.get(i);
                    if (seatEntity.state == TableAdapter.AVAILABLE) {
                        continue;
                    }
                    if (null != currentTable) {
                        if (currentTable.seat != seatEntity.seat) {
                            tables.add(seatEntity);
                        }
                    } else {
                        tables.add(seatEntity);
                    }
                }
            }


        }

        public void setSelectPosition(int position) {
            SeatEntity table = tables.get(position);
            if (table.state == TableAdapter.AVAILABLE) {
                choose_personcount_layout.setVisibility(View.VISIBLE);
            } else {
                choose_personcount_layout.setVisibility(View.GONE);
            }
            if (table.lock && table.lockedDevice != SanyiSDK.getDeviceId()) {
                Toast.makeText(mAct,"桌子已锁,无法切换",Toast.LENGTH_LONG).show();
                return;
            }
            //setOtherTableUnChoose(table);
            setTableChoose(table);
            listener.openTable(table, 0);
            dialog.dismiss();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tables.size();
        }

        @Override
        public SeatEntity getItem(int position) {
            // TODO Auto-generated method stub
            return tables.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            SeatEntity table = tables.get(position);
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.switch_table_item, null);
            }
            TextView tableName = (TextView) convertView.findViewById(R.id.table_name);
            ImageView imageViewLock = (ImageView) convertView.findViewById(R.id.imageViewLock);
            tableName.setTextColor(Color.parseColor("#ffffff"));
            tableName.setText(table.tableName);
            if (table.lock) {
                imageViewLock.setVisibility(View.VISIBLE);
            } else {
                imageViewLock.setVisibility(View.INVISIBLE);
            }
            if (table.state == AVAILABLE) { // table is available
                tableName.setTextColor(Color.parseColor("#000000"));
                convertView.setBackgroundResource(R.drawable.change_table_background_nor);
            } else {
                convertView.setBackgroundResource(R.drawable.table_background_ser);
                if ((table.state & MERGE) == MERGE) {
                    if (table.order.tag != null) {
                        convertView.setBackgroundResource(R.drawable.table_background_combine);
                    }
                }
                if ((table.state & PREPRINT) == PREPRINT) {
                    convertView.setBackgroundResource(R.drawable.table_background_pre);
                }
            }
//			if (table.isChooseCombine) {
//				if (table.state != AVAILABLE) {
//					isChooseOpenTable = true;
//					mPersonCountAdapter.setSelectPosition(-1);
//				}else{
//					mPersonCountAdapter.setSelectPosition(table.personCount-1);
//				}
//				convertView.setBackgroundResource(R.drawable.switch_table_item_bg_select);
//				tableName.setTextColor(Color.parseColor("#000000"));
//			}
            return convertView;
        }

    }

    private class PersonCountAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;

        private int currentPosition = -1;

        public PersonCountAdapter(Context context) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        public Integer getSelectNumber() {
            return currentPosition + 1;
        }

        public void setSelectPosition(int position) {
            currentPosition = position;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 20;
        }

        @Override
        public Integer getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.switch_table_item, null);
            }
            TextView tableName = (TextView) convertView.findViewById(R.id.table_name);
            ImageView imageViewLock = (ImageView) convertView.findViewById(R.id.imageViewLock);
            imageViewLock.setVisibility(View.GONE);
            tableName.setTextColor(Color.parseColor("#000000"));
            tableName.setText(String.valueOf(position + 1));
            convertView.setBackgroundResource(R.drawable.change_table_background_nor);
            if (currentPosition == position) {
                convertView.setBackgroundResource(R.drawable.switch_table_item_bg_select);
            }
            return convertView;
        }

    }

    public class GroupTableAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<TableGroup> tableGroups;

        public GroupTableAdapter(Context context) {
            this.context = context;
            initTableGroups();
        }

        public void initTableGroups() {
            tableGroups = new ArrayList<TableGroup>();
            for (int i = 0; i < SanyiSDK.rest.hasTableGroups.size(); i++) {
                TableGroup tableGroup = SanyiSDK.rest.hasTableGroups.get(i);
                if (isGroupHasOpenTable(tableGroup)) {
                    tableGroups.add(tableGroup);
                }
            }
        }

        private boolean isGroupHasOpenTable(TableGroup tableGroup) {
            List<SeatEntity> seats = SanyiSDK.rest.operationData.getTablesByTableGroupId(tableGroup.id);
            if (null != seats) {
                for (int i = 0; i < seats.size(); i++) {
                    SeatEntity seatEntity = seats.get(i);
                    if (seatEntity.state != TableAdapter.AVAILABLE) {
                        if ((null != currentTable)) {
                            if ((currentTable.seat != seatEntity.seat)) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TableGroup tableGroup = tableGroups.get(groupPosition);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.switch_table_item_layout, null);
            }
            GridView tableGridView = (GridView) convertView.findViewById(R.id.tableGridView);
            final SwitchTableAdapter mSwitchTableAdapter = new SwitchTableAdapter(mAct, tableGroup);
            tableGridView.setAdapter(mSwitchTableAdapter);
            tableGridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    mSwitchTableAdapter.setSelectPosition(position);
                }
            });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public TableGroup getGroup(int groupPosition) {
            return tableGroups.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return tableGroups.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = tableGroups.get(groupPosition).name;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.switch_table_group_layout, null);
            }
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.textViewGroupName);
            lblListHeader.setText(headerTitle);
            return convertView;
        }

        @Override
        public void onGroupCollapsed(int groupPosition) {
            // TODO Auto-generated method stub
            super.onGroupCollapsed(groupPosition);
        }

        @Override
        public void onGroupExpanded(int groupPosition) {
            // TODO Auto-generated method stub
            super.onGroupExpanded(groupPosition);
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public void refresh() {
            initTableGroups();
            notifyDataSetChanged();
        }
    }
}
