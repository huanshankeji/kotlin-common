import com.huanshankeji.cpnProject

plugins {
    id("multiplatform-conventions")
}
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(commonDependencies.kotlinx.coroutines.core())
                implementation(cpnProject(project, ":core"))
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
