import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("jvm-conventions")
}

java {
    registerFeature("vertxWeb") {
        usingSourceSet(sourceSets["main"])
    }
}

dependencies {
    implementation(project(":vertx"))
    with(commonDependencies.vertx) {
        implementation(platformStackDepchain())
        implementation(moduleWithoutVersion("core"))
        "vertxWebImplementation"(moduleWithoutVersion("web"))
        implementation(moduleWithoutVersion("lang-kotlin-coroutines"))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}
