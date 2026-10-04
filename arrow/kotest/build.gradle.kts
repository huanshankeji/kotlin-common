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
        jvmTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
