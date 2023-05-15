package com.huanshankeji.collections

import io.kotest.property.Arb
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.math.sign
import kotlin.test.Test
import kotlin.test.assertEquals

class LexicographicOrderListComparableTest {
    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun testLexicographicOrderListComparable() = runTest {
        val stringArb = Arb.string()
        checkAll(Arb.pair(stringArb, stringArb)) { (a, b) ->
            assertEquals(
                (a compareTo b).sign,
                (a.toCharArray().asList().lexicographicOrderComparable() compareTo
                        b.toCharArray().asList().lexicographicOrderComparable()).sign
            )
        }
    }
}