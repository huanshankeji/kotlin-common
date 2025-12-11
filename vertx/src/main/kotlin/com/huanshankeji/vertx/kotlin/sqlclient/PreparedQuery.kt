package com.huanshankeji.vertx.kotlin.sqlclient

import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.PreparedQuery
import io.vertx.sqlclient.SqlResult
import io.vertx.sqlclient.Tuple

suspend fun <SqlResultT : SqlResult<*>> PreparedQuery<SqlResultT>.executeBatchCoAwaitForSqlResultSequence(batch: List<Tuple>) =
    executeBatch(batch).coAwait().batchSqlResultSequence()

@Deprecated(
    "Use `executeBatchCoAwaitForSqlResultSequence`.",
    ReplaceWith("this.executeBatchCoAwaitForSqlResultSequence(batch)")
)
suspend fun <SqlResultT : SqlResult<*>> PreparedQuery<SqlResultT>.executeBatchAwaitForSqlResultSequence(batch: List<Tuple>) =
    executeBatchCoAwaitForSqlResultSequence(batch)
