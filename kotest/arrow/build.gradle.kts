plugins {
    `multiplatform-conventions`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(commonDependencies.arrow.module("fx-coroutines"))
                api(commonDependencies.kotest.module("framework-engine"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(commonDependencies.kotlinx.coroutines.test())
            }
        }
    }
}
