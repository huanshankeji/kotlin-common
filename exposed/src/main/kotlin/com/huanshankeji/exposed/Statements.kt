package com.huanshankeji.exposed

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.*

// The select queries are not executed eagerly so just use them directly.
/*
fun FieldSet.selectStatement(where: Op<Boolean>): Query =
    select(where)
*/

/**
 * @see org.jetbrains.exposed.sql.deleteAll
 */
fun Table.deleteAllStatement() =
    DeleteStatement(this)

/**
 * Adapted from [org.jetbrains.exposed.sql.deleteWhere].
 */
fun Table.deleteWhereStatement(
    limit: Int? = null, offset: Long? = null, op: Where
): DeleteStatement =
    DeleteStatement(this, SqlExpressionBuilder.op(), false, limit, offset)

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

/**
 * Adapted from [org.jetbrains.exposed.sql.update].
 */
fun <T : Table> T.updateStatement(
    where: Where? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement {
    val query = UpdateStatement(this, limit, where?.let { SqlExpressionBuilder.it() })
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
