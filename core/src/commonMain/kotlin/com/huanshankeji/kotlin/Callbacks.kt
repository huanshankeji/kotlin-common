package com.huanshankeji.kotlin

inline fun <T : Any> produceInCallback(block: (setValue: (T) -> Unit) -> Unit): T {
    var value: T? = null
    block { value = it }
    return value!!
}
