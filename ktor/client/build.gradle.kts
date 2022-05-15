plugins {
    id("kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
            }
        }
    }
}
