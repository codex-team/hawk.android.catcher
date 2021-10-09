package com.hawkcatcher.android.addons

import android.os.Build
import com.hawkcatcher.android.DeviceInfo
import org.json.JSONObject

/**
 * Addon that apply Device Specific information to event
 * @param deviceInfo class that provide information about device
 */
class DeviceSpecificAddon(private val deviceInfo: DeviceInfo): Addon {
    override fun fillJsonObject(jsonObject: JSONObject) {
        jsonObject.put("brand", Build.BRAND)
        jsonObject.put("device", Build.DEVICE)
        jsonObject.put("model", Build.MODEL)
        jsonObject.put("product", Build.PRODUCT)
        jsonObject.put("SDK", Build.VERSION.SDK_INT)
        jsonObject.put("release", Build.VERSION.RELEASE)
        jsonObject.put("screenSize", deviceInfo.getScreenSize())
    }
}