package com.huanshankeji.exposed

import org.jetbrains.exposed.v1.core.Op

// TODO These `BuildWhere`s should probably just be removed.

typealias BuildWhere = () -> Op<Boolean>
@Deprecated("Renamed to `WhereBuilder`", ReplaceWith("BuildWhere"/*, "com.huanshankeji.exposed.BuildWhere"*/))
typealias Where = BuildWhere
typealias WhereOp = Op<Boolean>
typealias TableAwareBuildWhere<T/*: FieldSet*/> = T.() -> Op<Boolean>
typealias TableAwareWithSqlExpressionBuilderBuildWhere<T> = T.() -> Op<Boolean>
