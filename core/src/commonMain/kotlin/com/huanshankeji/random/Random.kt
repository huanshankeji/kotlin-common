package com.huanshankeji.random

import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.random.nextUInt

fun Random.nextUShort() =
    nextUInt(UShort.MAX_VALUE.toUInt() + 1u)

fun Random.nextShort() =
    nextUShort().toShort()

fun Random.nextByte() =
    nextBytes(1).first()

@ExperimentalUnsignedTypes
fun Random.nextUByte() =
    nextUBytes(1).first()
