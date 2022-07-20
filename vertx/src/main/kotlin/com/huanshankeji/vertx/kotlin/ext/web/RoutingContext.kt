package com.huanshankeji.vertx.kotlin.ext.web

import io.vertx.ext.web.RoutingContext

/**
 * A `set` operator overloading extension function
 * as idiomatic Kotlin syntactic sugar to [RoutingContext.put].
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun RoutingContext.set(key: String, value: Any?) {
    put(key, value)
}
