pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("public-open-source-dependency-repositories") version
        "0.12.0-dev-commit-e2dcb9d3d327edd99d80a6dfa041fc47c2db7bbb-dirty-SNAPSHOT"
}

publicOpenSourceDependencyRepositories {
    huanshankejiMavenLocal()
    githubPackages("kotlin-common")
    mavenCentralExcludingHuanshankejiNonStable()
}

rootProject.name = "kotlin-common"

include(
    "core",
    "net",
    "web",

    "arrow",
    "coroutines",
    "exposed",
    "ktor:client",
    "reflect",
    "serialization",
    "serialization:benchmark",
    "serialization:benchmark:jvm-only",
    "vertx",
    "vertx:with-context-parameters",
)

fun ProjectDescriptor.setProjectConcatenatedNames(prefix: String) {
    name = prefix + name
    for (child in children)
        child.setProjectConcatenatedNames("$name-")
}
rootProject.setProjectConcatenatedNames("")
