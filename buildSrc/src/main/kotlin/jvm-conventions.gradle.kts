import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("common-conventions")
    kotlin("jvm")
    `java-library`
}

kotlin {
    jvmToolchain(11)

    compilerOptions {
        optIn.addAll(optIns)
    }
}
