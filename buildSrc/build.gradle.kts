plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    exclusiveContent {
        forRepository {
            mavenLocal()
        }
        forRepository {
            maven {
                // Same bootstrap as settings.gradle.kts pluginManagement — buildSrc resolves
                // gradle-common plugins as implementation deps, not via the plugins DSL.
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

val gradleCommonPluginsVersion =
    "0.12.0-dev-commit-656d3d5f54d76c571b79f96ecc236cb54b013f50"

dependencies {
    implementation(kotlin("gradle-plugin", "2.4.0"))
    implementation("com.huanshankeji:common-gradle-dependencies:0.10.0-20251024")
    implementation("com.huanshankeji.team:project-gradle-plugins:$gradleCommonPluginsVersion")
    implementation("com.huanshankeji:kotlin-common-project-gradle-plugins:$gradleCommonPluginsVersion")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.2.0")
}
