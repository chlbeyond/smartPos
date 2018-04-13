package com.rainbow.smartpos.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala.GetDetailOperationRequest;
import com.sanyipos.sdk.model.SeatEntity;
import com.sanyipos.sdk.model.scala.DetailOpGroupList;
import com.sanyipos.sdk.model.scala.DetailOpGroupList.DetailOpChildList;
import com.sanyipos.sdk.model.scala.DetailOpResult;
import com.sanyipos.sdk.utils.DateHelper;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

public class DetailOpLogsDialog {
    Context activity;
    Dialog dialog;
    private List<SeatEntity> tables;
    private ListView listViewDetailOp;
    private TableNameAdapter tableNameAdapter;
    private DetailOpLogsAdapter detailOpLogsAdapter;
    private ListView tableNameList;
    private List<DetailOpChildList> detailLogs = new ArrayList<DetailOpChildList>();

    private ImageButton close;


    public DetailOpLogsDialog(Context activity, List<SeatEntity> tables) {
        this.activity = activity;
        this.tables = tables;
    }

    public void show() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View iView = inflater.inflate(R.layout.show_detail_op_dialog, null, false);
        tableNameList = (ListView) iView.findViewById(R.id.listView_operation_tableName);
        tableNameAdapter = new TableNameAdapter(tables, activity);
        tableNameList.setAdapter(tableNameAdapter);
        tableNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onTableItemClick(position);
                tableNameAdapter.setSelect(position);
            }
        });

        listViewDetailOp = (ListView) iView.findViewById(R.id.listViewDetailOp);
        detailOpLogsAdapter = new DetailOpLogsAdapter(detailLogs, activity);
        listViewDetailOp.setAdapter(detailOpLogsAdapter);
        iView.findViewById(R.id.iv_close_dialog).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dlg.setView(iView);
        dialog = dlg.create();
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度设置为屏幕的0.8
        p.width = (int) (MainScreenActivity.getScreenWidth() * 0.7); // 宽度设置为屏幕的0.8
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);

        onTableItemClick(0);
    }

    public void onTableItemClick(int pos) {
        SeatEntity table = tableNameAdapter.getItem(pos);
        SanyiScalaRequests.getDetailOpRequest(table.order.order, new GetDetailOperationRequest.IGetDetailOpLogListener() {
            @Override
            public void onSuccess(DetailOpGroupList detailOpGroupList) {
                KLog.d(detailOpGroupList.childList.size());
                detailLogs = detailOpGroupList.childList;
                detailOpLogsAdapter = new DetailOpLogsAdapter(detailLogs, activity);
                listViewDetailOp.setAdapter(detailOpLogsAdapter);
            }

            //            @Override
            public void onSuccess(DetailOpResult resp) {
//                detailLogs = resp.logs;
//                detailOpLogsAdapter = new DetailOpLogsAdapter(detailLogs, activity);
//                listViewDetailOp.setAdapter(detailOpLogsAdapter);
            }


            @Override
            public void onFail(String error) {

            }
        });
    }


    private class TableNameAdapter extends BaseAdapter {
        private List<SeatEntity> tables;
        private LayoutInflater inflater;
        private int selectPos = 0;

        public TableNameAdapter(List<SeatEntity> tables, Context context) {
            super();
            this.tables = tables;
            this.inflater = LayoutInflater.from(context);
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

        public void setSelect(int pos) {
            selectPos = pos;
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (convertView == null) {
                vi = inflater.inflate(R.layout.pos_name_item, null);
            }
            TextView name = (TextView) vi.findViewById(R.id.name);
            name.setText(tables.get(position).tableName);
            name.setTextColor(Color.parseColor("#000000"));
            vi.setBackgroundColor(Color.parseColor("#EEEEEE"));
            if (position == selectPos) {
                name.setTextColor(Color.parseColor("#26A5F6"));
            }
            return vi;
        }

    }

    private class DishNameAdapter extends BaseAdapter {
        private List<DetailOpChildList> data;
        private LayoutInflater inflater;
        private Context context;
        private int selectPos = 0;

        public DishNameAdapter(List<DetailOpChildList> data, Context context) {
            super();
            this.data = data;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public DetailOpChildList getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        public void setSelect(int pos) {
            selectPos = pos;
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (convertView == null) {
                vi = inflater.inflate(R.layout.pos_name_item, null);
            }
            TextView name = (TextView) vi.findViewById(R.id.name);
            name.setText(data.get(position).name);
            name.setTextColor(Color.parseColor("#000000"));
            vi.setBackgroundColor(Color.parseColor("#EEEEEE"));
            if (position == selectPos) {
                name.setTextColor(Color.parseColor("#26A5F6"));
            }
            return vi;
        }

    }

    public class DetailOpLogsAdapter extends BaseAdapter {

        private List<DetailOpChildList> detailLogs;
        private LayoutInflater inflater;
        private Context context;

        public DetailOpLogsAdapter(List<DetailOpChildList> detailLogs, Context context) {
            super();
            this.detailLogs = detailLogs;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return detailLogs.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder = null;

            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.detail_op_logs_item, null);
                viewHolder.number = (TextView) convertView.findViewById(R.id.number);
                viewHolder.message = (TextView) convertView.findViewById(R.id.message);
                viewHolder.staffName = (TextView) convertView.findViewById(R.id.staffName);
                viewHolder.authStaffName = (TextView) convertView.findViewById(R.id.authStaffName);
                viewHolder.posName = (TextView) convertView.findViewById(R.id.posName);
                viewHolder.createon = (TextView) convertView.findViewById(R.id.createon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            DetailOpChildList detailLog = detailLogs.get(position);
            viewHolder.number.setText(String.valueOf(position + 1));
            viewHolder.message.setText(detailLog.detailLogs.get(0).message);
            viewHolder.staffName.setText(detailLog.detailLogs.get(0).staffName);
            viewHolder.authStaffName.setText(detailLog.detailLogs.get(0).authStaffName);
            viewHolder.posName.setText(detailLog.detailLogs.get(0).posName);
            viewHolder.createon.setText(DateHelper.hmsFormater.format(detailLog.detailLogs.get(0).createon));

            return convertView;
        }

        public class ViewHolder {
            TextView number;
            TextView message;
            TextView staffName;
            TextView authStaffName;
            TextView posName;
            TextView createon;
        }

    }
}
