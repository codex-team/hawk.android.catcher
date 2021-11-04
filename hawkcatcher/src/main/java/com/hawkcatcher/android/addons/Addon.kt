package com.hawkcatcher.android.addons

import org.json.JSONObject

/**
 * Interface that can apply own information to event
 */
interface Addon {
    /**
     * Apply data to json event
     * @param jsonObject object that need put inside data
     */
    fun fillJsonObject(jsonObject: JSONObject)
}