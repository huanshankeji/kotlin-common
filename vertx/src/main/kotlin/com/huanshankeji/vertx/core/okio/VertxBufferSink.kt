package com.huanshankeji.vertx.core.okio

import okio.Buffer
import okio.Sink
import okio.Timeout
import okio.buffer
import io.vertx.core.buffer.Buffer as VertxBuffer

@JvmInline
value class VertxBufferSink(val vertxBuffer: VertxBuffer) : Sink {
    override fun write(source: Buffer, byteCount: Long) {
        vertxBuffer.appendBytes(source.readByteArray(byteCount))
    }

    override fun flush() {}

    override fun timeout(): Timeout =
        Timeout.NONE //timeout

    override fun close() {}
}

fun VertxBuffer.toSink(): Sink =
    VertxBufferSink(this)

fun VertxBuffer.toBufferedSink() =
    toSink().buffer()
