package com.huanshankeji.kotlinx.serialization.protobuf

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@ExperimentalSerializationApi
@Serializable
class SumTypeSupertypeSurrogate(
    @ProtoNumber(1)
    val subtypeNumber: Int,
    @ProtoNumber(2)
    val backingSubtypeSerializedData: ByteArray? = null
) {
    val subtypeSerializedData: ByteArray
        get() = backingSubtypeSerializedData!!
}

@ExperimentalSerializationApi
interface ProtoBufSumTypeSerializer<Supertype> : KSerializer<Supertype> {
    val surrogateSerializer get() = SumTypeSupertypeSurrogate.serializer()
    override val descriptor: SerialDescriptor
        get() = surrogateSerializer.descriptor

    fun Supertype.toSurrogate(): SumTypeSupertypeSurrogate {
        val (subtypeNumber, subtypeSerializer) = getSubtypeNumberAndSubtypeSerializationStrategy(this)
        @Suppress("UNCHECKED_CAST")
        return SumTypeSupertypeSurrogate(
            // use `encodeToByteArrayNothingWorkaround` if something goes wrong
            subtypeNumber, ProtoBuf.encodeToByteArray(
                subtypeSerializer as SerializationStrategy<Any?>, this
            )
        )
    }

    fun getSubtypeNumberAndSubtypeSerializationStrategy(value: Supertype): Pair<Int, SerializationStrategy<*>>

    fun SumTypeSupertypeSurrogate.toOriginal(): Supertype =
        // use `decodeFromByteArrayNothingWorkaround` if something goes wrong
        @Suppress("UNCHECKED_CAST")
        ProtoBuf.decodeFromByteArray(
            getSubtypeDeserializationStrategy(subtypeNumber) as DeserializationStrategy<Supertype>,
            subtypeSerializedData
        )

    fun getSubtypeDeserializationStrategy(subtypeNumber: Int): DeserializationStrategy<*>

    override fun serialize(encoder: Encoder, value: Supertype) {
        encoder.encodeSerializableValue(surrogateSerializer, value.toSurrogate())
    }

    override fun deserialize(decoder: Decoder): Supertype =
        decoder.decodeSerializableValue(surrogateSerializer).toOriginal()
}
