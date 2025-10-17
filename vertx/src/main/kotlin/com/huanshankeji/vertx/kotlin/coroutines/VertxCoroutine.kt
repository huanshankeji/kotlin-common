package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlin.use
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
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
 */
suspend fun <T> Vertx.awaitExecuteBlocking(blockingCode: () -> T): T =
    executeBlocking(blockingCode).coAwait()

// This should be removed.
/**
 * Like [awaitExecuteBlocking] but [blockingCode] is a suspend function.
 */
@Deprecated("This API is deprecated for removal. " +
        "See https://github.com/vert-x3/wiki/wiki/4.4.5-Deprecations-and-breaking-changes#deprecation-of-execute-blocking-methods-with-a-handler-of-promise " +
        "and https://vertx.io/docs/guides/vertx-5-migration-guide/#_removal_of_execute_blocking_methods_with_a_handler_of_promise . " +
        "Please rewrite your code to pass only the blocking parts to `awaitExecuteBlocking`. " +
        "Also, this implementation is buggy. See https://github.com/vert-x3/vertx-lang-kotlin/pull/222/commits/fc3c5c5cc0c572eaddb3c2c37d07c696f75b4443#diff-162b76dc534138518a237d9a8ed527f1b3ecaca67385ea7d4357b6eff203f699R138-R217 for a fixed proposed version.")
suspend fun <T> Vertx.awaitSuspendExecuteBlocking(blockingCode: suspend () -> T): T =
    coroutineScope {
        executeBlocking({
            throw NotImplementedError("Please rewrite your code to pass only the blocking parts to `awaitExecuteBlocking`.")
            //runBlocking { blockingCode() } // This implementation is meaningless.
        }).coAwait()
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
    // Tried refactoring this function using `Future.future` but it seems not feasible.
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
