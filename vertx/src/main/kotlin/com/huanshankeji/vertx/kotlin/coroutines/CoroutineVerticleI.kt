package com.huanshankeji.vertx.kotlin.coroutines

import io.vertx.core.Verticle
import kotlinx.coroutines.CoroutineScope

interface CoroutineVerticleI : Verticle, CoroutineScope {
    suspend fun start() {}
    suspend fun stop() {}
}