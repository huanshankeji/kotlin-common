package com.huanshankeji.kotlinx.coroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AwaitTest {
    @Test
    fun simplyTestAwaitAnyAndJoinAny() = runTest {
        val mutex = Mutex()
        mutex.withLock {
            fun asyncOf(value: Int) = async { value }
            fun asyncSuspendUtilEnd() = async { mutex.withLock { } }

            fun deferredArray0() = arrayOf(asyncOf(0), asyncSuspendUtilEnd())
            fun deferredArray1() = arrayOf(asyncSuspendUtilEnd(), asyncOf(1))
            assertEquals(0, awaitAny(*deferredArray0()))
            assertEquals(1, awaitAny(*deferredArray1()))
            assertEquals(0, deferredArray0().asList().awaitAny())
            assertEquals(1, deferredArray1().asList().awaitAny())


            val deffereds = deferredArray0()
            assertFalse(deffereds[1].isCancelled)
            assertEquals(0, deffereds.asList().awaitAnyAndCancelOthers())
            assertTrue(deffereds[1].isCancelled)


            var value: Int?

            fun launchSetOf(newValue: Int) = launch { value = newValue }
            fun launchSuspendUtilEndAndSetOf(newValue: Int) = launch {
                mutex.withLock { }
                value = newValue
            }

            fun jobArray0() = arrayOf(launchSetOf(0), launchSuspendUtilEndAndSetOf(1))
            fun jobArray1() = arrayOf(launchSuspendUtilEndAndSetOf(0), launchSetOf(1))

            value = null
            joinAny(*jobArray0())
            assertEquals(0, value)

            value = null
            joinAny(*jobArray1())
            assertEquals(1, value)

            value = null
            jobArray0().asList().joinAny()
            assertEquals(0, value)

            value = null
            jobArray1().asList().joinAny()
            assertEquals(1, value)
        }
    }

    private inline fun assertFailsWithIae(block: () -> Unit) =
        assertFailsWith<IllegalArgumentException>(block = block)

    @Test
    fun simplyTestAwaitAnyAndJoinAnyWithNoTarget() = runTest {
        val emptyList = emptyList<Nothing>()
        assertFailsWithIae { awaitAny() }
        assertFailsWithIae { emptyList.awaitAny() }
        assertFailsWithIae { emptyList.awaitAnyAndCancelOthers() }
        assertFailsWithIae { joinAny() }
        assertFailsWithIae { emptyList.joinAny() }
    }
}