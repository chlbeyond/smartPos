package com.rainbow.smartpos.place;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

public class GridViewItemDecoration extends ItemDecoration {
	private int horizontalSpace;  
	private int verticalSpace;
	   
    public GridViewItemDecoration(int horizontalSpace, int verticalSpace) {  
        this.horizontalSpace = horizontalSpace;  
        this.verticalSpace = verticalSpace;
    }  

    @Override 
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {  

        if(parent.getChildPosition(view) != 0)  {
            outRect.top = verticalSpace; 
            outRect.right = horizontalSpace;
        }
    }  
}
