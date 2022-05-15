package com.huanshankeji.exposed

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

typealias Where = SqlExpressionBuilder.() -> Op<Boolean>