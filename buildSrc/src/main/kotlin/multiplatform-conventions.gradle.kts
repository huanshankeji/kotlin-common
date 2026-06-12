import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("common-conventions")
    id("com.huanshankeji.kotlin-multiplatform-conventional-targets")
    id("com.huanshankeji.kotlin-abi-validation-conventions")
}

kotlin {
    jvmToolchain(11)

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        //nodejs()
    }

    compilerOptions {
        optIn.addAll(optIns)
    }
}
