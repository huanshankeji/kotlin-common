package com.huanshankeji.kotlinx.serialization

import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

internal actual fun KType.copyWithArguments(arguments: List<KTypeProjection>): KType =
    object : KType {
        override val classifier = this@copyWithArguments.classifier
        override val arguments = arguments
        override val isMarkedNullable = this@copyWithArguments.isMarkedNullable
    }
