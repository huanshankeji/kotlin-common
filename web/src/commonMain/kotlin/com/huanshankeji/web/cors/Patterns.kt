package com.huanshankeji.web.cors

fun corsAllowedOriginPattern(isHttps: Boolean, host: String, port: Int? = null): String =
    Regex.escape(origin(isHttps, host, port))

const val CORS_LOCALHOST_ALL_PORTS_ALLOWED_ORIGIN_PATTERN = "http:\\/\\/localhost(?::\\d+)?"
