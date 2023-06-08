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
 * Excluding those with star projections.
 */
fun <T : Any> TypeAndClass<T>.concreteTypeConcreteSealedDirectSubtypes(): List<TypeAndClass<out T>> =
    concreteTypeSealedDirectSubtypes().filter { it.type.isConcreteType() }

/**
 * Excluding those with star projections.
 * @see sealedLeafSubclasses
 * @see concreteTypeConcreteSealedDirectSubtypes
 */
fun <T : Any> TypeAndClass<T>.concreteTypeConcreteSealedLeafSubtypes(): List<TypeAndClass<out T>> =
    if (clazz.isSealed) concreteTypeConcreteSealedDirectSubtypes().flatMap { it.concreteTypeConcreteSealedLeafSubtypes() }
    else listOf(this)
