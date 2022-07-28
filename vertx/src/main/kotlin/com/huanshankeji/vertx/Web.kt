package com.huanshankeji.vertx

import io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN
import io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED
import io.vertx.ext.web.RoutingContext

fun RoutingContext.failWithUnauthorized() =
    fail(UNAUTHORIZED.code())

fun RoutingContext.failWithForbidden() =
    fail(FORBIDDEN.code())
