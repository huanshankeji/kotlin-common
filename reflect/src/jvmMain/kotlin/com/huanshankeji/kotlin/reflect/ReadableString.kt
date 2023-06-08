package com.huanshankeji.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

/**
 * A useful utility function for debugging purposes.
 * It produces the string produced by [toString] if the class were a data class with all public properties recursively.
 * Note the data should not have cyclic references, otherwise the function call will not terminate.
 */
fun Any?.toReadableStringByReflection(): String =
    if (this === null)
        toString()
    else {
        @Suppress("UNCHECKED_CAST")
        val clazz = this::class as KClass<Any>
        if (clazz.java.methods.find { it.name == "toString" && it.parameterCount == 0 }!!.declaringClass == Any::class.java)
            "${clazz.simpleName}(${
                clazz.memberProperties.asSequence().filter { it.visibility == KVisibility.PUBLIC }.joinToString(", ") {
                    "${it.name}=${
                        try {
                            it.invoke(this).toReadableStringByReflection()
                        } catch (t: Throwable) {
                            t.printStackTrace()
                            t
                        }
                    }"
                }
            })"
        else
            toString()
    }
