package com.huanshankeji.kotlinx.coroutine

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.select

suspend fun <T> awaitAny(vararg deferreds: Deferred<T>): T {
    require(deferreds.isNotEmpty())
    return select { deferreds.forEach { it.onAwait { it } } }
}

suspend fun <T> Collection<Deferred<T>>.awaitAny(): T {
    require(isNotEmpty())
    return select { forEach { it.onAwait { it } } }
}

suspend fun <T> Collection<Deferred<T>>.awaitAnyAndCancelOthers(): T {
    require(isNotEmpty())
    val firstAwaited = select { forEachIndexed { index, deferred -> deferred.onAwait { IndexedValue(index, it) } } }
    val firstAwaitedIndex = firstAwaited.index
    forEachIndexed { index, deferred -> if (index != firstAwaitedIndex) deferred.cancel() }
    return firstAwaited.value
}

suspend fun joinAny(vararg jobs: Job) {
    require(jobs.isNotEmpty())
    select { jobs.forEach { it.onJoin { } } }
}

suspend fun Collection<Job>.joinAny() {
    require(isNotEmpty())
    select { forEach { it.onJoin { } } }
}
