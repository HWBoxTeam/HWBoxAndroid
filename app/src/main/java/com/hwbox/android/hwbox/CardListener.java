package com.hwbox.android.hwbox;

/**
 * Created by omer on 20.12.2014.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class CardListener implements RecyclerView.OnItemTouchListener
{
    private OnItemClickListener mListener;


    private boolean isLongPress = false;
    private boolean isShortPress = false;
    private boolean isDown = false; // yeah I'm down. :)
    private GestureDetector mGestureDetector;


    public interface OnItemClickListener
    {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
        public void onItemDown(View view, int position);
    }

    private void resetEvents()
    {
        isLongPress = false;
        isShortPress = false;
        isDown = false;
    }





    public CardListener(Context context, OnItemClickListener listener)
    {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                isShortPress = true;

                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                super.onLongPress(e);
                isLongPress = true;
            }

            @Override
            public boolean onDown(MotionEvent e)
            {
                isDown = true;

                return super.onDown(e);
            }


        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e)
    {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null )
        {
            mGestureDetector.onTouchEvent(e);

            if( isShortPress && !isLongPress)
                mListener.onItemClick(childView, view.getChildPosition(childView));
            else if( isLongPress)
                mListener.onItemLongClick( childView, view.getChildPosition( childView));
            else if( isDown)
                mListener.onItemDown( childView, view.getChildPosition( childView));

            resetEvents();

        }






        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }



    public static class CardGestureDetector extends GestureDetector.SimpleOnGestureListener
    {



    }
}
