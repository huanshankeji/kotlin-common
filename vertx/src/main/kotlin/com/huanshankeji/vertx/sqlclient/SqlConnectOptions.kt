package com.huanshankeji.vertx.sqlclient

import io.vertx.sqlclient.SqlConnectOptions

/**
 * Optimized for throughput.
 */
fun SqlConnectOptions.setUpConventionally() {
    cachePreparedStatements = true
}
