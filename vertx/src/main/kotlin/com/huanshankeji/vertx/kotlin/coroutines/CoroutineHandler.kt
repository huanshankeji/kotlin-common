package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlinx.coroutine.WithCoroutineScope
import io.vertx.core.Handler
import kotlinx.coroutines.launch

interface CoroutineHandler<E> : Handler<E>, WithCoroutineScope {
    override fun handle(event: E) {
        coroutineScope.launch { suspendHandle(event) }
    }

    suspend fun suspendHandle(event: E)
}