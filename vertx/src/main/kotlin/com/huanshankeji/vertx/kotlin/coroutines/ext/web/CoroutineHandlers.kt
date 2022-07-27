package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.vertx.ext.web.checkedRun
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Like [Route.handler] but with a suspend function as [requestHandler].
 */
fun CoroutineScope.coroutineHandler(): Route.(suspend (RoutingContext) -> Unit) -> Unit =
    { requestHandler ->
        handler { launch { requestHandler(it) } }
    }

/**
 * Like [coroutineHandler] and calls [RoutingContext.fail] if a [Throwable] is thrown in [requestHandler].
 */
fun CoroutineScope.checkedCoroutineHandler(): Route.(suspend (RoutingContext) -> Unit) -> Unit =
    { requestHandler ->
        (coroutineHandler()) { ctx -> ctx.checkedRun { requestHandler(ctx) } }
    }
