package com.huanshankeji.vertx.sqlclient

import com.huanshankeji.Untested
import com.huanshankeji.collections.lexicographicOrderComparable
import io.vertx.sqlclient.PreparedQuery
import io.vertx.sqlclient.Tuple

/**
 * There can easily be deadlocks when batch-updating data concurrently in multiple event loops
 * if they are overlapped and unsorted.
 */
@Untested
fun <QueryResultT, DataT, SelectorResultT : Comparable<SelectorResultT>> PreparedQuery<QueryResultT>.sortDataAndExecuteBatch(
    dataList: List<DataT>, selector: (DataT) -> SelectorResultT, toTuple: DataT.() -> Tuple
) =
    executeBatch(dataList.sortedBy(selector).map(toTuple))


@Untested
inline fun <QueryResultT, SelectorResultT : Comparable<SelectorResultT>> PreparedQuery<QueryResultT>.sortTuplesAndExecuteBatch(
    batch: List<Tuple>, crossinline selector: (Tuple) -> SelectorResultT
) =
    executeBatch(batch.sortedBy(selector))


@Untested
inline fun <QueryResultT, reified SelectorResultT : Comparable<SelectorResultT>> PreparedQuery<QueryResultT>.sortTuplesAndExecuteBatch(
    batch: List<Tuple>, selectorPosition: Int
) =
    sortTuplesAndExecuteBatch(batch) { it.get(SelectorResultT::class.java, selectorPosition) }

@Untested
fun <QueryResultT> PreparedQuery<QueryResultT>.sortTuplesAndExecuteBatch(
    batch: List<Tuple>, selectorPositions: List<Int>
) =
    sortTuplesAndExecuteBatch(batch) { tuple ->
        val listSelector = selectorPositions.map { i -> tuple.getValue(i) }
        @Suppress("UNCHECKED_CAST")
        (listSelector as List<Comparable<Any>>).lexicographicOrderComparable()
    }
