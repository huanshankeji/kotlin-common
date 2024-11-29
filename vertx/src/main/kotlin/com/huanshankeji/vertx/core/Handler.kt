package com.huanshankeji.vertx.core

import com.huanshankeji.ExperimentalApi
import io.vertx.core.Handler

typealias NullableHandler<E> = Handler<E>?

@Suppress("NOTHING_TO_INLINE")
@ExperimentalApi
inline operator fun <T> NullableHandler<T>.plus(other: NullableHandler<T>): NullableHandler<T> =
    if (this === null)
        other
    else if (other === null)
        this
    else {
        Handler {
            this@plus.handle(it)
            other.handle(it)
        }
    }
