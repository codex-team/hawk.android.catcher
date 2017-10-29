package com.hawkandroidcatcher.akscorp.hawkandroidcatcher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

import static java.lang.Thread.sleep;

/**
 * Created by AksCorp on 22.10.2017.
 */

public class HawkExceptionCatcher implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler oldHandler;

    private boolean isActive = false;
    private String EXCEPTION_POST_URL = "http://10.0.2.2:3000/catcher/javaAndroid",
            HAWK_TOKEN = "";

    Context context;
    private static Thread postThread;

    /**
     * @param token - hawk initialization project token
     */
    public HawkExceptionCatcher(Context context, String token) {
        HAWK_TOKEN = token;
        this.context = context;
    }

    /**
     * Start listen uncaught exceptions
     *
     * @throws Exception
     */
    public void start() throws Exception {
        try {
            oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        } catch (Exception e) {
            throw new Exception("HawkExceptionCatcher start error. " + e.toString());
        }
        Thread.setDefaultUncaughtExceptionHandler(this);
        isActive = true;
    }

    /**
     * Stop listen uncaught exceptions and set uncaught exception handler by default
     *
     * @throws Exception
     */
    public void finish() throws Exception {
        if (!isActive())
            throw new Exception("HawkExceptionCatcher not running");
        isActive = false;
        Thread.setDefaultUncaughtExceptionHandler(oldHandler);
    }

    /**
     * Return current catcher status
     *
     * @return
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Action when exception catch
     *
     * @param thread
     * @param throwable
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        startExceptionPostService(throwable);
        oldHandler.uncaughtException(thread, throwable);
    }

    /**
     * Start service with post data
     *
     * @param throwable
     */
    private void startExceptionPostService(Throwable throwable) {
        Bundle extras = new Bundle();
        extras.putSerializable("exception", (Serializable) throwable);
        extras.putString("token", HAWK_TOKEN);

        Intent intent = new Intent(context, PostExceptionService.class);
        intent.putExtras(extras);

        context.startService(intent);
    }
}