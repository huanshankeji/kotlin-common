package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.vertx.CALLBACK_MODEL_DEPRECATION_MESSAGE
import com.huanshankeji.vertx.core.CombinedVerticleFunctions
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.awaitResult

interface CoroutineCombinedVerticleFunctions : CombinedVerticleFunctions {
    @Deprecated(CALLBACK_MODEL_DEPRECATION_MESSAGE)
    fun <T> Handler<AsyncResult<T>>.toPromise(): Promise<T> =
        Promise.promise<T>().apply { future().onComplete { this@toPromise.handle(it) } }

    @Deprecated(CALLBACK_MODEL_DEPRECATION_MESSAGE)
    suspend fun subVerticlesStart() =
        awaitResult { subVerticlesStartUnit(it.toPromise()) }

    @Deprecated(CALLBACK_MODEL_DEPRECATION_MESSAGE)
    suspend fun subVerticlesStop() =
        awaitResult { subVerticlesStopUnit(it.toPromise()) }
}