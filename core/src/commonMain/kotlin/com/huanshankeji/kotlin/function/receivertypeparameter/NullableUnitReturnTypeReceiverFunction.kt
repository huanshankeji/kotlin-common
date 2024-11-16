package com.huanshankeji.kotlin.function.receivertypeparameter

import com.huanshankeji.ExperimentalApi

private typealias NullableUnitReturnTypeReceiverFunction<T> = (T.() -> Unit)?

@ExperimentalApi
inline operator fun <T> NullableUnitReturnTypeReceiverFunction<T>.plus(noinline other: NullableUnitReturnTypeReceiverFunction<T>): NullableUnitReturnTypeReceiverFunction<T> =
    if (this === null)
        other
    else if (other === null)
        this
    else {
        {
            this@plus()
            other()
        }
    }
