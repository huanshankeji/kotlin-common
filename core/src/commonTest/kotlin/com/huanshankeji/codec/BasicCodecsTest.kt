package com.huanshankeji.codec

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.byteArray
import io.kotest.property.arbitrary.default
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.of
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BasicCodecsTest {
    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalCoroutinesApi::class)
    @Test
    fun testConversionBetweenLongAndBigEndianBytes(): TestResult {
        val long = 0x0123456789ABCDEFU.toLong()
        val bytes = ubyteArrayOf(0x01U, 0x23U, 0x45U, 0x67U, 0x89U, 0xABU, 0xCDU, 0xEFU).asByteArray()
        assertContentEquals(bytes, long.toBigEndianBytes())
        assertEquals(long, bytes.bigEndianToLong())

        @OptIn(ExperimentalCoroutinesApi::class)
        return runTest {
            checkAll<Long> {
                assertEquals(it, it.toBigEndianBytes().bigEndianToLong())
                assertEquals(it, it.toBigEndianBytes().asList().bigEndianToLong())
            }
            checkAll<ByteArray>(Arb.byteArray(Exhaustive.of(8), Arb.default())) {
                assertContentEquals(it, it.bigEndianToLong().toBigEndianBytes())
            }
            checkAll<List<Byte>>(Arb.list(Arb.default(), 8..8)) {
                assertEquals(it, it.bigEndianToLong().toBigEndianBytes().asList())
            }
        }
    }
}