plugins {
    id("multiplatform-conventions")
    with(commonGradleClasspathDependencies.kotlin.serializationPlugin) { kotlin(moduleName) version version }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                with(commonDependencies.kotlinx.serialization) {
                    implementation(core())
                    implementation(protobuf())
                }
            }
        }
    }
}
