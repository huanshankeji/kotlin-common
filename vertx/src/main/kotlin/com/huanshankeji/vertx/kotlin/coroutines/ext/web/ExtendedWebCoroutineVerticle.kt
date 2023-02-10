package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.vertx.ext.web.checkedRun
import com.huanshankeji.vertx.kotlin.coroutines.CoroutineVerticleI
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface ExtendedWebCoroutineVerticleI : CoroutineVerticleI {
    /**
     * Like [Route.handler] but with a suspend function as [requestHandler].
     */
    fun Route.coroutineHandler(requestHandler: suspend (RoutingContext) -> Unit): Route =
        handler { launch(Dispatchers.Unconfined) { requestHandler(it) } }

    /**
     * Like [coroutineHandler] and calls [RoutingContext.fail] if a [Throwable] is thrown in [requestHandler].
     */
    fun Route.checkedCoroutineHandler(requestHandler: suspend (RoutingContext) -> Unit): Route =
        coroutineHandler { ctx -> ctx.checkedRun { requestHandler(ctx) } }
}

abstract class ExtendedWebCoroutineVerticle : CoroutineVerticle(), ExtendedWebCoroutineVerticleI {
    /**
     * The inline version of [coroutineHandler],
     * which might be slightly faster but can also make the stack trace difficult to debug.
     */
    inline fun Route.coroutineHandlerInline(crossinline requestHandler: suspend (RoutingContext) -> Unit): Route =
        handler { launch(Dispatchers.Unconfined) { requestHandler(it) } }

    /**
     * The inline version of [checkedCoroutineHandler],
     * which might be slightly faster but can also make the stack trace difficult to debug.
     */
    inline fun Route.checkedCoroutineHandlerInline(crossinline requestHandler: suspend (RoutingContext) -> Unit): Route =
        coroutineHandlerInline { ctx -> ctx.checkedRun { requestHandler(ctx) } }

    override suspend fun start() {}
    override suspend fun stop() {}
}