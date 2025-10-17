plugins {
    id("multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(commonDependencies.arrow.core())
            }
        }
    }
}
