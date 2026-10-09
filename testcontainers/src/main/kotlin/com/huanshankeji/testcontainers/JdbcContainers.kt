package com.huanshankeji.testcontainers

import com.huanshankeji.net.HostAndPort
import org.testcontainers.containers.ContainerState
import org.testcontainers.mssqlserver.MSSQLServerContainer
import org.testcontainers.mysql.MySQLContainer
import org.testcontainers.oracle.OracleContainer
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

// https://testcontainers.com/modules/postgresql/
fun LatestPostgreSQLContainer(): PostgreSQLContainer =
    PostgreSQLContainer(DockerImageName.parse("postgres:latest"))

// https://testcontainers.com/modules/mysql/
fun LatestMySQLContainer(): MySQLContainer =
    MySQLContainer(DockerImageName.parse("mysql:latest"))

// https://testcontainers.com/modules/oracle-free/
fun LatestOracleContainer(): OracleContainer =
    OracleContainer(DockerImageName.parse("gvenzl/oracle-free:latest"))

/*
https://testcontainers.com/modules/mssql/
https://learn.microsoft.com/en-us/sql/linux/quickstart-install-connect-docker
https://hub.docker.com/r/microsoft/mssql-server
*/
fun LatestMssqlContainer(): MSSQLServerContainer =
    MSSQLServerContainer(DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
        .acceptLicense()

/** `getHost()` and `getFirstMappedPort()` are declared on [ContainerState]. */
fun ContainerState.hostAndPort(): HostAndPort =
    HostAndPort(host, firstMappedPort)
