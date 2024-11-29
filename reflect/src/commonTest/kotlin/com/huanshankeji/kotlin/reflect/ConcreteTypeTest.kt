package com.huanshankeji.kotlin.reflect

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConcreteTypeTest {
    @Test
    fun testIsConcreteTypeWithAllActualKClasses() {
        assertTrue(typeOf<Unit>().isConcreteTypeWithAllActualKClasses())
        assertTrue(typeOf<List<Unit>>().isConcreteTypeWithAllActualKClasses())
        assertFalse(typeOf<List<*>>().isConcreteTypeWithAllActualKClasses())
    }
}