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
    "vertx:with-context-receivers",
    /*
    "vertx:kotlinx-io",
    "vertx:okio",
    */
)

fun ProjectDescriptor.setProjectConcatenatedNames(prefix: String) {
    name = prefix + name
    for (child in children)
        child.setProjectConcatenatedNames("$name-")
}
rootProject.setProjectConcatenatedNames("")

// This is needed for Kotlin Native and Dokka.
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}
