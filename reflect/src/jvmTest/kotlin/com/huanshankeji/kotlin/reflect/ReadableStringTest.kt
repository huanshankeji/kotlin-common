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

        val withListString = "Data(property=[Data(property=0), Data(property=1)])"
        assertEquals(DataClasses.Data(listOf(DataClasses.Data(0), DataClasses.Data(1))).toString(), withListString)
        assertEquals(
            withListString,
            NonDataClasses.Data(listOf(NonDataClasses.Data(0), NonDataClasses.Data(1))).toReadableStringByReflection()
        )

        val withSetString = "Data(property=[Data(property=0), Data(property=1)])"
        assertEquals(DataClasses.Data(setOf(DataClasses.Data(0), DataClasses.Data(1))).toString(), withSetString)
        assertEquals(
            withSetString,
            NonDataClasses.Data(setOf(NonDataClasses.Data(0), NonDataClasses.Data(1))).toReadableStringByReflection()
        )

        val withMapString = "Data(property={Data(property=0)=Data(property=1), Data(property=1)=Data(property=0)})"
        assertEquals(
            DataClasses.Data(
                mapOf(
                    DataClasses.Data(0) to DataClasses.Data(1),
                    DataClasses.Data(1) to DataClasses.Data(0)
                )
            ).toString(), withMapString
        )
        assertEquals(
            withMapString,
            NonDataClasses.Data(
                mapOf(
                    NonDataClasses.Data(0) to NonDataClasses.Data(1),
                    NonDataClasses.Data(1) to NonDataClasses.Data(0)
                )
            ).toReadableStringByReflection()
        )
    }
}