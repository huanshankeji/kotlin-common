package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlin.use
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import java.util.concurrent.Callable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Execute the [block] code and close the [Vertx] instance like [kotlin.use] on an [AutoCloseable].
 */
suspend inline fun <R> Vertx.use(block: (Vertx) -> R): R =
    use(block) { close().coAwait() }

/**
 * Execute [blockingCode] that returns the a [T] instance with [Vertx.executeBlocking]
 * and awaits its completion.
 *
 * Compared to [Vertx.executeBlocking]'s `blockingCodeHandler` argument,
 * [blockingCode] returns the result when the operation completes instead of calling [Promise.complete].
 */
suspend fun <T> Vertx.awaitExecuteBlocking(blockingCode: () -> T): T =
    executeBlocking(Callable {
        blockingCode()
    }).coAwait()



/**
 * Launch a coroutine and converts it into a [Future]
 * that completes when the suspended function returns and fails if it throws.
 */
fun <T> CoroutineScope.coroutineToFuture(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Future<T> {
    val promise = Promise.promise<T>()
    launch(context, start) {
        try {
            promise.complete(block())
        } catch (t: Throwable) {
            promise.fail(t)
        }
    }
    return promise.future()
}

/**
 * Awaits the completion of all the futures in the list without blocking the event loop, and fails as soon as any future fails.
 * @see Future.await
 * @see Future.all
 * @see kotlinx.coroutines.awaitAll
 */
suspend fun <T> List<Future<T>>.awaitAll(): List<T> =
    Future.all<T>(this).coAwait().list()
