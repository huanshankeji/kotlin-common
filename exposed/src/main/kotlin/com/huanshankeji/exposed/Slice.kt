package com.huanshankeji.exposed

import com.huanshankeji.Untested
import org.jetbrains.exposed.sql.ColumnSet

/**
 * To simplify the SQL in some expression queries.
 * For example `Dual.slice(exists(Table.emptySlice().select(op))).selectAll()` generates a SQL with no columns which is simpler.
 */
@Deprecated("This causes \"java.lang.IllegalArgumentException: Can't prepare SELECT statement without columns or expressions to retrieve\" in the latest version of Exposed.")
fun ColumnSet.emptySlice() =
    @Suppress("DEPRECATION_ERROR")
    slice(emptyList())

@Untested
fun ColumnSet.selectEmpty() =
    select(emptyList())
