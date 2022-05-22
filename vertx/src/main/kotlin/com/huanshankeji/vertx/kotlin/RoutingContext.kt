package com.huanshankeji.vertx.kotlin

import io.vertx.ext.web.RoutingContext

operator fun RoutingContext.set(key: String, value: Any?) {
    put(key, value)
}
