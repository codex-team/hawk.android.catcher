package com.hawkcatcher.android

import android.content.Context
import android.util.Log
import com.hawkcatcher.android.addons.Addon
import com.hawkcatcher.android.addons.DeviceSpecificAddon
import com.hawkcatcher.android.addons.UserAddon
import com.hawkcatcher.android.addons.UserAddonWrapper
import com.hawkcatcher.android.configurations.HawkConfigurations
import com.hawkcatcher.android.json.JSONStringer
import com.hawkcatcher.android.network.HawkClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Main class of Hawk Catcher.
 */
class HawkExceptionCatcher(context: Context) :
    Thread.UncaughtExceptionHandler {

    companion object {
        private val CATCHER_TYPE = "errors/android"
    }

    /**
     * Old handler that will setup like as default in application
     */
    private var oldHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * Return current catcher status
     *
     * @return
     */
    var isActive = false
        private set

    /**
     * Meta data provider for getting information
     */
    private val metaDataProvider = MetaDataProvider(context)

    /**
     * Contains common configuration for running Hawk Catcher
     */
    private val configuration: HawkConfigurations = HawkConfigurations(
        metaDataProvider.getToken(),
        listOf(DeviceSpecificAddon(DeviceInfo(context)))
    )

    /**
     * Client for sending events
     */
    private var client: HawkClient? = null

    /**
     * Start listen uncaught exceptions
     *
     */
    fun start() {
        if (!configuration.isCorrect) {
            Log.e("Hawk", "Cannot start HawkCatcher with incorrect token. Cant get integrationId")
            return
        }
        isActive = true
        client = HawkClient(configuration.integrationId)
        oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    fun addUserAddon(userAddon: UserAddon) {
        configuration.addUserAddon(userAddon)
    }

    fun removeUserAddon(userAddon: UserAddon) {
        configuration.removeUserAddon(userAddon)
    }

    fun removeUserAddonByName(name: String) {
        configuration.removeUserAddon(name)
    }

    /**
     * Stop listen uncaught exceptions and set uncaught exception handler by default
     */
    fun finish() {
        if (!isActive) {
            Log.w("Hawk", "HawkExceptionCatcher not running")
        }
        isActive = false
        Thread.setDefaultUncaughtExceptionHandler(oldHandler)
    }

    /**
     * Action when exception catch
     *
     * @param thread
     * @param throwable
     */
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val uncaughtExceptionJson = formingJsonExceptionInfo(throwable)

        startExceptionPostService(uncaughtExceptionJson)
        oldHandler?.uncaughtException(thread, throwable)
    }

    /**
     * Post any exception to server
     *
     * @param throwable
     */
    fun caught(throwable: Throwable) {
        startExceptionPostService(formingJsonExceptionInfo(throwable, isFatal = false))
    }

    /**
     * Post any exception to server
     *
     * @param throwable
     * @param map
     */
    fun caught(throwable: Throwable, map: Map<String, Any>) {
        startExceptionPostService(
            formingJsonExceptionInfo(
                throwable, isFatal = false,
                object : UserAddon {
                    override val name: String
                        get() = "customData"

                    override fun provideData(): Map<String, Any> = map
                }
            )
        )
    }

    /**
     * Post any exception to server
     *
     * @param throwable
     * @param customUserAddon
     */
    fun caught(throwable: Throwable, customUserAddon: UserAddon) {
        startExceptionPostService(
            formingJsonExceptionInfo(
                throwable,
                isFatal = false,
                customUserAddon
            )
        )
    }

    /**
     * Forming stack trace
     *
     * @param throwable
     * @return
     */
    private fun getStackTrace(throwable: Throwable?): JSONArray {
        val jsonBacktrace = JSONArray()
        val stackTraceElements = throwable!!.stackTrace
        stackTraceElements.map(::convertStackTraceElementToJson)
            .forEach(jsonBacktrace::put)
        return jsonBacktrace
    }

    /**
     * Forming stack trace information
     *
     * @param element stack trace element
     * @return json object with information of stack trace element
     */
    private fun convertStackTraceElementToJson(element: StackTraceElement): JSONObject {
        val jsonStackTraceElement = JSONObject()
        jsonStackTraceElement.put("file", element.className)
        jsonStackTraceElement.put("line", element.lineNumber)
        jsonStackTraceElement.put("column", null)
        jsonStackTraceElement.put("function", element.methodName)
        return jsonStackTraceElement
    }

    /**
     * Forming payload information of event
     *
     * @param throwable [Throwable]
     * @param isFatal Flag if throwable if fatal
     * @param userAddon Additional external information
     */
    private fun payload(
        throwable: Throwable,
        isFatal: Boolean = true,
        userAddon: Addon? = null
    ): JSONObject {
        val versionName = metaDataProvider.getVersionName()
        val appVersion = metaDataProvider.getAppVersion()
        val title = throwable.message
        val type = throwable::class.java.simpleName
        val backtrace = getStackTrace(throwable)
        val release = "$versionName($appVersion)"
        val addons = JSONObject().also { addonsObject ->
            configuration.addons.forEach { addon ->
                addon.fillJsonObject(addonsObject)
            }
        }
        val context = JSONObject().also { contextObject ->
            configuration.userAddons.forEach { addon ->
                addon.fillJsonObject(contextObject)
            }
            userAddon?.fillJsonObject(contextObject)
        }
        return JSONObject().apply {
            put("title", title)
            put("type", type)
            put("backtrace", backtrace)
            put("release", release)
            put("addons", addons)
            put("context", context)
        }
    }

    /**
     * Create json with exception and device information
     *
     * @param throwable
     * @param isFatal Flag if throwable if fatal
     * @param externalUserAddon Additional external information
     * @return
     */
    private fun formingJsonExceptionInfo(
        throwable: Throwable,
        isFatal: Boolean = true,
        externalUserAddon: UserAddon? = null
    ): JSONObject {
        val causeThrowable = throwable.cause ?: throwable
        val reportJson = JSONObject()
        try {
            reportJson.put("token", metaDataProvider.getToken())
            reportJson.put("catcherType", CATCHER_TYPE)
            reportJson.put(
                "payload",
                payload(causeThrowable, userAddon = externalUserAddon.wrapIfNotNull())
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.d("Hawk", "Post json = $reportJson")
        return reportJson
    }

    fun UserAddon?.wrapIfNotNull(): Addon? {
        return if (this != null) {
            UserAddonWrapper(this)
        } else {
            null
        }
    }

    /**
     * Start service with post data
     *
     * @param exceptionInfoJSON
     * @param withWaiting
     */
    private fun startExceptionPostService(
        exceptionInfoJSON: JSONObject,
        withWaiting: Boolean = true
    ) {
        val hawkClient = client
        if (hawkClient == null) {
            Log.e("Hawk", "Cant send event without correct a client")
            return
        }
        val json = JSONStringer()
        json.writeObject(exceptionInfoJSON)
        PostExecutionService(hawkClient)
            .postEvent(json.toString())
        if (withWaiting) {
            hawkClient.await()
        }
    }
}