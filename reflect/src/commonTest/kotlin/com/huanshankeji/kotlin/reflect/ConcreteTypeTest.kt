package com.huanshankeji.kotlin.reflect

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConcreteTypeTest {
    @Test
    fun testIsConcreteType() {
        assertTrue(typeOf<Unit>().isConcreteType())
        assertTrue(typeOf<List<Unit>>().isConcreteType())
        assertFalse(typeOf<List<*>>().isConcreteType())
    }
}