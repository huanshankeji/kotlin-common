import com.huanshankeji.cpnProject

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
    registerFeature("vertxPgClient") {
        usingSourceSet(sourceSets["main"])
    }
    registerFeature("kotlinxIo") {
        usingSourceSet(sourceSets["main"])
    }
    registerFeature("okio") {
        usingSourceSet(sourceSets["main"])
    }
}

dependencies {
    with(commonDependencies.vertx) {
        implementation(platformStackDepchain())
        implementation(moduleWithoutVersion("core"))
        "vertxWebImplementation"(moduleWithoutVersion("web"))
        "vertxSqlClientImplementation"(moduleWithoutVersion("sql-client"))
        "vertxPgClientImplementation"(moduleWithoutVersion("pg-client"))
        implementation(moduleWithoutVersion("lang-kotlin"))
        implementation(moduleWithoutVersion("lang-kotlin-coroutines"))
    }

    "kotlinxIoImplementation"("org.jetbrains.kotlinx:kotlinx-io-core:${commonVersions.kotlinxIo}")
    "okioImplementation"("com.squareup.okio:okio:${DependencyVersions.okio}")

    implementation(cpnProject(project, ":core"))
    implementation(cpnProject(project, ":coroutines"))

    testImplementation(kotlin("test"))
    testImplementation(commonDependencies.kotlinx.coroutines.test())
    with(commonDependencies.orgJunit) {
        testImplementation(platformBom())
        testImplementation(jupiter.withoutVersion())
    }
    with(commonDependencies.vertx) {
        testImplementation(moduleWithoutVersion("unit"))
        testImplementation(moduleWithoutVersion("junit5"))
        //testImplementation("io.vertx", "vertx-web", classifier = "tests") // This does not work well.
        testImplementation(moduleWithoutVersion("web-client"))
    }
    testImplementation(cpnProject(project, ":net"))
}

kotlin.jvmToolchain(11)

kotlin.sourceSets["test"].languageSettings {
    optIn("kotlin.RequiresOptIn")
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}
