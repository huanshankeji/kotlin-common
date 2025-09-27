package com.huanshankeji.exposed

import org.jetbrains.exposed.v1.core.ISqlExpressionBuilder
import org.jetbrains.exposed.v1.core.Op

typealias BuildWhere = () -> Op<Boolean>
@Deprecated("Renamed to `BuildWhere`", ReplaceWith("BuildWhere"))
typealias Where = BuildWhere
typealias WhereOp = Op<Boolean>
typealias TableAwareBuildWhere<T/*: FieldSet*/> = T.() -> Op<Boolean>
typealias TableAwareWithSqlExpressionBuilderBuildWhere<T> = T.(ISqlExpressionBuilder) -> Op<Boolean>
