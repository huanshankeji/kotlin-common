package com.huanshankeji.vertx.kotlin.sqlclient

import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.PreparedQuery
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple

suspend fun <R> PreparedQuery<RowSet<R>>.executeBatchAwaitForRowSetSequence(batch: List<Tuple>) =
    executeBatch(batch).await().batchSqlResultSequence()
