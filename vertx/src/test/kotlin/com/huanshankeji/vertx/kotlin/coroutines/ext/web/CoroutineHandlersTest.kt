package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.kotlin.use
import com.huanshankeji.vertx.VertxBaseTest
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.DefaultOnVertxEventLoop
import com.huanshankeji.vertx.kotlin.coroutines.ext.web.CoroutineHandlerLaunchMode.Unconfined
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertTrue

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

                get(DEFAULT_PATH).coroutineHandler(this@withContext, DefaultOnVertxEventLoop) {
                    assertTrue(isOnVertxEventLoop())
                    delay(1)
                    assertTrue(isOnVertxEventLoop())
                    it.response().end()
                }
                get(UNCONFINED_PATH).coroutineHandler(this@withContext, Unconfined) {
                    assertTrue(isOnVertxEventLoop())
                    delay(1)
                    assertTrue(isOnDefaultExecutor())
                    it.response().end()
                }
            }).listen(0).coAwait()

            WebClient.create(vertx, webClientOptionsOf(defaultPort = httpServer.actualPort())).use({ webClient ->
                webClient.get(DEFAULT_PATH).send().coAwait()
                webClient.get(UNCONFINED_PATH).send().coAwait()
            }, { close() })
        }
    }
}