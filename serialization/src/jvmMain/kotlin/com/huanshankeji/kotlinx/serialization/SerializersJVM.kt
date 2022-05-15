package com.huanshankeji.kotlinx.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Deprecated("A workaround Nothing serializer doesn't work for the JS target.")
object VoidSerializer : KSerializer<Void> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("Nothing") {}

    override fun deserialize(decoder: Decoder): Void =
        throwIse()

    override fun serialize(encoder: Encoder, value: Void) =
        throwIse()
}
