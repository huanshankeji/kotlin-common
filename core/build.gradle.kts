import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `multiplatform-conventions`
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("kotlin.RequiresOptIn")
    }

    val commonTest by getting {
        dependencies {
            implementation(kotlin("test"))
            implementation(commonDependencies.kotest.property())
            implementation(commonDependencies.kotlinx.coroutines.test())
        }
    }
}

tasks.named<KotlinJvmCompile>("compileTestKotlinJvm") {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

// TODO remove this if not needed
tasks.named<JavaCompile>("compileJvmTestJava") {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

