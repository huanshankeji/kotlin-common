package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.vertx.core.CombinedVerticleFunctions
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.awaitResult

interface CoroutineCombinedVerticleFunctions : CombinedVerticleFunctions {
    fun <T> Handler<AsyncResult<T>>.toPromise(): Promise<T> =
        Promise.promise<T>().apply { future().onComplete { this@toPromise.handle(it) } }

    suspend fun subVerticlesStart() =
        awaitResult<Unit> { subVerticlesStartUnit(it.toPromise()) }

    suspend fun subVerticlesStop() =
        awaitResult<Unit> { subVerticlesStopUnit(it.toPromise()) }
}