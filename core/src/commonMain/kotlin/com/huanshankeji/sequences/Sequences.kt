package com.huanshankeji.sequences

inline fun <T : Comparable<T>> Sequence<T>.isSorted(): Boolean =
    isSortedBy { it }

inline fun <T, R : Comparable<R>> Sequence<T>.isSortedBy(crossinline selector: (T) -> R?): Boolean =
    isSortedWith(compareBy(selector))

fun <T> Sequence<T>.isSortedWith(comparator: Comparator<in T>): Boolean =
    zipWithNext { a, b -> comparator.compare(a, b) <= 0 }.all { it }
