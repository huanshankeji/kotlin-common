package com.huanshankeji.kotlinx.serialization.benchmark

import com.huanshankeji.kotlinx.serialization.protobuf.encodeToByteArrayNothingWorkaround
import kotlinx.benchmark.*
import kotlinx.benchmark.Benchmark
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

@State(Scope.Benchmark)
@Warmup(1)
@Measurement(1)
class Benchmark {
    @Benchmark
    fun serializeToJson() =
        Json.encodeToString(testData)

    @Benchmark
    fun serializeToJsonWithStaticSerializer() =
        Json.encodeToString(testDataNothingSerializer, testData)

    @Benchmark
    fun serializeToProtobuf() =
        ProtoBuf.encodeToByteArray(testData)

    @Benchmark
    fun serializeToProtobufWithStaticSerializer() =
        ProtoBuf.encodeToByteArray(testDataNothingSerializer, testData)

    @Benchmark
    fun serializeToProtobufWithNothingWorkaround() =
        ProtoBuf.encodeToByteArrayNothingWorkaround(testData)
}