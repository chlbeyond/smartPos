package com.rainbow.smartpos.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rainbow.smartpos.R;

import java.util.List;

public class PrinterAdapter extends BaseAdapter {
	private List<String> data;
	private Context context;
	private LayoutInflater inflater;
	private int selection = -1;



	public PrinterAdapter(List<String> data, Context context) {
		this.data = data;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;  
        if (convertView == null) {  
            holder = new ViewHolder();  
            convertView = inflater.inflate( R.layout.printer_setting_dialog_view_item, null);
            holder.rb = (RadioButton) convertView.findViewById(R.id.radioButton);  
            holder.tv = (TextView) convertView.findViewById(R.id.printerName);  
            convertView.setTag(holder);  
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }
        holder.tv.setText(data.get(position));
        holder.rb.setChecked(false);
        if(selection == position){
        	holder.rb.setChecked(true);
        }
		return convertView;
	}
	public void addSelected(int position){
		selection = position;
	}
	class ViewHolder{
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
