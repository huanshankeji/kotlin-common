plugins {
    id("multiplatform-conventions")
    with(commonGradleClasspathDependencies.kotlin.serializationPlugin) { kotlin(moduleName) version version }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                with(commonDependencies.kotlinx.serialization) {
                    implementation(core())
                    implementation(protobuf())
                }
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
