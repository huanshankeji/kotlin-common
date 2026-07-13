import com.huanshankeji.team.artifacts.mavenCentralExcludingHuanshankeji

pluginManagement {
    repositories {
        gradlePluginPortal()
        // Bootstrap: same exclusiveContent as buildSrc/build.gradle.kts (pluginManagement is isolated).
        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            forRepository {
                maven {
                    url = uri("https://maven.pkg.github.com/huanshankeji/gradle-common")
                    credentials {
                        with(providers) {
                            username = gradleProperty("gpr.user").orElse(gradleProperty("gprUser")).getOrNull()
                            password = gradleProperty("gpr.key").orElse(gradleProperty("gprKey")).getOrNull()
                        }
                    }
                }
            }
            filter {
                includeVersionByRegex("""com\.huanshankeji(\..+)?""", ".*", """.*-dev-commit-[0-9a-f]+.*""")
            }
        }
    }
}

buildscript {
    val gradleCommonPluginsVersion =
        providers.gradleProperty("gradleCommonPluginsVersion").get()
    dependencies {
        classpath("com.huanshankeji.team:settings-gradle-plugins:$gradleCommonPluginsVersion")
    }
}

plugins {
    id("com.huanshankeji.base-settings-conventions") version providers.gradleProperty("gradleCommonPluginsVersion").get()
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

fun ProjectDescriptor.setProjectConcatenatedNames(prefix: String) {
    name = prefix + name
    for (child in children)
        child.setProjectConcatenatedNames("$name-")
}
rootProject.setProjectConcatenatedNames("")
