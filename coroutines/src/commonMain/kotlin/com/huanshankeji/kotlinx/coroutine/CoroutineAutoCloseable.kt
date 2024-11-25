package com.huanshankeji.kotlinx.coroutine

import com.huanshankeji.ExperimentalApi
import com.huanshankeji.kotlin.use

/**
 * @see AutoCloseable
 * Also see https://github.com/Kotlin/kotlinx.coroutines/issues/1191.
 * Made a functional interface so that there is no need to add a function like the [AutoCloseable] one.
 */
@ExperimentalApi
fun interface CoroutineAutoCloseable {
    suspend fun close()
}

suspend inline fun <T : CoroutineAutoCloseable?, R> T.use(block: (T) -> R): R =
    use(block) { this?.close() }
