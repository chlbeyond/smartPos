package com.rainbow.smartpos.order;

import android.content.Context;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AudioEffect.OnEnableStatusChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbow.smartpos.R;

public class OrderDishButton extends RelativeLayout {

	public TextView corText;
	public TextView midText;
	public ImageView imageView_order_dish_button;

	public OrderDishButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public OrderDishButton(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		LayoutInflater.from(context).inflate(R.layout.order_dish_button, this, true);
		midText = (TextView) findViewById(R.id.textView_order_dish_button);
		corText = (TextView) findViewById(R.id.textView_order_dish_button_corner);
		imageView_order_dish_button = (ImageView) findViewById(R.id.imageView_order_dish_button);
		this.setClickable(true);
	}

	public void setMidText(String text) {
		midText.setText(text);
		midText.setVisibility(View.VISIBLE);
		imageView_order_dish_button.setVisibility(View.GONE);
	}

	public void setCorText(String text) {
		corText.setVisibility(View.VISIBLE);
		corText.setText(text);
	}

	public void setMidImageView(int resId) {
		midText.setVisibility(View.GONE);
		corText.setVisibility(View.GONE);
		imageView_order_dish_button.setImageResource(resId);
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		midText.setEnabled(enabled);
		corText.setEnabled(enabled);
		imageView_order_dish_button.setEnabled(enabled);
	}

}
