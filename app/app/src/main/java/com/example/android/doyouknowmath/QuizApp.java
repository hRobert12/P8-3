package com.example.android.doyouknowmath;

import android.app.Application;
import android.content.Context;

/**
 * Created by Robert on 7/9/2017.
 */

public class QuizApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
