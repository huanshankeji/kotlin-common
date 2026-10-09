import com.huanshankeji.cpnProject

plugins {
    id("jvm-conventions")
}

// MySQL, Oracle, and SQL Server are feature variants so a PostgreSQL caller does not pull them.
java {
    listOf("mysql", "oracle", "mssql").forEach {
        registerFeature(it) {
            usingSourceSet(sourceSets["main"])
        }
    }
}

dependencies {
    api(cpnProject(project, ":net"))

    with(commonDependencies.testcontainers) {
        api(platformBom())
        api(testcontainers)
        api(testcontainersPostgresql)
        "mysqlApi"(moduleWithoutVersion("testcontainers-mysql"))
        "oracleApi"(moduleWithoutVersion("testcontainers-oracle-free"))
        "mssqlApi"(moduleWithoutVersion("testcontainers-mssqlserver"))
    }
}
