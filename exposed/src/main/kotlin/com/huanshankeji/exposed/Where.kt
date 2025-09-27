package com.huanshankeji.exposed

import org.jetbrains.exposed.v1.core.ISqlExpressionBuilder
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder

typealias BuildWhere = SqlExpressionBuilder.() -> Op<Boolean>
@Deprecated("Renamed to `WhereBuilder`", ReplaceWith("BuildWhere"/*, "com.huanshankeji.exposed.BuildWhere"*/))
typealias Where = BuildWhere
typealias WhereOp = Op<Boolean>
typealias TableAwareBuildWhere<T/*: FieldSet*/> = T.() -> Op<Boolean>
typealias TableAwareWithSqlExpressionBuilderBuildWhere<T> = T.(ISqlExpressionBuilder) -> Op<Boolean>
