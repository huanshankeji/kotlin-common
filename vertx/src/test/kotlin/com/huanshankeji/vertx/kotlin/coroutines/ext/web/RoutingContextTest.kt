package com.huanshankeji.vertx.kotlin.coroutines.ext.web

import com.huanshankeji.vertx.ext.web.RoutingContextBaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RoutingContextTest : RoutingContextBaseTest() {
    @Test
    fun `test launchChecked`() = runTest {
        testChecked {
            launchChecked(this) { throw it }
        }
    }
}