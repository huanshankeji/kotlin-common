package com.huanshankeji.exposed

import com.huanshankeji.Untested
import org.jetbrains.exposed.v1.core.ColumnSet

/**
 * To simplify the SQL in some expression queries.
 * For example `Dual.slice(exists(Table.emptySlice().select(op))).selectAll()` generates a SQL with no columns which is simpler.
 */
@Deprecated("This causes \"java.lang.IllegalArgumentException: Can't prepare SELECT statement without columns or expressions to retrieve\" in the latest version of Exposed.")
fun ColumnSet.emptySlice() =
    @Suppress("DEPRECATION_ERROR")
    slice(emptyList())

@Untested
@Deprecated("This causes \"java.lang.IllegalArgumentException: Can't prepare SELECT statement without columns or expressions to retrieve\" in Exposed 1.0.0+.", level = DeprecationLevel.ERROR)
fun ColumnSet.selectEmpty(): Nothing =
    throw UnsupportedOperationException("selectEmpty() is not supported in Exposed 1.0.0+ as it causes IllegalArgumentException. Use a proper column selection instead.")
