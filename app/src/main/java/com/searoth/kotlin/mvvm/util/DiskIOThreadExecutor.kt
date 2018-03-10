package com.searoth.kotlin.mvvm.util

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by cr on 2/26/2018.
 */
class DiskIOThreadExecutor : Executor {
    private val diskIO = Executors.newSingleThreadExecutor()

    override fun execute(command: Runnable) { diskIO.execute(command) }
}