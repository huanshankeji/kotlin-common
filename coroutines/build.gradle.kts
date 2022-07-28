plugins {
    id("multiplatform-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(commonDependencies.kotlinx.coroutines.core())
            }
        }
    }
}
