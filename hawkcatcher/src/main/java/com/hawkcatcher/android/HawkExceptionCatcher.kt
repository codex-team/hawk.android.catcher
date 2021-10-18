package com.hawkcatcher.android

import android.content.Context
import android.util.Log
import com.hawkcatcher.android.addons.DeviceSpecificAddon
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

    private var oldHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * Return current catcher status
     *
     * @return
     */
    var isActive = false
        private set

    /**
     *
     */
    private val metaDataProvider = MetaDataProvider(context)

    /**
     * Contains common configuration for running Hawk Catcher
     */
    private val configuration: HawkConfigurations = HawkConfigurations(
        metaDataProvider.getToken(),
        listOf(DeviceSpecificAddon(DeviceInfo(context)))
    )

    private var client: HawkClient? = null

    /**
     * @param token - hawk initialization project token
     */

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

    private fun convertStackTraceElementToJson(element: StackTraceElement): JSONObject {
        val jsonStackTraceElement = JSONObject()
        jsonStackTraceElement.put("file", element.className)
        jsonStackTraceElement.put("line", element.lineNumber)
        jsonStackTraceElement.put("column", null)
        jsonStackTraceElement.put("function", element.methodName)
        return jsonStackTraceElement
    }

    private fun payload(throwable: Throwable, isFatal: Boolean = true): JSONObject {
        val versionName = metaDataProvider.getVersionName()
        val appVersion = metaDataProvider.getAppVersion()
        val title = throwable.message
        val type = throwable::class.java.simpleName
        val backtrace = getStackTrace(throwable)
        val release = "$versionName($appVersion)"
        val addons = JSONObject().also { jsonObject ->
            configuration.addons.forEach { addon ->
                addon.fillJsonObject(jsonObject)
            }
        }
        return JSONObject().apply {
            put("title", title)
            put("type", type)
            put("backtrace", backtrace)
            put("release", release)
            put("addons", addons)
        }
    }

    /**
     * Create json with exception and device information
     *
     * @param throwable
     * @return
     */
    private fun formingJsonExceptionInfo(
        throwable: Throwable,
        isFatal: Boolean = true
    ): JSONObject {
        val causeThrowable = throwable.cause ?: throwable
        val reportJson = JSONObject()
        try {
            reportJson.put("token", metaDataProvider.getToken())
            reportJson.put("catcherType", CATCHER_TYPE)
            reportJson.put("payload", payload(causeThrowable))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.d("Post json", reportJson.toString())
        return reportJson
    }

    /**
     * Start service with post data
     *
     * @param exceptionInfoJSON
     */
    private fun startExceptionPostService(exceptionInfoJSON: JSONObject) {
        Log.d("Hawk", "startExceptionPostService ${System.currentTimeMillis()}")
        val hawkClient = client
        if (hawkClient == null) {
            Log.e("Hawk", "Cant send event without correct a client")
            return
        }
        val json = JSONStringer()
        json.writeObject(exceptionInfoJSON)
        Log.d("Hawk", "startExceptionPostService try post event at ${System.currentTimeMillis()}")
        PostExecutionService(hawkClient)
            .postEvent(json.toString())

        hawkClient.await()
        Log.d("Hawk", "startExceptionPostService after await at ${System.currentTimeMillis()}")
    }
}