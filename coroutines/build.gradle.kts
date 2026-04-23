// TODO consider deprecating this whole module in favor of Arrow coroutines (https://arrow-kt.io/learn/coroutines/)

import com.huanshankeji.cpnProject

plugins {
    `multiplatform-conventions`
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
