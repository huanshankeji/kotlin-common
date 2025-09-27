@file:OptIn(InternalApi::class)

package com.huanshankeji.exposed

import com.huanshankeji.InternalApi
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.statements.*

@InternalApi
const val SELECT_DSL_DEPRECATION_MESSAGE =
    "As part of Exposed SELECT DSL design changes, this will be removed in future releases."

// The select queries are not executed eagerly so just use them directly.
/**
 * Adapted from [org.jetbrains.exposed.sql.select].
 */
@Deprecated(
    SELECT_DSL_DEPRECATION_MESSAGE,
    ReplaceWith("selectAllStatement().where(where)"),
    level = DeprecationLevel.ERROR
)
fun FieldSet.selectStatement(where: WhereOp): Nothing =
    throw UnsupportedOperationException("select(where) method removed in Exposed 1.0.0. Use selectAll().where(where) instead.")

/**
 * Adapted from [org.jetbrains.exposed.sql.select].
 */
@Deprecated(
    SELECT_DSL_DEPRECATION_MESSAGE,
    ReplaceWith("selectAllStatement().where(where)"),
    level = DeprecationLevel.ERROR
)
fun FieldSet.selectStatement(where: BuildWhere): Nothing =
    throw UnsupportedOperationException("select(where) method removed in Exposed 1.0.0. Use selectAll().where(where) instead.")

@Deprecated(
    SELECT_DSL_DEPRECATION_MESSAGE,
    ReplaceWith("selectAllStatement().where(where)"),
    level = DeprecationLevel.ERROR
)
fun <T : FieldSet> T.selectStatementTableAware(where: TableAwareBuildWhere<T>): Nothing =
    throw UnsupportedOperationException("selectStatement methods removed in Exposed 1.0.0. Use selectAll().where(where) instead.")

/**
 * You can also just use [selectAll].
 */
@Deprecated("Use selectAll() directly", ReplaceWith("selectAll()"), level = DeprecationLevel.WARNING)
fun FieldSet.selectAllStatement(): Nothing =
    throw UnsupportedOperationException("selectAllStatement() removed in Exposed 1.0.0. Use selectAll() directly.")

/**
 * You can also just use [select].
 */
@Deprecated("Use select() directly", ReplaceWith("select(columns)"), level = DeprecationLevel.WARNING) 
fun ColumnSet.selectStatement(columns: List<Expression<*>>): Nothing =
    throw UnsupportedOperationException("selectStatement() removed in Exposed 1.0.0. Use select() directly.")

/**
 * You can also just use [select].
 */
@Deprecated("Use select() directly", ReplaceWith("select(column, *columns)"), level = DeprecationLevel.WARNING)
fun ColumnSet.selectStatement(column: Expression<*>, vararg columns: Expression<*>): Nothing =
    throw UnsupportedOperationException("selectStatement() removed in Exposed 1.0.0. Use select() directly.")

/**
 * @see org.jetbrains.exposed.sql.deleteAll
 */
fun Table.deleteAllStatement() =
    DeleteStatement(this)

fun Table.deleteWhereStatement(
    op: WhereOp, isIgnore: Boolean = false, limit: Int? = null, offset: Long? = null
): DeleteStatement =
    DeleteStatement(this, op)

/**
 * Adapted from [org.jetbrains.exposed.sql.deleteWhere].
 */
fun <T : Table> T.deleteWhereStatement(
    limit: Int? = null, offset: Long? = null, op: TableAwareWithSqlExpressionBuilderBuildWhere<T>
): DeleteStatement =
    DeleteStatement(this, op(this))

/**
 * Adapted from [org.jetbrains.exposed.sql.deleteWhere].
 */
fun <T : Table> T.deleteIgnoreWhereStatement(
    limit: Int? = null, offset: Long? = null, op: TableAwareWithSqlExpressionBuilderBuildWhere<T>
): DeleteStatement =
    DeleteStatement(this, op(this))

// to access the protected `arguments` in the super class
class HelperInsertStatement<Key : Any>(table: Table, isIgnore: Boolean = false) :
    InsertStatement<Key>(table, isIgnore) {
    public override var arguments: List<List<Pair<Column<*>, Any?>>>?
        get() = super.arguments
        set(value) {
            super.arguments = value
        }
}

/**
 * Adapted from [org.jetbrains.exposed.sql.insert].
 */
fun <T : Table> T.insertStatement(body: T.(InsertStatement<Number>) -> Unit): HelperInsertStatement<Number> =
    HelperInsertStatement<Number>(this).apply {
        body(this)
    }

/**
 * Adapted from [org.jetbrains.exposed.sql.insertIgnore].
 */
fun <T : Table> T.insertIgnoreStatement(body: T.(InsertStatement<Number>) -> Unit): HelperInsertStatement<Number> =
    HelperInsertStatement<Number>(this, true).apply {
        body(this)
    }

fun Table.defaultColumnsForInsertSelect() =
    columns.filter { !it.columnType.isAutoInc }

/**
 * Adapted from [org.jetbrains.exposed.sql.insert].
 */
fun <T : Table> T.insertSelectStatement(
    selectQuery: AbstractQuery,
    columns: List<Column<*>> = defaultColumnsForInsertSelect(),
    isIgnore: Boolean = false
): InsertSelectStatement =
    InsertSelectStatement(columns, selectQuery, isIgnore)

fun <T : Table> T.updateStatementWithWhereOp(
    where: WhereOp? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement {
    val query = UpdateStatement(this, where)
    body(query)
    return query
}

/**
 * Adapted from [org.jetbrains.exposed.sql.update].
 */
fun <T : Table> T.updateStatement(
    where: BuildWhere? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement {
    val query = UpdateStatement(this, where?.let { it() })
    body(query)
    return query
}

fun <T : Table> T.updateStatementTableAware(
    where: TableAwareBuildWhere<T>? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement {
    val query = UpdateStatement(this, where?.let { it() })
    body(query)
    return query
}

/**
 * Adapted from [org.jetbrains.exposed.sql.replace].
 */
fun <T : Table> T.replaceStatement(body: T.(UpdateBuilder<*>) -> Unit): ReplaceStatement<Long> =
    ReplaceStatement<Long>(this).apply {
        body(this)
    }
