package com.huanshankeji.collections

class LexicographicOrderListComparable<E : Comparable<E>> internal constructor(val list: List<E>) :
    Comparable<LexicographicOrderListComparable<E>> {
    override fun compareTo(other: LexicographicOrderListComparable<E>): Int {
        val otherList = other.list
        tailrec fun helper(index: Int): Int {
            val a = list.getOrNull(index)
            val b = otherList.getOrNull(index)
            return if (a === null)
                if (b === null) 0
                else -1
            else
                if (b === null) 1
                else {
                    val elementCompareResult = a.compareTo(b)
                    if (elementCompareResult != 0) elementCompareResult
                    else helper(index + 1)
                }
        }

        return helper(0)
    }
}

fun <E : Comparable<E>> List<E>.lexicographicOrderComparable() =
    LexicographicOrderListComparable(this)
