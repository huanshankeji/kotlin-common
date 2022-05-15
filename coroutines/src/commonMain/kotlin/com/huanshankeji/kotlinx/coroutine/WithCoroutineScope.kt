package com.huanshankeji.kotlinx.coroutine

import kotlinx.coroutines.CoroutineScope

interface WithCoroutineScope {
    val coroutineScope: CoroutineScope
}