@file:OptIn(InternalApi::class)

package com.huanshankeji.exposed

import com.huanshankeji.InternalApi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.*
import org.jetbrains.exposed.sql.statements.DeleteStatement

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
    throw NotImplementedError("The original API in Exposed is hidden.")

/**
 * Adapted from [org.jetbrains.exposed.sql.select].
 */
@Deprecated(
    SELECT_DSL_DEPRECATION_MESSAGE,
    ReplaceWith("selectAllStatement().where(where)")
)
fun FieldSet.selectStatement(where: BuildWhere): Query =
    throw NotImplementedError("The original API in Exposed is hidden.")

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
    where: WhereOp, isIgnore: Boolean = false, limit: Int? = null, offset: Long? = null
): DeleteStatement =
    // TODO `offset` unused
    DeleteStatement(targetsSet = this, where, isIgnore, limit, emptyList())

/**
 * Adapted from [org.jetbrains.exposed.sql.deleteWhere].
 */
fun <T : Table> T.deleteWhereStatement(
    limit: Int? = null, offset: Long? = null, where: TableAwareWithSqlExpressionBuilderBuildWhere<T>
): DeleteStatement =
    // TODO `offset` unused
    DeleteStatement(targetsSet = this, where(SqlExpressionBuilder), false, limit, emptyList())

/**
 * Adapted from [org.jetbrains.exposed.sql.deleteWhere].
 */
fun <T : Table> T.deleteIgnoreWhereStatement(
    limit: Int? = null, offset: Long? = null, where: TableAwareWithSqlExpressionBuilderBuildWhere<T>
): DeleteStatement =
    // TODO `offset` unused
    DeleteStatement(targetsSet = this, where(SqlExpressionBuilder), true, limit, emptyList())

// to access the protected `arguments` in the super class
class HelperInsertStatement<Key : Any>(table: Table, isIgnore: Boolean = false) :
    InsertStatement<Key>(table, isIgnore) {
    override var arguments: List<List<Pair<Column<*>, Any?>>>?
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
