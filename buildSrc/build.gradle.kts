plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.7.10"))
    implementation("com.huanshankeji:common-gradle-dependencies:0.3.1-20220728")
    implementation("com.huanshankeji.team:gradle-plugins:0.2.0-SNAPSHOT") // TODO: use a stable version
}
