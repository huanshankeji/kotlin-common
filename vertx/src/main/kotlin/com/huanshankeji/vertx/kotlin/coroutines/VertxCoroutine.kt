package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlin.use
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Execute the [block] code and close the [Vertx] instance like [kotlin.use] on an [AutoCloseable].
 */
suspend inline fun <R> Vertx.use(block: (Vertx) -> R): R =
    @Suppress("MoveLambdaOutsideParentheses")
    use(block, { close().await() })

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
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("This implementation is buggy. See https://github.com/vert-x3/vertx-lang-kotlin/pull/222/commits/fc3c5c5cc0c572eaddb3c2c37d07c696f75b4443#diff-162b76dc534138518a237d9a8ed527f1b3ecaca67385ea7d4357b6eff203f699R138-R217 for a fixed proposed version.")
suspend fun <T> Vertx.awaitSuspendExecuteBlocking(blockingCode: suspend () -> T): T =
    coroutineScope {
        executeBlocking<T> {
            launch { it.complete(blockingCode()) }
        }.await()
    }

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
 * @see CompositeFuture.all
 * @see kotlinx.coroutines.awaitAll
 */
suspend fun <T> List<Future<T>>.awaitAll(): List<T> =
    CompositeFuture.all(this).await().list()
