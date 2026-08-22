package com.honzens.hzpodcast;

import android.app.Application;

import com.honzens.hzpodcast.global_params;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        global_params.initialize();//[2025-7-11]
    }
}
