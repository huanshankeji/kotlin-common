package com.huanshankeji.vertx.kotlin.sqlclient

import io.vertx.sqlclient.*

fun <SqlResultT : SqlResult<*>> SqlResultT.batchSqlResultSequence(): Sequence<SqlResultT> =
    generateSequence(this) {
        @Suppress("UNCHECKED_CAST")
        it.next() as SqlResultT?
    }

fun SqlResult<*>.batchSqlResultRowCountSequence() =
    batchSqlResultSequence().map { it.rowCount() }

fun <R> RowSet<R>.batchSqlResultRowSequenceSequence() =
    batchSqlResultSequence().map { asSequence() }
