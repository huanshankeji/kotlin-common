@file:OptIn(InternalApi::class)

package com.huanshankeji.exposed

import com.huanshankeji.InternalApi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.*

@InternalApi
const val SELECT_DSL_DEPRECATION_MESSAGE =
    "As part of Exposed SELECT DSL design changes, this will be removed in future releases."

// The select queries are not executed eagerly so just use them directly.
/**
 * Adapted from [org.jetbrains.exposed.sql.select].
 */
@Deprecated(
    SELECT_DSL_DEPRECATION_MESSAGE,
    ReplaceWith("selectAllStatement().where(where)")
)
fun FieldSet.selectStatement(where: WhereOp): Query =
    @Suppress("DEPRECATION_ERROR")
    select(where)

/**
 * Adapted from [org.jetbrains.exposed.sql.select].
 */
@Deprecated(
    SELECT_DSL_DEPRECATION_MESSAGE,
    ReplaceWith("selectAllStatement().where(where)")
)
fun FieldSet.selectStatement(where: BuildWhere): Query =
    @Suppress("DEPRECATION_ERROR")
    select(where)

@Deprecated(
    SELECT_DSL_DEPRECATION_MESSAGE,
    ReplaceWith("selectAllStatement().where(where)")
)
fun <T : FieldSet> T.selectStatementTableAware(where: TableAwareBuildWhere<T>): Query =
    selectStatement(where())

/**
 * You can also just use [selectAll].
 */
fun FieldSet.selectAllStatement() =
    selectAll()

/**
 * You can also just use [select].
 */
fun ColumnSet.selectStatement(columns: List<Expression<*>>) =
    select(columns)

/**
 * You can also just use [select].
 */
fun ColumnSet.selectStatement(column: Expression<*>, vararg columns: Expression<*>): Query =
    select(column, *columns)

/**
 * @see org.jetbrains.exposed.sql.deleteAll
 */
fun Table.deleteAllStatement() =
    DeleteStatement(this)

fun Table.deleteWhereStatement(
    op: WhereOp, isIgnore: Boolean = false, limit: Int? = null, offset: Long? = null
): DeleteStatement =
    DeleteStatement(this, op, isIgnore, limit, offset)

/**
 * Adapted from [org.jetbrains.exposed.sql.deleteWhere].
 */
fun <T : Table> T.deleteWhereStatement(
    limit: Int? = null, offset: Long? = null, op: TableAwareWithSqlExpressionBuilderBuildWhere<T>
): DeleteStatement =
    DeleteStatement(this, op(SqlExpressionBuilder), false, limit, offset)

/**
 * Adapted from [org.jetbrains.exposed.sql.deleteWhere].
 */
fun <T : Table> T.deleteIgnoreWhereStatement(
    limit: Int? = null, offset: Long? = null, op: TableAwareWithSqlExpressionBuilderBuildWhere<T>
): DeleteStatement =
    DeleteStatement(this, op(SqlExpressionBuilder), true, limit, offset)

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
    columns.filter { !it.columnType.isAutoInc || it.autoIncColumnType?.nextValExpression != null }

/**
 * Adapted from [org.jetbrains.exposed.sql.insert].
 */
fun <T : Table> T.insertSelectStatement(
    selectQuery: AbstractQuery<*>,
    columns: List<Column<*>> = defaultColumnsForInsertSelect(),
    isIgnore: Boolean = false
): InsertSelectStatement =
    InsertSelectStatement(columns, selectQuery, isIgnore)

fun <T : Table> T.updateStatementWithWhereOp(
    where: WhereOp? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement {
    val query = UpdateStatement(this, limit, where)
    body(query)
    return query
}

/**
 * Adapted from [org.jetbrains.exposed.sql.update].
 */
fun <T : Table> T.updateStatement(
    where: BuildWhere? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement {
    val query = UpdateStatement(this, limit, where?.let { SqlExpressionBuilder.it() })
    body(query)
    return query
}

fun <T : Table> T.updateStatementTableAware(
    where: TableAwareBuildWhere<T>? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement {
    val query = UpdateStatement(this, limit, where?.let { it() })
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
