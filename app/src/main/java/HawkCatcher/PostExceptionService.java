package HawkCatcher;

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

    private final String EXCEPTION_POST_URL = "http://10.0.2.2:3000/catcher/javaAndroid";
    private String token = "";

    /**
     * Create json with exception and device information
     *
     * @param throwable
     * @return
     */
    private JSONObject formingJsonExceptionInfo(Throwable throwable) {
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("token", token);
            jsonParam.put("message", throwable.fillInStackTrace());
            jsonParam.put("errorLocation", throwable.getLocalizedMessage());
            jsonParam.put("stack", throwable.getStackTrace());

            jsonParam.put("brand", Build.BRAND);
            jsonParam.put("device", Build.DEVICE);
            jsonParam.put("model", Build.MODEL);
            jsonParam.put("product", Build.PRODUCT);
            jsonParam.put("SDK", Build.VERSION.SDK);
            jsonParam.put("release", Build.VERSION.RELEASE);
            jsonParam.put("incremental", Build.VERSION.INCREMENTAL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParam;
    }

    /**
     * Post exception information to hawk server
     *
     * @param throwable
     */
    private void postException(final Throwable throwable) {
        try {
            URL url = new URL(EXCEPTION_POST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(formingJsonExceptionInfo(throwable).toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());

            conn.disconnect();
        } catch (Exception e) {
        }
    }

    /**
     * Constructor with class name
     */
    public PostExceptionService() {
        super("PostExceptionService");
    }

    /**
     * Get post data and start post data to hawk server
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Throwable exception = (Throwable) extras.getSerializable("exception");
        token = extras.getString("token");
        postException(exception);
    }
}
