package com.huanshankeji.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter

// see: https://github.com/JetBrains/kotlin/blob/master/compiler/frontend/src/org/jetbrains/kotlin/types/TypeUnifier.java
/**
 * @return `null` when it fails
 */
fun concreteTypeUnify(knowConcreteType: KType, typeWithVariables: KType): Substitution? {
    require(knowConcreteType.isConcreteTypeWithAllActualKClasses())
    val result = mutableMapOf<KTypeParameter, KType>()
    return if (doConcreteTypeUnify(knowConcreteType, typeWithVariables, result)) result
    else null
}

/**
 * @return whether there is no old variable the old variable result type is the same as the new one
 */
private fun MutableMap<KTypeParameter, KType>.putUnificationVariableResult(key: KTypeParameter, value: KType): Boolean =
    merge(key, value) { oldValue, newValue ->
        if (oldValue == newValue) oldValue
        else null
    } != null

/**
 * @return `false` when it fails
 */
private fun doConcreteTypeUnify(
    knowConcreteType: KType, typeWithVariables: KType, result: MutableMap<KTypeParameter, KType>
): Boolean {
    val knowConcreteTypeArguments = knowConcreteType.arguments
    val typeWithVariablesArguments = typeWithVariables.arguments

    return when (val typeWithVariablesClassifier = typeWithVariables.classifier) {
        is KClass<*> -> {
            if (knowConcreteType.classifier as KClass<*> != typeWithVariablesClassifier) return false
            if (knowConcreteTypeArguments.size != typeWithVariablesArguments.size) return false
            (knowConcreteTypeArguments zip typeWithVariablesArguments).all { (knowConcreteTypeArgument, typeWithVariablesArgument) ->
                doConcreteTypeUnify(
                    knowConcreteTypeArgument.type!!,
                    typeWithVariablesArgument.type ?: throwStarProjectionsNotSupported(),
                    result
                )
            }
        }

        is KTypeParameter -> {
            require(typeWithVariablesArguments.isEmpty())
            result.putUnificationVariableResult(typeWithVariablesClassifier, knowConcreteType)
        }

        else -> throwNotAClassOrATypeParameter(typeWithVariablesClassifier)
    }
}

