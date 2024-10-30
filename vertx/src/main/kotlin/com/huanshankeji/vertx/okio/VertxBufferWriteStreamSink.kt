package com.huanshankeji.vertx.okio

import io.vertx.core.buffer.Buffer
import io.vertx.core.streams.WriteStream
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.runBlocking
import okio.Sink
import okio.Timeout
import okio.buffer

@JvmInline
value class VertxBufferWriteStreamSink(val writeStream: WriteStream<Buffer>) : Sink {
    override fun write(source: okio.Buffer, byteCount: Long) {
        runBlocking {
            writeStream.write(Buffer.buffer(source.readByteArray(byteCount))).coAwait()
        }
    }

    override fun flush() {}

    //private val timeout = Timeout()
    override fun timeout(): Timeout =
        Timeout.NONE //timeout

    override fun close() {
        writeStream.end()
    }
}

fun WriteStream<Buffer>.toSink(): Sink =
    VertxBufferWriteStreamSink(this)

fun WriteStream<Buffer>.toBufferedSink(): Sink =
    toSink().buffer()
