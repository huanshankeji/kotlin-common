package com.huanshankeji.exposed

import com.huanshankeji.Untested
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.FieldSet

/**
 * To simplify the SQL in some expression queries.
 * For example `Dual.slice(exists(Table.emptySlice().select(op))).selectAll()` generates a SQL with no columns which is simpler.
 */
@Deprecated(
    "This causes \"java.lang.IllegalArgumentException: Can't prepare SELECT statement without columns or expressions to retrieve\" in the latest version of Exposed. " +
            "Also, the original API in Exposed is removed."
)
fun ColumnSet.emptySlice(): FieldSet =
    throw NotImplementedError("The original API in Exposed is removed.")

@Untested
fun ColumnSet.selectEmpty() =
    select(emptyList())
