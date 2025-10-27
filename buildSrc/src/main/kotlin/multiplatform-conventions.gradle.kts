import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("common-conventions")
    id("com.huanshankeji.kotlin-multiplatform-conventional-targets")
}

kotlin {
    jvmToolchain(8)

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
