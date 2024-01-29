package com.huanshankeji.kotlinx.serialization.benchmark

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.protobuf.ProtoIntegerType
import kotlinx.serialization.protobuf.ProtoType
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Serializable
class TestData<T : Any>(val property1: String, val property2: T?)

@Serializable
class Wrapper<T : Any>(val value: T)

@Serializable
class IntWrapper(val value: Int)

@Serializable
class LongWrapper(val value: Long)

@Serializable
class DefaultLongWrapper(@ProtoType(ProtoIntegerType.DEFAULT) val value: Long)

@Serializable
class FixedLongWrapper(@ProtoType(ProtoIntegerType.FIXED) val value: Long)

@Serializable
class StringWrapper(val value: String)


class DataSerializationConfig<T>(val kType: KType, val serializer: SerializationStrategy<T>, val data: T)

inline fun <reified T> DataSerializationConfig(data: T) =
    DataSerializationConfig(typeOf<T>(), serializer(), data)

val testDataOfNothingDataSerializationConfig = DataSerializationConfig(TestData("string", null))
val testDataOfNothing = testDataOfNothingDataSerializationConfig.data
val testDataOfNothingSerializer = testDataOfNothingDataSerializationConfig.serializer

val zeroLongWrapperDataSerializationConfig = DataSerializationConfig(LongWrapper(0))
val maxLongWrapperDataSerializationConfig = DataSerializationConfig(LongWrapper(Long.MAX_VALUE))
val zeroDefaultLongWrapperDataSerializationConfig = DataSerializationConfig(DefaultLongWrapper(0))
val maxDefaultLongWrapperDataSerializationConfig = DataSerializationConfig(DefaultLongWrapper(Long.MAX_VALUE))
val zeroFixedLongWrapperDataSerializationConfig = DataSerializationConfig(FixedLongWrapper(0))
val maxFixedLongWrapperDataSerializationConfig = DataSerializationConfig(FixedLongWrapper(Long.MAX_VALUE))


interface IParamEnum {
    val dataSerializationConfig: DataSerializationConfig<*>
}

enum class DifferentDataParamEnum(override val dataSerializationConfig: DataSerializationConfig<*>) : IParamEnum {
    TestDataWithNothing(testDataOfNothingDataSerializationConfig),
    WrapperOfZeroInt(DataSerializationConfig(Wrapper(0))),
    WrapperOfZeroLong(DataSerializationConfig(Wrapper(0))),
    WrapperOfString(DataSerializationConfig(Wrapper("String"))),
    ZeroIntWrapper(DataSerializationConfig(IntWrapper(0))),
    ZeroLongWrapper(zeroLongWrapperDataSerializationConfig),
    MaxLongWrapper(maxLongWrapperDataSerializationConfig),
    StringWrapper(DataSerializationConfig(StringWrapper("string")));

    //constructor(data: Any) : this(serializerAndData(data)) // this doesn't work because the type parameter is needed
}
