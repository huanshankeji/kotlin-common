/*
 * Shared early classpath bootstrap for settings (pluginManagement) and buildSrc:
 * versions and repositories needed before project conventions are on the classpath.
 *
 * Currently wires GitHub Packages / mavenLocal for gradle-common *-dev-commit-* artifacts.
 * Copied and adapted from (gradle-common APIs are not on the classpath yet):
 * https://github.com/huanshankeji/gradle-common/blob/main/kotlin-common/gradle-library/src/main/kotlin/com/huanshankeji/github/packages/maven/GithubPackagesMavenRegistry.kt
 */

import org.gradle.api.artifacts.dsl.RepositoryHandler

extra["gradleCommonPluginsVersion"] = "0.12.0-dev-commit-656d3d5f54d76c571b79f96ecc236cb54b013f50"

extra["gradleCommonDevCommitRepos"] = fun RepositoryHandler.() {
    gradlePluginPortal()
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
