package com.huanshankeji.exposed.debug

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import java.io.PrintStream

// TODO remove or update this class
/**
 * An [UpdateBuilder] wrapper that print the columns set.
 */
class DebugUpdateBuilderWrapper<out T>(val updateBuilder: UpdateBuilder<T>, val out: PrintStream = System.out) :
    UpdateBuilder<T>(updateBuilder.type, updateBuilder.targets) {
    override fun arguments(): Iterable<Iterable<Pair<IColumnType<*>, Any?>>> = updateBuilder.arguments()
    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String =
        updateBuilder.prepareSQL(transaction, prepared)

    override fun PreparedStatementApi.executeInternal(transaction: Transaction): T? =
        with(updateBuilder) { executeInternal(transaction) }

    override fun <S> set(column: Column<S>, value: S) {
        out.println("$updateBuilder[$column] = $value")
        updateBuilder.set(column, value)
    }

    /*
    override fun <T, S : T, E : Expression<S>> set(column: Column<T>, value: E) {
        out.println("$updateBuilder[$column] = $value")
        updateBuilder.set(column, value)
    }
    */

    override fun <S> set(column: Column<S>, value: Query) {
        out.println("$updateBuilder[$column] = $value")
        updateBuilder.set(column, value)
    }
}
