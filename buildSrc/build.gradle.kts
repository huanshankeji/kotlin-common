plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.7.10"))
    implementation("com.huanshankeji:common-gradle-dependencies:0.1.1-20220527-kotlin-1.6.10")
    implementation("com.huanshankeji:kotlin-common-gradle-plugins:0.1.5-kotlin-1.6.10")
}
