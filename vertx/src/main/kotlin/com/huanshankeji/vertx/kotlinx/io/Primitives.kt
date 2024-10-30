package com.huanshankeji.vertx.kotlinx.io

@Suppress("NOTHING_TO_INLINE")
internal inline fun Long.toIntOrThrow(): Int {
    require(this in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong())
    return toInt()
}
