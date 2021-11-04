package com.hawkcatcher.android.configurations

import android.util.Base64
import android.util.Log
import com.hawkcatcher.android.addons.Addon
import com.hawkcatcher.android.addons.UserAddon
import com.hawkcatcher.android.addons.UserAddonWrapper
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * Common configuration for stable work with default list of addons that should be apply to sending
 * additional information
 * @param token for getting integrationId
 * @param defaultAddons default list of addons
 * @param userAddons list with user custom addons
 */
class HawkConfigurations(
    token: String,
    private val defaultAddons: List<Addon>,
    userAddons: List<UserAddon> = emptyList()
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
    private val _additionalAddons: List<Addon> = mutableListOf()

    /**
     * Map of user addons
     */
    private val _userAddons: MutableMap<String, Addon> =
        userAddons.associateBy(UserAddon::name, ::UserAddonWrapper).toMutableMap()

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
        get() = defaultAddons + _additionalAddons

    /**
     * Get list of user addons. Can contains additional user addons. For add new [UserAddon] use
     * [HawkConfigurations.addUserAddon]
     */
    override val userAddons: List<Addon>
        get() = _userAddons.values.toList()

    /**
     * Add [UserAddon] to map, [UserAddon.name] used as key
     *
     * @param userAddon
     */
    override fun addUserAddon(userAddon: UserAddon) {
        if (_userAddons.containsKey(userAddon.name)) {
            Log.w("Hawk", "User addon with name (${userAddon.name}) already added!")
        }
        _userAddons[userAddon.name] = UserAddonWrapper(userAddon)
    }

    /**
     * Remove [UserAddon] from map, [UserAddon.name] used as key
     *
     * @param userAddon
     */
    override fun removeUserAddon(userAddon: UserAddon) {
        val addon = _userAddons.remove(userAddon.name)
        if (addon == null) {
            Log.w("Hawk", "User addon with name (${userAddon.name}) already removed!")
        }
    }

    /**
     * Remove [UserAddon] from map by name, [UserAddon.name] used as key
     *
     * @param name
     */
    override fun removeUserAddon(name: String) {
        val addon = _userAddons.remove(name)
        if (addon == null) {
            Log.w("Hawk", "User addon with name ($name) already removed!")
        }
    }
}