import com.huanshankeji.kotlinx

plugins {
    id("kotlin-multiplatform-conventions")
    kotlin("plugin.serialization") version kotlinVersion
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlinx("serialization-core", kotlinxSerializationVersion))
                implementation(kotlinx("serialization-protobuf", kotlinxSerializationVersion))
            }
        }
    }
}
