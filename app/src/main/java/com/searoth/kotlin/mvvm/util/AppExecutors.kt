package com.searoth.kotlin.mvvm.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by cr on 2/26/2018.
 */
const val THREAD_COUNT = 3

/**
 * Global executor pools for the whole app
 *
 * Grouping tasks like this avoids the effects of task starvation. (e.g. disk reads dont wait behind web service requests)
*/

open class AppExecutors constructor(
    val diskIO: Executor = DiskIOThreadExecutor(),
    val networkIO: Executor = Executors.newFixedThreadPool(THREAD_COUNT),
    val mainThread: Executor = MainThreadExecutor()
){
    private class MainThreadExecutor : Executor{
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable){
            mainThreadHandler.post(command)
        }
    }
}