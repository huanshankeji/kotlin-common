package com.huanshankeji.vertx.core.kotlinx.io

// Consider adding all the variants to the `core` module or contributing to the Kotlin stdlib.
@Suppress("NOTHING_TO_INLINE")
internal inline fun Long.toIntOrThrow(): Int {
    require(this in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong())
    return toInt()
}
