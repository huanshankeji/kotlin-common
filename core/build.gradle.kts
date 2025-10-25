plugins {
    `multiplatform-conventions`
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("kotlin.RequiresOptIn")
    }

    commonTest {
        dependencies {
            implementation(kotlin("test"))
            implementation(commonDependencies.kotest.property())
            implementation(commonDependencies.kotlinx.coroutines.test())
        }
    }
}
