package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.vertx.ext.web.checkedRun
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.DefaultOnVertxEventLoop
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.Unconfined
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private fun coroutineHandler(
    coroutineScope: CoroutineScope, route: Route,
    context: CoroutineContext, start: CoroutineStart,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    route.handler { coroutineScope.launch(context, start) { requestHandler(it) } }

/**
 * @see CoroutineContext
 * @see Dispatchers
 * @see CoroutineStart
 */
enum class CoroutineHandlerLaunchMode {
    /**
     * In this mode, the coroutine is launched in a [CoroutineScope] probably with a [CoroutineDispatcher]
     * (such as a [CoroutineVerticle])
     * with the arguments [EmptyCoroutineContext] and [CoroutineStart.UNDISPATCHED].
     * After calling a suspending function that doesn't resume on the Vert.x event loop by default
     * (especially and mainly non-Vert.x functions, such as [delay]),
     * the coroutine is redispatched to the Vert.x event loop.
     */
    DefaultOnVertxEventLoop,

    /**
     * In this mode, the coroutine is launched with the argument [Dispatchers.Unconfined].
     * After calling a suspending function that doesn't resume on the Vert.x event loop by default,
     * the coroutine is not redispatched to the Vert.x event loop.
     *
     * It reduces the context (thread) switching overhead and provides slightly better performance in cases when handler code is light.
     * It's recommend when all the suspending functions called in the handler are Vert.x functions.
     *
     * As tested with the vertx-web-kotlinx portion of TechEmpower Framework Benchmarks, [Dispatchers.Unconfined] greatly improves the performance of the Plaintext test.
     * See <https://github.com/huanshankeji/FrameworkBenchmarks/commit/030ce1b14d1838784c1ccd2fd94f14446f693b3f> for more details.
     */
    Unconfined
}

// workaround for context receivers
fun coroutineHandler(
    coroutineScope: CoroutineScope, route: Route,
    launchMode: CoroutineHandlerLaunchMode = DefaultOnVertxEventLoop,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    coroutineHandler(
        coroutineScope, route,
        when (launchMode) {
            DefaultOnVertxEventLoop -> EmptyCoroutineContext
            Unconfined -> Dispatchers.Unconfined
        },
        CoroutineStart.UNDISPATCHED, requestHandler
    )

// workaround for context receivers
fun checkedCoroutineHandler(
    coroutineScope: CoroutineScope, route: Route,
    launchMode: CoroutineHandlerLaunchMode = DefaultOnVertxEventLoop,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    coroutineHandler(coroutineScope, route, launchMode) { ctx -> ctx.checkedRun { requestHandler(ctx) } }
