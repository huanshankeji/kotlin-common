import com.huanshankeji.team.artifacts.mavenCentralExcludingHuanshankeji
import com.huanshankeji.setProjectConcatenatedNames

pluginManagement {
    // Must apply inside this block: Kotlin DSL runs pluginManagement before top-level statements.
    apply(from = "gradle/classpath-bootstrap.gradle.kts")
    @Suppress("UNCHECKED_CAST")
    (extra["repositories"] as RepositoryHandler.() -> Unit)(repositories)
}

buildscript {
    dependencies {
        classpath("com.huanshankeji.team:settings-gradle-plugins:${settings.extra["gradleCommonPluginsVersion"]}")
        classpath("com.huanshankeji:kotlin-common-settings-gradle-plugins:${settings.extra["gradleCommonPluginsVersion"]}")
    }
}

plugins {
    id("com.huanshankeji.base-settings-conventions") version (extra["gradleCommonPluginsVersion"] as String)
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentralExcludingHuanshankeji()
    }
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

setProjectConcatenatedNames()
