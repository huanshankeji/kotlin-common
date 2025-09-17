plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    maven {
        url = uri("https://maven.pkg.github.com/huanshankeji/gradle-common")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation(kotlin("gradle-plugin", "2.1.0"))
    implementation("com.huanshankeji:common-gradle-dependencies:0.10.0-20241203-SNAPSHOT") // TODO don't use a snapshot version in a main branch
    implementation("com.huanshankeji.team:gradle-plugins:0.10.0-SNAPSHOT") // TODO don't use a snapshot version in a main branch
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.0.0-Beta")
}
