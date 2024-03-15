import com.huanshankeji.cpnProject

plugins {
    id("multiplatform-conventions")
    with(commonGradleClasspathDependencies.kotlin.plugin.serialization) { applyPluginWithVersion() }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                with(commonDependencies.kotlinx.serialization) {
                    implementation(core())
                    implementation(protobuf())
                }
                implementation(cpnProject(project, ":reflect"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
