package com.huanshankeji.vertx.pgclient

import io.vertx.pgclient.impl.PgPoolOptions

/**
 * Optimized for throughput.
 */
@Deprecated("This function causes `withTransaction` not to work.", ReplaceWith(""))
fun PgPoolOptions.setUpConventionally() {
    isPipelined = true
}
