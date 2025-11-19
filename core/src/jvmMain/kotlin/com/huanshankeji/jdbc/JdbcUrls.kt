package com.huanshankeji.jdbc

@Deprecated("The JDBC URL format is not actually universal.")
fun jdbcUrl(rdbms: String, host: String, port: Int?, database: String) =
    "jdbc:$rdbms://$host${port?.let { ":$it" } ?: ""}/$database"
