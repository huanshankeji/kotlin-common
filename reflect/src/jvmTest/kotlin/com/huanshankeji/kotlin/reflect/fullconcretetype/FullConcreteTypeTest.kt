package com.huanshankeji.kotlin.reflect.fullconcretetype

import com.huanshankeji.kotlin.reflect.ConcreteReturnTypeProperty1
import com.huanshankeji.kotlin.reflect.typeAndClassOf
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class FullConcreteTypeTest {
    sealed class TestClass<T1, T2>(val v1: T1) {
        class A<T1, T2>(v1: T1, val v2: T2) : TestClass<T1, T2>(v1)
        class B : TestClass<Int, Unit>(0)
        class C : TestClass<String, Unit>("")
    }

    @Test
    fun testFullConcreteTypeClassAndFullConcreteTypeProperty() {
        assertEquals(
            FullConcreteTypeClass(
                typeAndClassOf<TestClass<Int, Unit>>(),
                listOf(FullConcreteTypeProperty(ConcreteReturnTypeProperty1(TestClass<Int, Unit>::v1, typeOf<Int>()))),
                listOf(
                    FullConcreteTypeClass(
                        typeAndClassOf<TestClass.A<Int, Unit>>(),
                        listOf(
                            FullConcreteTypeProperty(
                                ConcreteReturnTypeProperty1(TestClass.A<Int, Unit>::v2, typeOf<Unit>())
                            ),
                            FullConcreteTypeProperty(
                                ConcreteReturnTypeProperty1(TestClass.A<Int, Unit>::v1, typeOf<Int>())
                            )
                        )
                    ),
                    FullConcreteTypeClass(
                        typeAndClassOf<TestClass.B>(),
                        listOf(
                            FullConcreteTypeProperty(
                                ConcreteReturnTypeProperty1(TestClass.B::v1, typeOf<Int>())
                            )
                        )
                    ),
                )
            ), fullConcreteTypeClassOf<TestClass<Int, Unit>>()
        )
    }
}