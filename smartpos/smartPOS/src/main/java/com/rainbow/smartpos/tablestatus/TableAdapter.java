package com.rainbow.smartpos.tablestatus;

import android.content.Context;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.ComparatorCombineTableByTag;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.rest.VirtualTable;
import com.sanyipos.sdk.utils.DateHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableAdapter extends BaseAdapter {

    public static final int AVAILABLE = 1;
    public static final int SERVING = 2;
    public static final int PREPRINT = 4;
    public static final int MERGE = 8;
    public static final int SPLIT = 16;
    public static final int OVERTIME = 64;
    public static final int RESERVE = 128;
    public static final int ALL = 0xffffffff;

    public static int filter = ALL;

    private Context mContext;
    private long tableGroup;
    private int tableGroupFragmentIndex;
    private boolean isMultiSelect = false;
    private long originTableId = -1;
    private long targetTableId = -1;

    public LongSparseArray<SeatEntity> selectedTable = new LongSparseArray<SeatEntity>();
    public List<SeatEntity> tableShown = SanyiSDK.rest.operationData.shopTables;
    GridView.LayoutParams params = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    final LayoutInflater inflater;
    public String[] colors = {"#8e6102", "#007a13", "#a4220c", "#9221be", "#7f5fbe"};
    public TableAdapter prevAdapter;

    public TableAdapter nextAdapter;

    public TableAdapter(Context c, int tableGroupIndex) {
        mContext = c;
        params.height = 50;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tableGroupFragmentIndex = tableGroupIndex;
        setTableGroup();
    }

    public int getCount() {
        // we need call setTableGroup to update tableShown
        // since table status including bill no may change
        // after merge and other action...
        setTableGroup();
        return tableShown.size();
    }

    public long getOriginTableId() {
        return originTableId;
    }

    public void setOriginTableId(long originTableId) {
        this.originTableId = originTableId;
    }

    public long getTargetTableId() {
        return targetTableId;
    }

    public void setTargetTableId(long targetTableId) {
        this.targetTableId = targetTableId;
    }

    // filter the table to show
    public void setFilter(int filter) {
        this.filter = filter;
        refresh();
        filter = ALL;
    }

    /**
     * @return the isMultiSelect
     */
    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    /**
     * @param isMultiSelect the isMultiSelect to set
     */
    public void setMultiSelect(boolean isMultiSelect) {

        this.isMultiSelect = isMultiSelect;
        notifyDataSetChanged();
    }

    public void startMultiSelectTable(long originTableId) {
        this.originTableId = originTableId;
        setMultiSelect(true);
    }

    public void endMultiSelectTable() {
        this.originTableId = -1;
        setTableGroup(); // refresh shown table
        setMultiSelect(false);
    }

    public synchronized void refresh() {
        setTableGroup(); // refresh shown table
        notifyDataSetChanged();
    }

    public SeatEntity getItem(int position) {
        return tableShown.get(position);
    }

    private static <T> long[] getKeys(LongSparseArray<T> a) {
        final int s = a.size();
        final long[] keys = new long[s];
        for (int i = 0; i < s; ++i) {
            keys[i] = a.keyAt(i);
        }
        return keys;
    }

    public long[] getSelectedTableIds() {
        return getKeys(selectedTable);
    }

    @Override
    public void notifyDataSetChanged() {
        refreshData();
        if (prevAdapter != null) {
            prevAdapter.refreshData();
        }
        if (nextAdapter != null) {
            nextAdapter.refreshData();
        }
    }

    public void refreshData() {
        super.notifyDataSetChanged();
    }

    public void setSelectedTable(int position) {
        SeatEntity selectedObj = getItem(position);
        long id = -1;
        id = selectedObj.seat;
        if (selectedTable.indexOfKey(id) < 0) {
            selectedTable.put(id, selectedObj);
        } else {
            selectedTable.remove(id);
        }
        notifyDataSetChanged();
    }

    public boolean isTableAvaliable(long tableId) {
        SeatEntity seat = getSeatById(tableId);
        if (seat != null)
            return seat.state == AVAILABLE;
        return false;
    }

    public SeatEntity getSeatById(long seatId) {
        for (SeatEntity seat : tableShown) {
            if (seat.seat == seatId) {
                return seat;
            }
        }
        return null;
    }

    // return table id
    public long getItemId(int position) {
        return position;
        // JSONObject obj = Restaurant.table.valueAt(position);
        // int id = 0;
        // try {
        // id = obj.getInt("id");
        // } catch (JSONException e) {
        // e.printStackTrace();
        // }
        // return id;
    }

    static class ViewHolder {
        public TextView style;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SeatEntity objTable = tableShown.get(position);
        TableTagView l;
        String personCount = "";
        String spending = "0.00";
        String openTime = "0:00";
//        if (convertView == null) {
        l = (TableTagView) inflater.inflate(R.layout.fragment_table_group_item_detail, parent, false);

//        } else {
//            l = (TableTagView) convertView;
//        }
        long state = objTable.state;
        l.setTableState((int) state);
        boolean lock = objTable.lock;
        long tableId = objTable.seat;
        ImageView imageViewLock = (ImageView) l.findViewById(R.id.imageViewLock);
        TextView imageViewReserved = (TextView) l.findViewById(R.id.imageViewReserved);

        if (lock) {
            imageViewLock.setVisibility(View.VISIBLE);
        } else {
            imageViewLock.setVisibility(View.INVISIBLE);
        }

        if ((objTable.state & RESERVE) == RESERVE) {
            imageViewReserved.setVisibility(View.VISIBLE);
        } else {
            imageViewReserved.setVisibility(View.INVISIBLE);
        }
//        l.setTableCombineTag(false, "", "");
        if (state == AVAILABLE) { // table is available
            l.setTableName(objTable.tableName);
            l.setTableSpending(spending);
            if (objTable.personCount != null) {
                personCount = Integer.toString(objTable.personCount);
            } else {
                personCount = "0";
            }
            l.setTablePersonCount(personCount);
            l.setTableOpenTime(openTime);
            l.setBackgroundResource(R.drawable.table_background_nor);
        } else {
            l.setTableName(objTable.tableName);
            if (objTable.order.personCount != null) {
                personCount = Integer.toString(objTable.order.personCount);
            } else {
                personCount = "0";
            }
            if (objTable.order.amount == 0) {
                spending = "0";
            } else {
                spending = Double.toString(objTable.order.amount);
            }
            if (objTable.order.createTime != null) {
                openTime = DateHelper.hmFormater.format(objTable.order.createTime);
            }
            l.setTableTag(objTable.order.tag);

            l.setBackgroundResource(R.drawable.table_background_ser);


            // }

            l.setTableSpending(spending);
            l.setTablePersonCount(personCount);
            l.setTableOpenTime(openTime);

//            if ((objTable.state & MERGE) == MERGE) {
//                if (objTable.order.tag != null) {
//                    l.setTablePersonCount(true, personCount);
////                    l.setTableCombineTag(true, objTable.order.tag, "#000000");
//                    l.setBackgroundResource(R.drawable.table_background_combine);
//                }
//
//            }

            if ((objTable.state & PREPRINT) == PREPRINT) {
                l.setBackgroundResource(R.drawable.table_background_pre);
            }

        }
        if (isMultiSelect && originTableId == tableId) {
            l.setBackgroundResource(R.color.LightBlue);
        }
        if (targetTableId == tableId) {
            l.setBackgroundResource(R.color.BurlyWood);
        }


        // if the table is in service, display the totoal order
        return l;
    }

    public void resetSelectedTable() {
        selectedTable = new LongSparseArray<SeatEntity>();
    }

    /**
     * @return the tableGroup
     */
    public long getTableGroup() {
        return tableGroup;
    }

    /**
     * @param
     */
    public synchronized void setTableGroup() {
        List<SeatEntity> targetTable = new ArrayList<SeatEntity>();
        int revisedIndex = tableGroupFragmentIndex - 1;
        if (revisedIndex < 0) {
            for (int i = 0; i < SanyiSDK.rest.operationData.shopTables.size(); ++i) {
                SeatEntity obj = SanyiSDK.rest.operationData.shopTables.get(i);
                if (obj == null) {
                    continue;
                }
                long state = obj.state;
                if (filter == MERGE) {
                    if ((MERGE & state) == MERGE) {
                        targetTable.add(obj);
                    }
                    Collections.sort(targetTable, new ComparatorCombineTableByTag());
                } else if (filter == PREPRINT) {
//					if (((SERVING | PREPRINT) == state) {
//						targetTable.add(obj);
//					}
                    if ((PREPRINT & state) == PREPRINT) {
                        targetTable.add(obj);
                    }
                } else {
                    if ((state & filter) == state) {
                        targetTable.add(obj);
                    }
//					if ((SERVING | PREPRINT) == state) {
//						if (!targetTable.contains(obj))
//							if (filter != AVAILABLE)
//								targetTable.add(obj);
//					}
                    if ((SERVING & state) == SERVING) {
                        if (!targetTable.contains(obj))
                            if (filter != AVAILABLE)
                                targetTable.add(obj);
                    }
                }
            }

            tableShown = targetTable;
            return;
        }

        this.tableGroup = SanyiSDK.rest.tableGroups.get(revisedIndex).id;

        // update table
        for (int i = 0; i < SanyiSDK.rest.operationData.shopTables.size(); ++i) {
            SeatEntity obj = SanyiSDK.rest.operationData.shopTables.get(i);
            try {
                if (obj.tableGroup == this.tableGroup) {
                    long state = obj.state;
//					if (filter == PREPRINT) {
//						if ((SERVING | filter) == state) {
//							targetTable.add(obj);
//						}
//					} else {
//						if ((state & filter) == state) {
//							targetTable.add(obj);
//						}
//
//						if ((SERVING | PREPRINT) == state) {
//							if (!targetTable.contains(obj))
//								if (filter != AVAILABLE)
//									targetTable.add(obj);
//						}
//					}
                    if (filter == MERGE) {
                        if ((MERGE & state) == MERGE) {
                            targetTable.add(obj);
                        }
                        Collections.sort(targetTable, new ComparatorCombineTableByTag());
                    } else if (filter == PREPRINT) {
//						if (((SERVING | PREPRINT) == state) {
//							targetTable.add(obj);
//						}
                        if ((PREPRINT & state) == PREPRINT) {
                            targetTable.add(obj);
                        }
                    } else {
                        if ((state & filter) == state) {
                            targetTable.add(obj);
                        }
//						if ((SERVING | PREPRINT) == state) {
//							if (!targetTable.contains(obj))
//								if (filter != AVAILABLE)
//									targetTable.add(obj);
//						}
                        if ((SERVING & state) == SERVING) {
                            if (!targetTable.contains(obj))
                                if (filter != AVAILABLE)
                                    targetTable.add(obj);
                        }
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        tableShown = targetTable;
    }

    public String getColor(String tag) {
        String color = colors[0];
        for (int i = 0; i < SanyiSDK.rest.virtualTables.size(); i++) {
            VirtualTable virtualTable = SanyiSDK.rest.virtualTables.get(i);
            if (tag.equals(virtualTable.tag)) {
                color = colors[i % (colors.length)];
            }
        }
        return color;
    }
}
