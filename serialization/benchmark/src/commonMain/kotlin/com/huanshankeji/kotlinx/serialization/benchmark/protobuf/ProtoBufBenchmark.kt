package com.huanshankeji.kotlinx.serialization.benchmark.protobuf

import com.huanshankeji.kotlinx.serialization.benchmark.*
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalSerializationApi::class)
class ProtoBufBenchmark : BaseBenchmark() {
    enum class ParamEnum(override val dataSerializationConfig: DataSerializationConfig<*>) : IParamEnum {
        ZeroLongWrapper(zeroLongWrapperDataSerializationConfig),
        MaxLongWrapper(maxLongWrapperDataSerializationConfig),
        ZeroDefaultLongWrapper(zeroDefaultLongWrapperDataSerializationConfig),
        MaxDefaultLongWrapper(maxDefaultLongWrapperDataSerializationConfig),
        ZeroFixedLong(zeroFixedLongWrapperDataSerializationConfig),
        MaxFixedLong(maxFixedLongWrapperDataSerializationConfig)
    }

    @Param
    lateinit var paramEnum: ParamEnum

    @Benchmark
    fun serialize() =
        with(paramEnum.dataSerializationConfig as DataSerializationConfig<Any?>) {
            ProtoBuf.encodeToByteArray(serializer, data)
        }
}