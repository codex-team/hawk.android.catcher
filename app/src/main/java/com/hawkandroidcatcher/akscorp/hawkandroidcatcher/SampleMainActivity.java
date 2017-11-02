package com.hawkandroidcatcher.akscorp.hawkandroidcatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SampleMainActivity extends AppCompatActivity {

    /**
     * Out application logic and etc
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //***
        if(true)
        {
            int error = 1 / 0;
        }
    }
}
