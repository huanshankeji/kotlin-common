package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlin.use
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Execute the [block] code and close the [Vertx] instance like [kotlin.use] on an [AutoCloseable].
 */
suspend inline fun <R> Vertx.use(block: (Vertx) -> R): R =
    use(block) { close().await() }

/**
 * Execute [blockingCode] that returns the a [T] instance with [Vertx.executeBlocking]
 * and awaits its completion.
 *
 * Compared to [Vertx.executeBlocking]'s `blockingCodeHandler` argument,
 * [blockingCode] returns the result when the operation completes instead of calling [Promise.complete].
 */
suspend fun <T> Vertx.awaitExecuteBlocking(blockingCode: () -> T): T =
    executeBlocking<T> {
        it.complete(blockingCode())
    }.await()

/**
 * Like [awaitExecuteBlocking] but [blockingCode] is a suspend function.
 */
suspend fun <T> Vertx.awaitSuspendExecuteBlocking(blockingCode: suspend () -> T): T =
    coroutineScope {
        executeBlocking<T> {
            launch { it.complete(blockingCode()) }
        }.await()
    }
