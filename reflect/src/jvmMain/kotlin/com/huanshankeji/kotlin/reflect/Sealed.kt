package com.huanshankeji.kotlin.reflect

import kotlin.reflect.KClass

// This implementation currently has poor performance (O(depth * size) time complexity).
fun <T : Any> KClass<T>.sealedLeafSubclasses(): List<KClass<out T>> =
    if (isSealed) sealedSubclasses.flatMap { it.sealedLeafSubclasses() }
    else listOf(this)
