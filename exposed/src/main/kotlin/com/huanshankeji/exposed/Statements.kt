@file:OptIn(InternalApi::class)

package com.huanshankeji.exposed

import com.huanshankeji.InternalApi
import com.huanshankeji.exposed.v1.core.BuildWhere
import com.huanshankeji.exposed.v1.core.TableAwareBuildWhere
import com.huanshankeji.exposed.v1.core.TableAwareWithSqlExpressionBuilderBuildWhere
import com.huanshankeji.exposed.v1.core.WhereOp
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.statements.*
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

@InternalApi
const val SELECT_DSL_DEPRECATION_MESSAGE =
    "As part of Exposed SELECT DSL design changes, this will be removed in future releases."
internal const val USE_EXPOSED_JDBC_API_DEPRECATION_MESSAGE = "Use the API in `org.jetbrains.exposed.v1.jdbc` directly."
internal const val USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE =
    "Use the new `buildStatement` API in Exposed."

// The select queries are not executed eagerly so just use them directly.
/**
 * Adapted from [Query].
 */
@Deprecated(
    SELECT_DSL_DEPRECATION_MESSAGE,
    ReplaceWith("selectAllStatement().where(where)")
)
fun FieldSet.selectStatement(where: WhereOp): Query =
    throw NotImplementedError("The original API in Exposed is hidden.")

/**
 * Adapted from [Query].
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
@Deprecated(
    USE_EXPOSED_JDBC_API_DEPRECATION_MESSAGE,
    ReplaceWith("selectAll()", "org.jetbrains.exposed.v1.jdbc.selectAll")
)
fun FieldSet.selectAllStatement() =
    selectAll()

/**
 * You can also just use [select].
 */
@Deprecated(
    USE_EXPOSED_JDBC_API_DEPRECATION_MESSAGE,
    ReplaceWith("select(columns)", "org.jetbrains.exposed.v1.jdbc.select")
)
fun ColumnSet.selectStatement(columns: List<Expression<*>>) =
    select(columns)

/**
 * You can also just use [select].
 */
@Deprecated(
    USE_EXPOSED_JDBC_API_DEPRECATION_MESSAGE,
    ReplaceWith("select(column, *columns)", "org.jetbrains.exposed.v1.jdbc.select")
)
fun ColumnSet.selectStatement(column: Expression<*>, vararg columns: Expression<*>): Query =
    select(column, *columns)

/**
 * @see org.jetbrains.exposed.v1.jdbc.deleteAll
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith("buildStatement { deleteAll() }", "org.jetbrains.exposed.v1.core.statements.buildStatement")
)
fun Table.deleteAllStatement() =
    buildStatement { deleteAll() }

/**
 * @see org.jetbrains.exposed.v1.jdbc.deleteWhere
 * @see StatementBuilder.deleteWhere
 * @see StatementBuilder.deleteIgnoreWhere
 * @param offset no longer used
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { if (isIgnore) deleteIgnoreWhere(limit) { where } else deleteWhere(limit) { where } }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun Table.deleteWhereStatement(
    where: WhereOp, isIgnore: Boolean = false, limit: Int? = null, offset: Long? = null
): DeleteStatement =
    buildStatement { if (isIgnore) deleteIgnoreWhere(limit) { where } else deleteWhere(limit) { where } }

/**
 * Adapted from [org.jetbrains.exposed.v1.jdbc.deleteWhere].
 * @param offset no longer used
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { deleteWhere(limit, where) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.deleteWhereStatement(
    limit: Int? = null, offset: Long? = null, where: TableAwareWithSqlExpressionBuilderBuildWhere<T>
): DeleteStatement =
    buildStatement { deleteWhere(limit, where) }

/**
 * Adapted from [org.jetbrains.exposed.v1.jdbc.deleteWhere].
 * @param offset no longer used
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { deleteIgnoreWhere(limit, where) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.deleteIgnoreWhereStatement(
    limit: Int? = null, offset: Long? = null, where: TableAwareWithSqlExpressionBuilderBuildWhere<T>
): DeleteStatement =
    buildStatement { deleteIgnoreWhere(limit, where) }

// to access the protected `arguments` in the super class
@Deprecated("No longer needed.", ReplaceWith("InsertStatement<Key>"))
class HelperInsertStatement<Key : Any>(table: Table, isIgnore: Boolean = false) :
    InsertStatement<Key>(table, isIgnore) {
    override var arguments: List<List<Pair<Column<*>, Any?>>>?
        get() = super.arguments
        set(value) {
            super.arguments = value
        }
}

/**
 * Adapted from [org.jetbrains.exposed.v1.jdbc.insert].
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { insert(body) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.insertStatement(body: T.(InsertStatement<Number>) -> Unit): InsertStatement<Number> =
    buildStatement { insert(body) }

/**
 * Adapted from [org.jetbrains.exposed.v1.jdbc.insertIgnore].
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { insertIgnore(body) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.insertIgnoreStatement(body: T.(InsertStatement<Number>) -> Unit): InsertStatement<Number> =
    buildStatement {
        // cast here to avoid changing the signature to match Exposed's API exactly
        @Suppress("UNCHECKED_CAST")
        insertIgnore(body as (T.(UpdateBuilder<*>) -> Unit)) as InsertStatement<Number>
    }

@Deprecated("Copied from Exposed. No longer needed.")
fun Table.defaultColumnsForInsertSelect() =
    columns.filter { !it.columnType.isAutoInc || it.autoIncColumnType?.nextValExpression != null }

/**
 * Adapted from [org.jetbrains.exposed.v1.jdbc.insert].
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { if (isIgnore) insertIgnore(selectQuery, columns) else insert(selectQuery, columns) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.insertSelectStatement(
    selectQuery: AbstractQuery<*>,
    columns: List<Column<*>> = defaultColumnsForInsertSelect(),
    isIgnore: Boolean = false
): InsertSelectStatement =
    buildStatement { if (isIgnore) insertIgnore(selectQuery, columns) else insert(selectQuery, columns) }

@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { update(where?.let { { it } }, limit, body) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.updateStatementWithWhereOp(
    where: WhereOp? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement =
    buildStatement { update(where?.let { { it } }, limit, body) }

/**
 * Adapted from [org.jetbrains.exposed.v1.jdbc.update].
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { update(where, limit, body) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.updateStatement(
    where: BuildWhere? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement =
    buildStatement { update(where, limit, body) }

@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { update(where?.let { { it() } }, limit, body) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.updateStatementTableAware(
    where: (T.() -> Op<Boolean>)? = null, limit: Int? = null, body: T.(UpdateStatement) -> Unit
): UpdateStatement =
    buildStatement { update(where?.let { { it() } }, limit, body) }

/**
 * Adapted from [org.jetbrains.exposed.v1.jdbc.replace].
 */
@Deprecated(
    USE_EXPOSED_BUILD_STATEMENT_API_DEPRECATION_MESSAGE,
    ReplaceWith(
        "buildStatement { replace(body) }",
        "org.jetbrains.exposed.v1.core.statements.buildStatement"
    )
)
fun <T : Table> T.replaceStatement(body: T.(UpdateBuilder<*>) -> Unit): ReplaceStatement<Long> =
    buildStatement { replace(body) }
