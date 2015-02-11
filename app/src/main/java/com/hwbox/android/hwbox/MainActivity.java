package com.hwbox.android.hwbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity
{

    private CardViewAdapter adapter;
    private LinearLayoutManager manager;
    private ListView leftDrawer;

    private Context appContext;

    private static TextView dateView;
    private static TextView timeView;

    private static Calendar calendar;

    private List<ParseObject> parseCourseObjects;
    private static String[] courseNames;

    private TextView addCourseTV;




    private String currentCourse;
    private List<ParseObject> currentListOfHWs;






    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        appContext = this;






        currentListOfHWs = null;

        currentCourse = "";




        calendar = Calendar.getInstance();

        RecyclerView cards = (RecyclerView) findViewById( R.id.cardList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        leftDrawer = (ListView)findViewById( R.id.left_drawer_list);

        manager = new LinearLayoutManager(this);
        adapter = new CardViewAdapter();

        addCourseTV = (TextView) findViewById( R.id.addCourseTextView);
        addCourseTV.setOnClickListener( courseAddListener);


        cards.setHasFixedSize(true);
        cards.setAdapter(  adapter);
        cards.setItemAnimator( new DefaultItemAnimator());
        cards.setLayoutManager( manager);

        cards.addOnItemTouchListener( new CardListener( this, new CardListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                // this method should  show all the info on the card.
                Log.d(" Recycle view listener", "click detected");

                View layout = getLayoutInflater().inflate( R.layout.info_layout, null);
                // set info
                TextView titleView = (TextView) layout.findViewById(R.id.hwTitleTextView);
                TextView dueDateView = (TextView) layout.findViewById(R.id.dueDateTextView);
                TextView descriptionView = (TextView) layout.findViewById(R.id.hwDescTextView);

                ParseObject clickedHW = currentListOfHWs.get(position);

                titleView.setText( clickedHW.getString("hwName"));
                dueDateView.setText( clickedHW.getDate("HwDueDate").toString());
                descriptionView.setText( clickedHW.getString("hwDescription"));

                AlertDialog.Builder builder = new AlertDialog.Builder( appContext);
                builder.setView( layout);
                builder.setCancelable(true);
                builder.show();










                ((CardView)view).setCardBackgroundColor(Color.WHITE);

                // do something
            }

            @Override
            public void onItemLongClick(View view, int position)
            {
                Log.d(" Recycle view listener", "long press detected");

                final int index = position;

                AlertDialog.Builder builder = new AlertDialog.Builder( appContext);
                builder.setTitle("Do you want to delete this note?");
                builder.setPositiveButton( "delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //delete the note.
                        currentListOfHWs.get( index).deleteInBackground( new DeleteCallback() {
                            @Override
                            public void done(ParseException e)
                            {
                                if( e == null)
                                {
                                    Toast.makeText( getBaseContext(), "note successfully deleted", Toast.LENGTH_SHORT).show();
                                    adapter.removeItem( index);
                                    currentListOfHWs.remove(index);
                                }

                                else
                                {
                                    Toast.makeText( getBaseContext(), "note could not be deleted", Toast.LENGTH_SHORT).show();
                                    Log.d("OnItemLongClick", e.getMessage());
                                }

                            }
                        });

                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                builder.show();

                ((CardView)view).setCardBackgroundColor( Color.WHITE);


            }

            @Override
            public void onItemDown(View view, int position)
            {

                Log.d("Recycle view listener", "on down detected");

                ((CardView)view).setCardBackgroundColor( Color.LTGRAY);
            }


        }));


        fab.setOnClickListener(fabListener);


        refreshLeftDrawer();//get all the data from parse and show it
        leftDrawer.setOnItemClickListener( drawerItemClickListener);







    }

    View.OnClickListener courseAddListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

            //pop a dialog and get the name
            final View dialogView = getLayoutInflater().inflate( R.layout.add_course_layout, null);

            AlertDialog.Builder builder = new AlertDialog.Builder( appContext);
            builder.setTitle( "enter course name")
                    .setView(dialogView)
                    .setPositiveButton("done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText nameET = (EditText) dialogView.findViewById(R.id.addCourseEditText);

                            ParseObject saveCourse = new ParseObject("fCourse");
                            saveCourse.put("courseName", nameET.getText().toString());
                            saveCourse.put("userPtr", ParseUser.getCurrentUser());


                            // I have to make a query first to find out if the course already exists but this will be implemented later. !!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            saveCourse.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null)
                                        refreshLeftDrawer();
                                    else
                                        Log.d("CourseAddListener", e.getMessage());
                                }
                            });


                        }
                    })
                    .show();
        }
    };


    AdapterView.OnItemClickListener drawerItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {

            //clean all hws
            while( adapter.getItemCount() > 0)
                adapter.removeItem(0);



            //show all hws necessary.

            Toast.makeText( getBaseContext(), "clicked " + position, Toast.LENGTH_SHORT).show();
            String name = ((TextView)view).getText().toString();
            ParseObject course = getCourse( name);

            currentCourse = name;

            //Log.d("LeftDrawerListener", "index is " + index);


            ParseQuery<ParseObject> hws = new ParseQuery<ParseObject>("fHomework");
            hws.whereEqualTo( "userPtr", ParseUser.getCurrentUser());
            hws.whereEqualTo( "coursePtr", course);
            hws.findInBackground( new FindCallback<ParseObject>()
            {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e)
                {
                    if(e == null)
                    {

                        currentListOfHWs = parseObjects;

                        Log.d("LeftDrawerListener", "hw find query successful with " + parseObjects.size()
                                + " objects found");

                        if( parseObjects.size() == 0)
                            Toast.makeText( getBaseContext(), "Yaaaay! No home works!", Toast.LENGTH_SHORT).show();

                        for(int i = 0; i < parseObjects.size(); i++)
                        {
                            ParseObject temp = parseObjects.get(i);

                            Date date = new Date();//(Date)temp.get( "hwDueDate");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime( date);


                            HomeWork newHW = new HomeWork( temp.getString("hwName"), calendar, temp.getString("hwDescription"), "not important");


                            populateScreen(newHW);
                        }

                    }
                    else
                    {

                        Log.d( "LeftDrawerListener", e.getMessage());
                    }
                }
            });


        }
    };



    View.OnClickListener fabListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //add note.

            // this method should be heavily modified since it is doing silly things.

            AlertDialog.Builder builder = new AlertDialog.Builder( appContext);
            final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_view, null);


            final EditText tagText = (EditText) dialogLayout.findViewById(R.id.CardTitle);
            final Spinner courses = (Spinner) dialogLayout.findViewById( R.id.course_picker);

            dateView = (TextView) dialogLayout.findViewById( R.id.dateView);
            dateView.setOnClickListener( dateTimeListener);
            timeView = (TextView) dialogLayout.findViewById( R.id.timeView);
            timeView.setOnClickListener( dateTimeListener);





            ArrayList<String> list = new ArrayList<String>();

            list.addAll( Arrays.asList( courseNames));


            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( getApplicationContext(),
                    android.R.layout.simple_spinner_item, list);

            dataAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
            courses.setAdapter(dataAdapter);


            builder.setCancelable( true)
                    .setView( dialogLayout)
                    .setTitle("enter tag")
                    .setCancelable(true)
                    .setPositiveButton("create", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final String note = tagText.getText().toString();
                            final String course = courses.getSelectedItem().toString();


                            // do what ever you want with a create command
                            HomeWork newHW = new HomeWork("", calendar, note, course);


                            // create a parse objects and save it.
                            final ParseObject saveHW = new ParseObject("fHomework");
                            saveHW.put("coursePtr", getCourse(course));
                            saveHW.put("hwDescription", note);
                            saveHW.put("hwName", "test hw");
                            saveHW.put("userPtr", ParseUser.getCurrentUser());
                            saveHW.put("hwDone", false);
                            saveHW.put("HwDueDate", calendar.getTime());


                            saveHW.saveInBackground(new SaveCallback()
                            {
                                @Override
                                public void done(ParseException e)
                                {
                                    if (e != null)
                                    {
                                        Log.d("Positive fab dialog click", e.getMessage());
                                        currentListOfHWs.add(0, saveHW);
                                    }
                                    else
                                        Log.d("Positive fab dialog click", "saved.");
                                }
                            });


                            // add the HW to the screen.
                            populateScreen(newHW);


                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    };



    View.OnClickListener dateTimeListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

            if( v.getId() == R.id.timeView)
            {

                DialogFragment timePicker = new TimePicker();
                timePicker.show( getFragmentManager(), "Time Picker");

            }
            else if( v.getId() == R.id.dateView)
            {
                DialogFragment datePicker = new DatePicker();
                datePicker.show( getFragmentManager(), "datePicker");
            }
        }
    };




    public static class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener
    {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog( getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat( getActivity()));
        }

        @Override
        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
        {
            timeView.setText( hourOfDay + "." + minute);
            calendar.set( Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set( Calendar.MINUTE, minute);
        }
    }



    public static class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog( getActivity(), this, c.get( Calendar.YEAR), c.get( Calendar.MONTH), c.get( Calendar.DAY_OF_MONTH));
        }


        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            dateView.setText( dayOfMonth + "." + monthOfYear + "." + year);
            calendar.set( year, monthOfYear, dayOfMonth);
        }
    }



    private void refreshLeftDrawer()
    {
        ParseQuery<ParseObject> courses = new ParseQuery<ParseObject>("fCourse");
        courses.whereEqualTo("userPtr", ParseUser.getCurrentUser());
        courses.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {

                    parseCourseObjects = parseObjects;

                    //refresh listView

                    int courseCount = parseCourseObjects.size();
                    String[] courseNames = new String[ courseCount];

                    for( int i = 0; i < courseCount; i++)
                        courseNames[i] = parseCourseObjects.get(i).getString( "courseName");

                    Arrays.sort( courseNames);

                    MainActivity.courseNames = courseNames;

                    leftDrawer.setAdapter( new ArrayAdapter<String>(getBaseContext(), R.layout.list_item, courseNames));


                }
            }
        });
    }


    private void populateScreen( HomeWork newHW)
    {

        adapter.addItem(newHW);
        manager.scrollToPosition(0);
    }





    private ParseObject getCourse( String courseName)
    {

        int index = -1;

        for( int i = 0; i < parseCourseObjects.size(); i ++)
        {

            if(parseCourseObjects.get(i).getString("courseName").equals( courseName))
            {
                index = i;
                break;
            }
        }
        return parseCourseObjects.get(index);
    }
}
