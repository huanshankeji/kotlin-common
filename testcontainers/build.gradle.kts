import com.huanshankeji.cpnProject

plugins {
    id("jvm-conventions")
}

// Database modules are feature variants so callers only pull the ones they use.
java {
    listOf("postgresql", "mysql", "oracle", "mssql").forEach {
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
        "postgresqlApi"(testcontainersPostgresql)
        "mysqlApi"(moduleWithoutVersion("testcontainers-mysql"))
        "oracleApi"(moduleWithoutVersion("testcontainers-oracle-free"))
        "mssqlApi"(moduleWithoutVersion("testcontainers-mssqlserver"))
    }
}
