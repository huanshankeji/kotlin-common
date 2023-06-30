package com.huanshankeji.kotlin.reflect.fullconcretetype

import com.huanshankeji.kotlin.reflect.*

// TODO: don't use data classes in public APIs

/**
 * A concrete type class representation that has all concrete types of its properties and sealed subclasses computed and cached, recursively,
 * so they can be reusable with re-computation,
 * in a generic algebraic data type fashion.
 *
 * Note: a concrete type class here refers to a class of a concrete type, not a concrete "type class"/typeclass as in Haskell.
 *
 * //@param subclassesToCompute the subclasses to compute types, which defaults to [clazz]'s sealed subclasses.
 */
data class FullConcreteTypeClass<T : Any> @PublishedApi internal constructor(
    val typeAndClass: TypeAndClass<T>,
    //subclassesToCompute: List<KClass<out T>>? = null,
    val properties: List<FullConcreteTypeProperty<T, *>> = run {
        val crtProperties = typeAndClass.concreteReturnTypeMemberProperties()
        crtProperties.map { FullConcreteTypeProperty(it) }
    },
    val sealedSubclasses: List<FullConcreteTypeClass<out T>> = typeAndClass.concreteTypeSealedDirectSubtypes().map {
        FullConcreteTypeClass(it)
    }
    /*
    run {
        val subclassesToCompute = subclassesToCompute?.map {
            TypeAndClass(inferSubclassTypeThrowOnFailing(it, typeAndClass), it)
        } ?: typeAndClass.concreteTypeSealedDirectSubtypes()
        subclassesToCompute.map {
            FullConcreteTypeClass(it)
        }
    }
    */
) {
    val type = typeAndClass.type
    val clazz = typeAndClass.clazz
}

data class FullConcreteTypeProperty<T, V> internal constructor(
    val concreteReturnTypeProperty1: ConcreteReturnTypeProperty1<T, V>,
    val returnType: FullConcreteTypeClass<V & Any> = FullConcreteTypeClass(concreteReturnTypeProperty1.concreteReturnTypeTypeAndClass)
) {
    val property = concreteReturnTypeProperty1.property
    val returnTypeKType = concreteReturnTypeProperty1.concreteReturnType
}

inline fun <reified T : Any> fullConcreteTypeClassOf() =
    FullConcreteTypeClass(typeAndClassOf<T>())
