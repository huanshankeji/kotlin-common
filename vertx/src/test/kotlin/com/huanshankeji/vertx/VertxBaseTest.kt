package com.huanshankeji.vertx

import io.vertx.core.Vertx
import io.vertx.ext.unit.junit.RunTestOnContext
import org.junit.Rule
import kotlin.test.BeforeTest

abstract class VertxBaseTest {
    @Rule
    @JvmField
    val rule = RunTestOnContext()

    protected lateinit var vertx: Vertx

    @BeforeTest
    fun initVertx() {
        vertx = rule.vertx()
    }
}