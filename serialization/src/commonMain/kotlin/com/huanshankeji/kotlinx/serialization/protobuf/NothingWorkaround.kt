package com.huanshankeji.kotlinx.serialization.protobuf

import com.huanshankeji.kotlinx.serialization.NOTHING_SUPPORTED_BY_KOTLINX_SERIALIZATION_MESSAGE
import com.huanshankeji.kotlinx.serialization.serializerNothingWorkaround
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@Deprecated(
    NOTHING_SUPPORTED_BY_KOTLINX_SERIALIZATION_MESSAGE, ReplaceWith(
        "this.encodeToByteArray<T>(value)",
        "kotlinx.serialization.encodeToByteArray"
    )
)
@ExperimentalSerializationApi
inline fun <reified T> ProtoBuf.encodeToByteArrayNothingWorkaround(value: T) =
    @Suppress("DEPRECATION")
    encodeToByteArray(serializerNothingWorkaround(), value)

@Deprecated(
    NOTHING_SUPPORTED_BY_KOTLINX_SERIALIZATION_MESSAGE, ReplaceWith(
        "this.decodeFromByteArray<T>(bytes)",
        "kotlinx.serialization.decodeFromByteArray"
    )
)
@ExperimentalSerializationApi
inline fun <reified T> ProtoBuf.decodeFromByteArrayNothingWorkaround(bytes: ByteArray) =
    @Suppress("DEPRECATION")
    decodeFromByteArray<T>(serializerNothingWorkaround(), bytes)
