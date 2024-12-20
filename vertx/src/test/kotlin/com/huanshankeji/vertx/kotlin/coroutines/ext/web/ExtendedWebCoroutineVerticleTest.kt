package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.kotlin.use
import com.huanshankeji.vertx.VertxBaseTest
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtendedWebCoroutineVerticleTest : VertxBaseTest() {
    companion object {
        object MemberFunctionNames {
            const val coroutineHandler = "coroutineHandler"
            const val coroutineHandlerInline = "coroutineHandlerInline"
            const val checkedCoroutineHandler = "checkedCoroutineHandler"
            const val checkedCoroutineHandlerInline = "checkedCoroutineHandlerInline"
        }
    }

    class Verticle : ExtendedWebCoroutineVerticle() {
        lateinit var httpServer: HttpServer

        override suspend fun start() {
            httpServer = vertx.createHttpServer()
                .requestHandler(Router.router(vertx).apply {
                    val handler: suspend (RoutingContext) -> Unit = {
                        if (it.queryParam("throws").run { size == 1 && single() == "true" })
                            throw Throwable()
                        it.end()
                    }

                    with(MemberFunctionNames) {
                        get("/$coroutineHandler").coroutineHandler(requestHandler = handler)
                        get("/$coroutineHandlerInline").coroutineHandlerInline(requestHandler = handler)
                        get("/$checkedCoroutineHandler").checkedCoroutineHandler(requestHandler = handler)
                        get("/$checkedCoroutineHandlerInline").checkedCoroutineHandlerInline(requestHandler = handler)
                    }
                })
                .listen(0).coAwait()
        }

        override suspend fun stop() {
            httpServer.close().coAwait()
        }
    }

    @Test
    fun `test ExtendedCoroutineVerticle`() = runTest {
        val verticle = Verticle()
        val deploymentId = vertx.deployVerticle(verticle).coAwait()

        WebClient.create(vertx, webClientOptionsOf(defaultPort = verticle.httpServer.actualPort())).use({ webClient ->
            suspend fun testOk(methodName: String) {
                assertEquals(200, webClient.get("/$methodName").send().coAwait().statusCode())
            }

            suspend fun testThrowable(methodName: String) {
                testOk(methodName)
                assertEquals(500, webClient.get("/$methodName?throws=true").send().coAwait().statusCode())
            }

            with(MemberFunctionNames) {
                listOf(coroutineHandler, coroutineHandlerInline, checkedCoroutineHandler, checkedCoroutineHandlerInline)
                    .forEach { testOk(it) }
                listOf(checkedCoroutineHandler, checkedCoroutineHandlerInline).forEach { testThrowable(it) }
            }
        }, { close() })

        vertx.undeploy(deploymentId).coAwait()
    }
}