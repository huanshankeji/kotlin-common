package com.huanshankeji

/**
 * Used in places when curly braces are needed (`if` and `when`) to make the code look better.
 */
inline fun lambdaOf(noinline block: () -> Unit) =
    block

/**
 * Used in places when curly braces are needed (`if` and `when`) to make the code look better.
 */
inline fun <T, R> lambdaOf(noinline block: (T) -> R) =
    block

/**
 * A shortcut for the empty lambda, used in places when curly braces are needed (`if` and `when`) to make the code look better.
 */
inline fun emptyLambda() =
    {}
