package com.huanshankeji.kotlinx.serialization.benchmark.protobuf

import com.google.protobuf.MessageLite
import com.huanshankeji.kotlinx.serialization.benchmark.GoogleProtobufBaseBenchmark
import com.huanshankeji.kotlinx.serialization.benchmark.protobuf.Protos.DefaultLongWrapper
import com.huanshankeji.kotlinx.serialization.benchmark.protobuf.Protos.FixedLongWrapper
import kotlinx.benchmark.Benchmark

/**
 * @see ProtoBufBenchmark to compare with
 */
class GoogleProtobufJavaLiteBenchmark : GoogleProtobufBaseBenchmark<MessageLite.Builder>() {
    @Benchmark
    fun withDefaultIntegerType() =
        serialize(DefaultLongWrapper::newBuilder, DefaultLongWrapper.Builder::setData)

    @Benchmark
    fun withFixedIntegerType() =
        serialize(FixedLongWrapper::newBuilder, FixedLongWrapper.Builder::setData)
}