package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.vertx.ext.web.checkedRun
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.DefaultOnVertxEventLoop
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.Unconfined
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coroutineRouter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * This function can be replaced by [coroutineRouter] and [CoroutineRouterSupport.coHandler]
 * which is newly introduced to the official "vertx-lang-kotlin-coroutines" library.
 * However, note that [CoroutineRouterSupport.coHandler] handles exceptions and is equivalent to [Route.checkedCoroutineHandler].
 * See the [official docs](https://vertx.io/docs/vertx-lang-kotlin-coroutines/kotlin/#_vert_x_web) for more details.
 *
 * This function is not deprecated yet and can still serve its purpose in some scenarios
 * because the approach above is still a bit ugly for the lack of [context parameters](https://github.com/Kotlin/KEEP/issues/367).
 */
fun Route.coroutineHandler(
    coroutineScope: CoroutineScope,
    context: CoroutineContext, start: CoroutineStart,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    handler { coroutineScope.launch(context, start) { requestHandler(it) } }

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
     * See https://github.com/huanshankeji/FrameworkBenchmarks/commit/030ce1b14d1838784c1ccd2fd94f14446f693b3f for more details.
     */
    Unconfined
}

// workaround for context receivers
fun Route.coroutineHandler(
    coroutineScope: CoroutineScope,
    launchMode: CoroutineHandlerLaunchMode = DefaultOnVertxEventLoop,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    coroutineHandler(
        coroutineScope,
        when (launchMode) {
            DefaultOnVertxEventLoop -> EmptyCoroutineContext
            Unconfined -> Dispatchers.Unconfined
        },
        CoroutineStart.UNDISPATCHED, requestHandler
    )

@Deprecated(
    "Use the extension version.",
    ReplaceWith("route.coroutineHandler(coroutineScope, launchMode, requestHandler)")
)
fun coroutineHandler(
    coroutineScope: CoroutineScope, route: Route,
    launchMode: CoroutineHandlerLaunchMode = DefaultOnVertxEventLoop,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    route.coroutineHandler(coroutineScope, launchMode, requestHandler)

/**
 * @see coroutineHandler
 */
// workaround for context receivers
fun Route.checkedCoroutineHandler(
    coroutineScope: CoroutineScope,
    launchMode: CoroutineHandlerLaunchMode = DefaultOnVertxEventLoop,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    coroutineHandler(coroutineScope, launchMode) { ctx -> ctx.checkedRun { requestHandler(ctx) } }

@Deprecated(
    "Use the extension version.",
    ReplaceWith("route.checkedCoroutineHandler(coroutineScope, launchMode, requestHandler)")
)
fun checkedCoroutineHandler(
    coroutineScope: CoroutineScope, route: Route,
    launchMode: CoroutineHandlerLaunchMode = DefaultOnVertxEventLoop,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    route.checkedCoroutineHandler(coroutineScope, launchMode, requestHandler)
