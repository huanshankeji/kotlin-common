import com.huanshankeji.cpnProject

plugins {
    //id("com.huanshankeji.kotlin-multiplatform-jvm-and-js-browser-conventions") // this causes the build to fail somehow
    id("com.huanshankeji.kotlin-multiplatform-conventions")
    with(commonGradleClasspathDependencies.kotlin.plugin.serialization) { applyPluginWithVersion() }
    id("com.huanshankeji.benchmark.kotlinx-benchmark-multiplatform-conventions")
}

kotlin {
    jvm()
    js(IR) {
        browser()
        nodejs()
    }
    // These two targets cause the build to fail. Try adding them in the future.
    //linuxX64()
    //wasmJs { nodejs() }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                with(commonDependencies.kotlinx.serialization) {
                    implementation(core())
                    implementation(json())
                    implementation(protobuf())
                }
                implementation(cpnProject(project, ":serialization"))

                // This has to be added explicitly for kotlinx.benchmark on the JS target, which is probably a bug.
                implementation(cpnProject(project, ":reflect"))
            }
        }
    }
}
