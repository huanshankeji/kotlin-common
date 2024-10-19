package com.huanshankeji.vertx

import io.netty.handler.codec.http.HttpResponseStatus.*
import io.vertx.ext.web.RoutingContext

fun RoutingContext.failWithUnauthorized() =
    fail(UNAUTHORIZED.code())

fun RoutingContext.failWithForbidden() =
    fail(FORBIDDEN.code())

fun RoutingContext.failWithBadRequest() =
    fail(BAD_REQUEST.code())

fun RoutingContext.failWithBadRequest(t: Throwable) =
    fail(BAD_REQUEST.code(), t)
