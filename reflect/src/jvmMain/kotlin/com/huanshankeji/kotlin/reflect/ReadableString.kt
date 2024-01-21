package com.huanshankeji.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

/**
 * A useful utility function for debugging purposes.
 * It produces the string produced by [toString] if the class were a data class with all public properties recursively.
 * Note the data should not have cyclic references, otherwise the function call will not terminate.
 * Note that if a class has an overriden [toString] method (for example a data class), it will be called directly,
 * so if it contains a public property whose class doesn't have an overriden [toString] method the default [Any.toString] will be called.
 */
fun Any?.toReadableStringByReflection(): String =
    if (this === null)
        toString()
    else {
        @Suppress("UNCHECKED_CAST")
        val clazz = this::class as KClass<Any>
        when {
            clazz.java.methods.find { it.name == "toString" && it.parameterCount == 0 }!!.declaringClass == Any::class.java -> "${clazz.simpleName}(${
                clazz.memberProperties.asSequence()
                    .filter { it.visibility == KVisibility.PUBLIC }
                    .joinToString(", ") { kProperty1 ->
                        "${kProperty1.name}=${
                            try {
                                kProperty1(this).toReadableStringByReflection()
                            } catch (t: Throwable) {
                                t.printStackTrace()
                                t
                            }
                        }"
                    }
            })"

            this is Collection<*> -> joinToString(", ", "[", "]") { it.toReadableStringByReflection() }
            this is Map<*, *> -> this.entries.joinToString(", ", "{", "}") {
                "${it.key.toReadableStringByReflection()}=${it.value.toReadableStringByReflection()}"
            }

            else -> toString()
        }
    }
