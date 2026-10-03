import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    id("base-conventions")
    id("com.huanshankeji.kotlin-multiplatform-js-browser-conventions")
}

kotlin {
    jvmToolchain(11)

    jvm()

    iosArm64()
    iosSimulatorArm64()

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
