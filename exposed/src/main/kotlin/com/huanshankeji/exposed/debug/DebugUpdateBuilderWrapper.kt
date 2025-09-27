package com.huanshankeji.exposed.debug

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.statements.*
import java.io.PrintStream

// TODO remove or update this class
/**
 * An [UpdateBuilder] wrapper that print the columns set.
 * @deprecated This class doesn't work with Exposed 1.0.0+ due to API changes and will be removed in a future version.
 */
@Deprecated("This class doesn't work with Exposed 1.0.0+ due to API changes and will be removed in a future version.")
class DebugUpdateBuilderWrapper<out T>(val updateBuilder: UpdateBuilder<T>, val out: PrintStream = System.out) {
    @Deprecated("This method doesn't work with Exposed 1.0.0+ due to API changes.", level = DeprecationLevel.ERROR)
    fun createWrapper(): Nothing = 
        throw UnsupportedOperationException("DebugUpdateBuilderWrapper is not compatible with Exposed 1.0.0+. Please use alternative debugging methods.")
}
