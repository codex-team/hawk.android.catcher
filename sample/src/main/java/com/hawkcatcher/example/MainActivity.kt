package com.hawkcatcher.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.btn_save_name
import kotlinx.android.synthetic.main.activity_main.btn_send_with_custom_field
import kotlinx.android.synthetic.main.activity_main.button_catch
import kotlinx.android.synthetic.main.activity_main.button_crash
import kotlinx.android.synthetic.main.activity_main.et_save_custom_field
import kotlinx.android.synthetic.main.activity_main.et_save_name

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

        btn_save_name.setOnClickListener {
            HawkApplication.userManager.name = et_save_name.text.toString()
            Toast.makeText(baseContext, "Name is saved!", Toast.LENGTH_LONG).show()
        }

        btn_send_with_custom_field.setOnClickListener {
            HawkApplication.hawkExceptionCatcher.caught(
                RuntimeException("Sample exception"),
                mapOf("custom_field" to et_save_custom_field.text.toString())
            )
        }
    }
}