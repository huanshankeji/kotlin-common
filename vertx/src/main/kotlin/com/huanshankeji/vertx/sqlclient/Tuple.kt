package com.huanshankeji.vertx.sqlclient

import io.vertx.sqlclient.Tuple

fun Tuple.toList() =
    List(size()) { getValue(it) }
