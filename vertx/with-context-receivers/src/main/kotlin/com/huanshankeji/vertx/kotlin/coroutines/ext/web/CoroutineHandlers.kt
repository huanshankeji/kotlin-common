package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.vertx.ext.web.checkedRun
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Like [Route.handler] but with a suspend function as [requestHandler].
 */
context(CoroutineScope, Route)
fun coroutineHandler(requestHandler: suspend (RoutingContext) -> Unit): Route =
    handler { launch(Dispatchers.Unconfined) { requestHandler(it) } }

/**
 * Like [coroutineHandler] and calls [RoutingContext.fail] if a [Throwable] is thrown in [requestHandler].
 */
context(CoroutineScope, Route)
fun checkedCoroutineHandler(requestHandler: suspend (RoutingContext) -> Unit): Route =
    coroutineHandler { ctx -> ctx.checkedRun { requestHandler(ctx) } }
