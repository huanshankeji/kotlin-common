package com.huanshankeji.testcontainers

import com.huanshankeji.net.HostAndPort
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

// https://testcontainers.com/modules/postgresql/
fun LatestPostgreSQLContainer(): PostgreSQLContainer =
    PostgreSQLContainer(DockerImageName.parse("postgres:latest"))

fun JdbcDatabaseContainer<*>.hostAndPort(): HostAndPort =
    HostAndPort(host, firstMappedPort)
