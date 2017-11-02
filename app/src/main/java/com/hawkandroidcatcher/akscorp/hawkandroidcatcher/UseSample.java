package com.hawkandroidcatcher.akscorp.hawkandroidcatcher;

import android.app.Application;

import hawk_catcher.HawkExceptionCatcher;

/**
 * Created by AksCorp on 23.10.2017.
 */

public class UseSample extends Application {

    HawkExceptionCatcher exceptionCatcher;
    public void defineExceptionCather()
    {
        exceptionCatcher = new HawkExceptionCatcher(this,"0927e8cc-f3f0-4ce4-aa27-916f0774af51");
        try {
            exceptionCatcher.start();
            exceptionCatcher.finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        defineExceptionCather();
    }
}
