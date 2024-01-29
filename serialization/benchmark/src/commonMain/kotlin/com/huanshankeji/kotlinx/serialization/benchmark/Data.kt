package com.huanshankeji.kotlinx.serialization.benchmark

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
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
class StringWrapper(val value: String)

class DataSerializationConfig<T>(val kType: KType, val serializer: SerializationStrategy<T>, val data: T)

inline fun <reified T> DataSerializationConfig(data: T) =
    DataSerializationConfig(typeOf<T>(), serializer(), data)

val testDataOfNothingDataSerializationConfig = DataSerializationConfig(TestData("string", null))
val testDataOfNothing = testDataOfNothingDataSerializationConfig.data
val testDataOfNothingSerializer = testDataOfNothingDataSerializationConfig.serializer

enum class DataEnum(val dataSerializationConfig: DataSerializationConfig<*>) {
    TestDataWithNothing(testDataOfNothingDataSerializationConfig),
    WrapperOfZeroInt(DataSerializationConfig(Wrapper(0))),
    WrapperOfZeroLong(DataSerializationConfig(Wrapper(0))),
    WrapperOfString(DataSerializationConfig(Wrapper("String"))),
    ZeroIntWrapper(DataSerializationConfig(IntWrapper(0))),
    ZeroLongWrapper(DataSerializationConfig(LongWrapper(0))),
    MaxLongWrapper(DataSerializationConfig(LongWrapper(Long.MAX_VALUE))),
    StringWrapper(DataSerializationConfig(StringWrapper("string")));

    //constructor(data: Any) : this(serializerAndData(data)) // this doesn't work because the type parameter is needed
}
