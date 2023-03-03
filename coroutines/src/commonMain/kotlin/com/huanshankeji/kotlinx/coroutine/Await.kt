package com.huanshankeji.kotlinx.coroutine

import com.huanshankeji.Untested
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.select

@Untested
suspend fun <T> awaitAny(vararg deferreds: Deferred<T>): T =
    select { deferreds.forEach { it.onAwait { it } } }

@Untested
suspend fun <T> Collection<Deferred<T>>.awaitAny(): T =
    select { forEach { it.onAwait { it } } }

@Untested
suspend fun joinAny(vararg jobs: Job): Unit =
    select { jobs.forEach { it.onJoin { it } } }

@Untested
suspend fun Collection<Job>.joinAny(): Unit =
    select { forEach { it.onJoin { it } } }
