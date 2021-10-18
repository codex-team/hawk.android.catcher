package com.hawkcatcher.example

import android.app.Application
import com.hawkcatcher.android.HawkExceptionCatcher

class HawkApplication : Application() {
    companion object {
        lateinit var hawkExceptionCatcher: HawkExceptionCatcher
    }

    override fun onCreate() {
        super.onCreate()
        hawkExceptionCatcher = HawkExceptionCatcher(this)
        hawkExceptionCatcher.start()
    }
}