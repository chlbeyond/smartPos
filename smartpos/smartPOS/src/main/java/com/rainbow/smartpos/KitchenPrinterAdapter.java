package com.rainbow.smartpos;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class KitchenPrinterAdapter extends BaseAdapter {
    private Context mContext;

    public KitchenPrinterAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return 10;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {

        public TextView style;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        ViewHolder holder;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            // textView.setPadding(8, 8, 8, 8);
            // textView.setBackgroundResource(R.drawable.table_button_shape);
            holder = new ViewHolder();
            holder.style = textView;
            textView.setTag(holder);

        } else {
            textView = (TextView) convertView;
        }

        String styledText = "<big> <font color='#fffffff'>" + "空闲" + "</font> </big>";
        textView.setText(Html.fromHtml(styledText));

        return textView;
    }

}
