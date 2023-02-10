package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.vertx.ext.web.checkedRun
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// workaround for context receivers
fun coroutineHandler(
    coroutineScope: CoroutineScope, route: Route,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    route.handler { coroutineScope.launch(Dispatchers.Unconfined) { requestHandler(it) } }

// workaround for context receivers
fun checkedCoroutineHandler(
    coroutineScope: CoroutineScope, route: Route,
    requestHandler: suspend (RoutingContext) -> Unit
): Route =
    coroutineHandler(coroutineScope, route) { ctx -> ctx.checkedRun { requestHandler(ctx) } }
