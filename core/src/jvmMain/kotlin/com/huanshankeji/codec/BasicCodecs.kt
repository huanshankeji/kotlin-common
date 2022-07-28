package com.huanshankeji.codec

import java.util.*

fun uLongBigEndianShiftOffset(index: Int): Int =
    (7 - index) * 8

fun ULong.toBigEndianUBytes(): UByteArray =
    (0 until 8).map { (this shr uLongBigEndianShiftOffset(it)).toUByte() }.toUByteArray()

fun Long.toBigEndianBytes(): ByteArray =
    toULong().toBigEndianUBytes().asByteArray()

fun UByteArray.bigEndianToULong(): ULong {
    require(size == 8)
    return asSequence()
        .mapIndexed { index, byte -> byte.toULong() shl uLongBigEndianShiftOffset(index) }
        .reduce { acc, long -> acc or long }
}

fun ByteArray.bigEndianToLong(): Long =
    asUByteArray().bigEndianToULong().toLong()

fun List<UByte>.bigEndianToULong(): ULong {
    require(size == 8)
    return asSequence()
        .mapIndexed { index, byte -> byte.toULong() shl uLongBigEndianShiftOffset(index) }
        .reduce { acc, long -> acc or long }
}

fun List<Byte>.bigEndianToLong(): Long =
    map { it.toUByte() }.bigEndianToULong().toLong()


fun ByteArray.toBase64String(): String =
    Base64.getEncoder().encodeToString(this)

fun String.base64ToBytes(): ByteArray =
    Base64.getDecoder().decode(this)
