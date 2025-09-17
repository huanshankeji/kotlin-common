import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("common-conventions")
    id("com.huanshankeji.kotlin-jvm-library-sonatype-ossrh-publish-conventions")
}

kotlin.jvmToolchain(17)

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
    forEachOptIn { compilerOptions.freeCompilerArgs.add("-opt-in=$it") }
}
