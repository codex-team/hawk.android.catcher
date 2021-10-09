package com.hawkcatcher.android

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

internal class MetaDataProvider(context: Context) {
    companion object {
        private const val HAWK_CATCHER_TOKEN_KEY = "hawk_catcher_token"
        const val UNKNOWN_TOKEN = ""
    }

    private val _appInfo = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    )

    private val _packageInfo = context.packageManager.getPackageInfo(
        context.packageName,
        0
    )

    private val _token = _appInfo.metaData?.getString(HAWK_CATCHER_TOKEN_KEY) ?: UNKNOWN_TOKEN

    private val _versionName: String = _packageInfo.versionName
    private val _appVersion: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        _packageInfo.longVersionCode.toInt()
    } else {
        _packageInfo.versionCode
    }

    fun getVersionName(): String = _versionName

    fun getAppVersion(): Int = _appVersion

    fun getToken(): String = _token
}