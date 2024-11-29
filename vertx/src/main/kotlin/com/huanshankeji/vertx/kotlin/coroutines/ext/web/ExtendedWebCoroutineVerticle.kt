package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.vertx.ext.web.checkedRun
import com.huanshankeji.vertx.kotlin.coroutines.CoroutineVerticleI
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.DefaultOnVertxEventLoop
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

interface ExtendedWebCoroutineVerticleI : CoroutineVerticleI {
    /**
     * Like [Route.handler] but with a suspend function as [requestHandler].
     */
    fun Route.coroutineHandler(
        launchMode: CoroutineHandlerLaunchMode = DefaultOnVertxEventLoop,
        requestHandler: suspend (RoutingContext) -> Unit
    ): Route =
        coroutineHandler(this@ExtendedWebCoroutineVerticleI, launchMode, requestHandler)

    /**
     * Like [coroutineHandler] and calls [RoutingContext.fail] if a [Throwable] is thrown in [requestHandler].
     */
    fun Route.checkedCoroutineHandler(
        launchMode: CoroutineHandlerLaunchMode = DefaultOnVertxEventLoop,
        requestHandler: suspend (RoutingContext) -> Unit
    ): Route =
        checkedCoroutineHandler(this@ExtendedWebCoroutineVerticleI, launchMode, requestHandler)
}

abstract class ExtendedWebCoroutineVerticle : CoroutineVerticle(), ExtendedWebCoroutineVerticleI {
    // TODO: `launchMode: CoroutineHandlerLaunchMode` is not supported in these inline versions yet

    /**
     * The inline version of [coroutineHandler],
     * which might be slightly faster but can also make the stack trace difficult to debug.
     */
    inline fun Route.coroutineHandlerInline(crossinline requestHandler: suspend (RoutingContext) -> Unit): Route =
        handler { launch { requestHandler(it) } }

    /**
     * The inline version of [checkedCoroutineHandler],
     * which might be slightly faster but can also make the stack trace difficult to debug.
     */
    inline fun Route.checkedCoroutineHandlerInline(crossinline requestHandler: suspend (RoutingContext) -> Unit): Route =
        coroutineHandlerInline { ctx -> ctx.checkedRun { requestHandler(ctx) } }

    override suspend fun start() {}
    override suspend fun stop() {}
}