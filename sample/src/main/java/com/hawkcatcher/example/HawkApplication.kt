package com.hawkcatcher.example

import android.app.Application
import com.hawkcatcher.android.HawkExceptionCatcher

class HawkApplication : Application() {
    companion object {
        lateinit var hawkExceptionCatcher: HawkExceptionCatcher
        lateinit var userManager: SimpleUserManager
    }

    override fun onCreate() {
        super.onCreate()
        userManager = SimpleUserManager()
        hawkExceptionCatcher = HawkExceptionCatcher(this)
        hawkExceptionCatcher.start()
        hawkExceptionCatcher.addUserAddon(UserInfoAddon(userManager))
    }
}