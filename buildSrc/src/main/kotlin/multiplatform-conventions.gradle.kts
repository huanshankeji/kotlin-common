import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("common-conventions")
    id("com.huanshankeji.kotlin-multiplatform-conventional-targets")
    id("com.huanshankeji.kotlin-multiplatform-sonatype-ossrh-publish-conventions")
}

kotlin {
    jvmToolchain(11) // Updated for Vert.x 5 compatibility (requires JDK 11+)

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        //nodejs()
    }

    sourceSets {
        all {
            forEachOptIn(languageSettings::optIn)
        }
    }
}
