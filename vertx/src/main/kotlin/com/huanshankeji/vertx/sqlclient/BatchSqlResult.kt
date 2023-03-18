package com.huanshankeji.vertx.sqlclient

import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlResult

fun <SqlResultT : SqlResult<*>> SqlResultT.batchSqlResultSequence(): Sequence<SqlResultT> =
    generateSequence(this) {
        @Suppress("UNCHECKED_CAST")
        it.next() as SqlResultT?
    }

fun SqlResult<*>.batchSqlResultRowCountSequence() =
    batchSqlResultSequence().map { it.rowCount() }

fun <R> RowSet<R>.batchSqlResultRowSequence() =
    batchSqlResultSequence().map { asSequence() }
