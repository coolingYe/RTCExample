package com.zeewain.api.example;

import com.zeewain.rtc.RtcFactory;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RtcFactory.initialize(getApplicationContext());
    }
}
