package com.hawkcatcher.example

import android.app.Application
import com.hawkcatcher.android.HawkExceptionCatcher

class HawkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        HawkExceptionCatcher(this).start()
    }
}