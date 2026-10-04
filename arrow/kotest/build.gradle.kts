import com.huanshankeji.cpnProject
import org.gradle.api.tasks.testing.Test

plugins {
    `multiplatform-conventions`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(cpnProject(project, ":core"))
                api(commonDependencies.arrow.module("fx-coroutines"))
                api(commonDependencies.kotest.module("framework-engine"))
            }
        }
        jvmTest {
            dependencies {
                implementation(commonDependencies.kotest.module("assertions-core"))
                implementation(commonDependencies.kotest.module("runner-junit5"))
            }
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}
