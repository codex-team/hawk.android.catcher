package com.hawkcatcher.akscorp.hawkcatcher;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static java.lang.Thread.sleep;

/**
 * Created by AksCorp on 22.10.2017.
 */

public class HawkExceptionCatcher implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler oldHandler;

    private boolean isActive = false;
    private String HAWK_TOKEN = "";

    private boolean isPostStackEnable = true;

    Context context;

    /**
     * @param token - hawk initialization project token
     */
    public HawkExceptionCatcher(Context context, String token) {
        HAWK_TOKEN = token;
        this.context = context;
    }

    /**
     * Set enable stack trace post
     *
     * @param isEnable
     */
    public void setEnableStackPost(boolean isEnable)
    {
        isPostStackEnable = isEnable;
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
        startExceptionPostService(formingJsonExceptionInfo(throwable).toString());
        oldHandler.uncaughtException(thread, throwable);
    }

    /**
     * Post any exception to server
     *
     * @param throwable
     */
    public void log(Throwable throwable)
    {
        startExceptionPostService(formingJsonExceptionInfo(throwable).toString());
    }

    /**
     * Forming stack trace
     *
     * @param throwable
     * @return
     */
    private String getStackTrace(Throwable throwable)
    {
        if(!isPostStackEnable)
            return "none";
        String result = "";
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for(int i=0;i<stackTraceElements.length;i++)
            result += stackTraceElements[i].toString() + "\n";
        return result;
    }

    /**
     * Create json with exception and device information
     *
     * @param throwable
     * @return
     */
    private JSONObject formingJsonExceptionInfo(Throwable throwable) {
        JSONObject jsonParam = new JSONObject();
        JSONObject deviceInfo = new JSONObject();

        if(throwable.getCause() != null)
            throwable = throwable.getCause();
        try {
            jsonParam.put("token", HAWK_TOKEN);
            jsonParam.put("message", throwable.toString());
            jsonParam.put("stack", getStackTrace(throwable));
            jsonParam.put("language", "Java");

            deviceInfo.put("brand", Build.BRAND);
            deviceInfo.put("device", Build.DEVICE);
            deviceInfo.put("model", Build.MODEL);
            deviceInfo.put("product", Build.PRODUCT);
            deviceInfo.put("SDK", Build.VERSION.SDK_INT);
            deviceInfo.put("release", Build.VERSION.RELEASE);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();

            deviceInfo.put("screenSize", Integer.toString(width) + "x" + Integer.toString(height));

            jsonParam.put("deviceInfo",deviceInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Post json", jsonParam.toString());
        return jsonParam;
    }

    /**
     * Start service with post data
     *
     * @param exceptionInfoJSON
     */
    private void startExceptionPostService(String exceptionInfoJSON) {
        try {
            Bundle extras = new Bundle();
            extras.putString("exceptionInfoJSON", exceptionInfoJSON);

            Intent intent = new Intent(context, PostExceptionService.class);
            intent.putExtras(extras);

            context.startService(intent);
        }
        catch (Exception e)
        {
            Log.e("Hawk catcher", e.toString());
        }
    }
}