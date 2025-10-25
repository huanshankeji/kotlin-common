package com.huanshankeji.exposed.v1.jdbc

import com.huanshankeji.Untested
import org.jetbrains.exposed.v1.core.ColumnSet
import org.jetbrains.exposed.v1.jdbc.select

@Untested
fun ColumnSet.selectEmpty() =
    select(emptyList())
