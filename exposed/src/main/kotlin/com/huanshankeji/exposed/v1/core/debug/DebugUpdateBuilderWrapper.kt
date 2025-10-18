package com.huanshankeji.exposed.v1.core.debug

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import java.io.PrintStream

/**
 * An [UpdateBuilder] wrapper that print the columns set.
 */
class DebugUpdateBuilderWrapper<out T>(val updateBuilder: UpdateBuilder<T>, val out: PrintStream = System.out) :
    UpdateBuilder<T>(updateBuilder.type, updateBuilder.targets) {
    override fun arguments(): Iterable<Iterable<Pair<IColumnType<*>, Any?>>> = updateBuilder.arguments()
    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String =
        updateBuilder.prepareSQL(transaction, prepared)

    override fun <S> set(column: Column<S>, value: S) {
        out.println("$updateBuilder[$column] = $value")
        super.set(column, value)
    }

    override fun <T, S : T?, E : Expression<S>> set(column: Column<T>, value: E) {
        out.println("$updateBuilder[$column] = $value")
        super.set(column, value)
    }

    override fun <S> set(column: Column<S>, value: AbstractQuery<*>) {
        out.println("$updateBuilder[$column] = $value")
        super.set(column, value)
    }

    override fun <S> set(column: CompositeColumn<S>, value: S) {
        out.println("$updateBuilder[$column] = $value")
        super.set(column, value)
    }
}
