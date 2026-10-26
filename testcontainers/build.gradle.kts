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
        implementation(platformBom())
        implementation(testcontainers)
        "postgresqlImplementation"(testcontainersPostgresql)
        "mysqlImplementation"(moduleWithoutVersion("testcontainers-mysql"))
        "oracleImplementation"(moduleWithoutVersion("testcontainers-oracle-free"))
        "mssqlImplementation"(moduleWithoutVersion("testcontainers-mssqlserver"))
    }
}
