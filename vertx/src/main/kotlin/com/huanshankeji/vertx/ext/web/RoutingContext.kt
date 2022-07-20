package com.huanshankeji.vertx.ext.web

import io.vertx.ext.web.RoutingContext

/**
 * Runs the [block] and calls [RoutingContext.fail] if a [Throwable] is thrown.
 */
inline fun RoutingContext.checkedRun(block: () -> Unit): Unit =
    try {
        block()
    } catch (t: Throwable) {
        fail(t)
    }
