package com.huanshankeji.exposed

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.*

// The select queries are not executed eagerly so just use them directly.
/**
 * Adapted from [org.jetbrains.exposed.sql.select].
 */
fun FieldSet.selectStatement(where: WhereOp): Query =
    select(where)

/**
 * Adapted from [org.jetbrains.exposed.sql.select].
 */
fun FieldSet.selectStatement(where: BuildWhere): Query =
    select(where)

fun <T : FieldSet> T.selectStatementTableAware(where: TableAwareBuildWhere<T>): Query =
    selectStatement(where())

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
fun Table.deleteWhereStatement(
    isIgnore: Boolean = false, limit: Int? = null, offset: Long? = null, op: BuildWhere
): DeleteStatement =
    DeleteStatement(this, SqlExpressionBuilder.op(), isIgnore, limit, offset)

fun <T : Table> T.deleteWhereStatementTableAware(
    isIgnore: Boolean = false, limit: Int? = null, offset: Long? = null, op: TableAwareBuildWhere<T>
): DeleteStatement =
    DeleteStatement(this, op(), isIgnore, limit, offset)

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

fun <T : Table> T.updateStatement(
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
