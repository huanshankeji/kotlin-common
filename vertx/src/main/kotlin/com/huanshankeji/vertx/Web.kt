package com.huanshankeji.vertx

import io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN
import io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun RoutingContext.failWithUnauthorized() =
    fail(UNAUTHORIZED.code())

fun RoutingContext.failWithForbidden() =
    fail(FORBIDDEN.code())

inline fun RoutingContext.checkedRun(block: () -> Unit): Unit =
    try {
        block()
    } catch (t: Throwable) {
        fail(t)
    }

inline fun CoroutineScope.launchChecked(
    ctx: RoutingContext,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.() -> Unit
) =
    launch(context, start) {
        ctx.checkedRun { block() }
    }
