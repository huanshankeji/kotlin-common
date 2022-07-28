plugins {
    id("multiplatform-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // TODO: this is not needed here
                //implementation(commonDependencies.ktor.client.core())
            }
        }
    }
}
