package com.huanshankeji.random

import kotlin.random.Random
import kotlin.random.nextUInt

fun Random.nextUShort() =
    nextUInt(UShort.MAX_VALUE.toUInt() + 1u)

fun Random.nextShort() =
    nextUShort().toShort()
