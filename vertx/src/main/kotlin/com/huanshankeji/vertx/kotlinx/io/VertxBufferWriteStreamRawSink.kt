package com.huanshankeji.vertx.kotlinx.io

import io.vertx.core.buffer.Buffer
import io.vertx.core.streams.WriteStream
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.runBlocking
import kotlinx.io.RawSink
import kotlinx.io.buffered
import kotlinx.io.readByteArray

@JvmInline
value class VertxBufferWriteStreamRawSink(val writeStream: WriteStream<Buffer>) : RawSink {
    override fun write(source: kotlinx.io.Buffer, byteCount: Long) {
        runBlocking {
            writeStream.write(Buffer.buffer(source.readByteArray(byteCount.toIntOrThrow()))).coAwait()
        }
    }

    override fun flush() {}

    override fun close() {
        writeStream.end()
    }
}

fun WriteStream<Buffer>.toRawSink(): RawSink =
    VertxBufferWriteStreamRawSink(this)

fun WriteStream<Buffer>.toSink() =
    toRawSink().buffered()
