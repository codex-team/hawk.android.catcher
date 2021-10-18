package com.hawkcatcher.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.button_catch
import kotlinx.android.synthetic.main.activity_main.button_crash

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_crash.setOnClickListener {
            throw RuntimeException("Example exception")
        }
        button_catch.setOnClickListener {
            try {
                1 / 0
            } catch (e: Throwable) {
                HawkApplication.hawkExceptionCatcher.caught(e)
            }
        }
    }
}