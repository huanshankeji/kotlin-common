package com.huanshankeji.collections

fun List<*>.areElementsDistinct() =
    // There is a more efficient implementation.
    distinct().size == size
