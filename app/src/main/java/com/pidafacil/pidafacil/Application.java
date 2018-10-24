package com.pidafacil.pidafacil;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by victor on 07-15-15.
 * Para evitar excepciones de tipo Dex
 */
public class Application extends MultiDexApplication {
    private Tracker mtracker;

    public synchronized Tracker getDefaultTracker(){
        if(mtracker == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mtracker = analytics.newTracker(R.xml.tracker);
        }
        return mtracker;
    }
}