package com.huanshankeji.kotlinx.serialization.benchmark

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KType

@OptIn(ExperimentalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
class DifferentDataBenchmark : BaseBenchmark() {
    @Param
    lateinit var dataEnum: DataEnum

    /**
     * Adapted from [TestDataBenchmark.serializeToJson] by inlining and passing the [KType] argument.
     */
    @Benchmark
    fun serializeToJsonWithSerializerFromKType() =
        with(dataEnum.dataSerializationConfig) {
            // `Json.serializersModule.serializer(kType)` is equivalent to `serializer(kType)`
            Json.encodeToString(Json.serializersModule.serializer(kType), data)
        }

    /**
     * Adapted from [TestDataBenchmark.serializeToJsonWithStaticSerializer].
     */
    @Benchmark
    fun serializeToJsonWithStaticSerializer() =
        with(dataEnum.dataSerializationConfig as DataSerializationConfig<Any?>) {
            Json.encodeToString(serializer, data)
        }

    /**
     * Adapted from [TestDataBenchmark.serializeToProtobuf] by inlining and passing the [KType] argument.
     */
    @Benchmark
    fun serializeToProtobufWithSerializerFromKType() =
        with(dataEnum.dataSerializationConfig) {
            // `ProtoBuf.serializersModule.serializer(kType)` is equivalent to `serializer(kType)`
            ProtoBuf.encodeToByteArray(ProtoBuf.serializersModule.serializer(kType), data)
        }

    /**
     * Adapted from [TestDataBenchmark.serializeToProtobufWithStaticSerializer].
     */
    @Benchmark
    fun serializeToProtobufWithStaticSerializer() =
        with(dataEnum.dataSerializationConfig as DataSerializationConfig<Any?>) {
            ProtoBuf.encodeToByteArray(serializer, data)
        }
}