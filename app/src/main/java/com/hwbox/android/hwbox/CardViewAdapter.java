package com.hwbox.android.hwbox;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder>
{


    private ArrayList<HomeWork> hws;


    // Provide a suitable constructor (depends on the kind of dataset)
    public CardViewAdapter( ArrayList<HomeWork> dataset)
    {
        hws = dataset;
    }

    public CardViewAdapter()
    {
        hws = new ArrayList<HomeWork>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card, null);


        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }



    //does not work
    public void removeItem( int position)
    {
        hws.remove(position);
        notifyItemRemoved( position);
    }


    public void addItem( HomeWork newHW)
    {
        hws.add(newHW);
        notifyItemInserted(getItemCount());
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        HomeWork hw = hws.get( position);

        viewHolder.setNote( hw.getNote());
        viewHolder.setCourse( hw.getCourse());

        Calendar calendar = hw.getDeadLine();
        String date = calendar.get( Calendar.DAY_OF_MONTH) + "." + calendar.get( Calendar.MONTH)
                                    + "." + calendar.get( Calendar.YEAR) + "  " + calendar.get( Calendar.HOUR_OF_DAY)
                                    + "." + calendar.get( Calendar.MINUTE);
        viewHolder.setDueDate( date);
        viewHolder.setTitle( hw.getTitle());




    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {

        return hws.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // this class should change when new views are added.

        private TextView title;
        private TextView note;
        private TextView dueDate;
        private TextView course;

        private Button deleteButton;

        public ViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            note = (TextView) itemLayoutView.findViewById(R.id.info_text);
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            dueDate = (TextView) itemLayoutView.findViewById(R.id.dueDate);
            course = (TextView) itemLayoutView.findViewById(R.id.course);


        }

        public void setTitle( String newTitle)
        {
            title.setText( newTitle);
        }

        public void setNote( String newNote)
        {
            note.setText( newNote);
        }

        public void setDueDate( String newDueDate)
        {
            dueDate.setText( newDueDate);
        }

        public void setCourse( String newCourse)
        {
            course.setText( newCourse);
        }

    }

}
