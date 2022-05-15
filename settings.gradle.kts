rootProject.name = "kotlin-common"

include(
    "core",
    "net",
    "web",

    "arrow",
    "coroutines",
    "exposed",
    "ktor:client",
    "serialization",
    "vertx",
)

buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.huanshankeji:kotlin-common-gradle-plugin:0.1.1-SNAPSHOT") // TODO: use 0.1.1
    }
}
