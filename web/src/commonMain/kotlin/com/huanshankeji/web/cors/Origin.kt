package com.huanshankeji.web.cors

fun origin(isHttps: Boolean, host: String, port: Int?) =
    (if (isHttps) "https" else "http") + "://" + host + (if (port !== null) ":$port" else "")
