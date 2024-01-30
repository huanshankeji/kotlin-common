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
)

fun ProjectDescriptor.setProjectConcatenatedNames(prefix: String) {
    name = prefix + name
    for (child in children)
        child.setProjectConcatenatedNames("$name-")
}
rootProject.setProjectConcatenatedNames("")
