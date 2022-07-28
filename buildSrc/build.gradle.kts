plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.7.10"))
    implementation("com.huanshankeji:common-gradle-dependencies:0.3.0-20220727")
    implementation("com.huanshankeji:kotlin-common-gradle-plugins:0.1.9")
}
