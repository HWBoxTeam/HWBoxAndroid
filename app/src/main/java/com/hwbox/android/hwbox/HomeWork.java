package com.hwbox.android.hwbox;

import java.util.Calendar;

/**
 * Created by omer on 18.12.2014.
 */
public class HomeWork
{
    private String title;
    private String note;
    private String course;
    private Calendar deadLine;


    public HomeWork( String title)
    {
        this.title = title;
    }

    public HomeWork( String title , Calendar calendar, String note, String course)
    {
        this.note = note;
        this.course = course;
        this.title = title;
        this.deadLine = calendar;
    }



    public void setNote(String note) {
        this.note = note;
    }

    public Calendar getDeadLine() {
        return deadLine;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setDeadLine(Calendar deadLine) {
        this.deadLine = deadLine;
    }



    public String getNote()
    {
        return note;
    }

    public String getTitle() {
        return title;
    }

    public String getCourse()
    {
        return course;
    }
}
