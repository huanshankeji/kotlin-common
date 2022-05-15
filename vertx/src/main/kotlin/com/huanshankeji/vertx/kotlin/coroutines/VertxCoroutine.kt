package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlin.use
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend inline fun <R> Vertx.use(block: (Vertx) -> R): R =
    use(block) { close().await() }

suspend fun <T> Vertx.awaitExecuteBlocking(blockingCode: () -> T): T =
    executeBlocking<T> {
        it.complete(blockingCode())
    }.await()

suspend fun <T> Vertx.awaitSuspendExecuteBlocking(blockingCode: suspend () -> T): T =
    coroutineScope {
        executeBlocking<T> {
            launch { it.complete(blockingCode()) }
        }.await()
    }
