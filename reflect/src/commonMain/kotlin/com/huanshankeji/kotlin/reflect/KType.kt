package com.huanshankeji.kotlin.reflect

import kotlin.reflect.*

private class DummyTypeConstructor<T> private constructor()

val nothingType: KType = typeOf<DummyTypeConstructor<Nothing>>().arguments.first().type!!

val nullableNothingType: KType = typeOf<DummyTypeConstructor<Nothing?>>().arguments.first().type!!

fun KType.isNothing() =
    nothingType == this

fun KType.isNullableNothing() =
    nullableNothingType == this


expect fun KType.copyWithArguments(arguments: List<KTypeProjection>): KType


fun throwStarProjectionsNotSupported(): Nothing =
    throw IllegalArgumentException("star projections in type arguments are not supported")

fun throwNotAClassOrATypeParameter(classifier: KClassifier?): Nothing =
    throw IllegalArgumentException("not a `KClass` or a type parameter: $classifier")


/**
 * A concrete type is a simple type or a parametrized type. See https://kotlinlang.org/spec/type-system.html#type-kinds for its definition.
 */
fun KType.isConcreteType(): Boolean =
    classifier is KClass<*> && arguments.all { it.type?.isConcreteType() ?: false }
