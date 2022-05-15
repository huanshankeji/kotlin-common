plugins {
    id("kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.arrow-kt:arrow-core:$arrowVersion")
            }
        }
    }
}
