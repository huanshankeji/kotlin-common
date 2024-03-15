package com.huanshankeji.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType

/**
 * @return `null` if it fails to unify type parameters
 */
fun inferSubclassType(subclass: KClass<*>, superclassTypeAndClass: TypeAndClass<*>): KType? {
    val supertype = subclass.supertypes.single { it.classifier == superclassTypeAndClass.clazz }
    val substitution = concreteTypeUnify(superclassTypeAndClass.type, supertype)
    /*
    if (substitution === null)
        throw IllegalArgumentException("failed to infer type parameters for $subclass as a subtype of ${superclassTypeAndClass.type}")
     */

    return substitution?.run {
        subclass.createType(subclass.typeParameters.map {
            this[it]?.let {
                KTypeProjection(KVariance.INVARIANT, it)
            } ?: KTypeProjection(null, null)
        })
    }
}

fun inferSubclassTypeThrowOnFailing(subclass: KClass<*>, superclassTypeAndClass: TypeAndClass<*>) =
    inferSubclassType(subclass, superclassTypeAndClass)
        ?: throw IllegalArgumentException("failed to infer the type for $subclass")

/**
 * For GADTs, the subclasses whose type parameters failed to be inferred will be discarded.
 */
fun <T : Any> TypeAndClass<T>.concreteTypeSealedDirectSubtypes(): List<TypeAndClass<out T>> =
    clazz.sealedSubclasses.mapNotNull { subclass ->
        inferSubclassType(subclass, this)?.let { subtype -> TypeAndClass(subtype, subclass) }
    }

/**
 * @see sealedLeafSubclasses
 */
fun <T : Any> TypeAndClass<T>.concreteTypeSealedLeafSubtypes(): List<TypeAndClass<out T>> =
    if (clazz.isSealed) concreteTypeSealedDirectSubtypes().flatMap { it.concreteTypeSealedLeafSubtypes() }
    else listOf(this)

/**
 * Excluding those with star projections (and instantiated with type parameters).
 */
fun <T : Any> TypeAndClass<T>.concreteTypeSealedDirectSubtypesWithAllActualKClasses(): List<TypeAndClass<out T>> =
    concreteTypeSealedDirectSubtypes().filter { it.type.isConcreteTypeWithAllActualKClasses() }

/**
 * Excluding those with star projections (and instantiated with type parameters).
 * @see sealedLeafSubclasses
 * @see concreteTypeSealedDirectSubtypesWithAllActualKClasses
 */
fun <T : Any> TypeAndClass<T>.concreteTypeSealedLeafSubtypesWithAllActualKClasses(): List<TypeAndClass<out T>> =
    if (clazz.isSealed) concreteTypeSealedDirectSubtypesWithAllActualKClasses().flatMap { it.concreteTypeSealedLeafSubtypesWithAllActualKClasses() }
    else listOf(this)
