package com.huanshankeji.vertx.ext.web

import io.vertx.ext.web.Router

interface VirtualHostCombinable {
    fun Router.routesOnVirtualHost()
}