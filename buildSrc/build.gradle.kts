plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", "2.4.0"))
    implementation("com.huanshankeji:common-gradle-dependencies:0.10.0-20251024")
    implementation(libs.huanshankeji.team.project.gradle.plugins)
    implementation(libs.huanshankeji.kotlin.common.project.gradle.plugins)
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.2.0")
}
