package com.huanshankeji.vertx.core

import io.netty.buffer.Unpooled
import io.vertx.core.buffer.Buffer

/**
 * Creates a wrapped [Buffer] so that unnecessary copy is avoided.
 * Also see https://github.com/eclipse-vertx/vert.x/issues/4407.
 */
fun wrappedBuffer(byteArray: ByteArray): Buffer =
    Buffer.buffer(Unpooled.wrappedBuffer(byteArray))
