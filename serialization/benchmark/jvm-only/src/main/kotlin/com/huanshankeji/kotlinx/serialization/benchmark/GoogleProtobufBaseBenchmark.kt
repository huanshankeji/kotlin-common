package com.huanshankeji.kotlinx.serialization.benchmark

import com.google.protobuf.MessageLite
import kotlinx.benchmark.Param

abstract class GoogleProtobufBaseBenchmark<BuilderUpperbound : MessageLite.Builder> : BaseBenchmark() {
    enum class ParamEnum(val longWrapper: LongWrapper) {
        Zero(zeroLongWrapperDataSerializationConfig.data), Max(maxLongWrapperDataSerializationConfig.data)
    }

    @Param
    lateinit var paramEnum: ParamEnum

    // probably made `open` by the `all-open` plugin if `final` is not added
    @Suppress("RedundantModalityModifier")
    final inline fun <reified BuilderT : BuilderUpperbound> serialize(
        newBuilder: () -> BuilderT, setData: BuilderT.(Long) -> Unit
    ): ByteArray {
        val longWrapper = paramEnum.longWrapper
        return newBuilder().apply {
            setData(longWrapper.value)
        }.build().toByteArray()
    }
}