package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlinx.coroutines.test.measureVirtualTime
import com.huanshankeji.test.DEFAULT_SLEEP_OR_DELAY_DURATION
import com.huanshankeji.vertx.VertxBaseTest
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.impl.NoStackTraceThrowable
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
        private const val LIST_SIZE = 4
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

    @Test
    fun `test awaitAll of succeeded futures`() = runTest {
        val list = List(LIST_SIZE) { it }
        val promises = list.map { Promise.promise<Int>() }
        val futures = promises.map { it.future() }
        awaitAll(
            async {
                assertEquals(list, futures.awaitAll())
            },
            *promises.mapIndexed { index, promise ->
                async { promise.complete(index) }
            }.toTypedArray()
        )
    }

    class IntThrowable(val value: Int) : Throwable()

    @Test
    fun `test awaitAll of futures containing failed futures`() = runTest {
        val list = List(LIST_SIZE) { it }
        val promises = list.map { Promise.promise<Int>() }
        val futures = promises.map { it.future() }
        val firstFailIndex = list.random()
        val secondFailIndex = (list - firstFailIndex).random()

        awaitAll(
            async {
                val throwable = assertThrows<IntThrowable> { futures.awaitAll() }
                assertEquals(firstFailIndex, throwable.value)
            },
            async {
                promises.asSequence().withIndex().shuffled()
                    .filterNot { it.index == secondFailIndex }
                    .map { (index, promise) ->
                        async {
                            when (index) {
                                firstFailIndex -> promise.fail(IntThrowable(index))
                                else -> promise.complete(index)
                            }
                        }
                    }
                    .toList().awaitAll()

                promises[secondFailIndex].fail(IntThrowable(secondFailIndex))
            }
        )
    }
}