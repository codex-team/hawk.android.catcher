package com.hawkcatcher.android.addons

import android.util.Log
import org.json.JSONObject

/**
 * Wrapper for converting from [UserAddon] to [Addon]
 */
class UserAddonWrapper(private val userAddon: UserAddon) : Addon {
    /**
     * Fill json object of information from map
     * @param jsonObject Json object that put all information
     */
    override fun fillJsonObject(jsonObject: JSONObject) {
        jsonObject.put(userAddon.name, handleMap(userAddon.provideData()))
    }

    /**
     * Handler for [Map]
     *
     * @param map
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    private fun handleMap(map: Map<String, *>): JSONObject {
        return JSONObject().apply {
            map.map { (key, value) ->
                if (value is Map<*, *>) {
                    value.keys.let {
                        if (it.isNotEmpty() && it.first() !is String) {
                            Log.e("Hawk", "Cannot handle map. Key is not String")
                            return@let
                        }
                        put(key, handleMap(value as Map<String, *>))
                    }
                } else {
                    put(key, value)
                }
            }
        }
    }
}