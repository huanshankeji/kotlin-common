package com.huanshankeji.kotlin.reflect

import com.huanshankeji.kotlin.reflect.SubclassTypeParameterInferenceTest.TestSealedClass.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SubclassTypeParameterInferenceTest {
    sealed class TestSealedClass<T1, T2> {
        sealed class T2EqUnit<T> : TestSealedClass<T, Unit>()
        class A<T> : T2EqUnit<T>()
        class B<T> : TestSealedClass<T, T>()
        class C<T1, T2> : T2EqUnit<T1>()
        class D<T> : T2EqUnit<List<T>>()
    }

    @Test
    fun `test concreteTypeSealedSubtypes`() {
        // T1 != T2
        assertEquals(
            listOf(typeAndClassOf<T2EqUnit<List<Unit>>>()),
            typeAndClassOf<TestSealedClass<List<Unit>, Unit>>().concreteTypeSealedDirectSubtypes()
        )
        // T1 == T2
        assertEquals(
            setOf(typeAndClassOf<T2EqUnit<Unit>>(), typeAndClassOf<B<Unit>>()),
            typeAndClassOf<TestSealedClass<Unit, Unit>>().concreteTypeSealedDirectSubtypes().toSet()
        )
    }

    @Test
    fun `test concreteTypeSealedLeafSubtypes`() {
        // T1 != T2
        assertEquals(
            listOf(typeAndClassOf<A<List<Unit>>>(), typeAndClassOf<C<List<Unit>, *>>(), typeAndClassOf<D<Unit>>()),
            typeAndClassOf<TestSealedClass<List<Unit>, Unit>>().concreteTypeSealedLeafSubtypes()
        )
        // T1 == T2
        assertEquals(
            setOf(typeAndClassOf<A<Unit>>(), typeAndClassOf<B<Unit>>(), typeAndClassOf<C<Unit, *>>()),
            typeAndClassOf<TestSealedClass<Unit, Unit>>().concreteTypeSealedLeafSubtypes().toSet()
        )
    }

    @Test
    fun `test concreteTypeConcreteSealedLeafSubtypes`() {
        // T1 == T2 == Unit
        assertEquals(
            setOf(typeAndClassOf<A<Unit>>(), typeAndClassOf<B<Unit>>()),
            typeAndClassOf<TestSealedClass<Unit, Unit>>().concreteTypeConcreteSealedLeafSubtypes().toSet()
        )
    }

    // TODO: consider this and star projections
    sealed class TestSealedClass2<Self> {
        class A : TestSealedClass2<A>()
        class B : TestSealedClass2<B>()
    }
}
