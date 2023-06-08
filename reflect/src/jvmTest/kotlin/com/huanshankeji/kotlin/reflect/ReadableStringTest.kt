package com.huanshankeji.kotlin.reflect

import java.lang.reflect.InvocationTargetException
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadableStringTest {
    companion object {
        val throwable = Throwable()
    }

    object DataClasses {
        data class Data<T>(val property: T)
    }

    object NonDataClasses {
        class Data<T>(val property: T)
    }

    class Data2<T>(private val property: T)

    class Data3 {
        val property: Unit get() = throw throwable
    }

    @Test
    fun `test toReadableStringByReflection`() {
        assertEquals(
            DataClasses.Data(DataClasses.Data(0)).toString(),
            NonDataClasses.Data(NonDataClasses.Data(0)).toReadableStringByReflection()
        )

        assertEquals("Data2()", Data2(Data2(0)).toReadableStringByReflection())

        assertEquals("Data3(property=${InvocationTargetException(throwable)})", Data3().toReadableStringByReflection())
    }
}