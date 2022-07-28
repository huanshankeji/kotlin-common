plugins {
    id("multiplatform-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                //implementation(commonDependencies.ktor.client.core())
            }
        }
    }
}
