package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.kotlin.use
import com.huanshankeji.vertx.VertxBaseTest
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.DefaultOnVertxEventLoop
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.Unconfined
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test

class CoroutineHandlersTest : VertxBaseTest() {
    companion object {
        val DEFAULT_PATH = "/default"
        val UNCONFINED_PATH = "/unconfined"
    }

    @Test
    fun `test checkedCoroutineHandler`() = runTest {
        val vertx = vertx
        withContext(vertx.dispatcher()) {
            val httpServer = vertx.createHttpServer().requestHandler(Router.router(vertx).apply {
                fun isOnVertxEventLoop() =
                    Thread.currentThread().name.contains("vert.x-eventloop-thread-")

                fun isOnDefaultExecutor() =
                    Thread.currentThread().name.contains("kotlinx.coroutines.DefaultExecutor")

                coroutineHandler(this@withContext, get(DEFAULT_PATH), DefaultOnVertxEventLoop) {
                    assert(isOnVertxEventLoop())
                    delay(1)
                    assert(isOnVertxEventLoop())
                    it.response().end()
                }
                coroutineHandler(this@withContext, get(UNCONFINED_PATH), Unconfined) {
                    assert(isOnVertxEventLoop())
                    delay(1)
                    assert(isOnDefaultExecutor())
                    it.response().end()
                }
            }).listen(0).await()

            WebClient.create(vertx, webClientOptionsOf(defaultPort = httpServer.actualPort())).use({ webClient ->
                webClient.get(DEFAULT_PATH).send().await()
                webClient.get(UNCONFINED_PATH).send().await()
            }, { close() })
        }
    }
}