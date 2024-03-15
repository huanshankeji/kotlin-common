package com.huanshankeji.kotlinx.serialization.benchmark.protobuf

import com.google.protobuf.Message
import com.huanshankeji.kotlinx.serialization.benchmark.GoogleProtobufBaseBenchmark
import com.huanshankeji.kotlinx.serialization.benchmark.protobuf.Protos.DefaultLongWrapper
import com.huanshankeji.kotlinx.serialization.benchmark.protobuf.Protos.FixedLongWrapper
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Measurement

/**
 * @see ProtoBufBenchmark to compare with
 */
class GoogleProtobufJavaBenchmark : GoogleProtobufBaseBenchmark<Message.Builder>() {
    @Benchmark
    @Measurement(3) // The first measurement iteration number is even lower than that of warmup. I don't know why.
    fun withDefaultIntegerType() =
        serialize(DefaultLongWrapper::newBuilder, DefaultLongWrapper.Builder::setData)

    @Benchmark
    fun withFixedIntegerType() =
        serialize(FixedLongWrapper::newBuilder, FixedLongWrapper.Builder::setData)
}