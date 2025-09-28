package com.huanshankeji.vertx.core

import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.kotlin.core.deploymentOptionsOf
import java.util.function.Supplier

fun Vertx.deployVerticleOnAllCores(verticleSupplier: Supplier<Verticle>) =
    deployVerticle(verticleSupplier, deploymentOptionsOf(instances = Runtime.getRuntime().availableProcessors()))
