package com.huanshankeji.kotlinx.serialization.protobuf

import com.huanshankeji.kotlinx.serialization.serializerNothingWorkaround
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
@ExperimentalStdlibApi
inline fun <reified T> ProtoBuf.encodeToByteArrayNothingWorkaround(value: T) =
    encodeToByteArray(serializerNothingWorkaround(), value)

@ExperimentalSerializationApi
@ExperimentalStdlibApi
inline fun <reified T> ProtoBuf.decodeFromByteArrayNothingWorkaround(bytes: ByteArray) =
    decodeFromByteArray<T>(serializerNothingWorkaround(), bytes)
