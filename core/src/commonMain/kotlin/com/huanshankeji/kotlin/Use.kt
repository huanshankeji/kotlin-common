package com.huanshankeji.kotlin

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Copied and adapted from [kotlin.use].
 */
@OptIn(ExperimentalContracts::class)
inline fun <T, R> T.use(block: (T) -> R, close: T.() -> Unit): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.closeFinally(exception, close)
    }
}

/**
 * Copied and adapted from [kotlin.closeFinally].
 */
/*private*/ inline fun <T> T.closeFinally(cause: Throwable?, close: T.() -> Unit) = when {
    this == null -> {
    }
    cause == null -> close()
    else ->
        try {
            close()
        } catch (closeException: Throwable) {
            cause.addSuppressed(closeException)
        }
}