package com.ttop.discmusicplayer.customlibs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class CustomTabs extends TabLayout {
    public CustomTabs(Context context) {
        super(context);
    }

    public CustomTabs(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabs(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup tabLayout = (ViewGroup)getChildAt(0);

        int childCount = tabLayout.getChildCount();

        int widths[] = new int[childCount+1];

        for(int i = 0; i < childCount; i++){
            widths[i] = tabLayout.getChildAt(i).getMeasuredWidth();
            widths[childCount] += widths[i];
        }

        int measuredWidth = getMeasuredWidth();
        for(int i = 0; i < childCount; i++){
            tabLayout.getChildAt(i).setMinimumWidth(measuredWidth*widths[i]/widths[childCount]);
        }
    }
}
