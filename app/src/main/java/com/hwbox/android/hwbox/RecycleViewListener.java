package com.hwbox.android.hwbox;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by omer on 21.01.2015.
 */
public class RecycleViewListener implements RecyclerView.OnItemTouchListener
{

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
    {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e)
    {

    }
}
