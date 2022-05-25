plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.6.10"))
    implementation("com.huanshankeji:common-gradle-dependencies:0.1.0-20220522-kotlin-1.6.10")
    implementation("com.huanshankeji:kotlin-common-gradle-plugins:0.1.2-kotlin-1.6.10")
    //implementation("io.codearte.gradle.nexus:gradle-nexus-staging-plugin:0.30.0")
}
