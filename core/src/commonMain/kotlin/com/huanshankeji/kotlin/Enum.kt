package com.huanshankeji.kotlin

import kotlin.enums.enumEntries

@Deprecated("Use the operator function instead.", ReplaceWith("first + second"))
fun <E : Enum<E>> enumPlus(values: Array<E>, first: E, second: Int): E =
    values[first.ordinal + second]

inline operator fun <reified E : Enum<E>> E.plus(other: Int): E =
    enumEntries<E>()[ordinal + other]

inline operator fun <reified E : Enum<E>> E.minus(other: Int) = this + -other
inline operator fun <reified E : Enum<E>> E.inc() = this + 1
inline operator fun <reified E : Enum<E>> E.dec() = this - 1
