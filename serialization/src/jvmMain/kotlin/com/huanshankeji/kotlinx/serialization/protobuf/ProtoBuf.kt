package com.huanshankeji.kotlinx.serialization.protobuf

import com.huanshankeji.kotlinx.serialization.VoidSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
/*actual*/ val extendedProtoBuf = ProtoBuf {
    serializersModule = SerializersModule {
        //contextual(Nothing::class, NothingSerializer)
        @Suppress("DEPRECATION")
        contextual(VoidSerializer)
    }
}
