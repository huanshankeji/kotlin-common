package com.huanshankeji.kotlinx.coroutine

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.select

suspend fun <T> awaitAny(vararg deferreds: Deferred<T>): T =
    select { deferreds.forEach { it.onAwait { it } } }

suspend fun <T> Collection<Deferred<T>>.awaitAny(): T =
    select { forEach { it.onAwait { it } } }

suspend fun joinAny(vararg jobs: Job): Unit =
    select { jobs.forEach { it.onJoin { } } }

suspend fun Collection<Job>.joinAny(): Unit =
    select { forEach { it.onJoin { } } }
