package com.huanshankeji.exposed.v1.core

import org.jetbrains.exposed.v1.core.Op

// TODO These `BuildWhere`s should probably just be removed.

typealias BuildWhere = () -> Op<Boolean>
@Deprecated("Renamed to `WhereBuilder`", ReplaceWith("BuildWhere"/*, "com.huanshankeji.exposed.v1.core.BuildWhere"*/))
typealias Where = BuildWhere
typealias WhereOp = Op<Boolean>
// This type tends to make the code harder to understand. Consider just inlining it.
typealias TableAwareBuildWhere<T/*: FieldSet*/> = T.() -> Op<Boolean>
@Deprecated(
    "`SqlExpressionBuilder` is deprecated and removed. This is now identical to `TableAwareBuildWhere`.",
    ReplaceWith("TableAwareBuildWhere<T>")
)
typealias TableAwareWithSqlExpressionBuilderBuildWhere<T> = T.() -> Op<Boolean>
