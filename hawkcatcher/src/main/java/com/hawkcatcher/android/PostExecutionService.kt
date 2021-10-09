package com.hawkcatcher.android

import com.hawkcatcher.android.json.JSONStringer
import com.hawkcatcher.android.network.HawkClient
import org.json.JSONObject
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future

/**
 * Post exception information to hawk server
 * @param client
 */
class PostExecutionService(private val client: HawkClient){

    /**
     * Post exception information to hawk server
     *
     * @param exceptionInfoString
     */
     fun postEvent(exceptionInfoString: String): Future<*> {
        return HawkExecutorServiceFactory.create
            .submit {
                client.sendEvent(exceptionInfoString)
            }
    }
}
