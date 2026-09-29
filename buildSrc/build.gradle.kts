import org.gradle.api.artifacts.dsl.RepositoryHandler

plugins {
    `kotlin-dsl`
}

apply(from = "../gradle/classpath-bootstrap.gradle.kts")
@Suppress("UNCHECKED_CAST")
(extra["repositories"] as RepositoryHandler.() -> Unit)(repositories)

val kotlinVersion = "2.4.20"
val gradleCommonPluginsVersion = extra["gradleCommonPluginsVersion"]

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation("com.huanshankeji:common-gradle-dependencies:0.10.0-20251224-dev-commit-638585398fba71c65927b2ae25213253993eabfc")
    implementation("com.huanshankeji.team:project-gradle-plugins:$gradleCommonPluginsVersion")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.2.0")
}

/*
kotlin {
    compilerOptions {
        optIn.addAll(
            "com.huanshankeji.GradleCommonExperimentalApi",
        )
        //freeCompilerArgs.add("-Xcontext-parameters")
    }
}
*/
