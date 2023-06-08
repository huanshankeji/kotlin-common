package com.huanshankeji.kotlin.reflect

import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

// TODO: don't use data classes in an API
data class ConcreteReturnTypeProperty1<T, out V>(
    val property: KProperty1<T, V>,
    val concreteReturnType: KType
)

/**
 * @see isConcreteType
 */
fun <T : Any> TypeAndClass<T>.concreteReturnTypeMemberProperties(): List<ConcreteReturnTypeProperty1<T, *>> {
    val bindingMap = (clazz.typeParameters zip
            type.arguments.map {
                it.type ?: throwStarProjectionsNotSupported()
            }).toMap()
    return clazz.memberProperties.map {
        ConcreteReturnTypeProperty1(it, it.returnType.substitute(bindingMap))
    }
}

inline fun <reified T : Any> concreteReturnTypeMemberProperties() =
    typeAndClassOf<T>().concreteReturnTypeMemberProperties()
