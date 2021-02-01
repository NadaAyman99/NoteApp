package com.example.noteapp.presentation.core;

import android.app.Application;
import android.content.Context;

public class TaskApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
