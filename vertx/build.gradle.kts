import com.huanshankeji.CommonDependencies

plugins {
    id("jvm-conventions")
}

java {
    registerFeature("vertxWeb") {
        usingSourceSet(sourceSets["main"])
    }
    registerFeature("vertxSqlClient") {
        usingSourceSet(sourceSets["main"])
    }
}

dependencies {
    with(CommonDependencies.Vertx) {
        implementation(platformStackDepchain())
        implementation(moduleWithoutVersion("core"))
        "vertxWebImplementation"(moduleWithoutVersion("web"))
        "vertxSqlClientImplementation"(moduleWithoutVersion("sql-client"))
        implementation(moduleWithoutVersion("lang-kotlin"))
        implementation(moduleWithoutVersion("lang-kotlin-coroutines"))
    }

    implementation(project(":core"))
    implementation(project(":coroutines"))
}
