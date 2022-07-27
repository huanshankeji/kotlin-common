import com.huanshankeji.CommonDependencies
import com.huanshankeji.DefaultVersions

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

    testImplementation(kotlin("test"))
    testImplementation(CommonDependencies.Kotlinx.Coroutines.test())
    testImplementation(platform("org.junit:junit-bom:${DefaultVersions.junitJupiter}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    with(CommonDependencies.Vertx) {
        testImplementation(moduleWithoutVersion("unit"))
        testImplementation(moduleWithoutVersion("junit5"))
        //testImplementation("io.vertx", "vertx-web", classifier = "tests") // This does not work well.
        testImplementation(moduleWithoutVersion("web-client"))
    }
}

kotlin.sourceSets["test"].languageSettings {
    optIn("kotlin.RequiresOptIn")
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}
