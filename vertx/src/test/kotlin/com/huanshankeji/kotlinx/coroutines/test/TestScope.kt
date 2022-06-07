package com.huanshankeji.kotlinx.coroutines.test

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlin.system.measureTimeMillis

/**
 * @see measureTimeMillis
 * @see TestScope.currentTime
 */
inline fun TestScope.measureVirtualTime(block: () -> Unit): Long {
    val start = currentTime
    block()
    return currentTime - start
}
