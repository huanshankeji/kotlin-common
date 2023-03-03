import com.huanshankeji.cpnProject

plugins {
    id("multiplatform-conventions")
}
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(commonDependencies.kotlinx.coroutines.core())
                implementation(cpnProject(project, ":core"))
            }
        }
    }
}
