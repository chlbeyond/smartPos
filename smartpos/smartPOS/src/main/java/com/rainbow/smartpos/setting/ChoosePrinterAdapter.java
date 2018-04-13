package com.rainbow.smartpos.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.model.ShopPosMachineDetail;
import com.sanyipos.sdk.model.ShopPrinter;

public class ChoosePrinterAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private int selection = -1;
    private int i;

    public ChoosePrinterAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        initSelection();

    }

    private void initSelection() {
        for (ShopPosMachineDetail pos : SanyiSDK.rest.operationData.shopPosMachine) {
            if (pos.id == SanyiSDK.registerData.getDeviceId()) {
                for (int i = 0; i < SanyiSDK.rest.operationData.shopPrinters.size(); i++) {
                    if (pos.usePrinterId .equals(SanyiSDK.rest.operationData.shopPrinters.get(i).id)) {
                        selection = i;
                    }
                }

            }
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return SanyiSDK.rest.operationData.shopPrinters.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ShopPrinter printer = SanyiSDK.rest.operationData.shopPrinters.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.printer_setting_dialog_view_item, null);
            holder.rb = (RadioButton) convertView.findViewById(R.id.radioButton);
            holder.tv = (TextView) convertView.findViewById(R.id.printerName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = printer.name;
        if (null != printer.ip && (!printer.ip.isEmpty())) {
            name = name + "(" + printer.ip + ")";
        }
        holder.tv.setText(name);
        holder.rb.setChecked(false);
        if (selection == position) {
            holder.rb.setChecked(true);
        }
        return convertView;
    }

    public void addSelected(int position) {
        selection = position;
    }

    class ViewHolder {
        TextView tv;
        RadioButton rb;
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

}
