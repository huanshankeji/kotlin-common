package com.huanshankeji.kotlin

fun <E : Enum<E>> enumPlus(values: Array<E>, first: E, second: Int): E =
    values[first.ordinal + second]
