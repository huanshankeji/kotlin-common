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

@ExperimentalStdlibApi
internal val serializableNothingType = typeOf<SerializableNothing>()

@ExperimentalStdlibApi
internal val nullableSerializableNothingType = typeOf<SerializableNothing?>()

@ExperimentalStdlibApi
fun KType.mapNothingToSerializableNothing(): KType =
    if (isNothing())
        serializableNothingType
    else if (isNullableNothing())
        nullableSerializableNothingType
    else
        copyWithArguments(arguments.map {
            KTypeProjection(it.variance, it.type!!.mapNothingToSerializableNothing())
        })

@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
inline fun <reified T> SerialFormat.serializerNothingWorkaround(): KSerializer<T> =
    serializersModule.serializer(typeOf<T>().mapNothingToSerializableNothing()) as KSerializer<T>
