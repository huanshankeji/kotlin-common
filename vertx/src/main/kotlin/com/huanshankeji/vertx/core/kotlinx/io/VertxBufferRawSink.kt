package com.huanshankeji.vertx.core.kotlinx.io

import kotlinx.io.RawSink
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import io.vertx.core.buffer.Buffer as VertxBuffer
import kotlinx.io.Buffer as KotlinxIoBuffer

@JvmInline
value class VertxBufferRawSink(val vertxBuffer: VertxBuffer) : RawSink {
    override fun write(source: KotlinxIoBuffer, byteCount: Long) {
        vertxBuffer.appendBytes(source.readByteArray(byteCount.toIntOrThrow()))
    }

    override fun flush() {}

    override fun close() {}
}

fun VertxBuffer.toRawSink(): RawSink =
    VertxBufferRawSink(this)

fun VertxBuffer.toSink(): Sink =
    toRawSink().buffered()
