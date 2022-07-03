package com.huanshankeji.vertx.ext.web

import io.vertx.core.Verticle
import io.vertx.ext.web.Router

interface VirtualHostCombinable {
    fun Router.routesOnVirtualHost()
}

interface SingleVirtualHostVerticle : VirtualHostCombinable, Verticle {
    override fun Router.routesOnVirtualHost() {
        mountSubRouter("/", subRouter()).virtualHost(virtualHost)
    }

    val virtualHost: String

    fun subRouter(): Router = Router.router(vertx).apply { routes() }

    fun Router.routes()
}
