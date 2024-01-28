package com.huanshankeji.kotlinx.serialization.benchmark

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
class TestData<T : Any>(val property1: String, val property2: T?)

val testData = TestData("string", null)
val testDataNothingSerializer = serializer<TestData<Nothing>>()

// TODO also benchmark data types with numbers
