package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlinx.coroutines.test.measureVirtualTime
import com.huanshankeji.test.DEFAULT_SLEEP_OR_DELAY_DURATION
import com.huanshankeji.vertx.VertxBaseTest
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.impl.NoStackTraceThrowable
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

//@RunWith(VertxUnitRunner::class)
class VertxCoroutineTest : VertxBaseTest() {
    class DummyVerticle : AbstractVerticle()

    suspend fun assertClosed(vertx: Vertx) =
        assertThrows<NoStackTraceThrowable> {
            vertx.deployVerticle(DummyVerticle()).await()
        }

    @Test
    fun `test use`() = runTest {
        val vertx = Vertx.vertx()
        vertx.use {
            assertDoesNotThrow {
                vertx.deployVerticle(DummyVerticle()).await()
            }
        }
        assertClosed(vertx)
    }

    @Test
    fun `test use with a throwable thrown inside`() = runTest {
        val vertx = Vertx.vertx()
        assertThrows<Throwable> {
            vertx.use {
                throw Throwable()
            }
        }

        assertClosed(vertx)
    }

    companion object {
        private val resultValue = Random.nextInt()
    }

    @Test
    fun `test awaitExecuteBlocking`() = runTest {
        assertTrue(measureTimeMillis {
            assertEquals(resultValue, vertx.awaitExecuteBlocking {
                Thread.sleep(DEFAULT_SLEEP_OR_DELAY_DURATION)
                resultValue
            })
        } >= DEFAULT_SLEEP_OR_DELAY_DURATION)
    }

    @Test
    fun `test awaitSuspendExecuteBlocking`() = runTest {
        assertTrue(measureVirtualTime {
            assertEquals(resultValue, vertx.awaitSuspendExecuteBlocking {
                delay(DEFAULT_SLEEP_OR_DELAY_DURATION)
                resultValue
            })
        } >= DEFAULT_SLEEP_OR_DELAY_DURATION)
    }

    @Test
    fun `test coroutineToFuture`() = runTest {
        assertTrue(measureVirtualTime {
            coroutineToFuture {
                delay(DEFAULT_SLEEP_OR_DELAY_DURATION)
            }.await()
        } >= DEFAULT_SLEEP_OR_DELAY_DURATION)

        assertTrue(measureTimeMillis {
            coroutineToFuture {
                @Suppress("BlockingMethodInNonBlockingContext")
                Thread.sleep(DEFAULT_SLEEP_OR_DELAY_DURATION)
            }.await()
        } >= DEFAULT_SLEEP_OR_DELAY_DURATION)
    }
}