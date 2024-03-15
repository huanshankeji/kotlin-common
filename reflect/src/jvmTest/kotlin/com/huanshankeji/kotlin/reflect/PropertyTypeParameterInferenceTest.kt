package com.huanshankeji.kotlin.reflect

import org.junit.Test
import kotlin.reflect.typeOf
import kotlin.test.assertEquals

class PropertyTypeParameterInferenceTest {
    class TestClass<T>(val property1: T, val property2: List<T>)

    @Test
    fun `test concreteReturnTypeMemberProperties`() {
        assertEquals(
            listOf(
                ConcreteReturnTypeProperty1(TestClass<Unit>::property1, typeOf<Unit>()),
                ConcreteReturnTypeProperty1(TestClass<Unit>::property2, typeOf<List<Unit>>())
            ),
            concreteReturnTypeMemberProperties<TestClass<Unit>>()
        )
    }
}