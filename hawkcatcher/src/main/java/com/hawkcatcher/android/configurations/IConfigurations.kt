package com.hawkcatcher.android.configurations

import com.hawkcatcher.android.addons.Addon

/**
 * Provide information of configuration
 */
interface IConfigurations {
    /**
     * Integration id that need to configure URL for sending event
     */
    val integrationId: String

    /**
     * Check if [integrationId] is valid
     */
    val isCorrect: Boolean

    /**
     * Provide list of addons that apply to event before sending
     */
    val addons: List<Addon>
}