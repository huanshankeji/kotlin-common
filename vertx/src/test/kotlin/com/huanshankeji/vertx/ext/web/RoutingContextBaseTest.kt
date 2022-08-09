package com.huanshankeji.vertx.ext.web

import com.huanshankeji.kotlin.use
import com.huanshankeji.net.LOCALHOST
import com.huanshankeji.vertx.VertxBaseTest
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/* Consider getting the vertx-web tests dependency to resolve correctly and extending WebTestBase.
See io.vertx.ext.web.impl.RoutingContextImplTest in vertx-web test for a reference. */

abstract class RoutingContextBaseTest : VertxBaseTest() {
    companion object {
        const val IN_LAUNCH_CHECKED_MESSAGE = "in launchChecked"
    }

    protected suspend inline fun testChecked(crossinline failWithThrowable: RoutingContext.(Throwable) -> Unit) {
        //val mutex = Mutex()
        val testContext = VertxTestContext()
        val httpServer = vertx.createHttpServer().requestHandler(Router.router(vertx).apply {
            route().handler {
                it.failWithThrowable(Throwable(IN_LAUNCH_CHECKED_MESSAGE))
            }
                .failureHandler {
                    val throwable = it.failure()
                    assertTrue(throwable is Throwable && throwable.message == IN_LAUNCH_CHECKED_MESSAGE)
                    testContext.completeNow()

                    it.next()
                }
        }).listen(0).await()

        httpServer.use({
            val port = httpServer.actualPort()
            WebClient.create(vertx).use({
                assertEquals(500, it.get(port, LOCALHOST, "").send().await().statusCode())
            }, { close() })
        }, { close().await() })
    }
}