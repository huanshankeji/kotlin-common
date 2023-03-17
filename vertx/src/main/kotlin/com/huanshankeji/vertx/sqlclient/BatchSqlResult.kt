package com.huanshankeji.vertx.sqlclient

import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlResult

fun <SqlResultT : SqlResult<*>> SqlResultT.batchSqlResultSequence(): Sequence<SqlResultT> = sequence {
    var currentSqlResult: SqlResultT? = this@batchSqlResultSequence

    while (currentSqlResult !== null) {
        yield(currentSqlResult)
        currentSqlResult = currentSqlResult.next() as SqlResultT?
    }
}

fun SqlResult<*>.batchSqlResultRowCountSequence() =
    batchSqlResultSequence().map { it.rowCount() }

fun <R> RowSet<R>.batchSqlResultRowSequence() =
    batchSqlResultSequence().map { asSequence() }
