import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    id("common-conventions")
    kotlin("multiplatform")
    id("com.huanshankeji.kotlin-multiplatform-sonatype-ossrh-publish-conventions")
}

kotlin {
    jvm()
    jvmToolchain(8)

    //androidTarget()

    js()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs()
}
