package com.huanshankeji.vertx.ext.web

import io.vertx.core.Vertx
import io.vertx.ext.web.Router

interface VirtualHostCombinable {
    fun Router.routesOnVirtualHost()
}

interface SingleVirtualHostCombinable : VirtualHostCombinable {
    override fun Router.routesOnVirtualHost() {
        mountSubRouter("/", subRouter()).virtualHost(virtualHost)
    }

    val virtualHost: String

    val vertx: Vertx
    fun subRouter(): Router =
        Router.router(vertx).apply { routes() }

    fun Router.routes()
}
