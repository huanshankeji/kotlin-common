package com.huanshankeji.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.createType

typealias Substitution = Map<KTypeParameter, KType>

fun KType.substitute(substitution: Substitution): KType =
    when (val classifier = classifier) {
        is KTypeParameter -> substitution.getValue(classifier)

        is KClass<*> -> classifier.createType(
            arguments.map {
                it.copy(type = it.type?.substitute(substitution)) // Star projections in property return type are kept.
            },
            isMarkedNullable, annotations
        )

        else -> throwNotAClassOrATypeParameter(classifier)
    }
