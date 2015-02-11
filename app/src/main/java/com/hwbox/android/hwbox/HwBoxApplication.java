package com.hwbox.android.hwbox;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by omer on 11.02.2015.
 */
public class HwBoxApplication extends Application
{


    @Override
    public void onCreate()
    {
        super.onCreate();

        Parse.enableLocalDatastore( this);



        Parse.initialize(this, "cf86jsjcwXfhr8OkIHl0Viizqtyh3XdfMmRc0Fmr", "XM6bc48H0f8r7dlqpecsHjfEnyIRDLyEDEqnnoEV");




    }
}

