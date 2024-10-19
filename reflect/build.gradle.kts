plugins {
    `multiplatform-conventions`
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            implementation(kotlin("reflect"))
        }
    }

    commonTest {
        dependencies {
            implementation(kotlin("test"))
        }
    }
}
