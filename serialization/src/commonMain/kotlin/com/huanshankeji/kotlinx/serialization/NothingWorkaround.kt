package com.huanshankeji.kotlinx.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.typeOf

internal class DummyTypeConstructor<T> private constructor()

@ExperimentalStdlibApi
internal val nothingType: KType = typeOf<DummyTypeConstructor<Nothing>>().arguments.first().type!!

@ExperimentalStdlibApi
internal val nullableNothingType: KType = typeOf<DummyTypeConstructor<Nothing?>>().arguments.first().type!!

@ExperimentalStdlibApi
internal fun KType.isNothing() =
    nothingType == this

@ExperimentalStdlibApi
internal fun KType.isNullableNothing() =
    nullableNothingType == this

internal expect fun KType.copyWithArguments(arguments: List<KTypeProjection>): KType

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
