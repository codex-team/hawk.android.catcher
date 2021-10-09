package com.hawkcatcher.android.configurations

import android.util.Base64
import android.util.Log
import com.hawkcatcher.android.addons.Addon
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * Common configuration for stable work with default list of addons that should be apply to sending
 * additional information
 * @param token for getting integrationId
 * @param defaultAddons default list of addons
 */
class HawkConfigurations(
    token: String,
    private val defaultAddons: List<Addon>
) : IConfigurations {

    companion object {
        /**
         * Key for getting integration id from token
         */
        const val INTEGRATION_ID_KEY = "integrationId"

        /**
         * Default value for unknown token
         */
        const val UNKNOWN_INTEGRATION_ID = "unknown"
    }

    /**
     * Contains integrationId
     */
    private val _integrationId: String

    /**
     * Default list of addons
     */
    private val additionalAddons: List<Addon> = mutableListOf()

    init {
        _integrationId = try {
            val decodedTokenJson = Base64.decode(token, Base64.DEFAULT)
                .toString(Charset.defaultCharset())
                .let { JSONObject(it) }
            decodedTokenJson.getString(INTEGRATION_ID_KEY)
        } catch (e: Exception) {
            Log.w("Hawk", "cannot initialize configuration. Wrong token")
            UNKNOWN_INTEGRATION_ID
        }
    }

    /**
     * Get integrationId
     */
    override val integrationId: String
        get() = _integrationId

    /**
     * Check if integrationId is valid
     */
    override val isCorrect: Boolean
        get() = _integrationId != UNKNOWN_INTEGRATION_ID

    /**
     * Get all list of addons. Contains default and additional list of addons
     */
    override val addons: List<Addon>
        get() = defaultAddons + additionalAddons
}