package com.hawkcatcher.android

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Work with <meta-data> and package information
 */
internal class MetaDataProvider(context: Context) {
    companion object {
        /**
         * Key for getting token from <meta-data>
         */
        private const val HAWK_CATCHER_TOKEN_KEY = "hawk_catcher_token"

        /**
         * Default value if token not found in <meta-data>
         */
        const val UNKNOWN_TOKEN = ""
    }

    /**
     * Application information provides access to <meta-data>
     */
    private val _appInfo = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    )

    /**
     * Package information provides access to version name and version code
     */
    private val _packageInfo = context.packageManager.getPackageInfo(
        context.packageName,
        0
    )

    /**
     * Hawk token from <meta-data>
     */
    private val _token = _appInfo.metaData?.getString(HAWK_CATCHER_TOKEN_KEY) ?: UNKNOWN_TOKEN

    /**
     * Version name of application
     */
    private val _versionName: String = _packageInfo.versionName

    /**
     * App version
     */
    private val _appVersion: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        _packageInfo.longVersionCode.toInt()
    } else {
        _packageInfo.versionCode
    }

    /**
     * Get version name of application
     * @return version name
     */
    fun getVersionName(): String = _versionName

    /**
     * Get application version code
     * @return version of application
     */
    fun getAppVersion(): Int = _appVersion

    /**
     * Get Hawk token
     * @return Hawk token
     */
    fun getToken(): String = _token
}