package com.huanshankeji.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// TODO: consider replacing `KType`s in function parameters
// TODO: don't use data classes in an API
@Suppress("UNCHECKED_CAST")
data class TypeAndClass<T : Any>(val type: KType, val clazz: KClass<T> = type.classifier as KClass<T>)

inline fun <reified T : Any> typeAndClassOf() =
    TypeAndClass(typeOf<T>(), T::class)
