package com.huanshankeji.exposed

import org.jetbrains.exposed.sql.ColumnSet

/**
 * To simplify the SQL in some expression queries.
 * For example `Dual.slice(exists(Table.emptySlice().select(op))).selectAll()` generates a SQL with no columns which is simpler.
 */
fun ColumnSet.emptySlice() =
    slice(emptyList())
