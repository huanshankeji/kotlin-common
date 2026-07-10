pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            forRepository {
                maven {
                    // Resolves the gradle-common settings plugin when it is not in mavenLocal().
                    // Mirrors gradle-common credential resolution; its APIs cannot be called from settings.gradle.kts:
                    // https://github.com/huanshankeji/gradle-common/blob/main/kotlin-common/gradle-library/src/main/kotlin/com/huanshankeji/github/packages/maven/GithubPackagesMavenRegistry.kt
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
                includeVersionByRegex("com\\.huanshankeji", ".*", ".*-dev-commit-[0-9a-f]+$")
            }
        }
    }
}

plugins {
    val gradleCommonPluginsVersion = "0.12.0-dev-commit-916286b26dcfaec3636e27447385c96da6d8ca92"
    id("com.huanshankeji.base-settings-conventions") version gradleCommonPluginsVersion
    id("com.huanshankeji.team.gitversioning.public-open-source-dependency-repositories") version gradleCommonPluginsVersion
}

publicOpenSourceDependencyRepositories {
    mavenCentralExcludingHuanshankeji()
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
