import com.huanshankeji.CommonDependencies

plugins {
    id("multiplatform-conventions")
    kotlin("plugin.serialization") version kotlinVersion
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                with(CommonDependencies.Kotlinx.Serialization) {
                    implementation(core())
                    implementation(protobuf())
                }
            }
        }
    }
}
