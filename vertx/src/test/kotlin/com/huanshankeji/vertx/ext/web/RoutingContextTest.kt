package com.huanshankeji.vertx.ext.web

import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RoutingContextTest : RoutingContextBaseTest() {
    @Test
    fun `test checkedRun`() = runTest {
        testChecked {
            checkedRun { throw it }
        }
    }
}