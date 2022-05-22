package com.huanshankeji.kotlinx.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// see: https://github.com/Kotlin/kotlinx.serialization/issues/614, https://github.com/Kotlin/kotlinx.serialization/issues/932


internal fun throwIse(): Nothing =
    throw IllegalStateException()

@Deprecated("A workaround Nothing serializer doesn't work for the JS target.")
object NothingSerializer : KSerializer<Nothing> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("Nothing") {}

    override fun serialize(encoder: Encoder, value: Nothing) =
        throwIse()

    override fun deserialize(decoder: Decoder): Nothing =
        throwIse()
}
