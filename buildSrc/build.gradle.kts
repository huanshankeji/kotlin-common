plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    // Bootstrap: same exclusiveContent as settings.gradle.kts pluginManagement.
    exclusiveContent {
        forRepository {
            mavenLocal()
        }
        forRepository {
            maven {
                url = uri("https://maven.pkg.github.com/huanshankeji/gradle-common")
                credentials {
                    username = providers.gradleProperty("gpr.user")
                        .orElse(providers.gradleProperty("gprUser")).getOrNull()
                    password = providers.gradleProperty("gpr.key")
                        .orElse(providers.gradleProperty("gprKey")).getOrNull()
                }
            }
        }
        filter {
            includeVersionByRegex("""com\.huanshankeji(\..+)?""", ".*", """.*-dev-commit-[0-9a-f]+.*""")
        }
    }
}

// buildSrc does not inherit root gradle.properties as Gradle properties.
val gradleCommonPluginsVersion =
    file("../gradle.properties").readLines()
        .map { it.substringBefore('#').trim() }
        .first { it.startsWith("gradleCommonPluginsVersion=") }
        .substringAfter("=")

dependencies {
    implementation(kotlin("gradle-plugin", "2.4.0"))
    implementation("com.huanshankeji:common-gradle-dependencies:0.10.0-20251024")
    implementation("com.huanshankeji.team:project-gradle-plugins:$gradleCommonPluginsVersion")
    implementation("com.huanshankeji:kotlin-common-project-gradle-plugins:$gradleCommonPluginsVersion")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.2.0")
}
