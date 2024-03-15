package com.huanshankeji

interface BidirectionalConversion<T1, T2> {
    fun to(value: T1): T2
    fun from(value: T2): T1
}