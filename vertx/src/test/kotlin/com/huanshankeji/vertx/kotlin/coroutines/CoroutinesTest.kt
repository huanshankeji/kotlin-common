package com.huanshankeji.vertx.kotlin.coroutines

import com.huanshankeji.kotlinx.coroutines.test.measureVirtualTime
import com.huanshankeji.test.DEFAULT_SLEEP_OR_DELAY_DURATION
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import java.lang.Thread.sleep
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

class CoroutinesTest {
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
                sleep(DEFAULT_SLEEP_OR_DELAY_DURATION)
            }.await()
        } >= DEFAULT_SLEEP_OR_DELAY_DURATION)
    }
}