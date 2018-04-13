package com.rainbow.smartpos.tablestatus;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.util.AutoFitText;
import com.rainbow.smartpos.Restaurant;

import static com.rainbow.smartpos.tablestatus.TableAdapter.*;

public class CheckableTableLayout extends FrameLayout implements Checkable {
    public float alpha = 1;
    private CheckedTextView _checkbox;
    public int tableState;

    public void setTableState(int tableState) {
        this.tableState = tableState;
    }

    public CheckableTableLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
        // 1212
        if (isInEditMode()) {
            return;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // find checked text view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View v = getChildAt(i);
            if (v instanceof CheckedTextView) {
                _checkbox = (CheckedTextView) v;
            }
        }
    }

    public String getTableName() {
        TextView tableName = (TextView) this.findViewById(R.id.textViewTableDetailName);
        return tableName.getText().toString();
    }

    public void setTableName(String name)

    {

        AutoFitText tableName = (AutoFitText) this.findViewById(R.id.textViewTableDetailName);
        tableName.setText(name);
        tableName.setTextColor(Color.parseColor("#C27054"));
        if ((tableState & TableAdapter.PREPRINT) == PREPRINT || tableState == AVAILABLE) {
            tableName.setTextColor(Color.WHITE);
        }

    }

    public void setVirualTableName(String name) {
        TextView virualTableName = (TextView) this.findViewById(R.id.textViewVirualTableName);
        virualTableName.setText(name);
        virualTableName.setTextColor(Color.WHITE);
    }

    public void setTableSpending( String spending) {
        TextView tableSpending = (TextView) this.findViewById(R.id.textViewTableSpending);
        tableSpending.setAlpha((float) 0.8);
        tableSpending.setText(Restaurant.currentcyFormatter.format(Double.parseDouble(spending)));
        if (tableState == TableAdapter.AVAILABLE) {
            tableSpending.setVisibility(View.INVISIBLE);
        } else {
            tableSpending.setVisibility(View.VISIBLE);
        }
        if ((tableState & TableAdapter.PREPRINT) == PREPRINT) {
            tableSpending.setTextColor(Color.WHITE);
        }

    }

    public void setTablePersonCount( String personCount) {
        TextView tablePersonCount = (TextView) this.findViewById(R.id.textViewTablePersonCount);
        tablePersonCount.setAlpha((float) 0.5);
        tablePersonCount.setText(personCount + "人");
        if (tableState == TableAdapter.AVAILABLE) {
            tablePersonCount.setTextColor(Color.WHITE);
        } else {
            tablePersonCount.setTextColor(Color.parseColor("#C27054"));
        }
        if ((tableState & TableAdapter.PREPRINT) == PREPRINT) {
            tablePersonCount.setTextColor(Color.WHITE);
        }
    }

    public void setTableCombineTag(boolean shown, String tag, String color) {
        TextView textViewTableCombineTag = (TextView) this.findViewById(R.id.textViewTableCombineTag);
        if (shown) {
            textViewTableCombineTag.setTextColor(Color.parseColor(color));
            textViewTableCombineTag.setVisibility(View.VISIBLE);
            textViewTableCombineTag.setText(tag);
        } else {
            textViewTableCombineTag.setVisibility(View.GONE);
        }
    }


    public void setTableOpenTime( String openTime) {
        TextView tableOpenTime = (TextView) this.findViewById(R.id.textViewTableOpenTime);
        tableOpenTime.setAlpha((float) 0.5);
        tableOpenTime.setText(openTime);

        if (tableState == TableAdapter.AVAILABLE) {
            tableOpenTime.setVisibility(View.INVISIBLE);
        } else {
            tableOpenTime.setVisibility(View.VISIBLE);
            tableOpenTime.setTextColor(Color.parseColor("#C27054"));
        }
        if ((tableState & TableAdapter.PREPRINT) == PREPRINT) {
            tableOpenTime.setTextColor(Color.WHITE);
        }
    }

    @Override
    public boolean isChecked() {
        return _checkbox != null ? _checkbox.isChecked() : false;
    }

    @Override
    public void setChecked(boolean checked) {
        if (_checkbox != null) {
            _checkbox.setChecked(checked);
        }
    }

    @Override
    public void toggle() {
        if (_checkbox != null) {
            _checkbox.toggle();
        }
    }
}
