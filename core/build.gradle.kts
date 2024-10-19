plugins {
    `multiplatform-conventions`
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("kotlin.RequiresOptIn")
    }

    val commonTest by getting {
        dependencies {
            implementation(kotlin("test"))
            implementation(commonDependencies.kotest.property())
            implementation(commonDependencies.kotlinx.coroutines.test())
        }
    }
}
