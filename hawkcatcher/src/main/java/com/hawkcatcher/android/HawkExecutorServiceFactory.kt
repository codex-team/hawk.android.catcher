package com.hawkcatcher.android

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * Factory for creating services. Used for sending event to server
 */
class HawkExecutorServiceFactory {
    companion object {
        /**
         * Maximum of threads
         */
        private const val THREADS = 1

        /**
         * Counter of threads
         */
        private var threadCount = AtomicInteger(0)

        /**
         * Thread name
         */
        private const val THREAD_NAME = "Hawk-Catcher-"

        /**
         * Create new fixed thread pool
         */
        val create: ExecutorService = Executors.newFixedThreadPool(THREADS) { r ->
            Thread(
                r,
                THREAD_NAME + threadCount.getAndIncrement()
            )
        }
    }
}