import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    id("base-conventions")
    id("com.huanshankeji.kotlin-multiplatform-conventional-targets")
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

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()
}
