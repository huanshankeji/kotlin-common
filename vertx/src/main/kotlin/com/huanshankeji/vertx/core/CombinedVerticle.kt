package com.huanshankeji.vertx.core

import io.vertx.core.*

interface CombinedVerticleFunctions : Verticle {
    val subVerticles: List<Verticle>

    fun subVerticlesInit(vertx: Vertx, context: Context) {
        for (verticle in subVerticles)
            verticle.init(vertx, context)
    }

    fun subVerticlesStart(startPromise: Promise<Void?>) =
        subVerticles.runAllWithPromise({ start(it) }, startPromise)

    fun subVerticlesStop(stopPromise: Promise<Void?>) =
        subVerticles.runAllWithPromise({ stop(it) }, stopPromise)

    fun subVerticlesStartUnit(startPromise: Promise<Unit>) =
        @Suppress("UNCHECKED_CAST")
        subVerticlesStart(startPromise as Promise<Void?>)

    fun subVerticlesStopUnit(stopPromise: Promise<Unit>) =
        @Suppress("UNCHECKED_CAST")
        subVerticlesStop(stopPromise as Promise<Void?>)

    fun <E> List<E>.runAllWithPromise(block: E.(promise: Promise<Void?>) -> Unit, promise: Promise<Void?>) {
        Future.all(map {
            @Suppress("NAME_SHADOWING") val promise = Promise.promise<Void?>()
            vertx.runOnContext { _ -> it.block(promise) }
            //it.block(promise)
            promise.future()
        }).onComplete {
            promise.handle(it.map { null })
        }
    }
}

// This class should preferably be used if multiple `Verticle`s don't have any shared resources. If they do, consider combining them using other ways of abstraction for better efficiency.
open class CombinedVerticle(override val subVerticles: List<Verticle>) : AbstractVerticle(), CombinedVerticleFunctions {
    override fun init(vertx: Vertx, context: Context) {
        super.init(vertx, context)
        subVerticlesInit(vertx, context)
    }

    override fun start(startPromise: Promise<Void?>) =
        subVerticlesStart(startPromise)

    override fun stop(stopPromise: Promise<Void?>) =
        subVerticlesStop(stopPromise)
}
