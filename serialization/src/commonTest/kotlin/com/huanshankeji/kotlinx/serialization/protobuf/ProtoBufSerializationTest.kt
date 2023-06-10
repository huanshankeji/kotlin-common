package com.huanshankeji.kotlinx.serialization.protobuf

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class, ExperimentalSerializationApi::class)
class ProtoBufSerializationTest {
    @Serializable
    data class TestData<T : Any>(@ProtoNumber(1) val field: T?)

    @Test
    fun testNothingField() {
        val testData = TestData(null)
        @Suppress("DEPRECATION")
        run {
            val encoded = ProtoBuf.encodeToByteArrayNothingWorkaround(testData)
            assertContentEquals(ByteArray(0), encoded)
            assertEquals(testData, ProtoBuf.decodeFromByteArrayNothingWorkaround(encoded))
        }
        run {
            val encoded = ProtoBuf.encodeToByteArray(testData)
            assertContentEquals(ByteArray(0), encoded)
            assertEquals(testData, ProtoBuf.decodeFromByteArray(encoded))
        }
    }
}