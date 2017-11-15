package com.hawkcatcher.akscorp.hawkcatcher;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AksCorp on 29.10.2017.
 */

public class PostExceptionService extends IntentService {

    private final String EXCEPTION_POST_URL = "https://hawk.so/catcher/android";
    private String exceptionInfoJSON = "";
    /**
     * Constructor with class name
     */
    public PostExceptionService() {
        super("PostExceptionService");
    }

    /**
     * Post exception information to hawk server
     *
     * @param exceptionInfoJSON
     */
    private void postException(final String exceptionInfoJSON) {
        try {
            URL url = new URL(EXCEPTION_POST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(exceptionInfoJSON);

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());

            conn.disconnect();
        } catch (Exception e) {
            Log.e("Service info", e.toString());
        }
    }

    /**
     * Get post data and start post data to hawk server
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        exceptionInfoJSON = extras.getString("exceptionInfoJSON");
        postException(exceptionInfoJSON);
    }
}
