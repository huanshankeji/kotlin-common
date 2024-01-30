package com.huanshankeji.kotlinx.serialization.benchmark

import com.huanshankeji.kotlinx.serialization.protobuf.encodeToByteArrayNothingWorkaround
import kotlinx.benchmark.Benchmark
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

class TestDataBenchmark : BaseBenchmark() {
    @Benchmark
    fun serializeToJson() =
        Json.encodeToString(testDataOfNothing)

    @Benchmark
    fun serializeToJsonWithStaticSerializer() =
        Json.encodeToString(testDataOfNothingSerializer, testDataOfNothing)

    @Benchmark
    fun serializeToProtobuf() =
        ProtoBuf.encodeToByteArray(testDataOfNothing)

    @Benchmark
    fun serializeToProtobufWithStaticSerializer() =
        ProtoBuf.encodeToByteArray(testDataOfNothingSerializer, testDataOfNothing)

    @Benchmark
    fun serializeToProtobufWithNothingWorkaround() =
        ProtoBuf.encodeToByteArrayNothingWorkaround(testDataOfNothing)
}
