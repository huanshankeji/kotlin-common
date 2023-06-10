package com.huanshankeji.kotlinx.serialization

import com.huanshankeji.kotlin.reflect.copyWithArguments
import com.huanshankeji.kotlin.reflect.isNothing
import com.huanshankeji.kotlin.reflect.isNullableNothing
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.typeOf

const val NOTHING_SUPPORTED_BY_KOTLINX_SERIALIZATION_MESSAGE =
    "The `Nothing` type is supported by kotlinx.serialization by default now. " +
            "See https://github.com/Kotlin/kotlinx.serialization/issues/932 " +
            "and https://github.com/Kotlin/kotlinx.serialization/pull/2150 ."

internal val serializableNothingType = typeOf<SerializableNothing>()

internal val nullableSerializableNothingType = typeOf<SerializableNothing?>()

fun KType.mapNothingToSerializableNothing(): KType =
    if (isNothing())
        serializableNothingType
    else if (isNullableNothing())
        nullableSerializableNothingType
    else
        copyWithArguments(arguments.map {
            KTypeProjection(it.variance, it.type!!.mapNothingToSerializableNothing())
        })

@Deprecated(
    NOTHING_SUPPORTED_BY_KOTLINX_SERIALIZATION_MESSAGE,
    ReplaceWith("this.serializersModule.serializer<T>()", "kotlinx.serialization.serializer")
)
@Suppress("UNCHECKED_CAST")
inline fun <reified T> SerialFormat.serializerNothingWorkaround(): KSerializer<T> =
    serializersModule.serializer(typeOf<T>().mapNothingToSerializableNothing()) as KSerializer<T>
