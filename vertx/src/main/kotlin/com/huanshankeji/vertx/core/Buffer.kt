package com.huanshankeji.vertx.core

import io.netty.buffer.Unpooled
import io.vertx.core.buffer.Buffer
import io.vertx.core.buffer.impl.BufferImpl

/**
 * Creates a wrapped [Buffer] so that unnecessary copy is avoided.
 * Also see https://github.com/eclipse-vertx/vert.x/issues/4407.
 */
fun wrappedBuffer(byteArray: ByteArray): Buffer =
    BufferImpl(Unpooled.wrappedBuffer(byteArray))
