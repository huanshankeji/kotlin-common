plugins {
    `multiplatform-conventions`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                //implementation(commonDependencies.ktor.client.core())
            }
        }
    }
}
