package com.huanshankeji.kotlin.reflect

import org.junit.Test
import kotlin.reflect.typeOf
import kotlin.test.assertEquals

private typealias KTypeSubstitutionTestType = KTypeSubstitutionTest.TestClass<Int, Set<Int>>

class KTypeSubstitutionTest {
    class TestClass<T1, T2>(
        val v1: T1,
        val v1s: List<T1>,
        val v2: T2,
        val v2s: List<T2>
    )

    @Test
    fun testTypeSubstitution() {
        assertEquals(
            listOf(
                ConcreteReturnTypeProperty1(KTypeSubstitutionTestType::v1, typeOf<Int>()),
                ConcreteReturnTypeProperty1(KTypeSubstitutionTestType::v1s, typeOf<List<Int>>()),
                ConcreteReturnTypeProperty1(KTypeSubstitutionTestType::v2, typeOf<Set<Int>>()),
                ConcreteReturnTypeProperty1(KTypeSubstitutionTestType::v2s, typeOf<List<Set<Int>>>())
            ), typeAndClassOf<KTypeSubstitutionTestType>().concreteReturnTypeMemberProperties()
        )
    }
}