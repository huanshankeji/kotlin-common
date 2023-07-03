package com.huanshankeji.exposed

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

typealias BuildWhere = SqlExpressionBuilder.() -> Op<Boolean>
@Deprecated("Renamed to `WhereBuilder`", ReplaceWith("BuildWhere"))
typealias Where = BuildWhere
typealias WhereOp = Op<Boolean>
typealias TableAwareBuildWhere<T/*: FieldSet*/> = T.() -> Op<Boolean>