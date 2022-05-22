package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.vertx.checkedRun
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

interface ExtendedCoroutineVerticleI : CoroutineVerticleI {
    fun Route.coroutineHandler(requestHandler: suspend (RoutingContext) -> Unit): Route =
        handler { launch { requestHandler(it) } }

    fun Route.checkedCoroutineHandler(requestHandler: suspend (RoutingContext) -> Unit): Route =
        coroutineHandler { ctx -> ctx.checkedRun { requestHandler(ctx) } }
}

abstract class ExtendedCoroutineVerticle : CoroutineVerticle(), ExtendedCoroutineVerticleI {
    inline fun Route.coroutineHandlerInline(crossinline requestHandler: suspend (RoutingContext) -> Unit): Route =
        handler { launch { requestHandler(it) } }

    inline fun Route.checkedCoroutineHandlerInline(crossinline requestHandler: suspend (RoutingContext) -> Unit): Route =
        coroutineHandlerInline { ctx -> ctx.checkedRun { requestHandler(ctx) } }

    override suspend fun start() {}
    override suspend fun stop() {}
}