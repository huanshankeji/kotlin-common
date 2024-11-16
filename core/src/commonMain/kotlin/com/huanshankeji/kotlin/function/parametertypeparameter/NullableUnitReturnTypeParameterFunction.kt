package com.huanshankeji.kotlin.function.parametertypeparameter

import com.huanshankeji.ExperimentalApi

private typealias NullableUnitReturnTypeParameterFunction<T> = ((T) -> Unit)?

@ExperimentalApi
inline operator fun <T> NullableUnitReturnTypeParameterFunction<T>.plus(noinline other: NullableUnitReturnTypeParameterFunction<T>): NullableUnitReturnTypeParameterFunction<T> =
    if (this === null)
        other
    else if (other === null)
        this
    else {
        {
            this@plus(it)
            other(it)
        }
    }
