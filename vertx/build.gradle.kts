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
        implementation(platform(stackDepchain()))
        implementation(module("core"))
        "vertxWebImplementation"(module("web"))
        "vertxSqlClientImplementation"(module("sql-client"))
        implementation(module("lang-kotlin"))
        implementation(module("lang-kotlin-coroutines"))
    }

    implementation(project(":core"))
    implementation(project(":coroutines"))
}
