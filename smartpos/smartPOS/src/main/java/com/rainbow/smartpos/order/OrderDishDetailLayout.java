package com.rainbow.smartpos.order;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;

import me.grantland.widget.AutofitTextView;


public class OrderDishDetailLayout extends FrameLayout {


    public OrderDishDetailLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public String getDishName() {
        AutofitTextView dishName = (AutofitTextView) this.findViewById(R.id.textViewDishDetailName);
        return dishName.getText().toString();
    }

    public void setDishName(String name) {
        AutofitTextView dishName = (AutofitTextView) this.findViewById(R.id.textViewDishDetailName);
        dishName.setText(name);
    }

    public String getDishPrice() {
        AutofitTextView dishPrice = (AutofitTextView) this.findViewById(R.id.textViewDishPrice);
        return dishPrice.getText().toString();
    }

    public void setDishPrice(String price) {
        AutofitTextView dishPrice = (AutofitTextView) this.findViewById(R.id.textViewDishPrice);
        //String temp = Restaurant.currentcyFormatter.format(price);
        if (dishPrice != null)
            dishPrice.setText(price);
    }


    public void setDishOrdered() {
        FrameLayout layout = (FrameLayout) this.findViewById(R.id.OrderDishDetailInnerLayout);
        layout.setBackgroundResource(R.drawable.dish_ordering);
    }

    public void setDishUnOrdered() {
        FrameLayout layout = (FrameLayout) this.findViewById(R.id.OrderDishDetailInnerLayout);
        layout.setBackgroundResource(R.drawable.dish);
    }

    public void setNameTextColor(String color) {
        TextView dishName = (TextView) this.findViewById(R.id.textViewDishDetailName);
        if (dishName != null)
            dishName.setTextColor(Color.parseColor(color));

    }

    public void setPriceTextColor(String color) {
        TextView dishPrice = (TextView) this.findViewById(R.id.textViewDishPrice);
        if (dishPrice != null)
            dishPrice.setTextColor(Color.parseColor(color));
    }
}
