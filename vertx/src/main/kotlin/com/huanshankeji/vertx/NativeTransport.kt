package com.huanshankeji.vertx

import com.huanshankeji.vertx.kotlin.coroutines.use
import io.vertx.core.Vertx
import io.vertx.kotlin.core.vertxOptionsOf

suspend fun isNativeTransportEnabled() =
    Vertx.vertx(vertxOptionsOf(preferNativeTransport = true)).use { it.isNativeTransportEnabled }
