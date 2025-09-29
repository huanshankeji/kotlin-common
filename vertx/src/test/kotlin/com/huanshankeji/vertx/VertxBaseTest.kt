package com.huanshankeji.vertx

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.BeforeTest

@ExtendWith(VertxExtension::class)
abstract class VertxBaseTest {
    protected lateinit var vertx: Vertx

    @BeforeTest
    fun initVertx(vertx: Vertx) {
        this.vertx = vertx
    }
}