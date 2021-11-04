package com.hawkcatcher.android

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.annotation.IntegerRes

/**
 * Class that know about device information
 * @param context required to get [WindowManager]
 */
class DeviceInfo(context: Context) {
    /**
     * [WindowManager] contains information about screen size and other device specific information
     */
    private val _windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    /**
     * Provide screen size in format WIDTHxHEIGHT
     * @return screen size like as string
     * @sample 360x640
     */
    fun getScreenSize(): String {
        val (width, height) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = _windowManager.currentWindowMetrics
            windowMetrics.bounds.width() to windowMetrics.bounds.height()
        } else {
            val display = _windowManager.defaultDisplay
            display.width to display.height
        }
        return "${width}x$height"
    }
}