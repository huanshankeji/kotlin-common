package com.huanshankeji.kotlinx.serialization.protobuf

import com.huanshankeji.kotlinx.serialization.NothingSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule

import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
actual val extendedProtoBuf = ProtoBuf {
    serializersModule = SerializersModule {
        @Suppress("DEPRECATION")
        contextual(Nothing::class, NothingSerializer) // doesn't work
        // This is uncompleted and deprecated. Consider removing this someday.
    }
}
