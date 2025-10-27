import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("common-conventions")
    kotlin("jvm")
}

kotlin.jvmToolchain(8)

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
    forEachOptIn { compilerOptions.freeCompilerArgs.add("-opt-in=$it") }
}
