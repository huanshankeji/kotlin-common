import com.huanshankeji.cpnProject

plugins {
    `multiplatform-conventions-without-ios`
}

/*
Compiling fails for the iOS targets:
```
e: Compilation failed: Backend Internal error: Exception during psi2ir
```
 */

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
